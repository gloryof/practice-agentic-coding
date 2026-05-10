package jp.glory.practice.agentic.shared.testinfra

import jp.glory.practice.agentic.shared.infra.adapter.persistence.table.authCredentialTable
import jp.glory.practice.agentic.shared.infra.adapter.persistence.table.authorTable
import jp.glory.practice.agentic.shared.infra.adapter.persistence.table.bookItemStockTable
import jp.glory.practice.agentic.shared.infra.adapter.persistence.table.bookItemTable
import jp.glory.practice.agentic.shared.infra.adapter.persistence.table.bookProductAuthorTable
import jp.glory.practice.agentic.shared.infra.adapter.persistence.table.bookProductTable
import jp.glory.practice.agentic.shared.infra.adapter.persistence.table.libraryUserTable
import jp.glory.practice.agentic.shared.infra.adapter.persistence.table.publisherTable
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.junit.jupiter.api.parallel.ResourceLock
import org.komapper.core.dsl.Meta
import org.komapper.core.dsl.QueryDsl
import org.komapper.jdbc.JdbcDatabase
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.MountableFile

@SpringBootTest
@Execution(ExecutionMode.SAME_THREAD)
@ResourceLock("postgres")
abstract class PostgreSqlTestBase {
    @Autowired
    protected lateinit var komapperDatabase: JdbcDatabase

    @Autowired
    protected lateinit var jdbcTemplate: JdbcTemplate

    private val bookItemStocks = Meta.bookItemStockTable.clone(table = "book_item_stocks")
    private val bookItems = Meta.bookItemTable.clone(table = "book_items")
    private val bookProductAuthors = Meta.bookProductAuthorTable.clone(table = "book_product_authors")
    private val bookProducts = Meta.bookProductTable.clone(table = "book_products")
    private val authors = Meta.authorTable.clone(table = "authors")
    private val publishers = Meta.publisherTable.clone(table = "publishers")
    private val credentials = Meta.authCredentialTable.clone(table = "auth_credentials")
    private val libraryUsers = Meta.libraryUserTable.clone(table = "library_users")

    @BeforeEach
    fun clearTables() {
        komapperDatabase.runQuery { QueryDsl.delete(bookItemStocks).all() }
        komapperDatabase.runQuery { QueryDsl.delete(bookItems).all() }
        komapperDatabase.runQuery { QueryDsl.delete(bookProductAuthors).all() }
        komapperDatabase.runQuery { QueryDsl.delete(bookProducts).all() }
        komapperDatabase.runQuery { QueryDsl.delete(authors).all() }
        komapperDatabase.runQuery { QueryDsl.delete(publishers).all() }
        komapperDatabase.runQuery { QueryDsl.delete(credentials).all() }
        komapperDatabase.runQuery { QueryDsl.delete(libraryUsers).all() }
    }

    companion object {
        @JvmStatic
        private val postgres = SharedPostgres.container

        @JvmStatic
        @DynamicPropertySource
        fun registerDataSourceProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url") { postgres.jdbcUrl.withUtcTimezoneOption() }
            registry.add("spring.datasource.username", postgres::getUsername)
            registry.add("spring.datasource.password", postgres::getPassword)
            registry.add("spring.flyway.enabled") { "false" }
        }

        private fun String.withUtcTimezoneOption(): String {
            val timezoneOption = "options=-c%20TimeZone=UTC"
            if (contains("TimeZone=UTC")) return this
            return if (contains("?")) {
                "$this&$timezoneOption"
            } else {
                "$this?$timezoneOption"
            }
        }
    }
}

private object SharedPostgres {
    val container: PostgreSQLContainer<*> =
        PostgreSQLContainer("postgres:16")
            .withDatabaseName("agentic")
            .withUsername("agentic")
            .withPassword("agentic")
            .withCopyFileToContainer(
                MountableFile.forClasspathResource("db/migration"),
                "/docker-entrypoint-initdb.d",
            ).also { postgres ->
                postgres.start()
                Runtime.getRuntime().addShutdownHook(
                    Thread {
                        if (postgres.isRunning) {
                            postgres.stop()
                        }
                    },
                )
            }
}
