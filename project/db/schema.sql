--
-- PostgreSQL database dump
--

-- Dumped from database version 11.5
-- Dumped by pg_dump version 11.5

-- Started on 2019-09-29 03:44:38 -03

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

DROP DATABASE dbslr;
--
-- TOC entry 3398 (class 1262 OID 16386)
-- Name: dbslr; Type: DATABASE; Schema: -; Owner: postgres
--

CREATE DATABASE dbslr WITH TEMPLATE = template0 ENCODING = 'UTF8' LC_COLLATE = 'en_US.UTF-8' LC_CTYPE = 'en_US.UTF-8';


ALTER DATABASE dbslr OWNER TO postgres;

\connect dbslr

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
-- TOC entry 6 (class 2615 OID 16387)
-- Name: slr; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA slr;


ALTER SCHEMA slr OWNER TO postgres;

--
-- TOC entry 239 (class 1255 OID 16404)
-- Name: slr_author_iud(character varying, character varying); Type: FUNCTION; Schema: slr; Owner: postgres
--

CREATE FUNCTION slr.slr_author_iud(procedimiento character varying, author_name character varying) RETURNS integer
    LANGUAGE plpgsql
    AS $$DECLARE
author_id 	integer;
BEGIN

	IF procedimiento = 'AUTH_INS' THEN
		IF NOT EXISTS (SELECT 1 FROM slr.author WHERE names = author_name)
		THEN
			--split nombres previo al insert
			INSERT INTO slr.author(names) VALUES(author_name);
			select max(id) into author_id from slr.author;
		ELSE 
			select id into author_id 
			from slr.author
			where names = author_name;
		END IF;
	END IF;

RETURN author_id;
END;$$;


ALTER FUNCTION slr.slr_author_iud(procedimiento character varying, author_name character varying) OWNER TO postgres;

--
-- TOC entry 252 (class 1255 OID 16405)
-- Name: slr_data_process_upd(character varying); Type: FUNCTION; Schema: slr; Owner: postgres
--

CREATE FUNCTION slr.slr_data_process_upd(proceso character varying) RETURNS character varying
    LANGUAGE plpgsql
    AS $$
DECLARE
registros	record;
articulos 	record;
rec 		record;
id_publication integer;
id_author	integer;

BEGIN

	IF proceso = 'UPDATE_DATA' THEN
		BEGIN
			FOR registros IN (
							SELECT *
							FROM slr.dblp_document d
							WHERE d.updated = false AND doc_type = 'article'
							LIMIT 5
							 )
				LOOP
					IF registros.doc_type = 'article' 
					THEN
						BEGIN
							--llamada function procesar article
							--REGISTRO DE PUBLICACION
							INSERT INTO slr.publication(title,type) 
							VALUES(registros.title,registros.doc_type);
							SELECT MAX(id) INTO id_publication FROM slr.publication;
							
							for rec in (select * from json_each_text(registros.authors) ) 
							loop
								--raise notice '%,%',rec.key,rec.value;
								if(rec.key is not null AND char_length(rec.key) > 0) then
									id_author = slr.slr_author_iud('AUTH_INS',rec.value);
									--insert author_publications
									--raise notice ' autor %, public %',id_author,id_publication;
									INSERT INTO slr.author_publications(author_id,publication_id,orden) 
									VALUES(id_author,id_publication,rec.key::INTEGER);
								end if;
							end loop;	
						END;
					END IF;
					--actualizacion updated = true en dblp_document
					--update slr.dblp_document set updated = true where id = registros.id;
				END LOOP;
		END;
	END IF;

RETURN 'SUCCESS';
END;$$;


ALTER FUNCTION slr.slr_data_process_upd(proceso character varying) OWNER TO postgres;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 199 (class 1259 OID 16406)
-- Name: author; Type: TABLE; Schema: slr; Owner: postgres
--

CREATE TABLE slr.author (
    id bigint NOT NULL,
    names text,
    created_at date DEFAULT now(),
    department_id integer,
    home_page character varying(200)
);


ALTER TABLE slr.author OWNER TO postgres;

--
-- TOC entry 200 (class 1259 OID 16413)
-- Name: author_id_seq; Type: SEQUENCE; Schema: slr; Owner: postgres
--

CREATE SEQUENCE slr.author_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE slr.author_id_seq OWNER TO postgres;

--
-- TOC entry 3399 (class 0 OID 0)
-- Dependencies: 200
-- Name: author_id_seq; Type: SEQUENCE OWNED BY; Schema: slr; Owner: postgres
--

ALTER SEQUENCE slr.author_id_seq OWNED BY slr.author.id;


--
-- TOC entry 201 (class 1259 OID 16415)
-- Name: author_publications; Type: TABLE; Schema: slr; Owner: postgres
--

CREATE TABLE slr.author_publications (
    id bigint NOT NULL,
    author_id integer,
    publication_id integer,
    herarchy integer,
    create_at timestamp without time zone DEFAULT now()
);


ALTER TABLE slr.author_publications OWNER TO postgres;

--
-- TOC entry 202 (class 1259 OID 16419)
-- Name: author_publications_id_seq; Type: SEQUENCE; Schema: slr; Owner: postgres
--

CREATE SEQUENCE slr.author_publications_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE slr.author_publications_id_seq OWNER TO postgres;

--
-- TOC entry 3400 (class 0 OID 0)
-- Dependencies: 202
-- Name: author_publications_id_seq; Type: SEQUENCE OWNED BY; Schema: slr; Owner: postgres
--

ALTER SEQUENCE slr.author_publications_id_seq OWNED BY slr.author_publications.id;


--
-- TOC entry 203 (class 1259 OID 16421)
-- Name: publication; Type: TABLE; Schema: slr; Owner: postgres
--

CREATE TABLE slr.publication (
    id integer NOT NULL,
    title text,
    publication_type character varying(100),
    abstract text,
    doi character varying(100),
    year integer,
    created_at timestamp without time zone DEFAULT now(),
    key_dblp character varying(100),
    pages character varying(50)
);


ALTER TABLE slr.publication OWNER TO postgres;

--
-- TOC entry 204 (class 1259 OID 16428)
-- Name: book; Type: TABLE; Schema: slr; Owner: postgres
--

CREATE TABLE slr.book (
    year integer,
    isbn character varying(100),
    book_id integer NOT NULL,
    publisher_id integer
)
INHERITS (slr.publication);


ALTER TABLE slr.book OWNER TO postgres;

--
-- TOC entry 205 (class 1259 OID 16435)
-- Name: book_book_id_seq; Type: SEQUENCE; Schema: slr; Owner: postgres
--

CREATE SEQUENCE slr.book_book_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE slr.book_book_id_seq OWNER TO postgres;

--
-- TOC entry 3401 (class 0 OID 0)
-- Dependencies: 205
-- Name: book_book_id_seq; Type: SEQUENCE OWNED BY; Schema: slr; Owner: postgres
--

ALTER SEQUENCE slr.book_book_id_seq OWNED BY slr.book.book_id;


--
-- TOC entry 206 (class 1259 OID 16437)
-- Name: book_chapter; Type: TABLE; Schema: slr; Owner: postgres
--

CREATE TABLE slr.book_chapter (
    pages character varying(50),
    book_chapter_id integer NOT NULL,
    publisher_id integer
)
INHERITS (slr.publication);


ALTER TABLE slr.book_chapter OWNER TO postgres;

--
-- TOC entry 207 (class 1259 OID 16444)
-- Name: book_chapter_book_chapter_id_seq; Type: SEQUENCE; Schema: slr; Owner: postgres
--

CREATE SEQUENCE slr.book_chapter_book_chapter_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE slr.book_chapter_book_chapter_id_seq OWNER TO postgres;

--
-- TOC entry 3402 (class 0 OID 0)
-- Dependencies: 207
-- Name: book_chapter_book_chapter_id_seq; Type: SEQUENCE OWNED BY; Schema: slr; Owner: postgres
--

ALTER SEQUENCE slr.book_chapter_book_chapter_id_seq OWNED BY slr.book_chapter.book_chapter_id;


--
-- TOC entry 208 (class 1259 OID 16446)
-- Name: conference; Type: TABLE; Schema: slr; Owner: postgres
--

CREATE TABLE slr.conference (
    id integer NOT NULL,
    description text
);


ALTER TABLE slr.conference OWNER TO postgres;

--
-- TOC entry 209 (class 1259 OID 16452)
-- Name: conference_editorial; Type: TABLE; Schema: slr; Owner: postgres
--

CREATE TABLE slr.conference_editorial (
    conference_editorial_id integer NOT NULL,
    edition_id integer
)
INHERITS (slr.publication);


ALTER TABLE slr.conference_editorial OWNER TO postgres;

--
-- TOC entry 210 (class 1259 OID 16459)
-- Name: conference_editorial_conference_editorial_id_seq; Type: SEQUENCE; Schema: slr; Owner: postgres
--

CREATE SEQUENCE slr.conference_editorial_conference_editorial_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE slr.conference_editorial_conference_editorial_id_seq OWNER TO postgres;

--
-- TOC entry 3403 (class 0 OID 0)
-- Dependencies: 210
-- Name: conference_editorial_conference_editorial_id_seq; Type: SEQUENCE OWNED BY; Schema: slr; Owner: postgres
--

ALTER SEQUENCE slr.conference_editorial_conference_editorial_id_seq OWNED BY slr.conference_editorial.conference_editorial_id;


--
-- TOC entry 211 (class 1259 OID 16461)
-- Name: conference_id_seq; Type: SEQUENCE; Schema: slr; Owner: postgres
--

CREATE SEQUENCE slr.conference_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE slr.conference_id_seq OWNER TO postgres;

--
-- TOC entry 3404 (class 0 OID 0)
-- Dependencies: 211
-- Name: conference_id_seq; Type: SEQUENCE OWNED BY; Schema: slr; Owner: postgres
--

ALTER SEQUENCE slr.conference_id_seq OWNED BY slr.conference.id;


--
-- TOC entry 212 (class 1259 OID 16463)
-- Name: conference_paper; Type: TABLE; Schema: slr; Owner: postgres
--

CREATE TABLE slr.conference_paper (
    conference_paper_id integer NOT NULL,
    edition_id integer
)
INHERITS (slr.publication);


ALTER TABLE slr.conference_paper OWNER TO postgres;

--
-- TOC entry 213 (class 1259 OID 16470)
-- Name: conference_paper_conference_paper_id_seq; Type: SEQUENCE; Schema: slr; Owner: postgres
--

CREATE SEQUENCE slr.conference_paper_conference_paper_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE slr.conference_paper_conference_paper_id_seq OWNER TO postgres;

--
-- TOC entry 3405 (class 0 OID 0)
-- Dependencies: 213
-- Name: conference_paper_conference_paper_id_seq; Type: SEQUENCE OWNED BY; Schema: slr; Owner: postgres
--

ALTER SEQUENCE slr.conference_paper_conference_paper_id_seq OWNED BY slr.conference_paper.conference_paper_id;


--
-- TOC entry 214 (class 1259 OID 16472)
-- Name: country; Type: TABLE; Schema: slr; Owner: postgres
--

CREATE TABLE slr.country (
    id integer NOT NULL,
    description text
);


ALTER TABLE slr.country OWNER TO postgres;

--
-- TOC entry 215 (class 1259 OID 16478)
-- Name: country_id_seq; Type: SEQUENCE; Schema: slr; Owner: postgres
--

CREATE SEQUENCE slr.country_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE slr.country_id_seq OWNER TO postgres;

--
-- TOC entry 3406 (class 0 OID 0)
-- Dependencies: 215
-- Name: country_id_seq; Type: SEQUENCE OWNED BY; Schema: slr; Owner: postgres
--

ALTER SEQUENCE slr.country_id_seq OWNED BY slr.country.id;


--
-- TOC entry 216 (class 1259 OID 16480)
-- Name: dblp_document_id_seq; Type: SEQUENCE; Schema: slr; Owner: postgres
--

CREATE SEQUENCE slr.dblp_document_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE slr.dblp_document_id_seq OWNER TO postgres;

--
-- TOC entry 217 (class 1259 OID 16482)
-- Name: dblp_document; Type: TABLE; Schema: slr; Owner: postgres
--

CREATE TABLE slr.dblp_document (
    id integer DEFAULT nextval('slr.dblp_document_id_seq'::regclass) NOT NULL,
    key_dblp character varying(100),
    authors json,
    doc_type character varying(100),
    editor character varying(100),
    booktitle character varying(250),
    pages character varying(50),
    year integer,
    title text,
    address text,
    journal text,
    volume character varying(100),
    number character varying(50),
    month character varying(50),
    url text,
    ee text,
    cdrom text,
    cite text,
    publisher text,
    note text,
    crossref text,
    isbn text,
    series text,
    school text,
    chapter text,
    publnr text,
    unknow_fields json,
    unknow_atts json,
    mdate character varying(100),
    updated boolean DEFAULT false
);


ALTER TABLE slr.dblp_document OWNER TO postgres;

--
-- TOC entry 218 (class 1259 OID 16490)
-- Name: department; Type: TABLE; Schema: slr; Owner: postgres
--

CREATE TABLE slr.department (
    id integer NOT NULL,
    description text,
    institution_id integer
);


ALTER TABLE slr.department OWNER TO postgres;

--
-- TOC entry 219 (class 1259 OID 16496)
-- Name: department_id_seq; Type: SEQUENCE; Schema: slr; Owner: postgres
--

CREATE SEQUENCE slr.department_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE slr.department_id_seq OWNER TO postgres;

--
-- TOC entry 3407 (class 0 OID 0)
-- Dependencies: 219
-- Name: department_id_seq; Type: SEQUENCE OWNED BY; Schema: slr; Owner: postgres
--

ALTER SEQUENCE slr.department_id_seq OWNED BY slr.department.id;


--
-- TOC entry 220 (class 1259 OID 16498)
-- Name: edition; Type: TABLE; Schema: slr; Owner: postgres
--

CREATE TABLE slr.edition (
    id integer NOT NULL,
    description text,
    conference_id integer,
    publisher_id integer
);


ALTER TABLE slr.edition OWNER TO postgres;

--
-- TOC entry 221 (class 1259 OID 16504)
-- Name: edition_id_seq; Type: SEQUENCE; Schema: slr; Owner: postgres
--

CREATE SEQUENCE slr.edition_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE slr.edition_id_seq OWNER TO postgres;

--
-- TOC entry 3408 (class 0 OID 0)
-- Dependencies: 221
-- Name: edition_id_seq; Type: SEQUENCE OWNED BY; Schema: slr; Owner: postgres
--

ALTER SEQUENCE slr.edition_id_seq OWNED BY slr.edition.id;


--
-- TOC entry 222 (class 1259 OID 16506)
-- Name: institution; Type: TABLE; Schema: slr; Owner: postgres
--

CREATE TABLE slr.institution (
    id integer NOT NULL,
    country_id integer,
    description text
);


ALTER TABLE slr.institution OWNER TO postgres;

--
-- TOC entry 223 (class 1259 OID 16512)
-- Name: institution_id_seq; Type: SEQUENCE; Schema: slr; Owner: postgres
--

CREATE SEQUENCE slr.institution_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE slr.institution_id_seq OWNER TO postgres;

--
-- TOC entry 3409 (class 0 OID 0)
-- Dependencies: 223
-- Name: institution_id_seq; Type: SEQUENCE OWNED BY; Schema: slr; Owner: postgres
--

ALTER SEQUENCE slr.institution_id_seq OWNED BY slr.institution.id;


--
-- TOC entry 224 (class 1259 OID 16514)
-- Name: journal; Type: TABLE; Schema: slr; Owner: postgres
--

CREATE TABLE slr.journal (
    id integer NOT NULL,
    description text
);


ALTER TABLE slr.journal OWNER TO postgres;

--
-- TOC entry 225 (class 1259 OID 16520)
-- Name: journal_editorial; Type: TABLE; Schema: slr; Owner: postgres
--

CREATE TABLE slr.journal_editorial (
    journal_editorial_id integer NOT NULL,
    volume_number_id integer
)
INHERITS (slr.publication);


ALTER TABLE slr.journal_editorial OWNER TO postgres;

--
-- TOC entry 226 (class 1259 OID 16527)
-- Name: journal_editorial_journal_editorial_id_seq; Type: SEQUENCE; Schema: slr; Owner: postgres
--

CREATE SEQUENCE slr.journal_editorial_journal_editorial_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE slr.journal_editorial_journal_editorial_id_seq OWNER TO postgres;

--
-- TOC entry 3410 (class 0 OID 0)
-- Dependencies: 226
-- Name: journal_editorial_journal_editorial_id_seq; Type: SEQUENCE OWNED BY; Schema: slr; Owner: postgres
--

ALTER SEQUENCE slr.journal_editorial_journal_editorial_id_seq OWNED BY slr.journal_editorial.journal_editorial_id;


--
-- TOC entry 227 (class 1259 OID 16529)
-- Name: journal_id_seq; Type: SEQUENCE; Schema: slr; Owner: postgres
--

CREATE SEQUENCE slr.journal_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE slr.journal_id_seq OWNER TO postgres;

--
-- TOC entry 3411 (class 0 OID 0)
-- Dependencies: 227
-- Name: journal_id_seq; Type: SEQUENCE OWNED BY; Schema: slr; Owner: postgres
--

ALTER SEQUENCE slr.journal_id_seq OWNED BY slr.journal.id;


--
-- TOC entry 228 (class 1259 OID 16531)
-- Name: journal_paper; Type: TABLE; Schema: slr; Owner: postgres
--

CREATE TABLE slr.journal_paper (
    journal_paper_id integer NOT NULL,
    volume_number_id integer
)
INHERITS (slr.publication);


ALTER TABLE slr.journal_paper OWNER TO postgres;

--
-- TOC entry 229 (class 1259 OID 16538)
-- Name: journal_paper_journal_paper_id_seq; Type: SEQUENCE; Schema: slr; Owner: postgres
--

CREATE SEQUENCE slr.journal_paper_journal_paper_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE slr.journal_paper_journal_paper_id_seq OWNER TO postgres;

--
-- TOC entry 3412 (class 0 OID 0)
-- Dependencies: 229
-- Name: journal_paper_journal_paper_id_seq; Type: SEQUENCE OWNED BY; Schema: slr; Owner: postgres
--

ALTER SEQUENCE slr.journal_paper_journal_paper_id_seq OWNED BY slr.journal_paper.journal_paper_id;


--
-- TOC entry 230 (class 1259 OID 16540)
-- Name: keyword; Type: TABLE; Schema: slr; Owner: postgres
--

CREATE TABLE slr.keyword (
    id integer NOT NULL,
    name character varying(255)
);


ALTER TABLE slr.keyword OWNER TO postgres;

--
-- TOC entry 231 (class 1259 OID 16543)
-- Name: keyword_id_seq; Type: SEQUENCE; Schema: slr; Owner: postgres
--

CREATE SEQUENCE slr.keyword_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE slr.keyword_id_seq OWNER TO postgres;

--
-- TOC entry 3413 (class 0 OID 0)
-- Dependencies: 231
-- Name: keyword_id_seq; Type: SEQUENCE OWNED BY; Schema: slr; Owner: postgres
--

ALTER SEQUENCE slr.keyword_id_seq OWNED BY slr.keyword.id;


--
-- TOC entry 232 (class 1259 OID 16545)
-- Name: publication_id_seq; Type: SEQUENCE; Schema: slr; Owner: postgres
--

CREATE SEQUENCE slr.publication_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE slr.publication_id_seq OWNER TO postgres;

--
-- TOC entry 3414 (class 0 OID 0)
-- Dependencies: 232
-- Name: publication_id_seq; Type: SEQUENCE OWNED BY; Schema: slr; Owner: postgres
--

ALTER SEQUENCE slr.publication_id_seq OWNED BY slr.publication.id;


--
-- TOC entry 233 (class 1259 OID 16547)
-- Name: publication_keywords; Type: TABLE; Schema: slr; Owner: postgres
--

CREATE TABLE slr.publication_keywords (
    id integer NOT NULL,
    publication_id integer,
    keyword_id integer
);


ALTER TABLE slr.publication_keywords OWNER TO postgres;

--
-- TOC entry 234 (class 1259 OID 16550)
-- Name: publication_keywords_id_seq; Type: SEQUENCE; Schema: slr; Owner: postgres
--

CREATE SEQUENCE slr.publication_keywords_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE slr.publication_keywords_id_seq OWNER TO postgres;

--
-- TOC entry 3415 (class 0 OID 0)
-- Dependencies: 234
-- Name: publication_keywords_id_seq; Type: SEQUENCE OWNED BY; Schema: slr; Owner: postgres
--

ALTER SEQUENCE slr.publication_keywords_id_seq OWNED BY slr.publication_keywords.id;


--
-- TOC entry 198 (class 1259 OID 16390)
-- Name: publisher; Type: TABLE; Schema: slr; Owner: postgres
--

CREATE TABLE slr.publisher (
    id bigint NOT NULL,
    description text,
    state character varying(80) DEFAULT 'activo'::character varying
);


ALTER TABLE slr.publisher OWNER TO postgres;

--
-- TOC entry 197 (class 1259 OID 16388)
-- Name: publisher_id_seq; Type: SEQUENCE; Schema: slr; Owner: postgres
--

CREATE SEQUENCE slr.publisher_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE slr.publisher_id_seq OWNER TO postgres;

--
-- TOC entry 3416 (class 0 OID 0)
-- Dependencies: 197
-- Name: publisher_id_seq; Type: SEQUENCE OWNED BY; Schema: slr; Owner: postgres
--

ALTER SEQUENCE slr.publisher_id_seq OWNED BY slr.publisher.id;


--
-- TOC entry 235 (class 1259 OID 16552)
-- Name: test; Type: TABLE; Schema: slr; Owner: postgres
--

CREATE TABLE slr.test (
    id integer NOT NULL,
    cadena json,
    numero integer
);


ALTER TABLE slr.test OWNER TO postgres;

--
-- TOC entry 236 (class 1259 OID 16558)
-- Name: test_id_seq; Type: SEQUENCE; Schema: slr; Owner: postgres
--

CREATE SEQUENCE slr.test_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE slr.test_id_seq OWNER TO postgres;

--
-- TOC entry 3417 (class 0 OID 0)
-- Dependencies: 236
-- Name: test_id_seq; Type: SEQUENCE OWNED BY; Schema: slr; Owner: postgres
--

ALTER SEQUENCE slr.test_id_seq OWNED BY slr.test.id;


--
-- TOC entry 237 (class 1259 OID 16560)
-- Name: volume_number; Type: TABLE; Schema: slr; Owner: postgres
--

CREATE TABLE slr.volume_number (
    id integer NOT NULL,
    volume integer,
    number integer,
    journal_id integer,
    publisher_id integer
);


ALTER TABLE slr.volume_number OWNER TO postgres;

--
-- TOC entry 238 (class 1259 OID 16563)
-- Name: volume_number_id_seq; Type: SEQUENCE; Schema: slr; Owner: postgres
--

CREATE SEQUENCE slr.volume_number_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE slr.volume_number_id_seq OWNER TO postgres;

--
-- TOC entry 3418 (class 0 OID 0)
-- Dependencies: 238
-- Name: volume_number_id_seq; Type: SEQUENCE OWNED BY; Schema: slr; Owner: postgres
--

ALTER SEQUENCE slr.volume_number_id_seq OWNED BY slr.volume_number.id;


--
-- TOC entry 3180 (class 2604 OID 16565)
-- Name: author id; Type: DEFAULT; Schema: slr; Owner: postgres
--

ALTER TABLE ONLY slr.author ALTER COLUMN id SET DEFAULT nextval('slr.author_id_seq'::regclass);


--
-- TOC entry 3182 (class 2604 OID 16566)
-- Name: author_publications id; Type: DEFAULT; Schema: slr; Owner: postgres
--

ALTER TABLE ONLY slr.author_publications ALTER COLUMN id SET DEFAULT nextval('slr.author_publications_id_seq'::regclass);


--
-- TOC entry 3185 (class 2604 OID 16567)
-- Name: book id; Type: DEFAULT; Schema: slr; Owner: postgres
--

ALTER TABLE ONLY slr.book ALTER COLUMN id SET DEFAULT nextval('slr.publication_id_seq'::regclass);


--
-- TOC entry 3187 (class 2604 OID 16569)
-- Name: book created_at; Type: DEFAULT; Schema: slr; Owner: postgres
--

ALTER TABLE ONLY slr.book ALTER COLUMN created_at SET DEFAULT now();


--
-- TOC entry 3186 (class 2604 OID 16568)
-- Name: book book_id; Type: DEFAULT; Schema: slr; Owner: postgres
--

ALTER TABLE ONLY slr.book ALTER COLUMN book_id SET DEFAULT nextval('slr.book_book_id_seq'::regclass);


--
-- TOC entry 3188 (class 2604 OID 16570)
-- Name: book_chapter id; Type: DEFAULT; Schema: slr; Owner: postgres
--

ALTER TABLE ONLY slr.book_chapter ALTER COLUMN id SET DEFAULT nextval('slr.publication_id_seq'::regclass);


--
-- TOC entry 3190 (class 2604 OID 16572)
-- Name: book_chapter created_at; Type: DEFAULT; Schema: slr; Owner: postgres
--

ALTER TABLE ONLY slr.book_chapter ALTER COLUMN created_at SET DEFAULT now();


--
-- TOC entry 3189 (class 2604 OID 16571)
-- Name: book_chapter book_chapter_id; Type: DEFAULT; Schema: slr; Owner: postgres
--

ALTER TABLE ONLY slr.book_chapter ALTER COLUMN book_chapter_id SET DEFAULT nextval('slr.book_chapter_book_chapter_id_seq'::regclass);


--
-- TOC entry 3191 (class 2604 OID 16573)
-- Name: conference id; Type: DEFAULT; Schema: slr; Owner: postgres
--

ALTER TABLE ONLY slr.conference ALTER COLUMN id SET DEFAULT nextval('slr.conference_id_seq'::regclass);


--
-- TOC entry 3192 (class 2604 OID 16574)
-- Name: conference_editorial id; Type: DEFAULT; Schema: slr; Owner: postgres
--

ALTER TABLE ONLY slr.conference_editorial ALTER COLUMN id SET DEFAULT nextval('slr.publication_id_seq'::regclass);


--
-- TOC entry 3194 (class 2604 OID 16576)
-- Name: conference_editorial created_at; Type: DEFAULT; Schema: slr; Owner: postgres
--

ALTER TABLE ONLY slr.conference_editorial ALTER COLUMN created_at SET DEFAULT now();


--
-- TOC entry 3193 (class 2604 OID 16575)
-- Name: conference_editorial conference_editorial_id; Type: DEFAULT; Schema: slr; Owner: postgres
--

ALTER TABLE ONLY slr.conference_editorial ALTER COLUMN conference_editorial_id SET DEFAULT nextval('slr.conference_editorial_conference_editorial_id_seq'::regclass);


--
-- TOC entry 3195 (class 2604 OID 16577)
-- Name: conference_paper id; Type: DEFAULT; Schema: slr; Owner: postgres
--

ALTER TABLE ONLY slr.conference_paper ALTER COLUMN id SET DEFAULT nextval('slr.publication_id_seq'::regclass);


--
-- TOC entry 3197 (class 2604 OID 16579)
-- Name: conference_paper created_at; Type: DEFAULT; Schema: slr; Owner: postgres
--

ALTER TABLE ONLY slr.conference_paper ALTER COLUMN created_at SET DEFAULT now();


--
-- TOC entry 3196 (class 2604 OID 16578)
-- Name: conference_paper conference_paper_id; Type: DEFAULT; Schema: slr; Owner: postgres
--

ALTER TABLE ONLY slr.conference_paper ALTER COLUMN conference_paper_id SET DEFAULT nextval('slr.conference_paper_conference_paper_id_seq'::regclass);


--
-- TOC entry 3198 (class 2604 OID 16580)
-- Name: country id; Type: DEFAULT; Schema: slr; Owner: postgres
--

ALTER TABLE ONLY slr.country ALTER COLUMN id SET DEFAULT nextval('slr.country_id_seq'::regclass);


--
-- TOC entry 3201 (class 2604 OID 16581)
-- Name: department id; Type: DEFAULT; Schema: slr; Owner: postgres
--

ALTER TABLE ONLY slr.department ALTER COLUMN id SET DEFAULT nextval('slr.department_id_seq'::regclass);


--
-- TOC entry 3202 (class 2604 OID 16582)
-- Name: edition id; Type: DEFAULT; Schema: slr; Owner: postgres
--

ALTER TABLE ONLY slr.edition ALTER COLUMN id SET DEFAULT nextval('slr.edition_id_seq'::regclass);


--
-- TOC entry 3203 (class 2604 OID 16583)
-- Name: institution id; Type: DEFAULT; Schema: slr; Owner: postgres
--

ALTER TABLE ONLY slr.institution ALTER COLUMN id SET DEFAULT nextval('slr.institution_id_seq'::regclass);


--
-- TOC entry 3204 (class 2604 OID 16584)
-- Name: journal id; Type: DEFAULT; Schema: slr; Owner: postgres
--

ALTER TABLE ONLY slr.journal ALTER COLUMN id SET DEFAULT nextval('slr.journal_id_seq'::regclass);


--
-- TOC entry 3205 (class 2604 OID 16585)
-- Name: journal_editorial id; Type: DEFAULT; Schema: slr; Owner: postgres
--

ALTER TABLE ONLY slr.journal_editorial ALTER COLUMN id SET DEFAULT nextval('slr.publication_id_seq'::regclass);


--
-- TOC entry 3207 (class 2604 OID 16587)
-- Name: journal_editorial created_at; Type: DEFAULT; Schema: slr; Owner: postgres
--

ALTER TABLE ONLY slr.journal_editorial ALTER COLUMN created_at SET DEFAULT now();


--
-- TOC entry 3206 (class 2604 OID 16586)
-- Name: journal_editorial journal_editorial_id; Type: DEFAULT; Schema: slr; Owner: postgres
--

ALTER TABLE ONLY slr.journal_editorial ALTER COLUMN journal_editorial_id SET DEFAULT nextval('slr.journal_editorial_journal_editorial_id_seq'::regclass);


--
-- TOC entry 3208 (class 2604 OID 16588)
-- Name: journal_paper id; Type: DEFAULT; Schema: slr; Owner: postgres
--

ALTER TABLE ONLY slr.journal_paper ALTER COLUMN id SET DEFAULT nextval('slr.publication_id_seq'::regclass);


--
-- TOC entry 3210 (class 2604 OID 16590)
-- Name: journal_paper created_at; Type: DEFAULT; Schema: slr; Owner: postgres
--

ALTER TABLE ONLY slr.journal_paper ALTER COLUMN created_at SET DEFAULT now();


--
-- TOC entry 3209 (class 2604 OID 16589)
-- Name: journal_paper journal_paper_id; Type: DEFAULT; Schema: slr; Owner: postgres
--

ALTER TABLE ONLY slr.journal_paper ALTER COLUMN journal_paper_id SET DEFAULT nextval('slr.journal_paper_journal_paper_id_seq'::regclass);


--
-- TOC entry 3211 (class 2604 OID 16591)
-- Name: keyword id; Type: DEFAULT; Schema: slr; Owner: postgres
--

ALTER TABLE ONLY slr.keyword ALTER COLUMN id SET DEFAULT nextval('slr.keyword_id_seq'::regclass);


--
-- TOC entry 3184 (class 2604 OID 16592)
-- Name: publication id; Type: DEFAULT; Schema: slr; Owner: postgres
--

ALTER TABLE ONLY slr.publication ALTER COLUMN id SET DEFAULT nextval('slr.publication_id_seq'::regclass);


--
-- TOC entry 3212 (class 2604 OID 16593)
-- Name: publication_keywords id; Type: DEFAULT; Schema: slr; Owner: postgres
--

ALTER TABLE ONLY slr.publication_keywords ALTER COLUMN id SET DEFAULT nextval('slr.publication_keywords_id_seq'::regclass);


--
-- TOC entry 3178 (class 2604 OID 16594)
-- Name: publisher id; Type: DEFAULT; Schema: slr; Owner: postgres
--

ALTER TABLE ONLY slr.publisher ALTER COLUMN id SET DEFAULT nextval('slr.publisher_id_seq'::regclass);


--
-- TOC entry 3213 (class 2604 OID 16595)
-- Name: test id; Type: DEFAULT; Schema: slr; Owner: postgres
--

ALTER TABLE ONLY slr.test ALTER COLUMN id SET DEFAULT nextval('slr.test_id_seq'::regclass);


--
-- TOC entry 3214 (class 2604 OID 16596)
-- Name: volume_number id; Type: DEFAULT; Schema: slr; Owner: postgres
--

ALTER TABLE ONLY slr.volume_number ALTER COLUMN id SET DEFAULT nextval('slr.volume_number_id_seq'::regclass);


--
-- TOC entry 3218 (class 2606 OID 16598)
-- Name: author author_pkey; Type: CONSTRAINT; Schema: slr; Owner: postgres
--

ALTER TABLE ONLY slr.author
    ADD CONSTRAINT author_pkey PRIMARY KEY (id);


--
-- TOC entry 3220 (class 2606 OID 16600)
-- Name: author_publications author_publications_pkey; Type: CONSTRAINT; Schema: slr; Owner: postgres
--

ALTER TABLE ONLY slr.author_publications
    ADD CONSTRAINT author_publications_pkey PRIMARY KEY (id);


--
-- TOC entry 3226 (class 2606 OID 16602)
-- Name: book_chapter book_chapter_pkey; Type: CONSTRAINT; Schema: slr; Owner: postgres
--

ALTER TABLE ONLY slr.book_chapter
    ADD CONSTRAINT book_chapter_pkey PRIMARY KEY (book_chapter_id);


--
-- TOC entry 3224 (class 2606 OID 16604)
-- Name: book book_pkey; Type: CONSTRAINT; Schema: slr; Owner: postgres
--

ALTER TABLE ONLY slr.book
    ADD CONSTRAINT book_pkey PRIMARY KEY (book_id);


--
-- TOC entry 3230 (class 2606 OID 16606)
-- Name: conference_editorial conference_editorial_pkey; Type: CONSTRAINT; Schema: slr; Owner: postgres
--

ALTER TABLE ONLY slr.conference_editorial
    ADD CONSTRAINT conference_editorial_pkey PRIMARY KEY (conference_editorial_id);


--
-- TOC entry 3232 (class 2606 OID 16608)
-- Name: conference_paper conference_paper_pkey; Type: CONSTRAINT; Schema: slr; Owner: postgres
--

ALTER TABLE ONLY slr.conference_paper
    ADD CONSTRAINT conference_paper_pkey PRIMARY KEY (conference_paper_id);


--
-- TOC entry 3228 (class 2606 OID 16610)
-- Name: conference conference_pkey; Type: CONSTRAINT; Schema: slr; Owner: postgres
--

ALTER TABLE ONLY slr.conference
    ADD CONSTRAINT conference_pkey PRIMARY KEY (id);


--
-- TOC entry 3234 (class 2606 OID 16612)
-- Name: country country_pkey; Type: CONSTRAINT; Schema: slr; Owner: postgres
--

ALTER TABLE ONLY slr.country
    ADD CONSTRAINT country_pkey PRIMARY KEY (id);


--
-- TOC entry 3236 (class 2606 OID 16614)
-- Name: department department_pkey; Type: CONSTRAINT; Schema: slr; Owner: postgres
--

ALTER TABLE ONLY slr.department
    ADD CONSTRAINT department_pkey PRIMARY KEY (id);


--
-- TOC entry 3238 (class 2606 OID 16616)
-- Name: edition edition_pkey; Type: CONSTRAINT; Schema: slr; Owner: postgres
--

ALTER TABLE ONLY slr.edition
    ADD CONSTRAINT edition_pkey PRIMARY KEY (id);


--
-- TOC entry 3240 (class 2606 OID 16618)
-- Name: institution institution_pkey; Type: CONSTRAINT; Schema: slr; Owner: postgres
--

ALTER TABLE ONLY slr.institution
    ADD CONSTRAINT institution_pkey PRIMARY KEY (id);


--
-- TOC entry 3244 (class 2606 OID 16620)
-- Name: journal_editorial journal_editorial_pkey; Type: CONSTRAINT; Schema: slr; Owner: postgres
--

ALTER TABLE ONLY slr.journal_editorial
    ADD CONSTRAINT journal_editorial_pkey PRIMARY KEY (journal_editorial_id);


--
-- TOC entry 3246 (class 2606 OID 16622)
-- Name: journal_paper journal_paper_pkey; Type: CONSTRAINT; Schema: slr; Owner: postgres
--

ALTER TABLE ONLY slr.journal_paper
    ADD CONSTRAINT journal_paper_pkey PRIMARY KEY (journal_paper_id);


--
-- TOC entry 3242 (class 2606 OID 16624)
-- Name: journal journal_pkey; Type: CONSTRAINT; Schema: slr; Owner: postgres
--

ALTER TABLE ONLY slr.journal
    ADD CONSTRAINT journal_pkey PRIMARY KEY (id);


--
-- TOC entry 3248 (class 2606 OID 16626)
-- Name: keyword keyword_pkey; Type: CONSTRAINT; Schema: slr; Owner: postgres
--

ALTER TABLE ONLY slr.keyword
    ADD CONSTRAINT keyword_pkey PRIMARY KEY (id);


--
-- TOC entry 3250 (class 2606 OID 16628)
-- Name: publication_keywords publication_keywords_pkey; Type: CONSTRAINT; Schema: slr; Owner: postgres
--

ALTER TABLE ONLY slr.publication_keywords
    ADD CONSTRAINT publication_keywords_pkey PRIMARY KEY (id);


--
-- TOC entry 3222 (class 2606 OID 16630)
-- Name: publication publication_pkey; Type: CONSTRAINT; Schema: slr; Owner: postgres
--

ALTER TABLE ONLY slr.publication
    ADD CONSTRAINT publication_pkey PRIMARY KEY (id);


--
-- TOC entry 3216 (class 2606 OID 16398)
-- Name: publisher publisher_pkey; Type: CONSTRAINT; Schema: slr; Owner: postgres
--

ALTER TABLE ONLY slr.publisher
    ADD CONSTRAINT publisher_pkey PRIMARY KEY (id);


--
-- TOC entry 3252 (class 2606 OID 16632)
-- Name: test test_pkey; Type: CONSTRAINT; Schema: slr; Owner: postgres
--

ALTER TABLE ONLY slr.test
    ADD CONSTRAINT test_pkey PRIMARY KEY (id);


--
-- TOC entry 3254 (class 2606 OID 16634)
-- Name: volume_number volume_number_pkey; Type: CONSTRAINT; Schema: slr; Owner: postgres
--

ALTER TABLE ONLY slr.volume_number
    ADD CONSTRAINT volume_number_pkey PRIMARY KEY (id);


--
-- TOC entry 3256 (class 2606 OID 16635)
-- Name: author_publications author__id_fk; Type: FK CONSTRAINT; Schema: slr; Owner: postgres
--

ALTER TABLE ONLY slr.author_publications
    ADD CONSTRAINT author__id_fk FOREIGN KEY (author_id) REFERENCES slr.author(id);


--
-- TOC entry 3263 (class 2606 OID 16640)
-- Name: edition conference__id_fk; Type: FK CONSTRAINT; Schema: slr; Owner: postgres
--

ALTER TABLE ONLY slr.edition
    ADD CONSTRAINT conference__id_fk FOREIGN KEY (conference_id) REFERENCES slr.conference(id);


--
-- TOC entry 3265 (class 2606 OID 16645)
-- Name: institution country__id_fk; Type: FK CONSTRAINT; Schema: slr; Owner: postgres
--

ALTER TABLE ONLY slr.institution
    ADD CONSTRAINT country__id_fk FOREIGN KEY (country_id) REFERENCES slr.country(id);


--
-- TOC entry 3255 (class 2606 OID 16650)
-- Name: author department__id_fk; Type: FK CONSTRAINT; Schema: slr; Owner: postgres
--

ALTER TABLE ONLY slr.author
    ADD CONSTRAINT department__id_fk FOREIGN KEY (department_id) REFERENCES slr.department(id);


--
-- TOC entry 3260 (class 2606 OID 16655)
-- Name: conference_editorial edition__id_fk2; Type: FK CONSTRAINT; Schema: slr; Owner: postgres
--

ALTER TABLE ONLY slr.conference_editorial
    ADD CONSTRAINT edition__id_fk2 FOREIGN KEY (edition_id) REFERENCES slr.edition(id);


--
-- TOC entry 3261 (class 2606 OID 16660)
-- Name: conference_paper edition_paper_id_fk; Type: FK CONSTRAINT; Schema: slr; Owner: postgres
--

ALTER TABLE ONLY slr.conference_paper
    ADD CONSTRAINT edition_paper_id_fk FOREIGN KEY (edition_id) REFERENCES slr.edition(id);


--
-- TOC entry 3262 (class 2606 OID 16665)
-- Name: department institutio__id_fk; Type: FK CONSTRAINT; Schema: slr; Owner: postgres
--

ALTER TABLE ONLY slr.department
    ADD CONSTRAINT institutio__id_fk FOREIGN KEY (institution_id) REFERENCES slr.institution(id);


--
-- TOC entry 3270 (class 2606 OID 16670)
-- Name: volume_number journal__id_fk; Type: FK CONSTRAINT; Schema: slr; Owner: postgres
--

ALTER TABLE ONLY slr.volume_number
    ADD CONSTRAINT journal__id_fk FOREIGN KEY (journal_id) REFERENCES slr.journal(id);


--
-- TOC entry 3268 (class 2606 OID 16675)
-- Name: publication_keywords keyword__id_fk; Type: FK CONSTRAINT; Schema: slr; Owner: postgres
--

ALTER TABLE ONLY slr.publication_keywords
    ADD CONSTRAINT keyword__id_fk FOREIGN KEY (keyword_id) REFERENCES slr.keyword(id);


--
-- TOC entry 3269 (class 2606 OID 16685)
-- Name: publication_keywords publication__keywords_id_fk; Type: FK CONSTRAINT; Schema: slr; Owner: postgres
--

ALTER TABLE ONLY slr.publication_keywords
    ADD CONSTRAINT publication__keywords_id_fk FOREIGN KEY (publication_id) REFERENCES slr.publication(id);


--
-- TOC entry 3257 (class 2606 OID 16680)
-- Name: author_publications publication_author_id_fk; Type: FK CONSTRAINT; Schema: slr; Owner: postgres
--

ALTER TABLE ONLY slr.author_publications
    ADD CONSTRAINT publication_author_id_fk FOREIGN KEY (publication_id) REFERENCES slr.publication(id);


--
-- TOC entry 3258 (class 2606 OID 16690)
-- Name: book publisher_book_id_fk; Type: FK CONSTRAINT; Schema: slr; Owner: postgres
--

ALTER TABLE ONLY slr.book
    ADD CONSTRAINT publisher_book_id_fk FOREIGN KEY (publisher_id) REFERENCES slr.publisher(id);


--
-- TOC entry 3259 (class 2606 OID 16695)
-- Name: book_chapter publisher_bookchapter_id_fk; Type: FK CONSTRAINT; Schema: slr; Owner: postgres
--

ALTER TABLE ONLY slr.book_chapter
    ADD CONSTRAINT publisher_bookchapter_id_fk FOREIGN KEY (publisher_id) REFERENCES slr.publisher(id);


--
-- TOC entry 3264 (class 2606 OID 16792)
-- Name: edition publisher_edition__id_fk; Type: FK CONSTRAINT; Schema: slr; Owner: postgres
--

ALTER TABLE ONLY slr.edition
    ADD CONSTRAINT publisher_edition__id_fk FOREIGN KEY (publisher_id) REFERENCES slr.publisher(id);


--
-- TOC entry 3271 (class 2606 OID 16797)
-- Name: volume_number publisher_volume__id_fk; Type: FK CONSTRAINT; Schema: slr; Owner: postgres
--

ALTER TABLE ONLY slr.volume_number
    ADD CONSTRAINT publisher_volume__id_fk FOREIGN KEY (publisher_id) REFERENCES slr.publisher(id);


--
-- TOC entry 3266 (class 2606 OID 16700)
-- Name: journal_editorial volume_number_journaleditorial_id_fk; Type: FK CONSTRAINT; Schema: slr; Owner: postgres
--

ALTER TABLE ONLY slr.journal_editorial
    ADD CONSTRAINT volume_number_journaleditorial_id_fk FOREIGN KEY (volume_number_id) REFERENCES slr.volume_number(id);


--
-- TOC entry 3267 (class 2606 OID 16705)
-- Name: journal_paper volume_number_journalpaper_id_fk; Type: FK CONSTRAINT; Schema: slr; Owner: postgres
--

ALTER TABLE ONLY slr.journal_paper
    ADD CONSTRAINT volume_number_journalpaper_id_fk FOREIGN KEY (volume_number_id) REFERENCES slr.volume_number(id);


-- Completed on 2019-09-29 03:44:39 -03

--
-- PostgreSQL database dump complete
--

