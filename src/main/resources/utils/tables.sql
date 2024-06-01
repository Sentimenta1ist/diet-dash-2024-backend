CREATE TABLE profile
(
    id         UUID PRIMARY KEY,
    name       VARCHAR(100),
    age        INTEGER,
    sex        VARCHAR(10),
    height     INTEGER,
    weight     INTEGER,
    talia      INTEGER,
    bedra      INTEGER,
    weight_aim INTEGER,
    aim        VARCHAR(255),
    limit_cal  INTEGER,
    carbs      INTEGER,
    proteins   INTEGER,
    fats       INTEGER,
    activity   INTEGER
);

CREATE TABLE product
(
    id       SERIAL PRIMARY KEY,
    name     VARCHAR(255),
    calories FLOAT,
    proteins FLOAT,
    fats     FLOAT,
    carbs    FLOAT
);


CREATE TABLE meal
(
    id         uuid PRIMARY KEY,
    product_id INT,
    profile_id UUID,
    time       TIME,
    date       DATE,
    amount     FLOAT
);

CREATE TABLE usr
(
    id       UUID PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    active   BOOLEAN      NOT NULL
);

CREATE TABLE user_role
(
    user_id UUID         NOT NULL,
    roles   VARCHAR(255) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES usr (id)
);

INSERT INTO product (name, calories, proteins, fats, carbs)
VALUES ('Product 1', 100.0, 10.0, 5.0, 20.0),
       ('Product 2', 200.0, 20.0, 10.0, 40.0),
       ('Product 3', 300.0, 30.0, 15.0, 60.0),
       ('Product 4', 400.0, 40.0, 20.0, 80.0),
       ('Product 5', 500.0, 50.0, 25.0, 100.0);

INSERT INTO profile (id, name, age, sex, height, weight, talia, bedra, weight_aim, aim, limit_cal, carbs, proteins,
                     fats, activity)
VALUES ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'Profile 1', 30, 'Male', 180, 75, 80, 100, 70, 'Lose weight', 2000, 200,
        150, 50, 1),
       ('b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', 'Profile 2', 25, 'Female', 165, 60, 70, 90, 55, 'Maintain weight', 1800,
        180, 120, 40, 2),
       ('c2eebc99-9c0b-4ef8-bb6d-6bb9bd380a13', 'Profile 3', 35, 'Male', 175, 80, 85, 105, 75, 'Gain weight', 2500, 250,
        200, 60, 3),
       ('d3eebc99-9c0b-4ef8-bb6d-6bb9bd380a14', 'Profile 4', 28, 'Female', 170, 65, 75, 95, 60, 'Lose weight', 1900,
        190, 130, 45, 2),
       ('e4eebc99-9c0b-4ef8-bb6d-6bb9bd380a15', 'Profile 5', 32, 'Male', 185, 90, 90, 110, 85, 'Maintain weight', 2200,
        220, 170, 55, 1);

INSERT INTO meal (id, product_id, profile_id, time, date, amount)
VALUES ('f5eebc99-9c0b-4ef8-bb6d-6bb9bd380a16', 1, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', '07:00:00', '2023-01-01',
        1.0),
       ('06eebc99-9c0b-4ef8-bb6d-6bb9bd380a17', 2, 'b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', '12:00:00', '2023-01-01',
        1.5),
       ('07eebc99-9c0b-4ef8-bb6d-6bb9bd380a18', 3, 'c2eebc99-9c0b-4ef8-bb6d-6bb9bd380a13', '18:00:00', '2023-01-01',
        2.0),
       ('08eebc99-9c0b-4ef8-bb6d-6bb9bd380a19', 4, 'd3eebc99-9c0b-4ef8-bb6d-6bb9bd380a14', '07:00:00', '2023-01-02',
        1.0),
       ('09eebc99-9c0b-4ef8-bb6d-6bb9bd380a20', 5, 'e4eebc99-9c0b-4ef8-bb6d-6bb9bd380a15', '12:00:00', '2023-01-02',
        1.5);