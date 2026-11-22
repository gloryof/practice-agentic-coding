// 検査対象を固定し、認証情報を対象ホスト以外へ送らない。
var Files = Java.type('java.nio.file.Files');
var Paths = Java.type('java.nio.file.Paths');
var URI = Java.type('org.apache.commons.httpclient.URI');
var HttpClient = Java.type('java.net.http.HttpClient');
var HttpRequest = Java.type('java.net.http.HttpRequest');
var HttpResponse = Java.type('java.net.http.HttpResponse');
var JavaURI = Java.type('java.net.URI');
var Duration = Java.type('java.time.Duration');
var authClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1)
    .connectTimeout(Duration.ofSeconds(10)).build();
var Source = Java.type('net.htmlparser.jericho.Source');
var stage = 'initial';

function bffRequest(path, cookie) {
    var builder = HttpRequest.newBuilder(JavaURI.create('http://host.docker.internal:3000' + path))
        .timeout(Duration.ofSeconds(10));
    if (cookie) builder.header('Cookie', cookie);
    return authClient.send(builder.GET().build(), HttpResponse.BodyHandlers.ofString());
}

function refreshBff(auth) {
    var page = bffRequest('/login', '');
    var forms = new Source(String(page.body())).getAllElements('form');
    var fields = null;
    for (var i = 0; i < forms.size(); i++) {
        var inputs = forms.get(i).getAllElements('input');
        var values = [];
        var hasEmail = false;
        for (var j = 0; j < inputs.size(); j++) {
            var name = inputs.get(j).getAttributeValue('name');
            if (!name) continue;
            name = String(name);
            var value = inputs.get(j).getAttributeValue('value');
            value = value === null ? '' : String(value);
            if (name === 'email') { value = auth.email; hasEmail = true; }
            if (name === 'password') value = auth.password;
            values.push([name, value]);
        }
        if (hasEmail) { fields = values; break; }
    }
    if (!fields) {
        Files.writeString(Paths.get('/tmp/zap-auth-error'), 'bff-refresh-form');
        return false;
    }
    var boundary = 'security-refresh-boundary';
    var body = '';
    fields.forEach(function(field) {
        body += '--' + boundary + '\r\nContent-Disposition: form-data; name="' + field[0] + '"\r\n\r\n' + field[1] + '\r\n';
    });
    body += '--' + boundary + '--\r\n';
    var request = HttpRequest.newBuilder(JavaURI.create('http://host.docker.internal:3000/login'))
        .timeout(Duration.ofSeconds(10)).header('Origin', 'http://host.docker.internal:3000')
        .header('Content-Type', 'multipart/form-data; boundary=' + boundary)
        .POST(HttpRequest.BodyPublishers.ofString(body)).build();
    var response = authClient.send(request, HttpResponse.BodyHandlers.ofString());
    if (response.statusCode() !== 303) {
        Files.writeString(Paths.get('/tmp/zap-auth-error'), 'bff-refresh-http-' + response.statusCode());
        return false;
    }
    var cookies = response.headers().allValues('set-cookie');
    for (var k = 0; k < cookies.size(); k++) {
        var match = String(cookies.get(k)).match(/^book_vista_session=([^;]+)/);
        if (match) {
            auth.cookie = 'book_vista_session=' + match[1];
            Files.writeString(Paths.get('/tmp/zap-auth.json'), JSON.stringify(auth));
            return true;
        }
    }
    Files.writeString(Paths.get('/tmp/zap-auth-error'), 'bff-refresh-cookie');
    return false;
}

function sendingRequest(msg, initiator, helper) {
    try {
        sendingChecked(msg, initiator, helper);
    } catch (error) {
        Files.writeString(Paths.get('/tmp/zap-auth-failed'), 'failed');
        Files.writeString(Paths.get('/tmp/zap-auth-error'), stage + ':' + String(error.name));
        msg.getRequestHeader().setURI(new URI('http://host.docker.internal:3000/__security_blocked__', false));
        msg.getRequestHeader().setHeader('Host', 'host.docker.internal:3000');
        msg.getRequestHeader().setHeader('Authorization', null);
        msg.getRequestHeader().setHeader('Cookie', null);
        msg.setRequestBody('');
        msg.getRequestHeader().setContentLength(0);
    }
}

function sendingChecked(msg, initiator, helper) {
    var uri = msg.getRequestHeader().getURI();
    var host = String(uri.getHost());
    var port = uri.getPort();
    var path = String(uri.getPath());
    var allowed = host === 'host.docker.internal' &&
        (port === 3000 || (port === 8080 && path.indexOf('/api/v1/') === 0 && path.indexOf('..') === -1));
    if (!allowed) {
        Files.writeString(Paths.get('/tmp/zap-blocked'), 'blocked');
        msg.getRequestHeader().setURI(new URI('http://host.docker.internal:3000/__security_blocked__', false));
        msg.getRequestHeader().setHeader('Host', 'host.docker.internal:3000');
        msg.getRequestHeader().setHeader('Authorization', null);
        msg.getRequestHeader().setHeader('Cookie', null);
        msg.setRequestBody('');
        msg.getRequestHeader().setContentLength(0);
        return;
    }
    var auth = JSON.parse(String(Files.readString(Paths.get('/tmp/zap-auth.json'))));
    if (auth.mode === 'authenticated') {
        // 認証維持の再ログイン枠を残し、既存のログイン制限は変更しない。
        if (port === 3000 && path === '/login') Java.type('java.lang.Thread').sleep(200);
        if (port === 3000) {
            stage = 'bff-probe';
            var checkedBff = bffRequest('/', auth.cookie);
            if (checkedBff.statusCode() !== 200 || String(checkedBff.body()).indexOf('ログアウトする') === -1) {
                stage = 'bff-refresh';
                if (!refreshBff(auth)) Files.writeString(Paths.get('/tmp/zap-auth-failed'), 'failed');
            }
        }
        // 保護対象を検査する直前に認証を確認する。ログアウトの検査は毎回新しいトークンを使う。
        // 同期処理が完了してから検査要求を送るため、要求は並行しない。
        var refresh = port === 8080 && path === '/api/v1/auth/logout';
        if (port === 8080 && path !== '/api/v1/auth/login' && path !== '/api/v1/library-users/registrations' && !refresh) {
            stage = 'api-probe';
            var probe = HttpRequest.newBuilder(JavaURI.create('http://host.docker.internal:8080/api/v1/book-items?isbn=9780000000001'))
                .timeout(Duration.ofSeconds(10)).header('Authorization', 'Bearer ' + auth.token).GET().build();
            var checked = authClient.send(probe, HttpResponse.BodyHandlers.ofString());
            refresh = checked.statusCode() === 401;
            if (checked.statusCode() !== 200 && !refresh) Files.writeString(Paths.get('/tmp/zap-auth-lost'), 'lost');
        }
        if (refresh) {
            stage = 'api-refresh';
            var request = HttpRequest.newBuilder(JavaURI.create('http://host.docker.internal:8080/api/v1/auth/login'))
                .timeout(Duration.ofSeconds(10)).header('Content-Type', 'application/json')
                .POST(HttpRequest.BodyPublishers.ofString(JSON.stringify({email: auth.email, password: auth.password}))).build();
            var response = authClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() !== 200) {
                Files.writeString(Paths.get('/tmp/zap-auth-lost'), 'lost');
            } else {
                auth.token = JSON.parse(String(response.body())).access_token;
                Files.writeString(Paths.get('/tmp/zap-auth.json'), JSON.stringify(auth));
            }
        }
        if (port === 3000 && auth.cookie) msg.getRequestHeader().setHeader('Cookie', auth.cookie);
        if (port === 8080 && auth.token) msg.getRequestHeader().setHeader('Authorization', 'Bearer ' + auth.token);
    } else {
        msg.getRequestHeader().setHeader('Cookie', null);
        msg.getRequestHeader().setHeader('Authorization', null);
    }
}

function responseReceived(msg, initiator, helper) {
    var auth = JSON.parse(String(Files.readString(Paths.get('/tmp/zap-auth.json'))));
    var uri = msg.getRequestHeader().getURI();
    var path = String(uri.getPath());
    var cookie = msg.getResponseHeader().getHeader('Set-Cookie');
    if (auth.mode === 'authenticated' && uri.getPort() === 3000 && cookie) {
        var match = String(cookie).match(/book_vista_session=([^;]+)/);
        if (match) {
            auth.cookie = 'book_vista_session=' + match[1];
            Files.writeString(Paths.get('/tmp/zap-auth.json'), JSON.stringify(auth));
        }
    }
    // 攻撃用要求の401だけでは失効と判定しない。
    // 認証維持はsendingCheckedの通常要求による確認と、区分終了時の確認で判定する。
}
