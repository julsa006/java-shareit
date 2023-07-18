

CREATE TABLE IF NOT EXISTS users (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  name VARCHAR(255) NOT NULL,
  email VARCHAR(512) NOT NULL,
  CONSTRAINT pk_user PRIMARY KEY (id),
  CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS items (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  name VARCHAR(255) NOT NULL,
  description VARCHAR(512) NOT NULL,
  available BOOLEAN NOT NULL,
  owner_id BIGINT NOT NULL,
  CONSTRAINT pk_item PRIMARY KEY (id),
  CONSTRAINT fk_item_owner FOREIGN KEY (owner_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS bookings (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  start_time TIMESTAMP NOT NULL,
  end_time TIMESTAMP NOT NULL,
  item_id BIGINT NOT NULL,
  booker_id BIGINT NOT NULL,
  status VARCHAR(255) NOT NULL,
  CONSTRAINT pk_booking PRIMARY KEY (id),
  CONSTRAINT fk_booking_booker FOREIGN KEY (booker_id) REFERENCES users(id),
  CONSTRAINT fk_booking_item FOREIGN KEY (item_id) REFERENCES items(id)
);

CREATE TABLE IF NOT EXISTS comments (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  text VARCHAR(512) NOT NULL,
  item_id BIGINT NOT NULL,
  author_id BIGINT NOT NULL,
  created TIMESTAMP NOT NULL,
  CONSTRAINT pk_comment PRIMARY KEY (id),
  CONSTRAINT fk_comment_author FOREIGN KEY (author_id) REFERENCES users(id),
  CONSTRAINT fk_comment_item FOREIGN KEY (item_id) REFERENCES items(id)
);