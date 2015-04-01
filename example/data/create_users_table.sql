create table users (
    id serial primary key,
    email varchar not null unique,
    name varchar
);
