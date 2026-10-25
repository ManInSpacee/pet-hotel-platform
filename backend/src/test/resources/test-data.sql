DELETE FROM booking_tags;
DELETE FROM bookings;
DELETE FROM pets;
DELETE FROM owners;
DELETE FROM enclosures;

INSERT INTO owners (id, login, full_name, phone) VALUES
    (1, 'ownera', 'Первый Владелец', '+79990000001'),
    (2, 'ownerb', 'Второй Владелец', '+79990000002');

INSERT INTO pets (id, owner_id, name, species, size) VALUES
    (1, 1, 'Рекс',  'DOG', 'LARGE'),
    (2, 2, 'Мурка', 'CAT', 'SMALL');

INSERT INTO enclosures (id, number, size) VALUES
    (1, 1, 'SMALL'),
    (2, 2, 'MEDIUM'),
    (3, 3, 'LARGE');

-- id, code, created_at и updated_at заполняет база; тарифы берутся из миграции V3
INSERT INTO bookings (pet_id, enclosure_id, start_date, end_date, status, total_price, updated_at) VALUES
    (1, 3, CURRENT_DATE + 1, CURRENT_DATE + 10, 'ACCEPTED', 7200.00, CURRENT_TIMESTAMP);
