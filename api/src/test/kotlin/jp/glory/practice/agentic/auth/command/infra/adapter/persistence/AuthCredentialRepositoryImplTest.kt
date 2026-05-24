package jp.glory.practice.agentic.auth.command.infra.adapter.persistence

import com.github.michaelbull.result.fold
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import jp.glory.practice.agentic.auth.command.domain.model.AuthCredential
import jp.glory.practice.agentic.auth.command.domain.model.PasswordHash
import jp.glory.practice.agentic.libraryuser.command.domain.model.LibraryUserId
import jp.glory.practice.agentic.shared.infra.adapter.persistence.dao.AuthCredentialDao
import jp.glory.practice.agentic.shared.infra.adapter.persistence.table.AuthCredentialTable
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class AuthCredentialRepositoryImplTest {
    @Nested
    inner class Save {
        @Test
        fun `given credential when save then delegates insert to dao`() {
            val dao = mockk<AuthCredentialDao>(relaxed = true)
            val sut = AuthCredentialRepositoryImpl(dao)
            val captured = slot<AuthCredentialTable>()
            val credential =
                AuthCredential(
                    libraryUserId = LibraryUserId("user0000000000000000000021"),
                    passwordHash = passwordHash("hashed-value-21"),
                )

            every { dao.insert(capture(captured)) } returns Unit

            sut.save(credential)

            verify(exactly = 1) { dao.insert(any()) }
            assertEquals(credential.libraryUserId.value, captured.captured.id)
            assertEquals(credential.libraryUserId.value, captured.captured.libraryUserId)
            assertEquals(credential.passwordHash.value, captured.captured.passwordHash)
        }
    }

    @Nested
    inner class FindByLibraryUserId {
        @Test
        fun `given existing record when find by library user id then returns credential`() {
            val dao = mockk<AuthCredentialDao>()
            val sut = AuthCredentialRepositoryImpl(dao)
            val userId = LibraryUserId("user0000000000000000000022")
            every { dao.findByLibraryUserId(userId.value) } returns
                AuthCredentialTable(
                    id = userId.value,
                    libraryUserId = userId.value,
                    passwordHash = "hashed-value-22",
                )

            val credential = sut.findByLibraryUserId(userId)

            assertNotNull(credential)
            assertEquals(userId.value, credential.libraryUserId.value)
            assertEquals("hashed-value-22", credential.passwordHash.value)
        }

        @Test
        fun `given missing record when find by library user id then returns null`() {
            val dao = mockk<AuthCredentialDao>()
            val sut = AuthCredentialRepositoryImpl(dao)
            every { dao.findByLibraryUserId("user0000000000000000000023") } returns null

            val credential = sut.findByLibraryUserId(LibraryUserId("user0000000000000000000023"))

            assertNull(credential)
        }

        @Test
        fun `given invalid stored hash when find by library user id then throws exception`() {
            val dao = mockk<AuthCredentialDao>()
            val sut = AuthCredentialRepositoryImpl(dao)
            every { dao.findByLibraryUserId("user0000000000000000000024") } returns
                AuthCredentialTable(
                    id = "user0000000000000000000024",
                    libraryUserId = "user0000000000000000000024",
                    passwordHash = "",
                )

            assertFailsWith<IllegalStateException> {
                sut.findByLibraryUserId(LibraryUserId("user0000000000000000000024"))
            }
        }
    }

    private fun passwordHash(raw: String): PasswordHash =
        PasswordHash.create(raw).fold(
            success = { it },
            failure = { error("expected valid password hash") },
        )
}
