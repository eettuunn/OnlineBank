create table public.bank_account
(
    id               uuid not null
        primary key,
    amount           numeric(100, 50),
    currency         varchar(255),
    creation_date timestamp,
    is_closed        boolean,
    name             varchar(255),
    number           varchar(255),
    owner_id         uuid
);

alter table public.bank_account
    owner to postgres;

create table public.transaction
(
    id                     uuid not null
        primary key,
    additional_information varchar(255),
    amount                 numeric(100, 50),
    transaction_date       timestamp,
    operation_type         integer,
    bank_account_id        uuid not null
        constraint fkec44dj1u86xnsku7ld84pirje
            references public.bank_account
);

alter table public.transaction
    owner to postgres;

INSERT INTO public.bank_account (id, amount, currency, creation_date, is_closed, name, number, owner_id)
VALUES ('cb1ef860-9f51-4e49-8e7d-f6694b10fc99', 10000000.0, 'RUB', CURRENT_TIMESTAMP, false, 'MASTER', '00000000000000000000', '7e58c0d2-7738-4f78-a1bb-9f8c7d7ce0f4');