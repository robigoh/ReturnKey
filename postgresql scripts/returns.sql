-- Table: public.returns

-- DROP TABLE IF EXISTS public.returns;

CREATE TABLE IF NOT EXISTS public.returns
(
    status character varying(30) COLLATE pg_catalog."default",
    id integer NOT NULL DEFAULT nextval('returns_id_seq'::regclass),
    token character varying COLLATE pg_catalog."default",
    order_id character varying COLLATE pg_catalog."default"
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.returns
    OWNER to postgres;