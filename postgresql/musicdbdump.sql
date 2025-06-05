--
-- PostgreSQL database dump
--

-- Dumped from database version 12.3
-- Dumped by pg_dump version 12.3

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

--
-- Name: MusicDB; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA "MusicDB";


ALTER SCHEMA "MusicDB" OWNER TO postgres;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: album; Type: TABLE; Schema: MusicDB; Owner: postgres
--

CREATE TABLE "MusicDB".album (
    albumid integer NOT NULL,
    title character varying(100) NOT NULL,
    year smallint NOT NULL,
    groupid integer NOT NULL
);


ALTER TABLE "MusicDB".album OWNER TO postgres;

--
-- Name: artist; Type: TABLE; Schema: MusicDB; Owner: postgres
--

CREATE TABLE "MusicDB".artist (
    artistid integer NOT NULL,
    name character varying(100) NOT NULL,
    groupid integer NOT NULL
);


ALTER TABLE "MusicDB".artist OWNER TO postgres;

--
-- Name: genre; Type: TABLE; Schema: MusicDB; Owner: postgres
--

CREATE TABLE "MusicDB".genre (
    genreid integer NOT NULL,
    name character varying(100) NOT NULL
);


ALTER TABLE "MusicDB".genre OWNER TO postgres;

--
-- Name: grouptable; Type: TABLE; Schema: MusicDB; Owner: postgres
--

CREATE TABLE "MusicDB".grouptable (
    groupid integer NOT NULL,
    name character varying(100) NOT NULL
);


ALTER TABLE "MusicDB".grouptable OWNER TO postgres;

--
-- Name: link; Type: TABLE; Schema: MusicDB; Owner: postgres
--

CREATE TABLE "MusicDB".link (
    musicid integer NOT NULL,
    link character varying(200) NOT NULL
);


ALTER TABLE "MusicDB".link OWNER TO postgres;

--
-- Name: music; Type: TABLE; Schema: MusicDB; Owner: postgres
--

CREATE TABLE "MusicDB".music (
    year smallint NOT NULL,
    title character varying(100) NOT NULL,
    musicid integer NOT NULL,
    authorid integer NOT NULL,
    albumid integer,
    genreid integer NOT NULL
);


ALTER TABLE "MusicDB".music OWNER TO postgres;

--
-- Data for Name: album; Type: TABLE DATA; Schema: MusicDB; Owner: postgres
--

COPY "MusicDB".album (albumid, title, year, groupid) FROM stdin;
1234	TestAlbum	1234	1234
1000	Uprising	2018	1001
7589320	Album di Test	1910	9999
17403	Best of Kevin MacLeod	2020	78103
692033	Sound Asleep	2020	529385
2222	test album numero zero	1999	1234
\.


--
-- Data for Name: artist; Type: TABLE DATA; Schema: MusicDB; Owner: postgres
--

COPY "MusicDB".artist (artistid, name, groupid) FROM stdin;
1000	Teminite	1000
1001	Chime	1000
1002	PsoGnar	1000
3694	TestArtist	1234
2222	prova artista	2222
537854	Spencer Hunt	529385
1982674	Nome Artista di Test	9999
83562	Kevin MacLeod	78103
\.


--
-- Data for Name: genre; Type: TABLE DATA; Schema: MusicDB; Owner: postgres
--

COPY "MusicDB".genre (genreid, name) FROM stdin;
1234	TestGenre
1000	Dubstep
16539	Genere di Test
2222	genere test
32849	Rock
952683	Soundtrack
382535	Chill-LoFi
\.


--
-- Data for Name: grouptable; Type: TABLE DATA; Schema: MusicDB; Owner: postgres
--

COPY "MusicDB".grouptable (groupid, name) FROM stdin;
1234	TestGroup
1001	Teminite
9999	Gruppo di Test
2222	test gruppo
78103	Kevin MacLeod
529385	Spencer Hunt
1000	Teminite X Chime X PsoGnar
\.


--
-- Data for Name: link; Type: TABLE DATA; Schema: MusicDB; Owner: postgres
--

COPY "MusicDB".link (musicid, link) FROM stdin;
1000	https://soundcloud.com/teminite/teminite-monster
1000	https://music.apple.com/us/album/uprising/1441514642
1000	https://open.spotify.com/album/6lFTz3iPOXLa6FBJ1PTSJH
58912	https://soundcloud.com/wowspencerhunt/i-love-you-goodnight
357357	https://soundcloud.com/wowspencerhunt/dreamscape
982365	https://soundcloud.com/wowspencerhunt/lonely
567823	https://soundcloud.com/wowspencerhunt/thoughts-of-you
678923	https://soundcloud.com/wowspencerhunt/moonlight
257853	https://soundcloud.com/wowspencerhunt/sleepy
46392	https://www.youtube.com/watch?v=Wx2FDyvt0gI
78965	https://www.youtube.com/watch?v=uQQZZygCGHY
\.


--
-- Data for Name: music; Type: TABLE DATA; Schema: MusicDB; Owner: postgres
--

COPY "MusicDB".music (year, title, musicid, authorid, albumid, genreid) FROM stdin;
1334	Test Music!?	1234	1234	7589320	1234
2018	Monster	1000	1000	1000	1000
2020	Test Music Number One	7364	1000	\N	1234
2014	The Builder	573463	78103	17403	952683
2006	Scheming Weasel	46392	78103	17403	952683
2008	Cold Funk	78965	78103	17403	32849
2020	dreamscape.	357357	529385	692033	382535
2020	lonely.	982365	529385	692033	382535
2020	thoughts of you.	567823	529385	692033	382535
2020	moonlight.	678923	529385	692033	382535
2020	sleepy.	257853	529385	692033	382535
2020	i love you, goodnight	58912	529385	692033	382535
2019	testing datalist 2	737473	2222	\N	2222
\.


--
-- Name: album Album_pkey; Type: CONSTRAINT; Schema: MusicDB; Owner: postgres
--

ALTER TABLE ONLY "MusicDB".album
    ADD CONSTRAINT "Album_pkey" PRIMARY KEY (albumid);


--
-- Name: artist Artist_pkey; Type: CONSTRAINT; Schema: MusicDB; Owner: postgres
--

ALTER TABLE ONLY "MusicDB".artist
    ADD CONSTRAINT "Artist_pkey" PRIMARY KEY (artistid);


--
-- Name: genre Genre_pkey; Type: CONSTRAINT; Schema: MusicDB; Owner: postgres
--

ALTER TABLE ONLY "MusicDB".genre
    ADD CONSTRAINT "Genre_pkey" PRIMARY KEY (genreid);


--
-- Name: grouptable Group_pkey; Type: CONSTRAINT; Schema: MusicDB; Owner: postgres
--

ALTER TABLE ONLY "MusicDB".grouptable
    ADD CONSTRAINT "Group_pkey" PRIMARY KEY (groupid);


--
-- Name: music Music_pkey; Type: CONSTRAINT; Schema: MusicDB; Owner: postgres
--

ALTER TABLE ONLY "MusicDB".music
    ADD CONSTRAINT "Music_pkey" PRIMARY KEY (musicid);


--
-- Name: album Album_GroupId_fkey; Type: FK CONSTRAINT; Schema: MusicDB; Owner: postgres
--

ALTER TABLE ONLY "MusicDB".album
    ADD CONSTRAINT "Album_GroupId_fkey" FOREIGN KEY (groupid) REFERENCES "MusicDB".grouptable(groupid);


--
-- Name: artist Artist_GroupId_fkey; Type: FK CONSTRAINT; Schema: MusicDB; Owner: postgres
--

ALTER TABLE ONLY "MusicDB".artist
    ADD CONSTRAINT "Artist_GroupId_fkey" FOREIGN KEY (groupid) REFERENCES "MusicDB".grouptable(groupid);


--
-- Name: link Link_MusicId_fkey; Type: FK CONSTRAINT; Schema: MusicDB; Owner: postgres
--

ALTER TABLE ONLY "MusicDB".link
    ADD CONSTRAINT "Link_MusicId_fkey" FOREIGN KEY (musicid) REFERENCES "MusicDB".music(musicid);


--
-- Name: music Music_AlbumId_fkey; Type: FK CONSTRAINT; Schema: MusicDB; Owner: postgres
--

ALTER TABLE ONLY "MusicDB".music
    ADD CONSTRAINT "Music_AlbumId_fkey" FOREIGN KEY (albumid) REFERENCES "MusicDB".album(albumid);


--
-- Name: music Music_AuthorId_fkey; Type: FK CONSTRAINT; Schema: MusicDB; Owner: postgres
--

ALTER TABLE ONLY "MusicDB".music
    ADD CONSTRAINT "Music_AuthorId_fkey" FOREIGN KEY (authorid) REFERENCES "MusicDB".grouptable(groupid);


--
-- Name: music Music_GenreId_fkey; Type: FK CONSTRAINT; Schema: MusicDB; Owner: postgres
--

ALTER TABLE ONLY "MusicDB".music
    ADD CONSTRAINT "Music_GenreId_fkey" FOREIGN KEY (genreid) REFERENCES "MusicDB".genre(genreid);


--
-- PostgreSQL database dump complete
--

