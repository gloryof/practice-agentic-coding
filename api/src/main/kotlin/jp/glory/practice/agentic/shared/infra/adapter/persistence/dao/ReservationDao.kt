package jp.glory.practice.agentic.shared.infra.adapter.persistence.dao

import org.komapper.core.dsl.QueryDsl
import org.komapper.core.dsl.query.bind
import org.komapper.core.dsl.query.getNotNull
import org.komapper.jdbc.JdbcDatabase
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
class ReservationDao(
    private val database: JdbcDatabase,
) {
    fun lockLibraryUser(libraryUserId: String): Boolean =
        database
            .runQuery {
                QueryDsl
                    .fromTemplate(
                        """
                        SELECT id
                        FROM library_users
                        WHERE id = /* libraryUserId */'user-id'
                        FOR UPDATE
                        """.trimIndent(),
                    ).bind("libraryUserId", libraryUserId)
                    .select { it.getNotNull<String>("id") }
            }.isNotEmpty()

    fun findBookProduct(bookProductId: String): ReservationBookProductRow? =
        database
            .runQuery {
                QueryDsl
                    .fromTemplate(
                        """
                        SELECT id, title, isbn
                        FROM book_products
                        WHERE id = /* bookProductId */'book-id'
                        """.trimIndent(),
                    ).bind("bookProductId", bookProductId)
                    .select { row ->
                        ReservationBookProductRow(
                            id = row.getNotNull("id"),
                            title = row.getNotNull("title"),
                            isbn = row.getNotNull("isbn"),
                        )
                    }
            }.firstOrNull()

    fun findReservedBookProductIds(libraryUserId: String): List<String> =
        database.runQuery {
            QueryDsl
                .fromTemplate(
                    """
                    SELECT book_product_id
                    FROM reservations
                    WHERE library_user_id = /* libraryUserId */'user-id'
                    ORDER BY book_product_id
                    """.trimIndent(),
                ).bind("libraryUserId", libraryUserId)
                .select { it.getNotNull<String>("book_product_id") }
        }

    fun countAvailableBookItems(bookProductId: String): Int =
        requireNotNull(
            database
                .runQuery {
                    QueryDsl
                        .fromTemplate(
                            """
                            SELECT COUNT(*) AS available_count
                            FROM book_items bi
                            INNER JOIN book_item_stocks bis ON bi.id = bis.book_item_id
                            WHERE bi.book_product_id = /* bookProductId */'book-id'
                              AND bis.status = 'AVAILABLE'::book_item_stock_status
                            """.trimIndent(),
                        ).bind("bookProductId", bookProductId)
                        .select { it.getNotNull<Long>("available_count") }
                }.firstOrNull(),
        ).toInt()

    fun reserveAvailableBookItem(bookProductId: String): String? =
        database
            .runQuery {
                QueryDsl
                    .fromTemplate(reserveAvailableBookItemSql())
                    .bind("bookProductId", bookProductId)
                    .select { it.getNotNull<String>("book_item_id") }
            }.firstOrNull()

    private fun reserveAvailableBookItemSql(): String =
        """
        WITH candidate AS (
            SELECT bis.id
            FROM book_items bi
            INNER JOIN book_item_stocks bis ON bi.id = bis.book_item_id
            WHERE bi.book_product_id = /* bookProductId */'book-id'
              AND bis.status = 'AVAILABLE'::book_item_stock_status
            ORDER BY bi.id
            LIMIT 1
            FOR UPDATE OF bis SKIP LOCKED
        ),
        updated AS (
            UPDATE book_item_stocks
            SET status = 'RESERVED'::book_item_stock_status,
                version = version + 1
            WHERE id = (SELECT id FROM candidate)
            RETURNING book_item_id
        )
        SELECT book_item_id
        FROM updated
        """.trimIndent()

    fun insertReservation(
        id: String,
        libraryUserId: String,
        bookProductId: String,
        bookItemId: String,
        reservedAt: Instant,
    ) {
        database.runQuery {
            QueryDsl
                .executeTemplate(
                    """
                    INSERT INTO reservations (id, library_user_id, book_product_id, book_item_id, reserved_at)
                    VALUES (
                        /* id */'reservation-id',
                        /* libraryUserId */'user-id',
                        /* bookProductId */'book-id',
                        /* bookItemId */'item-id',
                        /* reservedAt */'2026-01-01T00:00:00Z'
                    )
                    """.trimIndent(),
                ).bind("id", id)
                .bind("libraryUserId", libraryUserId)
                .bind("bookProductId", bookProductId)
                .bind("bookItemId", bookItemId)
                .bind("reservedAt", reservedAt)
        }
    }
}

data class ReservationBookProductRow(
    val id: String,
    val title: String,
    val isbn: String,
)
