package jp.glory.practice.agentic.libraryuser.command.web

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import jp.glory.practice.agentic.libraryuser.command.usecase.RegisterLibraryUserResult
import jp.glory.practice.agentic.libraryuser.command.usecase.RegisterLibraryUserUseCase
import jp.glory.practice.agentic.shared.spring.GlobalExceptionHandler
import jp.glory.practice.agentic.shared.usecase.UsecaseError
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder
import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RegistrationControllerTest {
    private data class TestContext(
        val mvc: MockMvc,
        val useCase: RegisterLibraryUserUseCase,
    )

    @Test
    fun `returns 201 on success`() {
        val context = createSut()
        every { context.useCase.register(any()) } returns
            Ok(
                RegisterLibraryUserResult(
                    libraryUserId = "user-id-1",
                    email = "user@example.com",
                    registeredAt = Instant.parse("2026-02-22T12:34:56Z"),
                    eventName = "LibraryUserRegisteredEvent",
                ),
            )

        val response =
            context.mvc
                .perform(
                    post("/api/v1/library-users/registrations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""{"email":"user@example.com","password":"Str0ng!Passw0rd"}"""),
                ).andReturn()
                .response

        assertEquals(201, response.status)
        assertTrue(response.contentAsString.contains("library_user_id"))
    }

    @Test
    fun `returns 400 on validation error`() {
        val context = createSut()

        val response =
            context.mvc
                .perform(
                    post("/api/v1/library-users/registrations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""{"email":"","password":"short"}"""),
                ).andReturn()
                .response

        assertEquals(400, response.status)
        assertTrue(response.contentAsString.contains("\"code\":\"VALIDATION_ERROR\""))
        assertTrue(response.contentAsString.contains("\"trace_id\":\""))
        verify(exactly = 0) { context.useCase.register(any()) }
    }

    @Test
    fun `returns 400 on duplicate email`() {
        val context = createSut()
        every { context.useCase.register(any()) } returns Err(UsecaseError.DuplicateEmail)

        val response =
            context.mvc
                .perform(
                    post("/api/v1/library-users/registrations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""{"email":"user@example.com","password":"Str0ng!Passw0rd"}"""),
                ).andReturn()
                .response

        assertEquals(400, response.status)
        assertTrue(response.contentAsString.contains("\"code\":\"DUPLICATE_EMAIL\""))
    }

    private fun createSut(useCase: RegisterLibraryUserUseCase = mockk()): TestContext {
        val builder: StandaloneMockMvcBuilder =
            MockMvcBuilders
                .standaloneSetup(RegistrationController(RegistrationRequestValidator(), useCase))
        builder.setControllerAdvice(GlobalExceptionHandler())
        return TestContext(
            mvc = builder.build(),
            useCase = useCase,
        )
    }
}
