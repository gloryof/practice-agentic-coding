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

CREATE TABLE book_products (
    id VARCHAR(36) PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    title_kana VARCHAR(255) NOT NULL,
    publisher_id VARCHAR(36) NOT NULL,
    isbn VARCHAR(20) NOT NULL,
    CONSTRAINT fk_book_products_publishers
        FOREIGN KEY (publisher_id) REFERENCES publishers (id)
);

CREATE TABLE book_product_authors (
    book_product_id VARCHAR(36) NOT NULL,
    author_id VARCHAR(36) NOT NULL,
    PRIMARY KEY (book_product_id, author_id),
    CONSTRAINT fk_book_product_authors_book_products
        FOREIGN KEY (book_product_id) REFERENCES book_products (id),
    CONSTRAINT fk_book_product_authors_authors
        FOREIGN KEY (author_id) REFERENCES authors (id)
);

CREATE TABLE book_items (
    id VARCHAR(36) PRIMARY KEY,
    book_product_id VARCHAR(36) NOT NULL,
    CONSTRAINT fk_book_items_book_products
        FOREIGN KEY (book_product_id) REFERENCES book_products (id)
);

CREATE INDEX idx_book_products_isbn ON book_products (isbn);
CREATE INDEX idx_book_products_publisher_id ON book_products (publisher_id);
CREATE INDEX idx_book_product_authors_author_id ON book_product_authors (author_id);
CREATE INDEX idx_book_items_book_product_id ON book_items (book_product_id);
