package jp.glory.practice.agentic.reservation.command.web

import jp.glory.practice.agentic.shared.web.ValidationApiException
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class PlaceReservationRequestValidatorTest {
    private val sut = PlaceReservationRequestValidator()

    @Nested
    inner class ValidateOrThrow {
        @Test
        fun `given valid request when validate then does not throw`() {
            sut.validateOrThrow(PlaceReservationRequest(bookProductId = "book-1"))
        }

        @Test
        fun `given null book product id when validate then returns validation error`() {
            val exception =
                assertFailsWith<ValidationApiException> {
                    sut.validateOrThrow(PlaceReservationRequest(bookProductId = null))
                }

            assertDetail(exception, "book_product_id", "required")
        }

        @Test
        fun `given empty book product id when validate then returns validation error`() {
            val exception =
                assertFailsWith<ValidationApiException> {
                    sut.validateOrThrow(PlaceReservationRequest(bookProductId = ""))
                }

            assertDetail(exception, "book_product_id", "required")
        }

        @Test
        fun `given blank book product id when validate then returns validation error`() {
            val exception =
                assertFailsWith<ValidationApiException> {
                    sut.validateOrThrow(PlaceReservationRequest(bookProductId = " "))
                }

            assertDetail(exception, "book_product_id", "required")
        }
    }

    private fun assertDetail(
        exception: ValidationApiException,
        field: String,
        reason: String,
    ) {
        val details = exception.details.map { it.field to it.reason }
        assertTrue(details.contains(field to reason))
    }
}
