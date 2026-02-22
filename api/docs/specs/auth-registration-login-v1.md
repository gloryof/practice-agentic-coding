# Auth Registration/Login API Spec (v1)

## Related Story
- `user-stories/US-0001-library-user-registration-login.md`

## Registration API
- Endpoint: `POST /api/v1/library-users/registrations`
- Request:
```json
{
  "email": "user@example.com",
  "password": "Str0ng!Passw0rd"
}
```
- Success Response: `201 Created`
```json
{
  "library_user_id": "01JZ....",
  "email": "user@example.com",
  "registered_at": "2026-02-22T12:34:56Z"
}
```

## Login API
- Endpoint: `POST /v1/auth/login`
- Request:
```json
{
  "email": "user@example.com",
  "password": "Str0ng!Passw0rd"
}
```
- Success Response: `200 OK`
```json
{
  "access_token": "3f3e0b7a-...-9a52",
  "token_type": "Bearer",
  "expires_in_seconds": 86400
}
```

## Validation Rules
- `email`: Required. Only half-width characters are allowed (no email format validation).
- `password`: Required. At least 12 characters and must include uppercase, lowercase, number, and symbol.

## Error Contract
- Common Error Response:
```json
{
  "code": "VALIDATION_ERROR",
  "message": "入力値に誤りがあります。",
  "details": [
    {
      "field": "email",
      "reason": "must_be_half_width"
    }
  ],
  "trace_id": "a1b2c3..."
}
```
- Status Codes:
  - `400 Bad Request`: Invalid input, duplicate email
  - `401 Unauthorized`: Login failed
