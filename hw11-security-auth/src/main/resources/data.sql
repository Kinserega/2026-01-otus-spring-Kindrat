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

insert into users (username, password)
values
('user',  '$2a$05$1a53IBUJ8eE.ZMIFFZyhy.Yt0tD0Yj81r8y27pdVhZmUe.0Qe72X2'); --пароль 'userpassword'