-- Table: public.orders

-- DROP TABLE IF EXISTS public.orders;

CREATE TABLE IF NOT EXISTS public.orders
(
    id integer NOT NULL DEFAULT nextval('orders_id_seq'::regclass),
    order_id character varying(10) COLLATE pg_catalog."default",
    email_address character varying(30) COLLATE pg_catalog."default",
    quantity numeric,
    returned boolean DEFAULT false,
    item_id integer,
    CONSTRAINT orders_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.orders
    OWNER to postgres;