-- DROP SCHEMA public;

CREATE SCHEMA public AUTHORIZATION pg_database_owner;

-- DROP SEQUENCE public.answers_id_seq;

CREATE SEQUENCE public.answers_id_seq
	INCREMENT BY 1
	MINVALUE 1
	MAXVALUE 2147483647
	START 1
	CACHE 1
	NO CYCLE;
-- DROP SEQUENCE public.questions_id_seq;

CREATE SEQUENCE public.questions_id_seq
	INCREMENT BY 1
	MINVALUE 1
	MAXVALUE 2147483647
	START 1
	CACHE 1
	NO CYCLE;
-- DROP SEQUENCE public.tests_id_seq;

CREATE SEQUENCE public.tests_id_seq
	INCREMENT BY 1
	MINVALUE 1
	MAXVALUE 2147483647
	START 1
	CACHE 1
	NO CYCLE;-- public.tests определение

-- Drop table

-- DROP TABLE public.tests;

CREATE TABLE public.tests (
	id serial4 NOT NULL,
	title varchar(255) NOT NULL,
	description text NULL,
	CONSTRAINT tests_pkey PRIMARY KEY (id)
);

-- public.questions определение

-- Drop table

-- DROP TABLE public.questions;

CREATE TABLE public.questions (
	id serial4 NOT NULL,
	test_id int4 NULL,
	question_text text NOT NULL,
	CONSTRAINT questions_pkey PRIMARY KEY (id),
	CONSTRAINT questions_test_id_fkey FOREIGN KEY (test_id) REFERENCES public.tests(id) ON DELETE CASCADE
);

-- public.answers определение

-- Drop table

-- DROP TABLE public.answers;

CREATE TABLE public.answers (
	id serial4 NOT NULL,
	question_id int4 NULL,
	answer_text text NOT NULL,
	is_correct bool DEFAULT false NOT NULL,
	CONSTRAINT answers_pkey PRIMARY KEY (id),
	CONSTRAINT answers_question_id_fkey FOREIGN KEY (question_id) REFERENCES public.questions(id) ON DELETE CASCADE
);

-- public.question_answers исходный текст

CREATE OR REPLACE VIEW public.question_answers
AS SELECT t.title AS test_title,
    q.question_text,
    a.answer_text,
    a.is_correct
   FROM tests t
     JOIN questions q ON t.id = q.test_id
     JOIN answers a ON q.id = a.question_id;

-- Вставка тестов
INSERT INTO tests (title, description) VALUES
('Общие знания о Земле', 'Тест на знание географии и общих знаний о Земле'),
('Основы программирования', 'Тест на знание основ программирования'),
('История', 'Тест на знание мировой истории');

-- Вставка вопросов для первого теста
INSERT INTO questions (test_id, question_text) VALUES
(1, 'Какой океан самый большой на Земле?'),
(1, 'Какой континент самый населенный?'),
(1, 'Какой химический элемент обозначается символом O?'),
(1, 'Какой город является столицей Японии?'),
(1, 'Какое животное является символом Австралии?');

-- Вставка ответов для первого теста
INSERT INTO answers (question_id, answer_text, is_correct) VALUES
(1, 'Атлантический', FALSE),
(1, 'Индийский', FALSE),
(1, 'Тихий', TRUE),
(1, 'Северный Ледовитый', FALSE),
(2, 'Африка', FALSE),
(2, 'Северная Америка', FALSE),
(2, 'Азия', TRUE),
(2, 'Австралия', FALSE),
(3, 'Золото', FALSE),
(3, 'Кислород', TRUE),
(3, 'Серебро', FALSE),
(3, 'Углерод', FALSE),
(4, 'Осака', FALSE),
(4, 'Токио', TRUE),
(4, 'Саппоро', FALSE),
(4, 'Кюсю', FALSE),
(5, 'Панда', FALSE),
(5, 'Кенгуру', TRUE),
(5, 'Лев', FALSE),
(5, 'Медведь', FALSE);

-- Вставка вопросов для второго теста
INSERT INTO questions (test_id, question_text) VALUES
(2, 'Какой язык программирования используется для разработки веб-страниц?'),
(2, 'Какое из следующих утверждений верно для объектов в ООП?'),
(2, 'Какой оператор используется для сравнения двух значений в большинстве языков программирования?'),
(2, 'Какой из этих языков является языком программирования общего назначения?'),
(2, 'Какой тип данных будет результатом выражения 5 / 2 в Python 3?');

-- Вставка ответов для второго теста
INSERT INTO answers (question_id, answer_text, is_correct) VALUES
(6, 'HTML', TRUE),
(6, 'Java', FALSE),
(6, 'C++', FALSE),
(6, 'SQL', FALSE),
(7, 'Объекты могут существовать независимо от классов', FALSE),
(7, 'Каждый объект является экземпляром класса', TRUE),
(7, 'Объекты не могут иметь состояния', FALSE),
(7, 'Объекты всегда публичны', FALSE),
(8, '=', FALSE),
(8, '==', TRUE),
(8, '===', FALSE),
(8, 'Все вышеперечисленные', TRUE),
(9, 'SQL', FALSE),
(9, 'HTML', FALSE),
(9, 'Python', TRUE),
(9, 'CSS', FALSE),
(10, 'Integer', FALSE),
(10, 'Float', TRUE),
(10, 'String', FALSE),
(10, 'Boolean', FALSE);

-- Вставка вопросов для третьего теста
INSERT INTO questions (test_id, question_text) VALUES
(3, 'В каком году началась Вторая мировая война?'),
(3, 'Кто был первым президентом США?'),
(3, 'Как называется древний город, известный своей архитектурой, находящийся в современном Ираке?'),
(3, 'Кто открыл Америку в 1492 году?'),
(3, 'Какое событие произошло в 1989 году, которое символизировало конец Холодной войны?');

-- Вставка ответов для третьего теста
INSERT INTO answers (question_id, answer_text, is_correct) VALUES
(11, '1938', FALSE),
(11, '1939', TRUE),
(11, '1941', FALSE),
(11, '1945', FALSE),
(12, 'Авраам Линкольн', FALSE),
(12, 'Джордж Вашингтон', TRUE),
(12, 'Томас Джефферсон', FALSE),
(12, 'Барак Обама', FALSE),
(13, 'Вавилон', TRUE),
(13, 'Рим', FALSE),
(13, 'Афины', FALSE),
(13, 'Каир', FALSE),
(14, 'Магеллан', FALSE),
(14, 'Колумб', TRUE),
(14, 'Да Гама', FALSE),
(14, 'Кук', FALSE),
(15, 'Падение Берлинской стены', TRUE),
(15, 'Падение Нью-Йоркской биржи', FALSE),
(15, 'Образование Европейского Союза', FALSE),
(15, 'Ввод военных сил в Афганистан', FALSE);