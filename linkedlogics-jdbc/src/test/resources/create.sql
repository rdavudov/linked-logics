CREATE TABLE ll_queue (
    id INTEGER NOT NULL AUTO_INCREMENT,
    queue VARCHAR(128) NOT NULL,
    payload VARCHAR(4000) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    consumed_by VARCHAR(128),
    PRIMARY KEY (id)
);

CREATE TABLE ll_topic (
    id INTEGER NOT NULL AUTO_INCREMENT,
    queue VARCHAR(128) NOT NULL,
    payload VARCHAR(4000) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    consumed_by VARCHAR(128),
    PRIMARY KEY (id)
);

CREATE TABLE ll_topic_consumed (
    id INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL,
    consumed_by VARCHAR(128) NOT NULL,
    consumed_at TIMESTAMP NOT NULL,
    PRIMARY KEY (id, consumed_by)
);

CREATE TABLE ll_context (
    id VARCHAR(128) NOT NULL,
    id_key VARCHAR(128),
    parent_id VARCHAR(128),
    status VARCHAR(32) NOT NULL,
    version INTEGER NOT NULL,
    process_id VARCHAR(128) NOT NULL,
    process_version INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    finished_at TIMESTAMP,
    expires_at TIMESTAMP,
    data VARCHAR NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE ll_trigger (
    id INTEGER NOT NULL AUTO_INCREMENT,
    context_id VARCHAR(128) NOT NULL,
    waiting_context_id VARCHAR(128) NOT NULL,
    waiting_position VARCHAR(256) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    expired_at TIMESTAMP,
    PRIMARY KEY (id)
);

CREATE TABLE ll_process (
    id VARCHAR(128) NOT NULL,
    version INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    builder VARCHAR NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE ll_limit (
    id VARCHAR(128) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    counter INTEGER NOT NULL,
    PRIMARY KEY (id)
);
