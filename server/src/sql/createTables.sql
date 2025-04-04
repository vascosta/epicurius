create schema dbo

create table dbo.user(
    id serial primary key,
    username varchar(25) unique not null ,
    email varchar(40) unique not null,
    password_hash varchar(80) not null,
    token_hash varchar(80) unique,
    country varchar(5) not null,
    privacy boolean not null,
    intolerances integer[] not null,
    diets integer[] not null,
    profile_picture_name varchar(80)
);

create table dbo.followers(
    user_id integer not null,
    follower_id integer not null,
    status integer not null,
    foreign key (user_id) references dbo.user(id),
    foreign key (follower_id) references dbo.user(id)
);

create table dbo.fridge(
    owner_id integer not null,
    entry_number serial not null,
    product_name varchar(50) not null,
    quantity integer not null,
    open_date date,
    expiration_date date not null,
    primary key (owner_id, entry_number),
    foreign key (owner_id) references dbo.user(id)
);

create table dbo.recipe(
    id serial primary key,
    name varchar(40) not null,
    author_id int not null,
    servings int not null,
    preparation_time int not null,
    dish_type varchar(30) not null,
    cuisine varchar(25) not null,
    intolerances varchar(20),
    diets varchar(20),
    calories int,
    protein int,
    fat int,
    carbs int
    foreign key (author_id) references user(id)
);

create table dbo.recipe_rating(
    recipe_id int not null,
    user_id int not null, -- cannot be the same as the author
    rating int not null,
    primary key (recipe_id, user_id),
    foreign key (recipe_id) references recipe(id),
    foreign key (user_id) references user(id)
);

create table dbo.ingredients(
    id serial primary key,
    recipe_id int not null,
    name varchar(50) not null,
    quantity int not null,
    unit varchar(20) not null,
    foreign key (recipe_id) references recipe(id)
);

create table dbo.collections(
    user_id int not null,
    recipe_id int not null,
    collection_name varchar(20) not null,
    collection_type int not null, -- favourite or kitchen book
    primary key (user_id, recipe_id, collection_name),
    foreign key (user_id) references user(id),
    foreign key (recipe_id) references recipe(id)
);

-- TODO MEAL PLANING TABLE

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