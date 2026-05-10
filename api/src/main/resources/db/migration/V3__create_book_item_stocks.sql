CREATE TYPE book_item_stock_status AS ENUM (
    'AVAILABLE',
    'CHECKED_OUT'
);

CREATE TABLE book_item_stocks (
    id VARCHAR(36) PRIMARY KEY,
    book_item_id VARCHAR(36) NOT NULL,
    status book_item_stock_status NOT NULL,
    CONSTRAINT uk_book_item_stocks_book_item_id UNIQUE (book_item_id),
    CONSTRAINT fk_book_item_stocks_book_items
        FOREIGN KEY (book_item_id) REFERENCES book_items (id)
);

CREATE INDEX idx_book_item_stocks_book_item_id ON book_item_stocks (book_item_id);
CREATE INDEX idx_book_item_stocks_book_item_id_status ON book_item_stocks (book_item_id, status);
