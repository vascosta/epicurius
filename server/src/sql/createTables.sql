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
    diet integer[] not null,
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
)

--create table recipe(
  --  id serial primary key,
 --   name varchar(40) not null,
 --   author int not null,
    --ingredients
 --   dish_type varchar(30) not null,
 --   cuisine varchar(25) not null,
  --  servings int not null,
    --intolerances varchar(20),
    --diet varchar(20),
 --   calories int,
  --  protein int,
  --  fat int,
 --   carbs int
 --   foreign key (user) references user(id)
--);
