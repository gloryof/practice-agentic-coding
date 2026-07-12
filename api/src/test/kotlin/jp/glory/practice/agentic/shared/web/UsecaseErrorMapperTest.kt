package jp.glory.practice.agentic.shared.web

import jp.glory.practice.agentic.shared.usecase.UsecaseError
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class UsecaseErrorMapperTest {
    @Nested
    inner class ToApiException {
        @Test
        fun `given validation error when map then returns validation api exception`() {
            val result = toApiException(UsecaseError.Validation(field = "email", reason = "required"))

            assertIs<ValidationApiException>(result)
            assertApiException(
                expectedCode = ValidationApiErrorCode.VALIDATION_ERROR,
                expectedDetails = listOf(ApiErrorDetail(field = "email", reason = "required")),
                actual = result,
            )
        }

        @Test
        fun `given authentication failure when map then returns authentication api exception`() {
            val result = toApiException(UsecaseError.AuthenticationFailed)

            assertIs<AuthenticationApiException>(result)
            assertApiException(
                expectedCode = AuthenticationApiErrorCode.UNAUTHORIZED,
                actual = result,
            )
        }

        @Test
        fun `given duplicate email when map then returns business api exception`() {
            val result = toApiException(UsecaseError.DuplicateEmail)

            assertIs<BusinessApiException>(result)
            assertApiException(
                expectedCode = BusinessApiErrorCode.DUPLICATE_EMAIL,
                actual = result,
            )
        }

        @Test
        fun `given missing reservation target when map then returns business api exception`() {
            val result = toApiException(UsecaseError.ReservationTargetNotFound)

            assertIs<BusinessApiException>(result)
            assertApiException(
                expectedCode = BusinessApiErrorCode.RESERVATION_TARGET_NOT_FOUND,
                actual = result,
            )
        }

        @Test
        fun `given unavailable reservation when map then returns business api exception with reasons`() {
            val result =
                toApiException(
                    UsecaseError.ReservationUnavailable(
                        reasons =
                            listOf(
                                UsecaseError.ReservationUnavailable.Reason.NO_AVAILABLE_BOOK_ITEM,
                                UsecaseError.ReservationUnavailable.Reason.RESERVATION_LIMIT_REACHED,
                            ),
                    ),
                )

            assertIs<BusinessApiException>(result)
            assertApiException(
                expectedCode = BusinessApiErrorCode.RESERVATION_UNAVAILABLE,
                expectedDetails =
                    listOf(
                        ApiErrorDetail(field = "reservation", reason = "no_available_book_item"),
                        ApiErrorDetail(field = "reservation", reason = "reservation_limit_reached"),
                    ),
                actual = result,
            )
        }
    }

    private fun assertApiException(
        expectedCode: ApiErrorCode,
        actual: ApiException,
        expectedDetails: List<ApiErrorDetail> = emptyList(),
    ) {
        assertEquals(expectedCode, actual.errorCode)
        assertEquals(expectedCode.value, actual.code)
        assertEquals(expectedCode.message, actual.message)
        assertEquals(expectedCode.status, actual.status)
        assertEquals(expectedDetails, actual.details)
    }
}
