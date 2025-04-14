create schema dbo

create table dbo.user(
    id serial primary key,
    username varchar(25) unique not null ,
    email varchar(40) unique not null,
    password_hash varchar(80) not null,
    token_hash varchar(80) unique,
    country varchar(5) not null,
    privacy boolean not null,
    intolerances int[] not null,
    diets int[] not null,
    profile_picture_name varchar(80)
);

create table dbo.followers(
    user_id int not null,
    follower_id int not null,
    status int not null,
    foreign key (user_id) references dbo.user(id),
    foreign key (follower_id) references dbo.user(id)
);

create table dbo.fridge(
    owner_id int not null,
    entry_number serial not null,
    product_name varchar(50) not null,
    quantity int not null,
    open_date date,
    expiration_date date not null,
    primary key (owner_id, entry_number),
    foreign key (owner_id) references dbo.user(id)
);

create table dbo.recipe(
    id serial primary key,
    name varchar(25) not null,
    author_id int not null,
    date date not null,
    servings int not null,
    preparation_time int not null, -- in minutes, e.g if value is 30, it means 30 minutes
    meal_type int not null,
    cuisine int not null,
    intolerances int[] not null,
    diets int[] not null,
    calories int check ( >= 0),
    protein int check ( >= 0),
    fat int check ( >= 0),
    carbs int check ( >= 0),
    pictures_names varchar(80)[] check (cardinality(pictures_names) BETWEEN 1 AND 3),
    foreign key (author_id) references dbo.user(id)
);

create table dbo.recipe_rating(
    recipe_id int not null,
    user_id int not null, -- cannot be the same as the author
    rating int not null,
    primary key (recipe_id, user_id),
    foreign key (recipe_id) references dbo.recipe(id),
    foreign key (user_id) references dbo.user(id)
);

create table dbo.ingredient(
    id serial primary key,
    recipe_id int not null,
    name varchar(20) not null,
    quantity int not null,
    unit int not null,
    foreign key (recipe_id) references dbo.recipe(id)
);

create table dbo.collection(
    user_id int not null,
    recipe_id int not null,
    collection_name varchar(20) not null,
    collection_type int not null, -- favourite or kitchen book
    primary key (user_id, recipe_id, collection_name),
    foreign key (user_id) references dbo.user(id),
    foreign key (recipe_id) references dbo.recipe(id)
);

create table dbo.meal_planner(
    user_id int not null,
    date date not null,
    primary key (user_id, date),
    foreign key (user_id) references dbo.user(id)
);

create table dbo.meal_planner_recipe(
    user_id int not null,
    date date not null,
    recipe_id int not null,
    meal_time int not null,
    primary key (user_id, date, recipe_id, meal_time),
    foreign key (user_id, date) references dbo.meal_planner(user_id, date),
    foreign key (recipe_id) references dbo.recipe(id)
);

create table dbo.calories(
    user_id int not null,
    date date not null,
    max_calories int not null,
    primary key (user_id, date),
    foreign key (user_id) references dbo.user(id),
    foreign key (user_id, date) references dbo.meal_planner(user_id, date)
);

create or replace function delete_product_with_no_quantity()
    returns trigger as
$$
begin
    if new.quantity = 0 then
        delete from dbo.fridge
        where owner_id = new.owner_id and entry_number = new.entry_number;
    end if;
    return null;
end;
$$ language plpgsql;

create trigger trg_delete_product_with_no_quantity
    after insert or update
    on dbo.fridge
    for each row
execute function delete_product_with_no_quantity();