-- Table: public.returns_dt

-- DROP TABLE IF EXISTS public.returns_dt;

CREATE TABLE IF NOT EXISTS public.returns_dt
(
    id integer NOT NULL DEFAULT nextval('returns_dt_id_seq'::regclass),
    returns_id integer,
    item_id integer,
    quantity numeric,
    qc_status character varying(20) COLLATE pg_catalog."default"
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.returns_dt
    OWNER to postgres;