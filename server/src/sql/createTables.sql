create schema dbo

create table dbo.user(
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
