--
-- PostgreSQL database dump
--

-- Dumped from database version 17.5 (Debian 17.5-1.pgdg120+1)
-- Dumped by pg_dump version 17.5 (Debian 17.5-1.pgdg120+1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Data for Name: _user; Type: TABLE DATA; Schema: public; Owner: username
--

SET SESSION AUTHORIZATION DEFAULT;

ALTER TABLE public._user DISABLE TRIGGER ALL;

INSERT INTO public._user (id, account_locked, created_date, date_of_birth, email, enabled, first_name, last_modified_date, last_name, password) VALUES (1, false, '2025-07-20 18:19:51.458606', NULL, 'datho2205@gmail.com', true, 'Dat', '2025-07-20 18:20:08.975118', 'Ho Quoc', '$2a$10$HLRjjquxBL9eKx9ASpQ18u09uO.pNn7dc8o5Wi1AJ9tIFASIvOk6C');
INSERT INTO public._user (id, account_locked, created_date, date_of_birth, email, enabled, first_name, last_modified_date, last_name, password) VALUES (2, false, '2025-07-20 21:15:36.537262', NULL, 'datho22051@gmail.com', true, 'Dat', '2025-07-20 21:16:45.498109', 'Ho Quoc', '$2a$10$ySL2aChuiVFZZfzfGmwpLeoWp3EpEwSGRCP0Cgr0z1I7/7U9noghO');


ALTER TABLE public._user ENABLE TRIGGER ALL;

--
-- Data for Name: role; Type: TABLE DATA; Schema: public; Owner: username
--

ALTER TABLE public.role DISABLE TRIGGER ALL;

INSERT INTO public.role (id, created_date, last_modified_date, name) VALUES (1, '2025-07-20 18:17:01.556003', NULL, 'USER');


ALTER TABLE public.role ENABLE TRIGGER ALL;

--
-- Data for Name: _user_roles; Type: TABLE DATA; Schema: public; Owner: username
--

ALTER TABLE public._user_roles DISABLE TRIGGER ALL;

INSERT INTO public._user_roles (users_id, roles_id) VALUES (1, 1);
INSERT INTO public._user_roles (users_id, roles_id) VALUES (2, 1);


ALTER TABLE public._user_roles ENABLE TRIGGER ALL;

--
-- Data for Name: book; Type: TABLE DATA; Schema: public; Owner: username
--

ALTER TABLE public.book DISABLE TRIGGER ALL;

INSERT INTO public.book (id, created_by, last_modified_by, created_date, last_modified_date, archived, author, book_cover, isbn, shareable, synopsis, title, owner_id) VALUES (1, 1, NULL, '2025-07-20 18:20:34.426774', NULL, false, 'Đạt', NULL, NULL, true, 'tom tat', 'Sách', 1);
INSERT INTO public.book (id, created_by, last_modified_by, created_date, last_modified_date, archived, author, book_cover, isbn, shareable, synopsis, title, owner_id) VALUES (2, 1, 1, '2025-07-20 18:25:09.079947', '2025-07-20 19:19:27.776058', false, 'Đạt', NULL, '978-1491952023', true, '12345', 'Sách', 1);
INSERT INTO public.book (id, created_by, last_modified_by, created_date, last_modified_date, archived, author, book_cover, isbn, shareable, synopsis, title, owner_id) VALUES (103, 1, 1, '2025-07-20 19:21:13.558418', '2025-07-20 20:40:06.859688', false, 'Đạt', 'https://cabihztuvxlsaapmsxis.supabase.co/storage/v1/object/public/book-image/users/1/ebd18862-8c72-4050-a1a6-76bbe6a4a9f9.jpg', '978-1491952023', true, 'noi dung 3', 'FPT', 1);
INSERT INTO public.book (id, created_by, last_modified_by, created_date, last_modified_date, archived, author, book_cover, isbn, shareable, synopsis, title, owner_id) VALUES (102, 1, 1, '2025-07-20 19:21:13.515782', '2025-07-21 08:02:36.219464', true, 'Đạt', NULL, '978-1491952023', true, 'noi dung 4', 'FPT', 1);
INSERT INTO public.book (id, created_by, last_modified_by, created_date, last_modified_date, archived, author, book_cover, isbn, shareable, synopsis, title, owner_id) VALUES (52, 1, 1, '2025-07-20 19:16:38.725842', '2025-07-21 08:02:53.598299', false, 'Đạt', 'https://cabihztuvxlsaapmsxis.supabase.co/storage/v1/object/public/book-image/users/1/b6e08d0d-9e5f-4024-8f91-34178be66bf6.png', '978-1491952023', true, '12345', 'Sách', 1);
INSERT INTO public.book (id, created_by, last_modified_by, created_date, last_modified_date, archived, author, book_cover, isbn, shareable, synopsis, title, owner_id) VALUES (104, 1, 1, '2025-07-20 19:23:06.381981', '2025-07-21 10:36:01.246598', false, 'Đạt', 'https://cabihztuvxlsaapmsxis.supabase.co/storage/v1/object/public/book-image/users/1/0331c37b-cacb-43eb-8470-cdc4d1cdfc9b.jpg', '978-1491952023', true, 'aaaa', 'Sách Y', 1);
INSERT INTO public.book (id, created_by, last_modified_by, created_date, last_modified_date, archived, author, book_cover, isbn, shareable, synopsis, title, owner_id) VALUES (152, 2, 2, '2025-07-21 10:27:36.485541', '2025-07-21 10:36:45.354948', true, 'J. K. Rowling', 'https://cabihztuvxlsaapmsxis.supabase.co/storage/v1/object/public/book-image/users/2/eef01522-ddbd-4290-9184-d80ad15530f2.png', '0747532699', true, ' Philosopher''s Stone ', 'Harry Potter', 2);


ALTER TABLE public.book ENABLE TRIGGER ALL;

--
-- Data for Name: book_transaction_history; Type: TABLE DATA; Schema: public; Owner: username
--

ALTER TABLE public.book_transaction_history DISABLE TRIGGER ALL;

INSERT INTO public.book_transaction_history (id, created_by, last_modified_by, created_date, last_modified_date, returned, returned_approved, book_id, user_id) VALUES (2, 2, NULL, '2025-07-20 22:07:00.559348', NULL, false, false, 104, 2);
INSERT INTO public.book_transaction_history (id, created_by, last_modified_by, created_date, last_modified_date, returned, returned_approved, book_id, user_id) VALUES (1, 2, 1, '2025-07-20 22:06:14.917824', '2025-07-21 10:12:11.96533', true, true, 103, 2);
INSERT INTO public.book_transaction_history (id, created_by, last_modified_by, created_date, last_modified_date, returned, returned_approved, book_id, user_id) VALUES (52, 1, 2, '2025-07-21 10:28:35.53657', '2025-07-21 10:29:51.998343', true, true, 152, 1);
INSERT INTO public.book_transaction_history (id, created_by, last_modified_by, created_date, last_modified_date, returned, returned_approved, book_id, user_id) VALUES (53, 1, 1, '2025-07-21 10:30:11.020496', '2025-07-21 10:30:23.950885', true, false, 152, 1);


ALTER TABLE public.book_transaction_history ENABLE TRIGGER ALL;

--
-- Data for Name: feedback; Type: TABLE DATA; Schema: public; Owner: username
--

ALTER TABLE public.feedback DISABLE TRIGGER ALL;

INSERT INTO public.feedback (id, created_by, last_modified_by, created_date, last_modified_date, comment, note, book_id) VALUES (1, 2, NULL, '2025-07-21 09:38:26.513869', NULL, 'aaaaaa', 4, 103);
INSERT INTO public.feedback (id, created_by, last_modified_by, created_date, last_modified_date, comment, note, book_id) VALUES (2, 2, NULL, '2025-07-21 10:19:49.364957', NULL, 'okieee', 5, 103);
INSERT INTO public.feedback (id, created_by, last_modified_by, created_date, last_modified_date, comment, note, book_id) VALUES (3, 1, NULL, '2025-07-21 10:29:11.519057', NULL, 'SAchs quas tuyet', 5, 152);


ALTER TABLE public.feedback ENABLE TRIGGER ALL;

--
-- Data for Name: token; Type: TABLE DATA; Schema: public; Owner: username
--

ALTER TABLE public.token DISABLE TRIGGER ALL;

INSERT INTO public.token (id, created_at, expires_at, token, validated_at, user_id) VALUES (1, '2025-07-20 18:19:51.471983', '2025-07-20 18:34:51.471983', '929583', '2025-07-20 18:20:08.98425', 1);
INSERT INTO public.token (id, created_at, expires_at, token, validated_at, user_id) VALUES (2, '2025-07-20 21:15:36.578675', '2025-07-20 21:30:36.578675', '807265', '2025-07-20 21:16:45.516194', 2);


ALTER TABLE public.token ENABLE TRIGGER ALL;

--
-- Name: _user_seq; Type: SEQUENCE SET; Schema: public; Owner: username
--

SELECT pg_catalog.setval('public._user_seq', 1651, true);


--
-- Name: book_seq; Type: SEQUENCE SET; Schema: public; Owner: username
--

SELECT pg_catalog.setval('public.book_seq', 201, true);


--
-- Name: book_transaction_history_seq; Type: SEQUENCE SET; Schema: public; Owner: username
--

SELECT pg_catalog.setval('public.book_transaction_history_seq', 101, true);


--
-- Name: feedback_seq; Type: SEQUENCE SET; Schema: public; Owner: username
--

SELECT pg_catalog.setval('public.feedback_seq', 51, true);


--
-- Name: role_seq; Type: SEQUENCE SET; Schema: public; Owner: username
--

SELECT pg_catalog.setval('public.role_seq', 1, true);


--
-- Name: token_seq; Type: SEQUENCE SET; Schema: public; Owner: username
--

SELECT pg_catalog.setval('public.token_seq', 1651, true);


--
-- PostgreSQL database dump complete
--

