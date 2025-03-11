CREATE SCHEMA IF NOT EXISTS bwg;

--------------------------------------------------------------------------------------

-- SEQUENCE: public.hibernate_sequence

-- DROP SEQUENCE IF EXISTS public.hibernate_sequence;

CREATE SEQUENCE IF NOT EXISTS bwg.hibernate_sequence
    INCREMENT 50
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

ALTER SEQUENCE bwg.hibernate_sequence
    OWNER TO postgres;

--------------------------------------------------------------------------------------

-- Table: bwg.users

CREATE TABLE IF NOT EXISTS bwg.users
(
    user_id bigint NOT NULL,
    u_user_id character varying(100) COLLATE pg_catalog."default" NOT NULL,
    first_name character varying(100) COLLATE pg_catalog."default" NOT NULL,
    last_name character varying(100) COLLATE pg_catalog."default" NOT NULL,
    email character varying(150) COLLATE pg_catalog."default" NOT NULL,
    password_hash character varying(255) COLLATE pg_catalog."default" NOT NULL,
    role character varying(255) DEFAULT 'couple' NOT NULL,
    phone_number character varying(20) COLLATE pg_catalog."default",
    profile_photo_url character varying(255) COLLATE pg_catalog."default",
    created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT users_pkey PRIMARY KEY (user_id),
    CONSTRAINT users_email_key UNIQUE (email)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS bwg.users
    OWNER to postgres;
-- Index: idx_role

-- DROP INDEX IF EXISTS bwg.idx_role;

CREATE INDEX IF NOT EXISTS idx_role
    ON bwg.users USING btree
    (role ASC NULLS LAST)
    TABLESPACE pg_default;

--------------------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS bwg.vendors
(
    vendor_id bigint NOT NULL,
    u_vendor_id character varying(100) COLLATE pg_catalog."default" NOT NULL,
    user_id bigint NOT NULL,
    business_name character varying(150) COLLATE pg_catalog."default" NOT NULL,
    location character varying(255) COLLATE pg_catalog."default" NOT NULL,
    description text COLLATE pg_catalog."default",
    rating numeric(2,1),
    total_reviews integer NOT NULL DEFAULT 0,
    created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT vendors_pkey PRIMARY KEY (vendor_id),
    CONSTRAINT vendors_user_id_key UNIQUE (user_id),
    CONSTRAINT fk_vendor_user FOREIGN KEY (user_id)
        REFERENCES bwg.users (user_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT vendors_rating_check CHECK (rating >= 1::numeric AND rating <= 5::numeric)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS bwg.vendors
    OWNER to postgres;
-- Index: idx_location

-- DROP INDEX IF EXISTS bwg.idx_location;

CREATE INDEX IF NOT EXISTS idx_location
    ON bwg.vendors USING btree
    (location COLLATE pg_catalog."default" ASC NULLS LAST)
    TABLESPACE pg_default;

--------------------------------------------------------------------------------------

-- Create the Categories table with the specified columns and constraints in the bwg schema
CREATE TABLE IF NOT EXISTS bwg.Categories (
    category_id BIGINT PRIMARY KEY NOT NULL,
    u_category_id VARCHAR(100) NOT NULL,
    category_name VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

--------------------------------------------------------------------------------------

-- Create the Services table with the specified columns and constraints in the bwg schema
CREATE TABLE IF NOT EXISTS bwg.Services (
    service_id BIGINT PRIMARY KEY NOT NULL,
    u_service_id VARCHAR(100) NOT NULL,
    vendor_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    service_name VARCHAR(200) NOT NULL,
    description TEXT,
    price_min DECIMAL(10,2) NOT NULL,
    price_max DECIMAL(10,2),
    availability VARCHAR(100),
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_service_vendor FOREIGN KEY (vendor_id) REFERENCES bwg.Vendors(vendor_id),
    CONSTRAINT fk_service_category FOREIGN KEY (category_id) REFERENCES bwg.Categories(category_id)
);

-- Create a function to update the updated_at column in the bwg schema
CREATE OR REPLACE FUNCTION bwg.update_updated_at_column_services()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create a trigger to call the function before each row update in the bwg schema
CREATE TRIGGER trigger_update_updated_at_services
BEFORE UPDATE ON bwg.Services
FOR EACH ROW
EXECUTE FUNCTION bwg.update_updated_at_column_services();

-- Create indexes on vendor_id and category_id columns in the bwg schema
CREATE INDEX idx_vendor ON bwg.Services (vendor_id);
CREATE INDEX idx_category ON bwg.Services (category_id);

--------------------------------------------------------------------------------------

-- Create the Reviews table with the specified columns and constraints in the bwg schema
CREATE TABLE IF NOT EXISTS bwg.Reviews (
    review_id BIGINT PRIMARY KEY NOT NULL,
    u_review_id VARCHAR(100) NOT NULL,
    user_id BIGINT NOT NULL,
    service_id BIGINT NOT NULL,
    rating INT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment TEXT,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_review_user FOREIGN KEY (user_id) REFERENCES bwg.Users(user_id),
    CONSTRAINT fk_review_service FOREIGN KEY (service_id) REFERENCES bwg.Services(service_id)
);

-- Create a function to update the updated_at column in the bwg schema (not required for this table as there is no updated_at column)

-- Create indexes on user_id and service_id columns in the bwg schema
CREATE INDEX idx_user ON bwg.Reviews (user_id);
CREATE INDEX idx_service ON bwg.Reviews (service_id);

--------------------------------------------------------------------------------------

-- Create the Bookings table with the specified columns and constraints in the bwg schema
CREATE TABLE IF NOT EXISTS bwg.Bookings (
    booking_id BIGINT PRIMARY KEY NOT NULL,
    u_booking_id VARCHAR(100) NOT NULL,
    user_id BIGINT NOT NULL,
    service_id BIGINT NOT NULL,
    event_date TIMESTAMPTZ NOT NULL,
    status character varying(255) DEFAULT 'pending' NOT NULL,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_booking_user FOREIGN KEY (user_id) REFERENCES bwg.Users(user_id),
    CONSTRAINT fk_booking_service FOREIGN KEY (service_id) REFERENCES bwg.Services(service_id)
);

-- Create indexes on user_id, service_id, event_date, and status columns in the bwg schema
CREATE INDEX idx_user_booking ON bwg.Bookings (user_id);
CREATE INDEX idx_service_booking ON bwg.Bookings (service_id);
CREATE INDEX idx_event_date ON bwg.Bookings (event_date);
CREATE INDEX idx_status ON bwg.Bookings (status);


--------------------------------------------------------------------------------------

-- Create the Payments table with the specified columns and constraints in the bwg schema
CREATE TABLE IF NOT EXISTS bwg.Payments (
    payment_id BIGINT PRIMARY KEY NOT NULL,
    u_payment_id VARCHAR(100) NOT NULL,
    booking_id BIGINT NOT NULL UNIQUE,
    amount DECIMAL(10,2) NOT NULL,
    currency VARCHAR(10) NOT NULL DEFAULT 'USD',
    status character varying(255) NOT NULL,
    transaction_reference VARCHAR(255) NOT NULL,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_payment_booking FOREIGN KEY (booking_id) REFERENCES bwg.Bookings(booking_id)
);

-- Create indexes on status and transaction_reference columns in the bwg schema
CREATE INDEX idx_status_payment ON bwg.Payments (status);
CREATE INDEX idx_transaction_ref ON bwg.Payments (transaction_reference);


--------------------------------------------------------------------------------------

-- Create the Messages table with the specified columns and constraints in the bwg schema
CREATE TABLE IF NOT EXISTS bwg.Messages (
    message_id BIGINT PRIMARY KEY NOT NULL,
    u_message_id VARCHAR(100) NOT NULL,
    sender_id BIGINT NOT NULL,
    receiver_id BIGINT NOT NULL,
    conversation_id VARCHAR(50),
    content TEXT NOT NULL,
    sent_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_message_sender FOREIGN KEY (sender_id) REFERENCES bwg.Users(user_id),
    CONSTRAINT fk_message_receiver FOREIGN KEY (receiver_id) REFERENCES bwg.Users(user_id)
);

-- Create indexes on sender_id and receiver_id columns in the bwg schema
CREATE INDEX idx_sender_receiver ON bwg.Messages (sender_id, receiver_id);


-----------------------------------------------------------------------------------

-- Create the Media table with the specified columns and constraints in the bwg schema
CREATE TABLE IF NOT EXISTS bwg.Media (
    media_id BIGINT PRIMARY KEY NOT NULL,
    u_media_id VARCHAR(100) NOT NULL,
    entity_type character varying(255) NOT NULL,
    entity_id BIGINT NOT NULL,
    media_url character varying(255) NOT NULL,
    thumbnail_url VARCHAR(255),
    mime_type VARCHAR(50),
    title VARCHAR(200),
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

-- Create an index on the entity_type and entity_id columns in the bwg schema
CREATE INDEX idx_entity ON bwg.Media (entity_type, entity_id);

-----------------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS bwg.tags (
    tag_id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, -- Auto-incremented ID
    name VARCHAR(100) NOT NULL UNIQUE, -- Unique tag name
    status VARCHAR(50) NOT NULL DEFAULT 'active', -- Status: active/inactive
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_tags_name ON bwg.tags (name);
CREATE INDEX idx_tags_status ON bwg.tags (status);


CREATE TABLE IF NOT EXISTS bwg.service_tags (
    service_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL,
    PRIMARY KEY (service_id, tag_id),
    CONSTRAINT fk_service_tag_service FOREIGN KEY (service_id) REFERENCES bwg.services(service_id) ON DELETE CASCADE,
    CONSTRAINT fk_service_tag_tag FOREIGN KEY (tag_id) REFERENCES bwg.tags(tag_id) ON DELETE CASCADE
);

CREATE INDEX idx_service_tags_service ON bwg.service_tags (service_id);
CREATE INDEX idx_service_tags_tag ON bwg.service_tags (tag_id);
CREATE INDEX idx_service_tags_composite ON bwg.service_tags (service_id, tag_id);



CREATE TABLE IF NOT EXISTS bwg.category_tags (
    category_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL,
    PRIMARY KEY (category_id, tag_id),
    CONSTRAINT fk_category_tag_category FOREIGN KEY (category_id) REFERENCES bwg.categories(category_id) ON DELETE CASCADE,
    CONSTRAINT fk_category_tag_tag FOREIGN KEY (tag_id) REFERENCES bwg.tags(tag_id) ON DELETE CASCADE
);

CREATE INDEX idx_category_tags_category ON bwg.category_tags (category_id);
CREATE INDEX idx_category_tags_tag ON bwg.category_tags (tag_id);
CREATE INDEX idx_category_tags_composite ON bwg.category_tags (category_id, tag_id);


