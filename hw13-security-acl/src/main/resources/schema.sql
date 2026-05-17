create table authors (
    id bigserial,
    full_name varchar(255),
    primary key (id)
);

create table genres (
    id bigserial,
    name varchar(255),
    primary key (id)
);

create table books (
    id bigserial,
    title varchar(255),
    author_id bigint references authors (id) on delete cascade,
    primary key (id)
);

create table books_genres (
    book_id bigint references books(id) on delete cascade,
    genre_id bigint references genres(id) on delete cascade,
    primary key (book_id, genre_id)
);

create table comments (
    id bigserial primary key,
    text varchar(1000) not null,
    book_id bigint not null references books(id) on delete cascade
);

create table users (
    id bigserial,
    user_name varchar(255) not null unique,
    user_password varchar(255) not null,
    primary key (id)
);

create table user_roles (
    user_id bigint not null,
    role_name varchar(20) not null,
    primary key (user_id, role_name),
    constraint fk_user_roles_user foreign key (user_id) references users(id) on delete cascade
);