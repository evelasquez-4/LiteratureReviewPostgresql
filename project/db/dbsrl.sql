--
-- PostgreSQL database dump
--

-- Dumped from database version 11.6
-- Dumped by pg_dump version 11.5

-- Started on 2019-12-20 17:47:40 -03

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
-- TOC entry 3556 (class 1262 OID 16386)
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
-- TOC entry 3557 (class 0 OID 0)
-- Dependencies: 8
-- Name: SCHEMA slr; Type: COMMENT; Schema: -; Owner: -
--

COMMENT ON SCHEMA slr IS 'Revision de la Bibliografica o de la literatura';


--
-- TOC entry 6 (class 2615 OID 16387)
-- Name: slr_v1; Type: SCHEMA; Schema: -; Owner: -
--

CREATE SCHEMA slr_v1;


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
-- TOC entry 3558 (class 0 OID 0)
-- Dependencies: 282
-- Name: FUNCTION slr_default_values(); Type: COMMENT; Schema: slr; Owner: -
--

COMMENT ON FUNCTION slr.slr_default_values() IS 'funcion para añadir valores por defecto en la tablas:
country, institution,deparment,author,keyword,publisher,
conference,edition,journal,volume_number';


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


--
-- TOC entry 278 (class 1255 OID 20199)
-- Name: slr_author_iud(character varying, integer, character varying, character varying, integer); Type: FUNCTION; Schema: slr_v1; Owner: -
--

CREATE FUNCTION slr_v1.slr_author_iud(procedimiento character varying, auth_id integer, home_page character varying, author_name character varying, depto_id integer, OUT author_id integer) RETURNS integer
    LANGUAGE plpgsql
    AS $$BEGIN
	IF procedimiento = 'AUTH_INS' THEN
	
		IF NOT EXISTS (SELECT 1 
					   FROM slr.author aut 
					   WHERE lower(aut.names) = lower(author_name ) )
		THEN
		
			INSERT INTO slr.author(names,home_page,department_id) VALUES(author_name,home_page, depto_id);
			select  max(id) ::integer into author_id 
			from slr.author;
			
		ELSE  
			select aut.id::integer into author_id 
			from slr.author  aut
			where lower(aut.names) = lower(author_name);
		END IF;
		
		
	END IF;

END;$$;


--
-- TOC entry 280 (class 1255 OID 20234)
-- Name: slr_author_publication_iud(character varying, integer, integer, character varying, integer); Type: FUNCTION; Schema: slr_v1; Owner: -
--

CREATE FUNCTION slr_v1.slr_author_publication_iud(accion character varying, author_id integer, publication_id integer, publication_type character varying, herarchy integer, OUT res character varying) RETURNS character varying
    LANGUAGE plpgsql
    AS $$declare
pub record;
auth record;
public_id integer;
auth_id integer; 
depto_id integer;
begin 
	--@params : accion , publication_type
	 if accion = 'AUTHPUB_INS'then
	 	select dep.id into depto_id 
		from slr.department dep 
		where dep.id = 0; 
		
		for pub IN ( select *
					 from public.dblp_publication p
				     where p.doc_type = publication_type 
						AND p.updated_state = '1.inserted' limit 5000)
		loop
			public_id = slr.slr_publication_iud('PUB_INS'::text,null,pub.crossref,
						null,pub.ee,pub.key_dblp,pub.url,pub.mdate::date,
						pub.month,pub.note,pub.doc_type,pub.series,pub.title,
						pub.volume,pub.year::integer);
						
			for auth in (select * from json_each_text(pub.authors) )
			loop
			
				if(auth.key is not null AND char_length(auth.key) > 0) then
					auth_id = slr.slr_author_iud('AUTH_INS',null,'DEFAULT HOME PAGE',auth.value,depto_id);
				else
					select a.id into auth_id 
					from slr.author aut 
					where aut.id = 0; 
				end if;
				--insert author_publications
				--raise notice ' autor %, public %',id_author,id_publication;
				INSERT INTO slr.author_publications(create_at,herarchy,author_id,publication_id) 
				VALUES(now(),auth.key::INTEGER,auth_id,public_id);
				
			end loop;
			--update row in public.dblp_publications
			-- 1.inserted -> 2.process
			update public.dblp_publication
			set	updated_state = '2.process'
			where id = pub.id;
		end loop;
	 
	 end if;
	 res = 'success';
end;$$;


--
-- TOC entry 273 (class 1255 OID 16405)
-- Name: slr_data_process_upd(character varying); Type: FUNCTION; Schema: slr_v1; Owner: -
--

CREATE FUNCTION slr_v1.slr_data_process_upd(proceso character varying) RETURNS character varying
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


--
-- TOC entry 274 (class 1255 OID 17366)
-- Name: slr_default_values(); Type: FUNCTION; Schema: slr_v1; Owner: -
--

CREATE FUNCTION slr_v1.slr_default_values() RETURNS character varying
    LANGUAGE plpgsql
    AS $$declare 
res character;
max_value integer;
statements CURSOR FOR
	    SELECT tablename FROM pg_tables p
        WHERE p.tableowner = 'postgres' AND p.schemaname = 'slr'
		AND p.tablename IN (
			'country', 'institution','department',
			'author','keyword','publisher',
			'conference','edition','journal','volume_number'
		);

begin 
	--country
	IF NOT EXISTS(SELECT 1 FROM slr.country WHERE id = 0) THEN
		INSERT INTO slr.country(id,description) VALUES(0,'DEFAULT');
	END IF;
	select id into max_value from slr.country where id = 0;
	
	--institution
	IF NOT EXISTS(SELECT 1 FROM slr.institution WHERE id = 0) THEN
		INSERT INTO slr.institution(id,country_id,description) VALUES(0,max_value,'DEFAULT');
	END IF;
	--department
	if not EXISTS(SELECT 1 FROM slr.department WHERE id = 0) then
		--SELECT id into max_value FROM slr.institution WHERE id = 0;
		INSERT INTO slr.department(id,description,institution_id) VALUES(0,'DEFAULT',0);
	end if;
	--author
	if not EXISTS(SELECT 1 FROM slr.author WHERE id = 0) then
		INSERT INTO slr.author(id,names,department_id,home_page) VALUES(0,'DEFAULT',max_value,'DEFAULT');
	end if;
	--keyword
	if not exists(select 1 from slr.keyword where id = 0) then
		insert into slr.keyword(id,name) values(0,'DEFAULT');
	end if;
	--publisher
	if not exists(select 1 from slr.publisher where id=0) then
		insert into slr.publisher(id,description,state) values(0,'DEFAULT','active');
	end if;
	--conference
	if not EXISTS(select 1 from slr.conference where id = 0) then
		insert into slr.conference(id,description) values(0,'DEFAULT');
	end if;
	--edition
	if not exists(select 1 from slr.edition where id = 0)then
		insert into slr.edition(id,description,conference_id,publisher_id) values(0,'DEFAULT',max_value,max_value);
	end if;
	--journal
	if not exists(select 1 from slr.journal where id = 0) then 
		insert into slr.journal(id,description) values(0,'DEFAULT');
	end if;
	--volume_number
	if not exists(select 1 from slr.volume_number where id = 0) then
		insert into slr.volume_number(id,journal_id,publisher_id) values(0,max_value,max_value);
	end if;


return 'default values inserted';
end;$$;


--
-- TOC entry 3559 (class 0 OID 0)
-- Dependencies: 274
-- Name: FUNCTION slr_default_values(); Type: COMMENT; Schema: slr_v1; Owner: -
--

COMMENT ON FUNCTION slr_v1.slr_default_values() IS 'funcion para añadir valores por defecto en la tablas:
country, institution,deparment,author,keyword,publisher,
conference,edition,journal,volume_number';


--
-- TOC entry 279 (class 1255 OID 20428)
-- Name: slr_publication_iud(character varying, character varying, character varying, character varying, character varying, character varying, character varying, date, character varying, character varying, character varying, character varying, character varying, character varying, integer, character varying); Type: FUNCTION; Schema: slr_v1; Owner: -
--

CREATE FUNCTION slr_v1.slr_publication_iud(action character varying, abstract character varying, crossref character varying, doi character varying, ee character varying, key_dblp character varying, local_url character varying, modified_date date, pub_month character varying, note character varying, pub_type character varying, series character varying, title character varying, volume character varying, pub_year integer, pages character varying, OUT publication_id integer) RETURNS integer
    LANGUAGE plpgsql
    AS $$begin
	if action = 'PUB_INS' then
		
		insert into slr.publication
		(abstract_text,crossref,doi,ee,key_dblp,local_url,modified_date,month,
		note,publication_type,series,title,volume,year,updated_state,pages)
		values(abstract,crossref,doi,ee,key_dblp,local_url,modified_date,pub_month,
		note,pub_type,series,title,volume,pub_year,'1.inserted');
		
		select MAX(id) into publication_id	from slr.publication;
		 
	end if;
end;$$;


--
-- TOC entry 276 (class 1255 OID 16840)
-- Name: slr_publisher_iud(character varying, integer, character varying); Type: FUNCTION; Schema: slr_v1; Owner: -
--

CREATE FUNCTION slr_v1.slr_publisher_iud(accion character varying, publisher_id integer, publisher_description character varying, OUT p_id integer, OUT p_description character varying) RETURNS record
    LANGUAGE plpgsql
    AS $$DECLARE
BEGIN

	IF accion = 'INS_BOOK' 
	THEN	
		IF NOT EXISTS( SELECT 1  
				  FROM slr.publisher pb 
				  WHERE LOWER(pb.description) =  LOWER(publisher_description)
					  AND pb.state = 'activo' )
		THEN 
			INSERT INTO slr.publisher (description) values(publisher_description);
		
			SELECT pb.id,pb.description INTO p_id,p_description
			FROM slr.publisher pb
			WHERE pb.id = (select max(p.id) from slr.publisher p)
				 AND pb.state = 'activo';
		
		ELSE
			SELECT pb.id,pb.description INTO p_id,p_description
			FROM slr.publisher pb
			WHERE LOWER(pb.description) =  publisher_description
			AND pb.state = 'activo'
			ORDER BY pb.created_at DESC
			LIMIT 1;
		END IF;
	ELSIF (accion = 'PUB_BYID') 
	THEN
		BEGIN
			IF NOT EXISTS (SELECT 1 FROM slr.publisher pu WHERE pu.id = 0)
			THEN
				INSERT INTO slr.publisher(id,description,state) 
				VALUES (publisher_id,'DEFAULT PUBLISHER','activo');
			END IF;
			
			select into p_id
			from slr.publisher pu
			where pu.id = publisher_id AND pu.state = 'activo';
		END;
	END IF;

END;$$;


--
-- TOC entry 277 (class 1255 OID 17215)
-- Name: slr_truncate_tables(character varying); Type: FUNCTION; Schema: slr_v1; Owner: -
--

CREATE FUNCTION slr_v1.slr_truncate_tables(username character varying) RETURNS void
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


--
-- TOC entry 198 (class 1259 OID 16480)
-- Name: dblp_document_id_seq; Type: SEQUENCE; Schema: slr_v1; Owner: -
--

CREATE SEQUENCE slr_v1.dblp_document_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 199 (class 1259 OID 16482)
-- Name: dblp_document; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.dblp_document (
    id integer DEFAULT nextval('slr_v1.dblp_document_id_seq'::regclass) NOT NULL,
    key_dblp character varying(200),
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


--
-- TOC entry 241 (class 1259 OID 20043)
-- Name: dblp_publication_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.dblp_publication_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 200 (class 1259 OID 16552)
-- Name: test; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.test (
    id integer NOT NULL,
    cadena json,
    numero integer
);


--
-- TOC entry 201 (class 1259 OID 16558)
-- Name: test_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.test_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3560 (class 0 OID 0)
-- Dependencies: 201
-- Name: test_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.test_id_seq OWNED BY public.test.id;


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
-- TOC entry 203 (class 1259 OID 19510)
-- Name: author; Type: TABLE; Schema: slr_v1; Owner: -
--

CREATE TABLE slr_v1.author (
    id bigint NOT NULL,
    created_at date,
    home_page character varying(200),
    names character varying(255),
    department_id bigint
);


--
-- TOC entry 202 (class 1259 OID 19508)
-- Name: author_id_seq; Type: SEQUENCE; Schema: slr_v1; Owner: -
--

CREATE SEQUENCE slr_v1.author_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3561 (class 0 OID 0)
-- Dependencies: 202
-- Name: author_id_seq; Type: SEQUENCE OWNED BY; Schema: slr_v1; Owner: -
--

ALTER SEQUENCE slr_v1.author_id_seq OWNED BY slr_v1.author.id;


--
-- TOC entry 205 (class 1259 OID 19518)
-- Name: author_publications; Type: TABLE; Schema: slr_v1; Owner: -
--

CREATE TABLE slr_v1.author_publications (
    id bigint NOT NULL,
    create_at timestamp without time zone,
    herarchy integer,
    author_id bigint,
    publication_id bigint
);


--
-- TOC entry 204 (class 1259 OID 19516)
-- Name: author_publications_id_seq; Type: SEQUENCE; Schema: slr_v1; Owner: -
--

CREATE SEQUENCE slr_v1.author_publications_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3562 (class 0 OID 0)
-- Dependencies: 204
-- Name: author_publications_id_seq; Type: SEQUENCE OWNED BY; Schema: slr_v1; Owner: -
--

ALTER SEQUENCE slr_v1.author_publications_id_seq OWNED BY slr_v1.author_publications.id;


--
-- TOC entry 207 (class 1259 OID 19526)
-- Name: book; Type: TABLE; Schema: slr_v1; Owner: -
--

CREATE TABLE slr_v1.book (
    book_id bigint NOT NULL,
    abstract character varying(255),
    created_at timestamp without time zone,
    crossref character varying(255),
    doi character varying(100),
    ee character varying(255),
    id bigint NOT NULL,
    isbn character varying(100),
    key_dblp character varying(100),
    local_url character varying(255),
    modified_date date,
    month character varying(80),
    note character varying(255),
    publication_type character varying(100),
    series character varying(255),
    title character varying(255),
    updated_state character varying(250),
    volume character varying(255),
    year integer,
    publisher_id bigint
);


--
-- TOC entry 206 (class 1259 OID 19524)
-- Name: book_book_id_seq; Type: SEQUENCE; Schema: slr_v1; Owner: -
--

CREATE SEQUENCE slr_v1.book_book_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3563 (class 0 OID 0)
-- Dependencies: 206
-- Name: book_book_id_seq; Type: SEQUENCE OWNED BY; Schema: slr_v1; Owner: -
--

ALTER SEQUENCE slr_v1.book_book_id_seq OWNED BY slr_v1.book.book_id;


--
-- TOC entry 209 (class 1259 OID 19537)
-- Name: book_chapter; Type: TABLE; Schema: slr_v1; Owner: -
--

CREATE TABLE slr_v1.book_chapter (
    book_chapter_id bigint NOT NULL,
    abstract character varying(255),
    created_at timestamp without time zone,
    crossref character varying(255),
    doi character varying(100),
    ee character varying(255),
    id bigint NOT NULL,
    isbn character varying(100),
    key_dblp character varying(100),
    local_url character varying(255),
    modified_date date,
    month character varying(80),
    note character varying(255),
    pages character varying(50),
    publication_type character varying(100),
    series character varying(255),
    title character varying(255),
    updated_state character varying(250),
    volume character varying(255),
    year integer,
    publisher_id bigint
);


--
-- TOC entry 208 (class 1259 OID 19535)
-- Name: book_chapter_book_chapter_id_seq; Type: SEQUENCE; Schema: slr_v1; Owner: -
--

CREATE SEQUENCE slr_v1.book_chapter_book_chapter_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3564 (class 0 OID 0)
-- Dependencies: 208
-- Name: book_chapter_book_chapter_id_seq; Type: SEQUENCE OWNED BY; Schema: slr_v1; Owner: -
--

ALTER SEQUENCE slr_v1.book_chapter_book_chapter_id_seq OWNED BY slr_v1.book_chapter.book_chapter_id;


--
-- TOC entry 211 (class 1259 OID 19548)
-- Name: conference; Type: TABLE; Schema: slr_v1; Owner: -
--

CREATE TABLE slr_v1.conference (
    id bigint NOT NULL,
    description character varying(255)
);


--
-- TOC entry 213 (class 1259 OID 19556)
-- Name: conference_editorial; Type: TABLE; Schema: slr_v1; Owner: -
--

CREATE TABLE slr_v1.conference_editorial (
    conference_editorial_id bigint NOT NULL,
    abstract character varying(255),
    created_at timestamp without time zone,
    crossref character varying(255),
    doi character varying(100),
    ee character varying(255),
    id bigint NOT NULL,
    key_dblp character varying(100),
    local_url character varying(255),
    modified_date date,
    month character varying(80),
    note character varying(255),
    publication_type character varying(100),
    series character varying(255),
    title character varying(255),
    updated_state character varying(250),
    volume character varying(255),
    year integer,
    edition_id bigint
);


--
-- TOC entry 212 (class 1259 OID 19554)
-- Name: conference_editorial_conference_editorial_id_seq; Type: SEQUENCE; Schema: slr_v1; Owner: -
--

CREATE SEQUENCE slr_v1.conference_editorial_conference_editorial_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3565 (class 0 OID 0)
-- Dependencies: 212
-- Name: conference_editorial_conference_editorial_id_seq; Type: SEQUENCE OWNED BY; Schema: slr_v1; Owner: -
--

ALTER SEQUENCE slr_v1.conference_editorial_conference_editorial_id_seq OWNED BY slr_v1.conference_editorial.conference_editorial_id;


--
-- TOC entry 210 (class 1259 OID 19546)
-- Name: conference_id_seq; Type: SEQUENCE; Schema: slr_v1; Owner: -
--

CREATE SEQUENCE slr_v1.conference_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3566 (class 0 OID 0)
-- Dependencies: 210
-- Name: conference_id_seq; Type: SEQUENCE OWNED BY; Schema: slr_v1; Owner: -
--

ALTER SEQUENCE slr_v1.conference_id_seq OWNED BY slr_v1.conference.id;


--
-- TOC entry 215 (class 1259 OID 19567)
-- Name: conference_paper; Type: TABLE; Schema: slr_v1; Owner: -
--

CREATE TABLE slr_v1.conference_paper (
    conference_paper_id bigint NOT NULL,
    abstract character varying(255),
    created_at timestamp without time zone,
    crossref character varying(255),
    doi character varying(100),
    ee character varying(255),
    id bigint NOT NULL,
    key_dblp character varying(100),
    local_url character varying(255),
    modified_date date,
    month character varying(80),
    note character varying(255),
    publication_type character varying(100),
    series character varying(255),
    title character varying(255),
    updated_state character varying(250),
    volume character varying(255),
    year integer,
    edition_id bigint
);


--
-- TOC entry 214 (class 1259 OID 19565)
-- Name: conference_paper_conference_paper_id_seq; Type: SEQUENCE; Schema: slr_v1; Owner: -
--

CREATE SEQUENCE slr_v1.conference_paper_conference_paper_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3567 (class 0 OID 0)
-- Dependencies: 214
-- Name: conference_paper_conference_paper_id_seq; Type: SEQUENCE OWNED BY; Schema: slr_v1; Owner: -
--

ALTER SEQUENCE slr_v1.conference_paper_conference_paper_id_seq OWNED BY slr_v1.conference_paper.conference_paper_id;


--
-- TOC entry 217 (class 1259 OID 19578)
-- Name: country; Type: TABLE; Schema: slr_v1; Owner: -
--

CREATE TABLE slr_v1.country (
    id bigint NOT NULL,
    description character varying(255)
);


--
-- TOC entry 216 (class 1259 OID 19576)
-- Name: country_id_seq; Type: SEQUENCE; Schema: slr_v1; Owner: -
--

CREATE SEQUENCE slr_v1.country_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3568 (class 0 OID 0)
-- Dependencies: 216
-- Name: country_id_seq; Type: SEQUENCE OWNED BY; Schema: slr_v1; Owner: -
--

ALTER SEQUENCE slr_v1.country_id_seq OWNED BY slr_v1.country.id;


--
-- TOC entry 219 (class 1259 OID 19586)
-- Name: department; Type: TABLE; Schema: slr_v1; Owner: -
--

CREATE TABLE slr_v1.department (
    id bigint NOT NULL,
    description character varying(255),
    institution_id bigint
);


--
-- TOC entry 218 (class 1259 OID 19584)
-- Name: department_id_seq; Type: SEQUENCE; Schema: slr_v1; Owner: -
--

CREATE SEQUENCE slr_v1.department_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3569 (class 0 OID 0)
-- Dependencies: 218
-- Name: department_id_seq; Type: SEQUENCE OWNED BY; Schema: slr_v1; Owner: -
--

ALTER SEQUENCE slr_v1.department_id_seq OWNED BY slr_v1.department.id;


--
-- TOC entry 221 (class 1259 OID 19594)
-- Name: edition; Type: TABLE; Schema: slr_v1; Owner: -
--

CREATE TABLE slr_v1.edition (
    id bigint NOT NULL,
    description character varying(255),
    conference_id bigint,
    publisher_id bigint
);


--
-- TOC entry 220 (class 1259 OID 19592)
-- Name: edition_id_seq; Type: SEQUENCE; Schema: slr_v1; Owner: -
--

CREATE SEQUENCE slr_v1.edition_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3570 (class 0 OID 0)
-- Dependencies: 220
-- Name: edition_id_seq; Type: SEQUENCE OWNED BY; Schema: slr_v1; Owner: -
--

ALTER SEQUENCE slr_v1.edition_id_seq OWNED BY slr_v1.edition.id;


--
-- TOC entry 223 (class 1259 OID 19602)
-- Name: institution; Type: TABLE; Schema: slr_v1; Owner: -
--

CREATE TABLE slr_v1.institution (
    id bigint NOT NULL,
    description character varying(255),
    country_id bigint
);


--
-- TOC entry 222 (class 1259 OID 19600)
-- Name: institution_id_seq; Type: SEQUENCE; Schema: slr_v1; Owner: -
--

CREATE SEQUENCE slr_v1.institution_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3571 (class 0 OID 0)
-- Dependencies: 222
-- Name: institution_id_seq; Type: SEQUENCE OWNED BY; Schema: slr_v1; Owner: -
--

ALTER SEQUENCE slr_v1.institution_id_seq OWNED BY slr_v1.institution.id;


--
-- TOC entry 225 (class 1259 OID 19610)
-- Name: journal; Type: TABLE; Schema: slr_v1; Owner: -
--

CREATE TABLE slr_v1.journal (
    id bigint NOT NULL,
    description character varying(255)
);


--
-- TOC entry 227 (class 1259 OID 19618)
-- Name: journal_editorial; Type: TABLE; Schema: slr_v1; Owner: -
--

CREATE TABLE slr_v1.journal_editorial (
    journal_editorial_id bigint NOT NULL,
    abstract character varying(255),
    created_at timestamp without time zone,
    crossref character varying(255),
    doi character varying(100),
    ee character varying(255),
    id bigint NOT NULL,
    key_dblp character varying(100),
    local_url character varying(255),
    modified_date date,
    month character varying(80),
    note character varying(255),
    publication_type character varying(100),
    series character varying(255),
    title character varying(255),
    updated_state character varying(250),
    volume character varying(255),
    year integer,
    volume_number_id bigint
);


--
-- TOC entry 226 (class 1259 OID 19616)
-- Name: journal_editorial_journal_editorial_id_seq; Type: SEQUENCE; Schema: slr_v1; Owner: -
--

CREATE SEQUENCE slr_v1.journal_editorial_journal_editorial_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3572 (class 0 OID 0)
-- Dependencies: 226
-- Name: journal_editorial_journal_editorial_id_seq; Type: SEQUENCE OWNED BY; Schema: slr_v1; Owner: -
--

ALTER SEQUENCE slr_v1.journal_editorial_journal_editorial_id_seq OWNED BY slr_v1.journal_editorial.journal_editorial_id;


--
-- TOC entry 224 (class 1259 OID 19608)
-- Name: journal_id_seq; Type: SEQUENCE; Schema: slr_v1; Owner: -
--

CREATE SEQUENCE slr_v1.journal_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3573 (class 0 OID 0)
-- Dependencies: 224
-- Name: journal_id_seq; Type: SEQUENCE OWNED BY; Schema: slr_v1; Owner: -
--

ALTER SEQUENCE slr_v1.journal_id_seq OWNED BY slr_v1.journal.id;


--
-- TOC entry 229 (class 1259 OID 19629)
-- Name: journal_paper; Type: TABLE; Schema: slr_v1; Owner: -
--

CREATE TABLE slr_v1.journal_paper (
    journal_paper_id bigint NOT NULL,
    abstract character varying(255),
    created_at timestamp without time zone,
    crossref character varying(255),
    doi character varying(100),
    ee character varying(255),
    id bigint NOT NULL,
    key_dblp character varying(100),
    local_url character varying(255),
    modified_date date,
    month character varying(80),
    note character varying(255),
    publication_type character varying(100),
    series character varying(255),
    title character varying(255),
    updated_state character varying(250),
    volume character varying(255),
    year integer,
    volume_number_id bigint
);


--
-- TOC entry 228 (class 1259 OID 19627)
-- Name: journal_paper_journal_paper_id_seq; Type: SEQUENCE; Schema: slr_v1; Owner: -
--

CREATE SEQUENCE slr_v1.journal_paper_journal_paper_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3574 (class 0 OID 0)
-- Dependencies: 228
-- Name: journal_paper_journal_paper_id_seq; Type: SEQUENCE OWNED BY; Schema: slr_v1; Owner: -
--

ALTER SEQUENCE slr_v1.journal_paper_journal_paper_id_seq OWNED BY slr_v1.journal_paper.journal_paper_id;


--
-- TOC entry 231 (class 1259 OID 19640)
-- Name: keyword; Type: TABLE; Schema: slr_v1; Owner: -
--

CREATE TABLE slr_v1.keyword (
    id bigint NOT NULL,
    name character varying(255)
);


--
-- TOC entry 230 (class 1259 OID 19638)
-- Name: keyword_id_seq; Type: SEQUENCE; Schema: slr_v1; Owner: -
--

CREATE SEQUENCE slr_v1.keyword_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3575 (class 0 OID 0)
-- Dependencies: 230
-- Name: keyword_id_seq; Type: SEQUENCE OWNED BY; Schema: slr_v1; Owner: -
--

ALTER SEQUENCE slr_v1.keyword_id_seq OWNED BY slr_v1.keyword.id;


--
-- TOC entry 233 (class 1259 OID 19648)
-- Name: publication; Type: TABLE; Schema: slr_v1; Owner: -
--

CREATE TABLE slr_v1.publication (
    id bigint NOT NULL,
    abstract_text text,
    created_at timestamp without time zone DEFAULT now(),
    crossref text,
    doi character varying(255),
    ee text,
    key_dblp character varying(255),
    local_url text,
    modified_date date,
    month character varying(150),
    note text,
    publication_type character varying(200),
    series text,
    title text,
    updated_state character varying(250),
    volume character varying(255),
    year integer,
    pages character varying(100)
);


--
-- TOC entry 232 (class 1259 OID 19646)
-- Name: publication_id_seq; Type: SEQUENCE; Schema: slr_v1; Owner: -
--

CREATE SEQUENCE slr_v1.publication_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3576 (class 0 OID 0)
-- Dependencies: 232
-- Name: publication_id_seq; Type: SEQUENCE OWNED BY; Schema: slr_v1; Owner: -
--

ALTER SEQUENCE slr_v1.publication_id_seq OWNED BY slr_v1.publication.id;


--
-- TOC entry 235 (class 1259 OID 19659)
-- Name: publication_keywords; Type: TABLE; Schema: slr_v1; Owner: -
--

CREATE TABLE slr_v1.publication_keywords (
    id bigint NOT NULL,
    keyword_id bigint,
    publication_id bigint
);


--
-- TOC entry 234 (class 1259 OID 19657)
-- Name: publication_keywords_id_seq; Type: SEQUENCE; Schema: slr_v1; Owner: -
--

CREATE SEQUENCE slr_v1.publication_keywords_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3577 (class 0 OID 0)
-- Dependencies: 234
-- Name: publication_keywords_id_seq; Type: SEQUENCE OWNED BY; Schema: slr_v1; Owner: -
--

ALTER SEQUENCE slr_v1.publication_keywords_id_seq OWNED BY slr_v1.publication_keywords.id;


--
-- TOC entry 237 (class 1259 OID 19667)
-- Name: publisher; Type: TABLE; Schema: slr_v1; Owner: -
--

CREATE TABLE slr_v1.publisher (
    id bigint NOT NULL,
    created_at date,
    description character varying(255),
    state character varying(80)
);


--
-- TOC entry 236 (class 1259 OID 19665)
-- Name: publisher_id_seq; Type: SEQUENCE; Schema: slr_v1; Owner: -
--

CREATE SEQUENCE slr_v1.publisher_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3578 (class 0 OID 0)
-- Dependencies: 236
-- Name: publisher_id_seq; Type: SEQUENCE OWNED BY; Schema: slr_v1; Owner: -
--

ALTER SEQUENCE slr_v1.publisher_id_seq OWNED BY slr_v1.publisher.id;


--
-- TOC entry 239 (class 1259 OID 19675)
-- Name: volume_number; Type: TABLE; Schema: slr_v1; Owner: -
--

CREATE TABLE slr_v1.volume_number (
    id bigint NOT NULL,
    number integer,
    volume integer,
    journal_id bigint,
    publisher_id bigint
);


--
-- TOC entry 238 (class 1259 OID 19673)
-- Name: volume_number_id_seq; Type: SEQUENCE; Schema: slr_v1; Owner: -
--

CREATE SEQUENCE slr_v1.volume_number_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3579 (class 0 OID 0)
-- Dependencies: 238
-- Name: volume_number_id_seq; Type: SEQUENCE OWNED BY; Schema: slr_v1; Owner: -
--

ALTER SEQUENCE slr_v1.volume_number_id_seq OWNED BY slr_v1.volume_number.id;


--
-- TOC entry 3280 (class 2604 OID 16595)
-- Name: test id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.test ALTER COLUMN id SET DEFAULT nextval('public.test_id_seq'::regclass);


--
-- TOC entry 3281 (class 2604 OID 19513)
-- Name: author id; Type: DEFAULT; Schema: slr_v1; Owner: -
--

ALTER TABLE ONLY slr_v1.author ALTER COLUMN id SET DEFAULT nextval('slr_v1.author_id_seq'::regclass);


--
-- TOC entry 3282 (class 2604 OID 19521)
-- Name: author_publications id; Type: DEFAULT; Schema: slr_v1; Owner: -
--

ALTER TABLE ONLY slr_v1.author_publications ALTER COLUMN id SET DEFAULT nextval('slr_v1.author_publications_id_seq'::regclass);


--
-- TOC entry 3283 (class 2604 OID 19529)
-- Name: book book_id; Type: DEFAULT; Schema: slr_v1; Owner: -
--

ALTER TABLE ONLY slr_v1.book ALTER COLUMN book_id SET DEFAULT nextval('slr_v1.book_book_id_seq'::regclass);


--
-- TOC entry 3284 (class 2604 OID 19540)
-- Name: book_chapter book_chapter_id; Type: DEFAULT; Schema: slr_v1; Owner: -
--

ALTER TABLE ONLY slr_v1.book_chapter ALTER COLUMN book_chapter_id SET DEFAULT nextval('slr_v1.book_chapter_book_chapter_id_seq'::regclass);


--
-- TOC entry 3285 (class 2604 OID 19551)
-- Name: conference id; Type: DEFAULT; Schema: slr_v1; Owner: -
--

ALTER TABLE ONLY slr_v1.conference ALTER COLUMN id SET DEFAULT nextval('slr_v1.conference_id_seq'::regclass);


--
-- TOC entry 3286 (class 2604 OID 19559)
-- Name: conference_editorial conference_editorial_id; Type: DEFAULT; Schema: slr_v1; Owner: -
--

ALTER TABLE ONLY slr_v1.conference_editorial ALTER COLUMN conference_editorial_id SET DEFAULT nextval('slr_v1.conference_editorial_conference_editorial_id_seq'::regclass);


--
-- TOC entry 3287 (class 2604 OID 19570)
-- Name: conference_paper conference_paper_id; Type: DEFAULT; Schema: slr_v1; Owner: -
--

ALTER TABLE ONLY slr_v1.conference_paper ALTER COLUMN conference_paper_id SET DEFAULT nextval('slr_v1.conference_paper_conference_paper_id_seq'::regclass);


--
-- TOC entry 3288 (class 2604 OID 19581)
-- Name: country id; Type: DEFAULT; Schema: slr_v1; Owner: -
--

ALTER TABLE ONLY slr_v1.country ALTER COLUMN id SET DEFAULT nextval('slr_v1.country_id_seq'::regclass);


--
-- TOC entry 3289 (class 2604 OID 19589)
-- Name: department id; Type: DEFAULT; Schema: slr_v1; Owner: -
--

ALTER TABLE ONLY slr_v1.department ALTER COLUMN id SET DEFAULT nextval('slr_v1.department_id_seq'::regclass);


--
-- TOC entry 3290 (class 2604 OID 19597)
-- Name: edition id; Type: DEFAULT; Schema: slr_v1; Owner: -
--

ALTER TABLE ONLY slr_v1.edition ALTER COLUMN id SET DEFAULT nextval('slr_v1.edition_id_seq'::regclass);


--
-- TOC entry 3291 (class 2604 OID 19605)
-- Name: institution id; Type: DEFAULT; Schema: slr_v1; Owner: -
--

ALTER TABLE ONLY slr_v1.institution ALTER COLUMN id SET DEFAULT nextval('slr_v1.institution_id_seq'::regclass);


--
-- TOC entry 3292 (class 2604 OID 19613)
-- Name: journal id; Type: DEFAULT; Schema: slr_v1; Owner: -
--

ALTER TABLE ONLY slr_v1.journal ALTER COLUMN id SET DEFAULT nextval('slr_v1.journal_id_seq'::regclass);


--
-- TOC entry 3293 (class 2604 OID 19621)
-- Name: journal_editorial journal_editorial_id; Type: DEFAULT; Schema: slr_v1; Owner: -
--

ALTER TABLE ONLY slr_v1.journal_editorial ALTER COLUMN journal_editorial_id SET DEFAULT nextval('slr_v1.journal_editorial_journal_editorial_id_seq'::regclass);


--
-- TOC entry 3294 (class 2604 OID 19632)
-- Name: journal_paper journal_paper_id; Type: DEFAULT; Schema: slr_v1; Owner: -
--

ALTER TABLE ONLY slr_v1.journal_paper ALTER COLUMN journal_paper_id SET DEFAULT nextval('slr_v1.journal_paper_journal_paper_id_seq'::regclass);


--
-- TOC entry 3295 (class 2604 OID 19643)
-- Name: keyword id; Type: DEFAULT; Schema: slr_v1; Owner: -
--

ALTER TABLE ONLY slr_v1.keyword ALTER COLUMN id SET DEFAULT nextval('slr_v1.keyword_id_seq'::regclass);


--
-- TOC entry 3296 (class 2604 OID 19651)
-- Name: publication id; Type: DEFAULT; Schema: slr_v1; Owner: -
--

ALTER TABLE ONLY slr_v1.publication ALTER COLUMN id SET DEFAULT nextval('slr_v1.publication_id_seq'::regclass);


--
-- TOC entry 3298 (class 2604 OID 19662)
-- Name: publication_keywords id; Type: DEFAULT; Schema: slr_v1; Owner: -
--

ALTER TABLE ONLY slr_v1.publication_keywords ALTER COLUMN id SET DEFAULT nextval('slr_v1.publication_keywords_id_seq'::regclass);


--
-- TOC entry 3299 (class 2604 OID 19670)
-- Name: publisher id; Type: DEFAULT; Schema: slr_v1; Owner: -
--

ALTER TABLE ONLY slr_v1.publisher ALTER COLUMN id SET DEFAULT nextval('slr_v1.publisher_id_seq'::regclass);


--
-- TOC entry 3300 (class 2604 OID 19678)
-- Name: volume_number id; Type: DEFAULT; Schema: slr_v1; Owner: -
--

ALTER TABLE ONLY slr_v1.volume_number ALTER COLUMN id SET DEFAULT nextval('slr_v1.volume_number_id_seq'::regclass);


--
-- TOC entry 3319 (class 2606 OID 16632)
-- Name: test test_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.test
    ADD CONSTRAINT test_pkey PRIMARY KEY (id);


--
-- TOC entry 3363 (class 2606 OID 20446)
-- Name: authors authors_pkey; Type: CONSTRAINT; Schema: slr; Owner: -
--

ALTER TABLE ONLY slr.authors
    ADD CONSTRAINT authors_pkey PRIMARY KEY (id);


--
-- TOC entry 3381 (class 2606 OID 20634)
-- Name: book_chapters book_chapters_pkey; Type: CONSTRAINT; Schema: slr; Owner: -
--

ALTER TABLE ONLY slr.book_chapters
    ADD CONSTRAINT book_chapters_pkey PRIMARY KEY (book_chapter_id);


--
-- TOC entry 3379 (class 2606 OID 20641)
-- Name: books books_pkey; Type: CONSTRAINT; Schema: slr; Owner: -
--

ALTER TABLE ONLY slr.books
    ADD CONSTRAINT books_pkey PRIMARY KEY (book_id);


--
-- TOC entry 3385 (class 2606 OID 20666)
-- Name: conferece_editorials conferece_editorials_pkey; Type: CONSTRAINT; Schema: slr; Owner: -
--

ALTER TABLE ONLY slr.conferece_editorials
    ADD CONSTRAINT conferece_editorials_pkey PRIMARY KEY (conference_editorial_id);


--
-- TOC entry 3383 (class 2606 OID 20673)
-- Name: conferece_papers conferece_papers_pkey; Type: CONSTRAINT; Schema: slr; Owner: -
--

ALTER TABLE ONLY slr.conferece_papers
    ADD CONSTRAINT conferece_papers_pkey PRIMARY KEY (conference_paper_id);


--
-- TOC entry 3373 (class 2606 OID 20494)
-- Name: conferences conferences_pkey; Type: CONSTRAINT; Schema: slr; Owner: -
--

ALTER TABLE ONLY slr.conferences
    ADD CONSTRAINT conferences_pkey PRIMARY KEY (id);


--
-- TOC entry 3365 (class 2606 OID 20455)
-- Name: countries countries_pkey; Type: CONSTRAINT; Schema: slr; Owner: -
--

ALTER TABLE ONLY slr.countries
    ADD CONSTRAINT countries_pkey PRIMARY KEY (id);


--
-- TOC entry 3359 (class 2606 OID 20042)
-- Name: dblp_publication dblp_publication_pkey; Type: CONSTRAINT; Schema: slr; Owner: -
--

ALTER TABLE ONLY slr.dblp_publication
    ADD CONSTRAINT dblp_publication_pkey PRIMARY KEY (id);


--
-- TOC entry 3377 (class 2606 OID 20543)
-- Name: departments departments_pkey; Type: CONSTRAINT; Schema: slr; Owner: -
--

ALTER TABLE ONLY slr.departments
    ADD CONSTRAINT departments_pkey PRIMARY KEY (id);


--
-- TOC entry 3391 (class 2606 OID 20654)
-- Name: editions editions_pkey; Type: CONSTRAINT; Schema: slr; Owner: -
--

ALTER TABLE ONLY slr.editions
    ADD CONSTRAINT editions_pkey PRIMARY KEY (id);


--
-- TOC entry 3367 (class 2606 OID 20464)
-- Name: institutions institutions_pkey; Type: CONSTRAINT; Schema: slr; Owner: -
--

ALTER TABLE ONLY slr.institutions
    ADD CONSTRAINT institutions_pkey PRIMARY KEY (id);


--
-- TOC entry 3387 (class 2606 OID 20715)
-- Name: journal_editorials journal_editorials_pkey; Type: CONSTRAINT; Schema: slr; Owner: -
--

ALTER TABLE ONLY slr.journal_editorials
    ADD CONSTRAINT journal_editorials_pkey PRIMARY KEY (journal_editorial_id);


--
-- TOC entry 3389 (class 2606 OID 20708)
-- Name: journal_papers journal_papers_pkey; Type: CONSTRAINT; Schema: slr; Owner: -
--

ALTER TABLE ONLY slr.journal_papers
    ADD CONSTRAINT journal_papers_pkey PRIMARY KEY (journal_paper_id);


--
-- TOC entry 3395 (class 2606 OID 20701)
-- Name: journals journals_pkey; Type: CONSTRAINT; Schema: slr; Owner: -
--

ALTER TABLE ONLY slr.journals
    ADD CONSTRAINT journals_pkey PRIMARY KEY (id);


--
-- TOC entry 3369 (class 2606 OID 20479)
-- Name: keywords keywords_pkey; Type: CONSTRAINT; Schema: slr; Owner: -
--

ALTER TABLE ONLY slr.keywords
    ADD CONSTRAINT keywords_pkey PRIMARY KEY (id);


--
-- TOC entry 3371 (class 2606 OID 20485)
-- Name: publication_keywords publication_keywords_pkey; Type: CONSTRAINT; Schema: slr; Owner: -
--

ALTER TABLE ONLY slr.publication_keywords
    ADD CONSTRAINT publication_keywords_pkey PRIMARY KEY (id);


--
-- TOC entry 3361 (class 2606 OID 20438)
-- Name: publications publication_pkey; Type: CONSTRAINT; Schema: slr; Owner: -
--

ALTER TABLE ONLY slr.publications
    ADD CONSTRAINT publication_pkey PRIMARY KEY (id);


--
-- TOC entry 3375 (class 2606 OID 20503)
-- Name: publishers publishers_pkey; Type: CONSTRAINT; Schema: slr; Owner: -
--

ALTER TABLE ONLY slr.publishers
    ADD CONSTRAINT publishers_pkey PRIMARY KEY (id);


--
-- TOC entry 3393 (class 2606 OID 20686)
-- Name: volume_numbers volume_numbers_pkey; Type: CONSTRAINT; Schema: slr; Owner: -
--

ALTER TABLE ONLY slr.volume_numbers
    ADD CONSTRAINT volume_numbers_pkey PRIMARY KEY (id);


--
-- TOC entry 3321 (class 2606 OID 19515)
-- Name: author author_pkey; Type: CONSTRAINT; Schema: slr_v1; Owner: -
--

ALTER TABLE ONLY slr_v1.author
    ADD CONSTRAINT author_pkey PRIMARY KEY (id);


--
-- TOC entry 3323 (class 2606 OID 19523)
-- Name: author_publications author_publications_pkey; Type: CONSTRAINT; Schema: slr_v1; Owner: -
--

ALTER TABLE ONLY slr_v1.author_publications
    ADD CONSTRAINT author_publications_pkey PRIMARY KEY (id);


--
-- TOC entry 3327 (class 2606 OID 19545)
-- Name: book_chapter book_chapter_pkey; Type: CONSTRAINT; Schema: slr_v1; Owner: -
--

ALTER TABLE ONLY slr_v1.book_chapter
    ADD CONSTRAINT book_chapter_pkey PRIMARY KEY (book_chapter_id);


--
-- TOC entry 3325 (class 2606 OID 19534)
-- Name: book book_pkey; Type: CONSTRAINT; Schema: slr_v1; Owner: -
--

ALTER TABLE ONLY slr_v1.book
    ADD CONSTRAINT book_pkey PRIMARY KEY (book_id);


--
-- TOC entry 3331 (class 2606 OID 19564)
-- Name: conference_editorial conference_editorial_pkey; Type: CONSTRAINT; Schema: slr_v1; Owner: -
--

ALTER TABLE ONLY slr_v1.conference_editorial
    ADD CONSTRAINT conference_editorial_pkey PRIMARY KEY (conference_editorial_id);


--
-- TOC entry 3333 (class 2606 OID 19575)
-- Name: conference_paper conference_paper_pkey; Type: CONSTRAINT; Schema: slr_v1; Owner: -
--

ALTER TABLE ONLY slr_v1.conference_paper
    ADD CONSTRAINT conference_paper_pkey PRIMARY KEY (conference_paper_id);


--
-- TOC entry 3329 (class 2606 OID 19553)
-- Name: conference conference_pkey; Type: CONSTRAINT; Schema: slr_v1; Owner: -
--

ALTER TABLE ONLY slr_v1.conference
    ADD CONSTRAINT conference_pkey PRIMARY KEY (id);


--
-- TOC entry 3335 (class 2606 OID 19583)
-- Name: country country_pkey; Type: CONSTRAINT; Schema: slr_v1; Owner: -
--

ALTER TABLE ONLY slr_v1.country
    ADD CONSTRAINT country_pkey PRIMARY KEY (id);


--
-- TOC entry 3337 (class 2606 OID 19591)
-- Name: department department_pkey; Type: CONSTRAINT; Schema: slr_v1; Owner: -
--

ALTER TABLE ONLY slr_v1.department
    ADD CONSTRAINT department_pkey PRIMARY KEY (id);


--
-- TOC entry 3339 (class 2606 OID 19599)
-- Name: edition edition_pkey; Type: CONSTRAINT; Schema: slr_v1; Owner: -
--

ALTER TABLE ONLY slr_v1.edition
    ADD CONSTRAINT edition_pkey PRIMARY KEY (id);


--
-- TOC entry 3341 (class 2606 OID 19607)
-- Name: institution institution_pkey; Type: CONSTRAINT; Schema: slr_v1; Owner: -
--

ALTER TABLE ONLY slr_v1.institution
    ADD CONSTRAINT institution_pkey PRIMARY KEY (id);


--
-- TOC entry 3345 (class 2606 OID 19626)
-- Name: journal_editorial journal_editorial_pkey; Type: CONSTRAINT; Schema: slr_v1; Owner: -
--

ALTER TABLE ONLY slr_v1.journal_editorial
    ADD CONSTRAINT journal_editorial_pkey PRIMARY KEY (journal_editorial_id);


--
-- TOC entry 3347 (class 2606 OID 19637)
-- Name: journal_paper journal_paper_pkey; Type: CONSTRAINT; Schema: slr_v1; Owner: -
--

ALTER TABLE ONLY slr_v1.journal_paper
    ADD CONSTRAINT journal_paper_pkey PRIMARY KEY (journal_paper_id);


--
-- TOC entry 3343 (class 2606 OID 19615)
-- Name: journal journal_pkey; Type: CONSTRAINT; Schema: slr_v1; Owner: -
--

ALTER TABLE ONLY slr_v1.journal
    ADD CONSTRAINT journal_pkey PRIMARY KEY (id);


--
-- TOC entry 3349 (class 2606 OID 19645)
-- Name: keyword keyword_pkey; Type: CONSTRAINT; Schema: slr_v1; Owner: -
--

ALTER TABLE ONLY slr_v1.keyword
    ADD CONSTRAINT keyword_pkey PRIMARY KEY (id);


--
-- TOC entry 3353 (class 2606 OID 19664)
-- Name: publication_keywords publication_keywords_pkey; Type: CONSTRAINT; Schema: slr_v1; Owner: -
--

ALTER TABLE ONLY slr_v1.publication_keywords
    ADD CONSTRAINT publication_keywords_pkey PRIMARY KEY (id);


--
-- TOC entry 3351 (class 2606 OID 19656)
-- Name: publication publication_pkey; Type: CONSTRAINT; Schema: slr_v1; Owner: -
--

ALTER TABLE ONLY slr_v1.publication
    ADD CONSTRAINT publication_pkey PRIMARY KEY (id);


--
-- TOC entry 3355 (class 2606 OID 19672)
-- Name: publisher publisher_pkey; Type: CONSTRAINT; Schema: slr_v1; Owner: -
--

ALTER TABLE ONLY slr_v1.publisher
    ADD CONSTRAINT publisher_pkey PRIMARY KEY (id);


--
-- TOC entry 3357 (class 2606 OID 19680)
-- Name: volume_number volume_number_pkey; Type: CONSTRAINT; Schema: slr_v1; Owner: -
--

ALTER TABLE ONLY slr_v1.volume_number
    ADD CONSTRAINT volume_number_pkey PRIMARY KEY (id);


--
-- TOC entry 3413 (class 2606 OID 20557)
-- Name: authors fk_author__department; Type: FK CONSTRAINT; Schema: slr; Owner: -
--

ALTER TABLE ONLY slr.authors
    ADD CONSTRAINT fk_author__department FOREIGN KEY (department_id) REFERENCES slr.departments(id);


--
-- TOC entry 3415 (class 2606 OID 20562)
-- Name: author_publications fk_author_publication__author; Type: FK CONSTRAINT; Schema: slr; Owner: -
--

ALTER TABLE ONLY slr.author_publications
    ADD CONSTRAINT fk_author_publication__author FOREIGN KEY (author_id) REFERENCES slr.authors(id);


--
-- TOC entry 3414 (class 2606 OID 20567)
-- Name: author_publications fk_author_publication__publication; Type: FK CONSTRAINT; Schema: slr; Owner: -
--

ALTER TABLE ONLY slr.author_publications
    ADD CONSTRAINT fk_author_publication__publication FOREIGN KEY (publication_id) REFERENCES slr.publications(id);


--
-- TOC entry 3420 (class 2606 OID 20642)
-- Name: books fk_book__publisher; Type: FK CONSTRAINT; Schema: slr; Owner: -
--

ALTER TABLE ONLY slr.books
    ADD CONSTRAINT fk_book__publisher FOREIGN KEY (publisher_id) REFERENCES slr.publishers(id);


--
-- TOC entry 3421 (class 2606 OID 20635)
-- Name: book_chapters fk_book_chapter__publisher; Type: FK CONSTRAINT; Schema: slr; Owner: -
--

ALTER TABLE ONLY slr.book_chapters
    ADD CONSTRAINT fk_book_chapter__publisher FOREIGN KEY (publisher_id) REFERENCES slr.publishers(id);


--
-- TOC entry 3422 (class 2606 OID 20674)
-- Name: conferece_papers fk_conferece_paper__edition; Type: FK CONSTRAINT; Schema: slr; Owner: -
--

ALTER TABLE ONLY slr.conferece_papers
    ADD CONSTRAINT fk_conferece_paper__edition FOREIGN KEY (edition_id) REFERENCES slr.editions(id);


--
-- TOC entry 3423 (class 2606 OID 20667)
-- Name: conferece_editorials fk_conference_editorial__edition; Type: FK CONSTRAINT; Schema: slr; Owner: -
--

ALTER TABLE ONLY slr.conferece_editorials
    ADD CONSTRAINT fk_conference_editorial__edition FOREIGN KEY (edition_id) REFERENCES slr.editions(id);


--
-- TOC entry 3419 (class 2606 OID 20550)
-- Name: departments fk_department__institution; Type: FK CONSTRAINT; Schema: slr; Owner: -
--

ALTER TABLE ONLY slr.departments
    ADD CONSTRAINT fk_department__institution FOREIGN KEY (institution_id) REFERENCES slr.institutions(id);


--
-- TOC entry 3426 (class 2606 OID 20660)
-- Name: editions fk_edition__conference; Type: FK CONSTRAINT; Schema: slr; Owner: -
--

ALTER TABLE ONLY slr.editions
    ADD CONSTRAINT fk_edition__conference FOREIGN KEY (conference_id) REFERENCES slr.conferences(id);


--
-- TOC entry 3427 (class 2606 OID 20655)
-- Name: editions fk_edition__publisher; Type: FK CONSTRAINT; Schema: slr; Owner: -
--

ALTER TABLE ONLY slr.editions
    ADD CONSTRAINT fk_edition__publisher FOREIGN KEY (publisher_id) REFERENCES slr.publishers(id);


--
-- TOC entry 3416 (class 2606 OID 20545)
-- Name: institutions fk_institution__country; Type: FK CONSTRAINT; Schema: slr; Owner: -
--

ALTER TABLE ONLY slr.institutions
    ADD CONSTRAINT fk_institution__country FOREIGN KEY (country_id) REFERENCES slr.countries(id);


--
-- TOC entry 3424 (class 2606 OID 20716)
-- Name: journal_editorials fk_journal_editorial__volume_number; Type: FK CONSTRAINT; Schema: slr; Owner: -
--

ALTER TABLE ONLY slr.journal_editorials
    ADD CONSTRAINT fk_journal_editorial__volume_number FOREIGN KEY (volume_number_id) REFERENCES slr.volume_numbers(id);


--
-- TOC entry 3425 (class 2606 OID 20709)
-- Name: journal_papers fk_journal_paper__volume_number; Type: FK CONSTRAINT; Schema: slr; Owner: -
--

ALTER TABLE ONLY slr.journal_papers
    ADD CONSTRAINT fk_journal_paper__volume_number FOREIGN KEY (volume_number_id) REFERENCES slr.volume_numbers(id);


--
-- TOC entry 3418 (class 2606 OID 20577)
-- Name: publication_keywords fk_publcation_keywords__keyword; Type: FK CONSTRAINT; Schema: slr; Owner: -
--

ALTER TABLE ONLY slr.publication_keywords
    ADD CONSTRAINT fk_publcation_keywords__keyword FOREIGN KEY (keyword_id) REFERENCES slr.keywords(id);


--
-- TOC entry 3417 (class 2606 OID 20572)
-- Name: publication_keywords fk_publication_keywords__publication; Type: FK CONSTRAINT; Schema: slr; Owner: -
--

ALTER TABLE ONLY slr.publication_keywords
    ADD CONSTRAINT fk_publication_keywords__publication FOREIGN KEY (publication_id) REFERENCES slr.publications(id);


--
-- TOC entry 3429 (class 2606 OID 20702)
-- Name: volume_numbers fk_volume_number__journal; Type: FK CONSTRAINT; Schema: slr; Owner: -
--

ALTER TABLE ONLY slr.volume_numbers
    ADD CONSTRAINT fk_volume_number__journal FOREIGN KEY (journal_id) REFERENCES slr.journals(id);


--
-- TOC entry 3428 (class 2606 OID 20687)
-- Name: volume_numbers fk_volume_number__publisher; Type: FK CONSTRAINT; Schema: slr; Owner: -
--

ALTER TABLE ONLY slr.volume_numbers
    ADD CONSTRAINT fk_volume_number__publisher FOREIGN KEY (publisher_id) REFERENCES slr.publishers(id);


--
-- TOC entry 3404 (class 2606 OID 19721)
-- Name: edition fk13vilihq8ntohjwygs4ec33tm; Type: FK CONSTRAINT; Schema: slr_v1; Owner: -
--

ALTER TABLE ONLY slr_v1.edition
    ADD CONSTRAINT fk13vilihq8ntohjwygs4ec33tm FOREIGN KEY (conference_id) REFERENCES slr_v1.conference(id);


--
-- TOC entry 3396 (class 2606 OID 19681)
-- Name: author fk405yh731kqj9vfiqvym5py99w; Type: FK CONSTRAINT; Schema: slr_v1; Owner: -
--

ALTER TABLE ONLY slr_v1.author
    ADD CONSTRAINT fk405yh731kqj9vfiqvym5py99w FOREIGN KEY (department_id) REFERENCES slr_v1.department(id);


--
-- TOC entry 3406 (class 2606 OID 19731)
-- Name: institution fk4clvgurkl7935gesrdab85uo5; Type: FK CONSTRAINT; Schema: slr_v1; Owner: -
--

ALTER TABLE ONLY slr_v1.institution
    ADD CONSTRAINT fk4clvgurkl7935gesrdab85uo5 FOREIGN KEY (country_id) REFERENCES slr_v1.country(id);


--
-- TOC entry 3401 (class 2606 OID 19706)
-- Name: conference_editorial fk5r7qt97kqdgwrb540epn0ky5w; Type: FK CONSTRAINT; Schema: slr_v1; Owner: -
--

ALTER TABLE ONLY slr_v1.conference_editorial
    ADD CONSTRAINT fk5r7qt97kqdgwrb540epn0ky5w FOREIGN KEY (edition_id) REFERENCES slr_v1.edition(id);


--
-- TOC entry 3400 (class 2606 OID 19701)
-- Name: book_chapter fk5xi2pxqhpubd1jsvqc395wkw; Type: FK CONSTRAINT; Schema: slr_v1; Owner: -
--

ALTER TABLE ONLY slr_v1.book_chapter
    ADD CONSTRAINT fk5xi2pxqhpubd1jsvqc395wkw FOREIGN KEY (publisher_id) REFERENCES slr_v1.publisher(id);


--
-- TOC entry 3407 (class 2606 OID 19736)
-- Name: journal_editorial fk6rn317avphkdtsm1he8y8wq8b; Type: FK CONSTRAINT; Schema: slr_v1; Owner: -
--

ALTER TABLE ONLY slr_v1.journal_editorial
    ADD CONSTRAINT fk6rn317avphkdtsm1he8y8wq8b FOREIGN KEY (volume_number_id) REFERENCES slr_v1.volume_number(id);


--
-- TOC entry 3411 (class 2606 OID 19756)
-- Name: volume_number fk7harpw6ud8l2sleyesn7aav5l; Type: FK CONSTRAINT; Schema: slr_v1; Owner: -
--

ALTER TABLE ONLY slr_v1.volume_number
    ADD CONSTRAINT fk7harpw6ud8l2sleyesn7aav5l FOREIGN KEY (journal_id) REFERENCES slr_v1.journal(id);


--
-- TOC entry 3412 (class 2606 OID 19761)
-- Name: volume_number fk8qk73i4bx7d8kl8grl5nmv08b; Type: FK CONSTRAINT; Schema: slr_v1; Owner: -
--

ALTER TABLE ONLY slr_v1.volume_number
    ADD CONSTRAINT fk8qk73i4bx7d8kl8grl5nmv08b FOREIGN KEY (publisher_id) REFERENCES slr_v1.publisher(id);


--
-- TOC entry 3402 (class 2606 OID 19711)
-- Name: conference_paper fkboqq8ga2xd3y1qoqu3t78qex1; Type: FK CONSTRAINT; Schema: slr_v1; Owner: -
--

ALTER TABLE ONLY slr_v1.conference_paper
    ADD CONSTRAINT fkboqq8ga2xd3y1qoqu3t78qex1 FOREIGN KEY (edition_id) REFERENCES slr_v1.edition(id);


--
-- TOC entry 3397 (class 2606 OID 19686)
-- Name: author_publications fkeesipob2kuay3s4pbju9whhw2; Type: FK CONSTRAINT; Schema: slr_v1; Owner: -
--

ALTER TABLE ONLY slr_v1.author_publications
    ADD CONSTRAINT fkeesipob2kuay3s4pbju9whhw2 FOREIGN KEY (author_id) REFERENCES slr_v1.author(id);


--
-- TOC entry 3398 (class 2606 OID 19691)
-- Name: author_publications fkesqjsnyhk8mio5wbgkt6sw4yp; Type: FK CONSTRAINT; Schema: slr_v1; Owner: -
--

ALTER TABLE ONLY slr_v1.author_publications
    ADD CONSTRAINT fkesqjsnyhk8mio5wbgkt6sw4yp FOREIGN KEY (publication_id) REFERENCES slr_v1.publication(id);


--
-- TOC entry 3405 (class 2606 OID 19726)
-- Name: edition fkg2qgt9wer0mqe587e28hq0x5t; Type: FK CONSTRAINT; Schema: slr_v1; Owner: -
--

ALTER TABLE ONLY slr_v1.edition
    ADD CONSTRAINT fkg2qgt9wer0mqe587e28hq0x5t FOREIGN KEY (publisher_id) REFERENCES slr_v1.publisher(id);


--
-- TOC entry 3399 (class 2606 OID 19696)
-- Name: book fkgtvt7p649s4x80y6f4842pnfq; Type: FK CONSTRAINT; Schema: slr_v1; Owner: -
--

ALTER TABLE ONLY slr_v1.book
    ADD CONSTRAINT fkgtvt7p649s4x80y6f4842pnfq FOREIGN KEY (publisher_id) REFERENCES slr_v1.publisher(id);


--
-- TOC entry 3408 (class 2606 OID 19741)
-- Name: journal_paper fkhktpu1pk6haehxmo7uweb2c7f; Type: FK CONSTRAINT; Schema: slr_v1; Owner: -
--

ALTER TABLE ONLY slr_v1.journal_paper
    ADD CONSTRAINT fkhktpu1pk6haehxmo7uweb2c7f FOREIGN KEY (volume_number_id) REFERENCES slr_v1.volume_number(id);


--
-- TOC entry 3410 (class 2606 OID 19751)
-- Name: publication_keywords fkqi0i4yabfb2spvgfihvcooud1; Type: FK CONSTRAINT; Schema: slr_v1; Owner: -
--

ALTER TABLE ONLY slr_v1.publication_keywords
    ADD CONSTRAINT fkqi0i4yabfb2spvgfihvcooud1 FOREIGN KEY (publication_id) REFERENCES slr_v1.publication(id);


--
-- TOC entry 3403 (class 2606 OID 19716)
-- Name: department fkqt1nyu91ehpp8ynoqhlhkc4s6; Type: FK CONSTRAINT; Schema: slr_v1; Owner: -
--

ALTER TABLE ONLY slr_v1.department
    ADD CONSTRAINT fkqt1nyu91ehpp8ynoqhlhkc4s6 FOREIGN KEY (institution_id) REFERENCES slr_v1.institution(id);


--
-- TOC entry 3409 (class 2606 OID 19746)
-- Name: publication_keywords fkqvo2xfway1iik4c8vto1o6wbe; Type: FK CONSTRAINT; Schema: slr_v1; Owner: -
--

ALTER TABLE ONLY slr_v1.publication_keywords
    ADD CONSTRAINT fkqvo2xfway1iik4c8vto1o6wbe FOREIGN KEY (keyword_id) REFERENCES slr_v1.keyword(id);


-- Completed on 2019-12-20 17:47:40 -03

--
-- PostgreSQL database dump complete
--

