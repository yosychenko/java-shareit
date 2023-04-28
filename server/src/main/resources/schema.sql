DROP TABLE IF EXISTS users, item_requests, items, bookings, comments CASCADE;

CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT GENERATED BY DEFAULT AS IDENTITY,
    name  VARCHAR(255) NOT NULL,
    email VARCHAR(512) NOT NULL,
    CONSTRAINT pk_user_id PRIMARY KEY (id),
    CONSTRAINT uq_user_email UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS item_requests
(
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY,
    description VARCHAR(1000) NOT NULL,
    requestor   BIGINT        NOT NULL,
    created     TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
    CONSTRAINT pk_item_request_id PRIMARY KEY (id),
    CONSTRAINT fk_item_request_requestor FOREIGN KEY (requestor) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS items
(
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY,
    name        VARCHAR(255)  NOT NULL,
    description VARCHAR(1000) NOT NULL,
    available   bool          NOT NULL,
    owner       BIGINT        NOT NULL,
    request     BIGINT,
    CONSTRAINT pk_item_id PRIMARY KEY (id),
    CONSTRAINT fk_item_owner FOREIGN KEY (owner) REFERENCES users (id),
    CONSTRAINT fk_item_request FOREIGN KEY (request) REFERENCES item_requests (id)
);

CREATE TABLE IF NOT EXISTS bookings
(
    id       BIGINT GENERATED BY DEFAULT AS IDENTITY,
    start_ts TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    end_ts   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    item     BIGINT                      NOT NULL,
    booker   BIGINT,
    status   VARCHAR(20),
    CONSTRAINT pk_booking_id PRIMARY KEY (id),
    CONSTRAINT fk_booking_item FOREIGN KEY (item) REFERENCES items (id),
    CONSTRAINT fk_booking_booker FOREIGN KEY (booker) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS comments
(
    id      BIGINT GENERATED BY DEFAULT AS IDENTITY,
    text    VARCHAR(1000) NOT NULL,
    item    BIGINT        NOT NULL,
    author  BIGINT        NOT NULL,
    created TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
    CONSTRAINT pk_comment_id PRIMARY KEY (id),
    CONSTRAINT fk_comment_item FOREIGN KEY (item) REFERENCES items (id),
    CONSTRAINT fk_comment_author FOREIGN KEY (author) REFERENCES users (id)
);
