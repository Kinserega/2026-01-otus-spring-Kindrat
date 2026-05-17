insert into authors(full_name)
values ('Лорен Магазинер'), ('Пол Андерсон'), ('Екатерина Леснова');

insert into genres(name)
values ('Роман'), ('Детектив'), ('Мистика'),
       ('Драма'), ('Триллер'), ('Фантастика');

insert into books(title, author_id)
values ('Тайна старого особняка', 1), ('Звёздные странники', 2), ('Хозяйка Долины ветров', 3);

insert into books_genres(book_id, genre_id)
values (1, 1),   (1, 2),
       (2, 3),   (2, 4),
       (3, 5),   (3, 6);

insert into comments(text, book_id)
values ('Захватывающий детектив', 1),
       ('Необычный формат', 1),
       ('Масштабная и продуманная научная фантастика', 2);

insert into users (id, user_name, user_password)
values
(1, 'user',  '$2a$05$IWbFGZV2jZZNpJqWicRqIuf8pJA9r2hrZ1qhSTm1maCBjS0mtSOIG'), --userpassword
(2, 'admin', '$2a$05$Vyb0rDYzXmke7zbFf/4Iqe3ZgnsdTEUsYy644/oHbAWlW5HMwVhNy'); --adminpassword

insert into user_roles (user_id, role_name)
values
(1, 'USER'),
(2, 'USER'),
(2, 'ADMIN');

alter table users alter column id restart with 3;