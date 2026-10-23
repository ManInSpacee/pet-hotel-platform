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

INSERT INTO bookings (id, pet_id, enclosure_id, start_date, end_date, status) VALUES
    (1, 1, 3, CURRENT_DATE + 1, CURRENT_DATE + 10, 'ACCEPTED');
