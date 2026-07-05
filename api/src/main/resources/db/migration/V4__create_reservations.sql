ALTER TYPE book_item_stock_status ADD VALUE 'RESERVED';

ALTER TABLE book_item_stocks
ADD COLUMN version INTEGER NOT NULL DEFAULT 0;

CREATE TABLE reservations (
    id VARCHAR(36) PRIMARY KEY,
    library_user_id VARCHAR(36) NOT NULL,
    book_product_id VARCHAR(36) NOT NULL,
    book_item_id VARCHAR(36) NOT NULL,
    reserved_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT fk_reservations_library_users
        FOREIGN KEY (library_user_id) REFERENCES library_users (id),
    CONSTRAINT fk_reservations_book_products
        FOREIGN KEY (book_product_id) REFERENCES book_products (id),
    CONSTRAINT fk_reservations_book_items
        FOREIGN KEY (book_item_id) REFERENCES book_items (id),
    CONSTRAINT uk_reservations_library_user_id_book_product_id
        UNIQUE (library_user_id, book_product_id),
    CONSTRAINT uk_reservations_book_item_id
        UNIQUE (book_item_id)
);

CREATE INDEX idx_reservations_library_user_id ON reservations (library_user_id);
CREATE INDEX idx_reservations_book_product_id ON reservations (book_product_id);
CREATE INDEX idx_reservations_book_item_id ON reservations (book_item_id);
