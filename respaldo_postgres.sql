--
-- PostgreSQL database dump
--

\restrict GXU7NzAGchhtwHpddRC7Iiy4D8hzM0BKVBk3r3eiHd9tCDm0OM2OnwR5ZyHn5rO

-- Dumped from database version 16.8
-- Dumped by pg_dump version 16.10 (Ubuntu 16.10-0ubuntu0.24.04.1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: pagos; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.pagos (
    id integer NOT NULL,
    email character varying(255) NOT NULL,
    transaction_id character varying(255) NOT NULL,
    amount character varying(50) NOT NULL,
    fecha timestamp without time zone DEFAULT now(),
    url_qr character varying(255) NOT NULL
);


ALTER TABLE public.pagos OWNER TO postgres;

--
-- Name: pagos_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.pagos_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.pagos_id_seq OWNER TO postgres;

--
-- Name: pagos_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.pagos_id_seq OWNED BY public.pagos.id;


--
-- Name: users; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.users (
    id integer NOT NULL,
    username character varying(50) NOT NULL,
    email character varying(100) NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE public.users OWNER TO postgres;

--
-- Name: users_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.users_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.users_id_seq OWNER TO postgres;

--
-- Name: users_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.users_id_seq OWNED BY public.users.id;


--
-- Name: usersregister; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.usersregister (
    id integer NOT NULL,
    username character varying(50) NOT NULL,
    password character varying(255) NOT NULL,
    name character varying(100) NOT NULL,
    email character varying(100) NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE public.usersregister OWNER TO postgres;

--
-- Name: usersregister_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.usersregister_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.usersregister_id_seq OWNER TO postgres;

--
-- Name: usersregister_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.usersregister_id_seq OWNED BY public.usersregister.id;


--
-- Name: usersregistercontenido; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.usersregistercontenido (
    id integer NOT NULL,
    url text NOT NULL,
    email character varying(255) NOT NULL
);


ALTER TABLE public.usersregistercontenido OWNER TO postgres;

--
-- Name: usersregistercontenido_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.usersregistercontenido_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.usersregistercontenido_id_seq OWNER TO postgres;

--
-- Name: usersregistercontenido_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.usersregistercontenido_id_seq OWNED BY public.usersregistercontenido.id;


--
-- Name: pagos id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.pagos ALTER COLUMN id SET DEFAULT nextval('public.pagos_id_seq'::regclass);


--
-- Name: users id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users ALTER COLUMN id SET DEFAULT nextval('public.users_id_seq'::regclass);


--
-- Name: usersregister id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.usersregister ALTER COLUMN id SET DEFAULT nextval('public.usersregister_id_seq'::regclass);


--
-- Name: usersregistercontenido id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.usersregistercontenido ALTER COLUMN id SET DEFAULT nextval('public.usersregistercontenido_id_seq'::regclass);


--
-- Data for Name: pagos; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.pagos (id, email, transaction_id, amount, fecha, url_qr) FROM stdin;
7	carolinamendezmaulen@gmail.com	TX123456789100	1010	2025-09-15 20:39:59.025131	https://contenedoreternallegacyqrinicio.s3.us-east-2.amazonaws.com/imagenes_qr_cargadas/pago_TX123456789100.png
8	maurolguin@hotmail.com	TX123456789199	10101	2025-09-22 01:45:42.331898	https://contenedoreternallegacyqrinicio.s3.us-east-2.amazonaws.com/imagenes_qr_cargadas/pago_TX123456789199.png
9	mau.olg@hotmail.com	TX123456789192	10101	2025-09-22 01:48:34.95342	https://contenedoreternallegacyqrinicio.s3.us-east-2.amazonaws.com/imagenes_qr_cargadas/pago_TX123456789192.png
\.


--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.users (id, username, email, created_at) FROM stdin;
1	Juan	juan@example.com	2025-03-28 18:15:40.89181
2	Mauricio	Mauricio.olguin@example.com	2025-03-28 18:17:32.090828
3	Carolina Mendez	Carolina.Mendez@example.com	2025-03-28 18:18:31.221372
\.


--
-- Data for Name: usersregister; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.usersregister (id, username, password, name, email, created_at) FROM stdin;
1	juanperez	hashedpassword123	Juan Pérez	juan@example.com	2025-03-31 01:35:55.860013
6	MaximilianoOlguin	1234	maximiliano olg	vulcano@example.com	2025-04-01 00:20:51.570325
7	Luciano122025	123	Luciano olguin 	luciano@gmail.com	2025-04-17 01:45:40.713505
8	MauriciOlguin	123	Mauricio Olguin	mau.olg@gmail.com	2025-04-17 02:39:48.028344
2	CarolinaMendez	123	Carolina mendez maules	carolinamendezmaulen@gmail.com	2025-03-31 23:32:06.961788
9	patriciolguin	123	Patricio Olguin	mau.olg@hotmail.com	2025-09-22 01:14:42.366867
\.


--
-- Data for Name: usersregistercontenido; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.usersregistercontenido (id, url, email) FROM stdin;
1	https://contenedoreternallegacyqrinicio.s3.us-east-2.amazonaws.com/contenedoreternallegacyqrinicio/IFPP1602.JPG	carolinamendezmaulen@gmail.com
2	https://contenedoreternallegacyqrinicio.s3.us-east-2.amazonaws.com/contenedoreternallegacyqrinicio/IMG_7869.JPG	carolinamendezmaulen@gmail.com
6	https://contenedoreternallegacyqrinicio.s3.us-east-2.amazonaws.com/contenedoreternallegacyqrinicio/IMG_7617.JPG	carolinamendezmaulen@gmail.com
9	https://contenedoreternallegacyqrinicio.s3.us-east-2.amazonaws.com/contenedoreternallegacyqrinicio/IMG_8999.JPG	mau.olg@hotmail.com
14	https://contenedoreternallegacyqrinicio.s3.us-east-2.amazonaws.com/contenedoreternallegacyqrinicio/image.png	mau.olg@hotmail.com
15	https://contenedoreternallegacyqrinicio.s3.us-east-2.amazonaws.com/contenedoreternallegacyqrinicio/image_1.png	mau.olg@hotmail.com
16	https://contenedoreternallegacyqrinicio.s3.us-east-2.amazonaws.com/contenedoreternallegacyqrinicio/image_2.png	mau.olg@hotmail.com
17	https://contenedoreternallegacyqrinicio.s3.us-east-2.amazonaws.com/contenedoreternallegacyqrinicio/IMG_E1392.JPG	mau.olg@hotmail.com
20	https://contenedoreternallegacyqrinicio.s3.us-east-2.amazonaws.com/contenedoreternallegacyqrinicio/video_papa01.mp4	mau.olg@hotmail.com
23	https://contenedoreternallegacyqrinicio.s3.us-east-2.amazonaws.com/contenedoreternallegacyqrinicio/DSCN2951.jpg	mau.olg@hotmail.com
\.


--
-- Name: pagos_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.pagos_id_seq', 9, true);


--
-- Name: users_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.users_id_seq', 3, true);


--
-- Name: usersregister_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.usersregister_id_seq', 9, true);


--
-- Name: usersregistercontenido_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.usersregistercontenido_id_seq', 23, true);


--
-- Name: pagos pagos_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.pagos
    ADD CONSTRAINT pagos_pkey PRIMARY KEY (id);


--
-- Name: users users_email_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_email_key UNIQUE (email);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- Name: usersregister usersregister_email_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.usersregister
    ADD CONSTRAINT usersregister_email_key UNIQUE (email);


--
-- Name: usersregister usersregister_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.usersregister
    ADD CONSTRAINT usersregister_pkey PRIMARY KEY (id);


--
-- Name: usersregister usersregister_username_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.usersregister
    ADD CONSTRAINT usersregister_username_key UNIQUE (username);


--
-- Name: usersregistercontenido usersregistercontenido_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.usersregistercontenido
    ADD CONSTRAINT usersregistercontenido_pkey PRIMARY KEY (id);


--
-- Name: TABLE users; Type: ACL; Schema: public; Owner: postgres
--

GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.users TO PUBLIC;


--
-- Name: TABLE usersregister; Type: ACL; Schema: public; Owner: postgres
--

GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.usersregister TO PUBLIC;


--
-- PostgreSQL database dump complete
--

\unrestrict GXU7NzAGchhtwHpddRC7Iiy4D8hzM0BKVBk3r3eiHd9tCDm0OM2OnwR5ZyHn5rO

