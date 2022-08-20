-- Table: public.items

-- DROP TABLE IF EXISTS public.items;

CREATE TABLE IF NOT EXISTS public.items
(
    id integer NOT NULL DEFAULT nextval('items_id_seq'::regclass),
    sku character varying(20) COLLATE pg_catalog."default",
    item_name character varying(100) COLLATE pg_catalog."default",
    price numeric,
    CONSTRAINT items_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.items
    OWNER to postgres;