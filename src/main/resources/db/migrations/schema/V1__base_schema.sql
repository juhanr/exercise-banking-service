CREATE TABLE country
(
    iso_code   char(3)   NOT NULL PRIMARY KEY,
    created_at timestamp NOT NULL DEFAULT now(),
    updated_at timestamp NOT NULL DEFAULT now(),
    name       text      NOT NULL
);


CREATE TABLE currency
(
    iso_code   char(3)   NOT NULL PRIMARY KEY,
    created_at timestamp NOT NULL DEFAULT now(),
    updated_at timestamp NOT NULL DEFAULT now(),
    name       text      NOT NULL
);


CREATE TABLE account
(
    id               bigserial NOT NULL PRIMARY KEY,
    created_at       timestamp NOT NULL DEFAULT now(),
    updated_at       timestamp NOT NULL DEFAULT now(),
    customer_id      bigint    NOT NULL,
    country_iso_code char(3)   NOT NULL REFERENCES country (iso_code)
);


CREATE TABLE balance
(
    id                bigserial NOT NULL PRIMARY KEY,
    created_at        timestamp NOT NULL DEFAULT now(),
    updated_at        timestamp NOT NULL DEFAULT now(),
    account_id        int8      NOT NULL REFERENCES account (id),
    amount            int8      NOT NULL,
    currency_iso_code char(3)   NOT NULL REFERENCES currency (iso_code)
);


CREATE TABLE transaction
(
    id                    bigserial  NOT NULL PRIMARY KEY,
    created_at            timestamp  NOT NULL DEFAULT now(),
    updated_at            timestamp  NOT NULL DEFAULT now(),
    account_id            int8       NOT NULL REFERENCES account (id),
    amount                int8       NOT NULL,
    currency_iso_code     char(3)    NOT NULL REFERENCES currency (iso_code),
    direction             varchar(3) NOT NULL,
    description           text       NOT NULL,
    account_balance_after int8       NOT NULL,
    CONSTRAINT transaction_direction_check CHECK (direction IN ('IN', 'OUT'))
);


CREATE OR REPLACE FUNCTION trigger_force_updated_at()
    RETURNS TRIGGER
    LANGUAGE plpgsql
AS $$
BEGIN
    NEW.updated_at = now();
    RETURN NEW;
END;
$$
;

CREATE TRIGGER trigger_force_updated_at BEFORE UPDATE ON country FOR EACH ROW EXECUTE FUNCTION trigger_force_updated_at();
CREATE TRIGGER trigger_force_updated_at BEFORE UPDATE ON currency FOR EACH ROW EXECUTE FUNCTION trigger_force_updated_at();
CREATE TRIGGER trigger_force_updated_at BEFORE UPDATE ON account FOR EACH ROW EXECUTE FUNCTION trigger_force_updated_at();
CREATE TRIGGER trigger_force_updated_at BEFORE UPDATE ON balance FOR EACH ROW EXECUTE FUNCTION trigger_force_updated_at();
CREATE TRIGGER trigger_force_updated_at BEFORE UPDATE ON transaction FOR EACH ROW EXECUTE FUNCTION trigger_force_updated_at();
