-- 1. Вставка данных в таблицу "app_user"
INSERT INTO app_user (id, first_name, last_name, login, password, confirmation_token, enabled)
VALUES
    (1, 'John', 'Doe', 'johndoe', 'password123', 'b033a91e-4b6d-48b2-961f-d2cb5b2f9639', TRUE),
    (2, 'Jane', 'Smith', 'janesmith', 'password456', 'a25b213e-2a5d-45c9-9632-44b1fe79c98e', TRUE),
    (3, 'Mike', 'Jordan', 'mikejordan', 'password789', 'f953ac25-3504-41f9-bf7e-bd6c7db712c4', TRUE),
    (4, 'Sarah', 'Connor', 'sarahconnor', 'password101', '3fcb1947-e1be-43ea-a812-8960e572e9f2', TRUE),
    (5, 'Emma', 'Stone', 'emmastone', 'password202', 'f2a1ed37-b57d-47d0-81b8-90d14b7e4d8f', TRUE);

INSERT INTO app_user (first_name, last_name, login, password, confirmation_token, enabled)
VALUES
    ('John', 'Doe', 'asdf', 'password123', 'b033a91e-4b6d-48b2-961f-d2cb5b2f9639', TRUE);

-- 2. Вставка данных в таблицу "product"
INSERT INTO product (id, name, calories, proteins, fats, carbs, lang, user_id)
VALUES
    (1, 'Apple', 52, 0.3, 0.2, 14, 'en', 1),
    (2, 'Banana', 89, 1.1, 0.3, 23, 'en', 1),
    (3, 'Carrot', 41, 0.9, 0.2, 10, 'en', 2),
    (4, 'Broccoli', 55, 3.7, 0.6, 11, 'en', 2),
    (5, 'Eggplant', 25, 1, 0.2, 6, 'en', 3),
    (6, 'Tomato', 18, 0.9, 0.2, 4, 'en', 4),
    (7, 'Cucumber', 16, 0.7, 0.1, 4, 'en', 5);

-- Вставка данных в таблицу "profile" с UUID
INSERT INTO profile (id, name, age, gender, height, weight, waist, hips, goal_weight, activity, body_mass_index, daily_calorie_norm, selected_calorie_limit, daily_protein_limit, daily_fat_limit, daily_carbs_limit, daily_protein_percentage, daily_fat_percentage, daily_carbs_percentage, user_id)
VALUES
    ('2d8b539d-9519-44ae-9c7a-d2cb2f56482c', 'John Doe Profile', 30, 'Male', 180, 75, 32, 40, 70, 1, 23.1, 2500, 2200, 150, 70, 250, 20.0, 30.0, 50.0, 1),
    ('f85cb072-c9c4-4850-b61e-b85c19585e62', 'Jane Smith Profile', 28, 'Female', 165, 65, 28, 38, 60, 2, 24.0, 2000, 1800, 120, 60, 220, 25.0, 35.0, 40.0, 2),
    ('e4e256af-07f7-4579-8fbb-0e3b07e7e29d', 'Mike Jordan Profile', 35, 'Male', 190, 90, 34, 42, 85, 3, 25.0, 3000, 2800, 200, 90, 300, 18.0, 35.0, 47.0, 3),
    ('b7224b9b-1713-463f-a69b-3fa736dc8d9c', 'Sarah Connor Profile', 40, 'Female', 170, 70, 30, 38, 65, 1, 24.5, 2200, 2000, 130, 70, 240, 22.0, 28.0, 50.0, 4),
    ('c0d7e55b-1262-48d8-bc25-cf8705bc9db1', 'Emma Stone Profile', 32, 'Female', 160, 55, 25, 35, 50, 2, 21.5, 1800, 1600, 100, 50, 200, 23.0, 27.0, 50.0, 5);

INSERT INTO ration (id, date, profile_id)
VALUES
    (1, '2025-02-20', '2d8b539d-9519-44ae-9c7a-d2cb2f56482c'),
    (2, '2025-02-20', 'f85cb072-c9c4-4850-b61e-b85c19585e62'),
    (3, '2025-02-20', 'e4e256af-07f7-4579-8fbb-0e3b07e7e29d'),
    (4, '2025-02-20', 'b7224b9b-1713-463f-a69b-3fa736dc8d9c'),
    (5, '2025-02-20', 'c0d7e55b-1262-48d8-bc25-cf8705bc9db1');

-- 5. Вставка данных в таблицу "meal"
INSERT INTO meal (id, time, grams, product_id, ration_id)
VALUES
    (1, '08:00:00', 100, 1, 1),   -- John eats an apple in the morning
    (2, '12:00:00', 150, 2, 1),   -- John eats a banana for lunch
    (3, '18:00:00', 200, 3, 1),   -- John eats carrots for dinner
    (4, '08:30:00', 100, 4, 2),   -- Jane eats broccoli in the morning
    (5, '13:00:00', 120, 5, 2),   -- Jane eats eggplant for lunch
    (6, '19:00:00', 150, 6, 2),   -- Jane eats tomatoes for dinner
    (7, '08:00:00', 150, 7, 3),   -- Mike eats cucumbers in the morning
    (8, '12:00:00', 200, 2, 3),   -- Mike eats a banana for lunch
    (9, '18:30:00', 250, 4, 4),   -- Sarah eats broccoli for dinner
    (10, '07:45:00', 100, 1, 4),  -- Sarah eats an apple in the morning
    (11, '12:30:00', 150, 3, 5),  -- Emma eats carrots for lunch
    (12, '19:15:00', 200, 5, 5);  -- Emma eats eggplant for dinner