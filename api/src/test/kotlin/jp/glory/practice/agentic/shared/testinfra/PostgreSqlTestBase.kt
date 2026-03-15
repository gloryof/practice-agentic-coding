package jp.glory.practice.agentic.shared.testinfra

import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.MountableFile

@SpringBootTest
abstract class PostgreSqlTestBase {
    @Autowired
    protected lateinit var jdbcTemplate: JdbcTemplate

    @BeforeEach
    fun clearTables() {
        jdbcTemplate.update("DELETE FROM auth_credentials")
        jdbcTemplate.update("DELETE FROM library_users")
    }

    companion object {
        @JvmStatic
        private val postgres = SharedPostgres.container

        @JvmStatic
        @DynamicPropertySource
        fun registerDataSourceProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl)
            registry.add("spring.datasource.username", postgres::getUsername)
            registry.add("spring.datasource.password", postgres::getPassword)
            registry.add("spring.flyway.enabled") { "false" }
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
