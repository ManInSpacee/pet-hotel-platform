CREATE TABLE tariffs
(
    id                       BIGSERIAL PRIMARY KEY,
    enclosure_size           VARCHAR(10)    NOT NULL CHECK ( enclosure_size IN ('SMALL', 'MEDIUM', 'LARGE') ) UNIQUE,
    price_per_day            DECIMAL(10, 2) NOT NULL CHECK (price_per_day > 0),
    special_care_coefficient DECIMAL(3, 2)  NOT NULL DEFAULT 1.2
);



INSERT INTO tariffs (enclosure_size, price_per_day)
VALUES ('SMALL', 500);
INSERT INTO tariffs (enclosure_size, price_per_day)
VALUES ('MEDIUM', 650);
INSERT INTO tariffs (enclosure_size, price_per_day)
VALUES ('LARGE', 800);

ALTER TABLE enclosures
    ADD COLUMN active BOOLEAN DEFAULT true;



ALTER TABLE bookings
    ADD COLUMN code VARCHAR(20);
UPDATE bookings
SET code = 'BK-' || lpad(id::text, 6, '0');
ALTER TABLE bookings
    ALTER COLUMN code SET NOT NULL;
ALTER TABLE bookings
    ADD CONSTRAINT uq_bookings_code UNIQUE (code);
CREATE SEQUENCE booking_code_seq START WITH 15;
ALTER TABLE bookings
    ALTER COLUMN code SET DEFAULT 'BK-' || lpad(nextval('booking_code_seq')::text, 6, '0');
ALTER TABLE bookings
    ADD COLUMN uuid UUID DEFAULT gen_random_uuid() NOT NULL;
ALTER TABLE bookings
    DROP CONSTRAINT bookings_pkey;
ALTER TABLE bookings
    DROP COLUMN id;
ALTER TABLE bookings
    RENAME COLUMN uuid TO id;
ALTER TABLE bookings
    ADD PRIMARY KEY (id);
ALTER TABLE bookings
    ADD COLUMN special_care BOOLEAN DEFAULT false NOT NULL;
ALTER TABLE bookings
    ADD COLUMN comment TEXT;
ALTER TABLE bookings
    ADD COLUMN version BIGINT NOT NULL DEFAULT 0;
ALTER TABLE bookings
    ADD COLUMN archived BOOLEAN DEFAULT FALSE NOT NULL;
ALTER TABLE bookings
    ADD COLUMN archived_at TIMESTAMP;
ALTER TABLE bookings
    ADD COLUMN updated_at TIMESTAMP;
UPDATE bookings
SET updated_at = bookings.created_at;
ALTER TABLE bookings
    ALTER COLUMN updated_at SET NOT NULL;
ALTER TABLE bookings
    ADD COLUMN total_price DECIMAL(10, 2);
UPDATE bookings b
SET total_price = (SELECT t.price_per_day * (b.end_date - b.start_date)
                   FROM tariffs t
                            JOIN enclosures e ON e.size = t.enclosure_size
                   WHERE e.id = b.enclosure_id);
ALTER TABLE bookings
    ALTER COLUMN total_price SET NOT NULL;

CREATE TABLE booking_tags
(
    PRIMARY KEY (booking_id, tag_name),
    booking_id UUID        NOT NULL,
    CONSTRAINT fk_tags_bookings
        FOREIGN KEY (booking_id) REFERENCES bookings (id),
    tag_name   VARCHAR(20) NOT NULL
);