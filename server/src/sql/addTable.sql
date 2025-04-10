insert into dbo.recipe(name, author_id, servings, preparation_time, dish_type, cuisine, intolerances, diets, calories, protein, fat, carbs)
values ('Chicken Curry', 1, 4, 30, 'Main Course', 'Indian', 'None', 'None', 500, 40, 20, 50),
       ('Vegetable Stir Fry', 2, 2, 20, 'Main Course', 'Chinese', 'None', 'Vegan', 300, 10, 5, 40),
       ('Pasta Salad', 3, 4, 15, 'Salad', 'Italian', 'Gluten', 'Vegetarian', 250, 8, 10, 30),
       ('Beef Tacos', 4, 3, 25, 'Main Course', 'Mexican', 'None', 'None', 600, 35, 25, 45),
       ('Fruit Smoothie', 5, 1, 10, 'Drink', 'American', 'None', 'Vegan', 200, 5, 2, 30);