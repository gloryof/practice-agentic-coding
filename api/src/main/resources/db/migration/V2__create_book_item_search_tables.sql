CREATE TABLE publishers (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    name_kana VARCHAR(255) NOT NULL
);

CREATE TABLE authors (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    name_kana VARCHAR(255) NOT NULL
);

CREATE TABLE book_items (
    id VARCHAR(36) PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    title_kana VARCHAR(255) NOT NULL,
    publisher_id VARCHAR(36) NOT NULL,
    isbn VARCHAR(20) NOT NULL,
    CONSTRAINT fk_book_items_publishers
        FOREIGN KEY (publisher_id) REFERENCES publishers (id)
);

CREATE TABLE book_item_authors (
    book_item_id VARCHAR(36) NOT NULL,
    author_id VARCHAR(36) NOT NULL,
    PRIMARY KEY (book_item_id, author_id),
    CONSTRAINT fk_book_item_authors_book_items
        FOREIGN KEY (book_item_id) REFERENCES book_items (id),
    CONSTRAINT fk_book_item_authors_authors
        FOREIGN KEY (author_id) REFERENCES authors (id)
);

CREATE INDEX idx_book_items_isbn ON book_items (isbn);
CREATE INDEX idx_book_items_publisher_id ON book_items (publisher_id);
CREATE INDEX idx_book_item_authors_author_id ON book_item_authors (author_id);
