--
-- PostgreSQL database dump
--

-- Dumped from database version 11.6
-- Dumped by pg_dump version 11.5

-- Started on 2019-12-20 18:06:54 -03

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
-- TOC entry 3455 (class 1262 OID 16386)
-- Name: dbslr; Type: DATABASE; Schema: -; Owner: -
--

CREATE DATABASE dbslr WITH TEMPLATE = template0 ENCODING = 'UTF8' LC_COLLATE = 'en_US.UTF-8' LC_CTYPE = 'en_US.UTF-8';


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
-- TOC entry 8 (class 2615 OID 20429)
-- Name: slr; Type: SCHEMA; Schema: -; Owner: -
--

CREATE SCHEMA slr;


--
-- TOC entry 283 (class 1255 OID 20726)
-- Name: slr_author_iud(character varying, integer, character varying, character varying, integer); Type: FUNCTION; Schema: slr; Owner: -
--

CREATE FUNCTION slr.slr_author_iud(procedimiento character varying, auth_id integer, home_page character varying, author_name character varying, depto_id integer, OUT author_id integer) RETURNS integer
    LANGUAGE plpgsql
    AS $$BEGIN
	IF procedimiento = 'AUTH_INS' THEN
	
		IF NOT EXISTS (SELECT 1 
					   FROM slr.authors aut 
					   WHERE lower(aut.names) = lower(author_name ) )
		THEN
		
			INSERT INTO slr.authors(names,home_page,department_id) VALUES(author_name,home_page, depto_id);
			select  max(id) ::integer into author_id 
			from slr.authors;
			
		ELSE  
			select aut.id::integer into author_id 
			from slr.authors  aut
			where lower(aut.names) = lower(author_name);
		END IF;
		
		
	END IF;

END;$$;


--
-- TOC entry 284 (class 1255 OID 20741)
-- Name: slr_author_publication_iud(character varying, integer, integer, character varying, integer, integer); Type: FUNCTION; Schema: slr; Owner: -
--

CREATE FUNCTION slr.slr_author_publication_iud(accion character varying, author_id integer, publication_id integer, publication_type character varying, limite integer, herarchy integer, OUT res character varying) RETURNS character varying
    LANGUAGE plpgsql
    AS $$declare
pub record;
auth record;
public_id integer;
auth_id integer; 
depto_id integer;
query_sql text := 'SELECT *
				   FROM slr.dblp_publication p
				   WHERE p.doc_type = '''|| publication_type || 
				   '''  AND p.updated_state = ''1.inserted''  ';
aux record;
begin 
	--@params : accion , publication_type
	 if accion = 'AUTHPUB_INS'then
	 	select dep.id into depto_id  
		from slr.departments dep  where dep.id = 0; 
		
		if limite is not null AND limite > 0 then
			query_sql = query_sql||' LIMIT '||limite||';' ;
		end if;
		
		
		for pub IN EXECUTE query_sql
		loop
			public_id = slr.slr_publication_iud('PUB_INS'::text,null,pub.title,pub.pages,
						pub.year::integer,pub.address,pub.journal,pub.volume,
						pub.number,pub.month,pub.url,pub.ee,pub.cite,pub.publisher,
						pub.note,pub.crossref,pub.isbn,pub.series,pub,chapter,
						pub.publnr,pub.mdate::date,pub.key_dblp,pub.doc_type);
						
			for auth in (select * from json_each_text(pub.authors) )
			loop
			
				if(auth.key is not null AND char_length(auth.key) > 0) then
					auth_id = slr.slr_author_iud('AUTH_INS',null,
												 'DEFAULT HOME PAGE',
												 auth.value,depto_id);
				else
					select a.id into auth_id 
					from slr.authors aut 
					where aut.id = 0; 
				end if;
				--insert author_publications
				--raise notice ' autor %, public %',id_author,id_publication;
				INSERT INTO slr.author_publications(herarchy,author_id,publication_id) 
				VALUES(auth.key::INTEGER,auth_id,public_id);
				
			end loop;
			--update row in public.dblp_publications
			-- 1.inserted -> 2.process
			update srl.dblp_publication
			set	updated_state = '2.process'
			where id = pub.id;
			
		end loop;
	 end if;
	 res = 'success';
end;$$;


--
-- TOC entry 282 (class 1255 OID 20725)
-- Name: slr_default_values(); Type: FUNCTION; Schema: slr; Owner: -
--

CREATE FUNCTION slr.slr_default_values() RETURNS character varying
    LANGUAGE plpgsql
    AS $$declare 
res character;
max_value integer;
statements CURSOR FOR
	    SELECT tablename FROM pg_tables p
        WHERE p.tableowner = 'postgres' AND p.schemaname = 'slr'
		AND p.tablename IN (
			'countries', 'institutions','departments',
			'authors','keywords','publishers',
			'conferences','editions','journals','volume_numbers'
		);

begin 
	--country
	IF NOT EXISTS(SELECT 1 FROM slr.countries WHERE id = 0) THEN
		INSERT INTO slr.countries(id,country_name,code) VALUES(0,'DEFAULT','DEFAULT');
	END IF;
	select id into max_value from slr.country where id = 0;
	
	--institution
	IF NOT EXISTS(SELECT 1 FROM slr.institutions WHERE id = 0) THEN
		INSERT INTO slr.institutions(id,country_id,description) VALUES(0,max_value,'DEFAULT');
	END IF;
	--department
	if not EXISTS(SELECT 1 FROM slr.departments WHERE id = 0) then
		--SELECT id into max_value FROM slr.institution WHERE id = 0;
		INSERT INTO slr.departments(id,description,institution_id) VALUES(0,'DEFAULT',0);
	end if;
	--author
	if not EXISTS(SELECT 1 FROM slr.authors WHERE id = 0) then
		INSERT INTO slr.authors(id,names,department_id,home_page) VALUES(0,'DEFAULT',max_value,'DEFAULT');
	end if;
	--keyword
	if not exists(select 1 from slr.keywords where id = 0) then
		insert into slr.keywords(id,description) values(0,'DEFAULT');
	end if;
	--publisher
	if not exists(select 1 from slr.publishers where id=0) then
		insert into slr.publishers(id,description,state) values(0,'DEFAULT','active');
	end if;
	--conference
	if not EXISTS(select 1 from slr.conferences where id = 0) then
		insert into slr.conferences(id,description,abreviation) values(0,'DEFAULT','DEFAULT');
	end if;
	--edition
	if not exists(select 1 from slr.editions where id = 0)then
		insert into slr.editions(id,editors,year,number,conference_id,publisher_id) values(0,'DEFAULT',0,'0',max_value,max_value);
	end if;
	--journal
	if not exists(select 1 from slr.journals where id = 0) then 
		insert into slr.journals(id,name,abreviation) values(0,'DEFAULT','DEFAULT');
	end if;
	--volume_number
	if not exists(select 1 from slr.volume_numbers where id = 0) then
		insert into slr.volume_numbers(id,journal_id,publisher_id) values(0,max_value,max_value);
	end if;

return 'default values inserted';
end;$$;


--
-- TOC entry 275 (class 1255 OID 20732)
-- Name: slr_publication_iud(character varying, character varying, character varying, character varying, integer, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, date, character varying, character varying); Type: FUNCTION; Schema: slr; Owner: -
--

CREATE FUNCTION slr.slr_publication_iud(action character varying, abstract character varying, title character varying, pages character varying, year integer, address character varying, journal character varying, volume character varying, number character varying, month character varying, url character varying, ee character varying, cite character varying, publisher character varying, note character varying, crossref character varying, isbn character varying, series character varying, chapter character varying, publnr character varying, mdate date, dblp_key character varying, doc_type character varying, OUT publication_id integer) RETURNS integer
    LANGUAGE plpgsql
    AS $$begin
	if action = 'PUB_INS' then
		
		insert into slr.publications
		(abstract,title,pages,year,address,journal,volume,number,
		 month,url,ee,cite,publisher,note,crossref,isbn,series,
		 chapter,publnr,mdate,dblp_key,doctype)
		 values(abstract,title,pages,year,address,journal,volume,number,
		 month,url,ee,cite,publisher,note,crossref,isbn,series,
		 chapter,publnr,mdate,dblp_key,	doc_type);
		
		select MAX(id) into publication_id	from slr.publication;
		 
	end if;
end;$$;


--
-- TOC entry 281 (class 1255 OID 20724)
-- Name: slr_truncate_tables(character varying); Type: FUNCTION; Schema: slr; Owner: -
--

CREATE FUNCTION slr.slr_truncate_tables(username character varying) RETURNS void
    LANGUAGE plpgsql
    AS $$DECLARE
    statements CURSOR FOR
        SELECT tablename FROM pg_tables
        WHERE tableowner = username AND schemaname = 'slr';
BEGIN
    FOR stmt IN statements LOOP
        EXECUTE 'TRUNCATE TABLE ' ||'slr.'||quote_ident(stmt.tablename) || ' CASCADE;';
    END LOOP;
END;
$$;


SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 244 (class 1259 OID 20447)
-- Name: author_publications; Type: TABLE; Schema: slr; Owner: -
--

CREATE TABLE slr.author_publications (
    id bigint,
    herarchy integer,
    author_id bigint,
    publication_id bigint,
    create_at date DEFAULT now()
);


--
-- TOC entry 243 (class 1259 OID 20439)
-- Name: authors; Type: TABLE; Schema: slr; Owner: -
--

CREATE TABLE slr.authors (
    id bigint NOT NULL,
    names text,
    email character varying(200) DEFAULT NULL::character varying,
    picture_file text,
    home_page text,
    department_id integer,
    created_at date DEFAULT now()
);


--
-- TOC entry 242 (class 1259 OID 20430)
-- Name: publications; Type: TABLE; Schema: slr; Owner: -
--

CREATE TABLE slr.publications (
    id bigint NOT NULL,
    abstract text,
    title text,
    pages character varying(100),
    year integer,
    address text,
    journal character varying(200),
    volume character varying(255),
    number character varying(255),
    month character varying(255),
    url text,
    ee text,
    cite text,
    publisher text,
    note text,
    crossref text,
    isbn text,
    series text,
    chapter text,
    publnr text,
    updated_state character varying(150),
    mdate date,
    dblp_key text,
    doc_type character varying(100)
);


--
-- TOC entry 253 (class 1259 OID 20598)
-- Name: book_chapters; Type: TABLE; Schema: slr; Owner: -
--

CREATE TABLE slr.book_chapters (
    book_chapter_id bigint NOT NULL,
    publisher_id integer
)
INHERITS (slr.publications);


--
-- TOC entry 252 (class 1259 OID 20591)
-- Name: books; Type: TABLE; Schema: slr; Owner: -
--

CREATE TABLE slr.books (
    book_id bigint NOT NULL,
    publisher_id integer
)
INHERITS (slr.publications);


--
-- TOC entry 255 (class 1259 OID 20612)
-- Name: conferece_editorials; Type: TABLE; Schema: slr; Owner: -
--

CREATE TABLE slr.conferece_editorials (
    conference_editorial_id integer NOT NULL,
    edition_id integer
)
INHERITS (slr.publications);


--
-- TOC entry 254 (class 1259 OID 20605)
-- Name: conferece_papers; Type: TABLE; Schema: slr; Owner: -
--

CREATE TABLE slr.conferece_papers (
    conference_paper_id bigint NOT NULL,
    edition_id integer
)
INHERITS (slr.publications);


--
-- TOC entry 249 (class 1259 OID 20486)
-- Name: conferences; Type: TABLE; Schema: slr; Owner: -
--

CREATE TABLE slr.conferences (
    id bigint NOT NULL,
    description text,
    abreviation character varying(200),
    created_at date DEFAULT now()
);


--
-- TOC entry 245 (class 1259 OID 20450)
-- Name: countries; Type: TABLE; Schema: slr; Owner: -
--

CREATE TABLE slr.countries (
    id bigint NOT NULL,
    country_name character varying(255),
    code character varying(5),
    created_at date DEFAULT now()
);


--
-- TOC entry 240 (class 1259 OID 20034)
-- Name: dblp_publication; Type: TABLE; Schema: slr; Owner: -
--

CREATE TABLE slr.dblp_publication (
    id integer DEFAULT nextval('public.dblp_publication_id_seq'::regclass) NOT NULL,
    key_dblp character varying(200),
    authors json,
    doc_type character varying(100),
    editor character varying(100),
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
    mdate character varying(100),
    reg_date date DEFAULT now(),
    updated_state character varying(100) DEFAULT '1.inserted'::character varying
);


--
-- TOC entry 251 (class 1259 OID 20535)
-- Name: departments; Type: TABLE; Schema: slr; Owner: -
--

CREATE TABLE slr.departments (
    id bigint NOT NULL,
    description text,
    "position" character varying(255),
    skills text,
    institution_id integer,
    created_at date DEFAULT now()
);


--
-- TOC entry 258 (class 1259 OID 20647)
-- Name: editions; Type: TABLE; Schema: slr; Owner: -
--

CREATE TABLE slr.editions (
    id bigint NOT NULL,
    editors text,
    year integer,
    number character varying(100),
    conference_id integer,
    publisher_id integer,
    created_at date DEFAULT now()
);


--
-- TOC entry 246 (class 1259 OID 20456)
-- Name: institutions; Type: TABLE; Schema: slr; Owner: -
--

CREATE TABLE slr.institutions (
    id bigint NOT NULL,
    description text,
    country_id integer,
    created_at date DEFAULT now()
);


--
-- TOC entry 256 (class 1259 OID 20619)
-- Name: journal_editorials; Type: TABLE; Schema: slr; Owner: -
--

CREATE TABLE slr.journal_editorials (
    journal_editorial_id bigint NOT NULL,
    volume_number_id integer
)
INHERITS (slr.publications);


--
-- TOC entry 257 (class 1259 OID 20626)
-- Name: journal_papers; Type: TABLE; Schema: slr; Owner: -
--

CREATE TABLE slr.journal_papers (
    journal_paper_id bigint NOT NULL,
    volume_number_id integer
)
INHERITS (slr.publications);


--
-- TOC entry 260 (class 1259 OID 20692)
-- Name: journals; Type: TABLE; Schema: slr; Owner: -
--

CREATE TABLE slr.journals (
    id bigint NOT NULL,
    name text,
    abreviation character varying(255),
    created_at date DEFAULT now()
);


--
-- TOC entry 247 (class 1259 OID 20474)
-- Name: keywords; Type: TABLE; Schema: slr; Owner: -
--

CREATE TABLE slr.keywords (
    id bigint NOT NULL,
    decription character varying(255),
    created_at date DEFAULT now()
);


--
-- TOC entry 248 (class 1259 OID 20480)
-- Name: publication_keywords; Type: TABLE; Schema: slr; Owner: -
--

CREATE TABLE slr.publication_keywords (
    id bigint NOT NULL,
    keyword_id integer,
    publication_id integer,
    created_at date DEFAULT now()
);


--
-- TOC entry 250 (class 1259 OID 20495)
-- Name: publishers; Type: TABLE; Schema: slr; Owner: -
--

CREATE TABLE slr.publishers (
    id bigint NOT NULL,
    description text,
    state character varying(200) DEFAULT 'active'::character varying,
    created_at date DEFAULT now()
);


--
-- TOC entry 259 (class 1259 OID 20679)
-- Name: volume_numbers; Type: TABLE; Schema: slr; Owner: -
--

CREATE TABLE slr.volume_numbers (
    id bigint NOT NULL,
    volume integer,
    number integer,
    pages text,
    publisher_id integer,
    journal_id integer,
    created_at date DEFAULT now()
);


--
-- TOC entry 3279 (class 2606 OID 20446)
-- Name: authors authors_pkey; Type: CONSTRAINT; Schema: slr; Owner: -
--

ALTER TABLE ONLY slr.authors
    ADD CONSTRAINT authors_pkey PRIMARY KEY (id);


--
-- TOC entry 3297 (class 2606 OID 20634)
-- Name: book_chapters book_chapters_pkey; Type: CONSTRAINT; Schema: slr; Owner: -
--

ALTER TABLE ONLY slr.book_chapters
    ADD CONSTRAINT book_chapters_pkey PRIMARY KEY (book_chapter_id);


--
-- TOC entry 3295 (class 2606 OID 20641)
-- Name: books books_pkey; Type: CONSTRAINT; Schema: slr; Owner: -
--

ALTER TABLE ONLY slr.books
    ADD CONSTRAINT books_pkey PRIMARY KEY (book_id);


--
-- TOC entry 3301 (class 2606 OID 20666)
-- Name: conferece_editorials conferece_editorials_pkey; Type: CONSTRAINT; Schema: slr; Owner: -
--

ALTER TABLE ONLY slr.conferece_editorials
    ADD CONSTRAINT conferece_editorials_pkey PRIMARY KEY (conference_editorial_id);


--
-- TOC entry 3299 (class 2606 OID 20673)
-- Name: conferece_papers conferece_papers_pkey; Type: CONSTRAINT; Schema: slr; Owner: -
--

ALTER TABLE ONLY slr.conferece_papers
    ADD CONSTRAINT conferece_papers_pkey PRIMARY KEY (conference_paper_id);


--
-- TOC entry 3289 (class 2606 OID 20494)
-- Name: conferences conferences_pkey; Type: CONSTRAINT; Schema: slr; Owner: -
--

ALTER TABLE ONLY slr.conferences
    ADD CONSTRAINT conferences_pkey PRIMARY KEY (id);


--
-- TOC entry 3281 (class 2606 OID 20455)
-- Name: countries countries_pkey; Type: CONSTRAINT; Schema: slr; Owner: -
--

ALTER TABLE ONLY slr.countries
    ADD CONSTRAINT countries_pkey PRIMARY KEY (id);


--
-- TOC entry 3275 (class 2606 OID 20042)
-- Name: dblp_publication dblp_publication_pkey; Type: CONSTRAINT; Schema: slr; Owner: -
--

ALTER TABLE ONLY slr.dblp_publication
    ADD CONSTRAINT dblp_publication_pkey PRIMARY KEY (id);


--
-- TOC entry 3293 (class 2606 OID 20543)
-- Name: departments departments_pkey; Type: CONSTRAINT; Schema: slr; Owner: -
--

ALTER TABLE ONLY slr.departments
    ADD CONSTRAINT departments_pkey PRIMARY KEY (id);


--
-- TOC entry 3307 (class 2606 OID 20654)
-- Name: editions editions_pkey; Type: CONSTRAINT; Schema: slr; Owner: -
--

ALTER TABLE ONLY slr.editions
    ADD CONSTRAINT editions_pkey PRIMARY KEY (id);


--
-- TOC entry 3283 (class 2606 OID 20464)
-- Name: institutions institutions_pkey; Type: CONSTRAINT; Schema: slr; Owner: -
--

ALTER TABLE ONLY slr.institutions
    ADD CONSTRAINT institutions_pkey PRIMARY KEY (id);


--
-- TOC entry 3303 (class 2606 OID 20715)
-- Name: journal_editorials journal_editorials_pkey; Type: CONSTRAINT; Schema: slr; Owner: -
--

ALTER TABLE ONLY slr.journal_editorials
    ADD CONSTRAINT journal_editorials_pkey PRIMARY KEY (journal_editorial_id);


--
-- TOC entry 3305 (class 2606 OID 20708)
-- Name: journal_papers journal_papers_pkey; Type: CONSTRAINT; Schema: slr; Owner: -
--

ALTER TABLE ONLY slr.journal_papers
    ADD CONSTRAINT journal_papers_pkey PRIMARY KEY (journal_paper_id);


--
-- TOC entry 3311 (class 2606 OID 20701)
-- Name: journals journals_pkey; Type: CONSTRAINT; Schema: slr; Owner: -
--

ALTER TABLE ONLY slr.journals
    ADD CONSTRAINT journals_pkey PRIMARY KEY (id);


--
-- TOC entry 3285 (class 2606 OID 20479)
-- Name: keywords keywords_pkey; Type: CONSTRAINT; Schema: slr; Owner: -
--

ALTER TABLE ONLY slr.keywords
    ADD CONSTRAINT keywords_pkey PRIMARY KEY (id);


--
-- TOC entry 3287 (class 2606 OID 20485)
-- Name: publication_keywords publication_keywords_pkey; Type: CONSTRAINT; Schema: slr; Owner: -
--

ALTER TABLE ONLY slr.publication_keywords
    ADD CONSTRAINT publication_keywords_pkey PRIMARY KEY (id);


--
-- TOC entry 3277 (class 2606 OID 20438)
-- Name: publications publication_pkey; Type: CONSTRAINT; Schema: slr; Owner: -
--

ALTER TABLE ONLY slr.publications
    ADD CONSTRAINT publication_pkey PRIMARY KEY (id);


--
-- TOC entry 3291 (class 2606 OID 20503)
-- Name: publishers publishers_pkey; Type: CONSTRAINT; Schema: slr; Owner: -
--

ALTER TABLE ONLY slr.publishers
    ADD CONSTRAINT publishers_pkey PRIMARY KEY (id);


--
-- TOC entry 3309 (class 2606 OID 20686)
-- Name: volume_numbers volume_numbers_pkey; Type: CONSTRAINT; Schema: slr; Owner: -
--

ALTER TABLE ONLY slr.volume_numbers
    ADD CONSTRAINT volume_numbers_pkey PRIMARY KEY (id);


--
-- TOC entry 3312 (class 2606 OID 20557)
-- Name: authors fk_author__department; Type: FK CONSTRAINT; Schema: slr; Owner: -
--

ALTER TABLE ONLY slr.authors
    ADD CONSTRAINT fk_author__department FOREIGN KEY (department_id) REFERENCES slr.departments(id);


--
-- TOC entry 3314 (class 2606 OID 20562)
-- Name: author_publications fk_author_publication__author; Type: FK CONSTRAINT; Schema: slr; Owner: -
--

ALTER TABLE ONLY slr.author_publications
    ADD CONSTRAINT fk_author_publication__author FOREIGN KEY (author_id) REFERENCES slr.authors(id);


--
-- TOC entry 3313 (class 2606 OID 20567)
-- Name: author_publications fk_author_publication__publication; Type: FK CONSTRAINT; Schema: slr; Owner: -
--

ALTER TABLE ONLY slr.author_publications
    ADD CONSTRAINT fk_author_publication__publication FOREIGN KEY (publication_id) REFERENCES slr.publications(id);


--
-- TOC entry 3319 (class 2606 OID 20642)
-- Name: books fk_book__publisher; Type: FK CONSTRAINT; Schema: slr; Owner: -
--

ALTER TABLE ONLY slr.books
    ADD CONSTRAINT fk_book__publisher FOREIGN KEY (publisher_id) REFERENCES slr.publishers(id);


--
-- TOC entry 3320 (class 2606 OID 20635)
-- Name: book_chapters fk_book_chapter__publisher; Type: FK CONSTRAINT; Schema: slr; Owner: -
--

ALTER TABLE ONLY slr.book_chapters
    ADD CONSTRAINT fk_book_chapter__publisher FOREIGN KEY (publisher_id) REFERENCES slr.publishers(id);


--
-- TOC entry 3321 (class 2606 OID 20674)
-- Name: conferece_papers fk_conferece_paper__edition; Type: FK CONSTRAINT; Schema: slr; Owner: -
--

ALTER TABLE ONLY slr.conferece_papers
    ADD CONSTRAINT fk_conferece_paper__edition FOREIGN KEY (edition_id) REFERENCES slr.editions(id);


--
-- TOC entry 3322 (class 2606 OID 20667)
-- Name: conferece_editorials fk_conference_editorial__edition; Type: FK CONSTRAINT; Schema: slr; Owner: -
--

ALTER TABLE ONLY slr.conferece_editorials
    ADD CONSTRAINT fk_conference_editorial__edition FOREIGN KEY (edition_id) REFERENCES slr.editions(id);


--
-- TOC entry 3318 (class 2606 OID 20550)
-- Name: departments fk_department__institution; Type: FK CONSTRAINT; Schema: slr; Owner: -
--

ALTER TABLE ONLY slr.departments
    ADD CONSTRAINT fk_department__institution FOREIGN KEY (institution_id) REFERENCES slr.institutions(id);


--
-- TOC entry 3325 (class 2606 OID 20660)
-- Name: editions fk_edition__conference; Type: FK CONSTRAINT; Schema: slr; Owner: -
--

ALTER TABLE ONLY slr.editions
    ADD CONSTRAINT fk_edition__conference FOREIGN KEY (conference_id) REFERENCES slr.conferences(id);


--
-- TOC entry 3326 (class 2606 OID 20655)
-- Name: editions fk_edition__publisher; Type: FK CONSTRAINT; Schema: slr; Owner: -
--

ALTER TABLE ONLY slr.editions
    ADD CONSTRAINT fk_edition__publisher FOREIGN KEY (publisher_id) REFERENCES slr.publishers(id);


--
-- TOC entry 3315 (class 2606 OID 20545)
-- Name: institutions fk_institution__country; Type: FK CONSTRAINT; Schema: slr; Owner: -
--

ALTER TABLE ONLY slr.institutions
    ADD CONSTRAINT fk_institution__country FOREIGN KEY (country_id) REFERENCES slr.countries(id);


--
-- TOC entry 3323 (class 2606 OID 20716)
-- Name: journal_editorials fk_journal_editorial__volume_number; Type: FK CONSTRAINT; Schema: slr; Owner: -
--

ALTER TABLE ONLY slr.journal_editorials
    ADD CONSTRAINT fk_journal_editorial__volume_number FOREIGN KEY (volume_number_id) REFERENCES slr.volume_numbers(id);


--
-- TOC entry 3324 (class 2606 OID 20709)
-- Name: journal_papers fk_journal_paper__volume_number; Type: FK CONSTRAINT; Schema: slr; Owner: -
--

ALTER TABLE ONLY slr.journal_papers
    ADD CONSTRAINT fk_journal_paper__volume_number FOREIGN KEY (volume_number_id) REFERENCES slr.volume_numbers(id);


--
-- TOC entry 3317 (class 2606 OID 20577)
-- Name: publication_keywords fk_publcation_keywords__keyword; Type: FK CONSTRAINT; Schema: slr; Owner: -
--

ALTER TABLE ONLY slr.publication_keywords
    ADD CONSTRAINT fk_publcation_keywords__keyword FOREIGN KEY (keyword_id) REFERENCES slr.keywords(id);


--
-- TOC entry 3316 (class 2606 OID 20572)
-- Name: publication_keywords fk_publication_keywords__publication; Type: FK CONSTRAINT; Schema: slr; Owner: -
--

ALTER TABLE ONLY slr.publication_keywords
    ADD CONSTRAINT fk_publication_keywords__publication FOREIGN KEY (publication_id) REFERENCES slr.publications(id);


--
-- TOC entry 3328 (class 2606 OID 20702)
-- Name: volume_numbers fk_volume_number__journal; Type: FK CONSTRAINT; Schema: slr; Owner: -
--

ALTER TABLE ONLY slr.volume_numbers
    ADD CONSTRAINT fk_volume_number__journal FOREIGN KEY (journal_id) REFERENCES slr.journals(id);


--
-- TOC entry 3327 (class 2606 OID 20687)
-- Name: volume_numbers fk_volume_number__publisher; Type: FK CONSTRAINT; Schema: slr; Owner: -
--

ALTER TABLE ONLY slr.volume_numbers
    ADD CONSTRAINT fk_volume_number__publisher FOREIGN KEY (publisher_id) REFERENCES slr.publishers(id);


-- Completed on 2019-12-20 18:06:54 -03

--
-- PostgreSQL database dump complete
--

