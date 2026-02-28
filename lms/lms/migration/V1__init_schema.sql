CREATE TABLE `organization`
(
    id          BIGINT AUTO_INCREMENT NOT NULL,
    name        VARCHAR(255) NULL,
    code        VARCHAR(255) NULL,
    created_at  datetime NULL,
    modified_at datetime NULL,
    CONSTRAINT pk_organization PRIMARY KEY (id)
);

CREATE TABLE users
(
    id              BIGINT AUTO_INCREMENT NOT NULL,
    name            VARCHAR(255) NOT NULL,
    user_name       VARCHAR(255) NOT NULL,
    email           VARCHAR(255) NOT NULL,
    password        VARCHAR(255) NOT NULL,
    `role`          VARCHAR(255) NOT NULL,
    status          VARCHAR(255) NOT NULL,
    organization_id BIGINT       NOT NULL,
    created_at      datetime NULL,
    modified_at     datetime NULL,
    created_by      BIGINT NULL,
    modified_by     BIGINT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

ALTER TABLE users
    ADD CONSTRAINT uc_users_email UNIQUE (email);

ALTER TABLE users
    ADD CONSTRAINT uc_users_user_name UNIQUE (user_name);

ALTER TABLE users
    ADD CONSTRAINT FK_USERS_ON_ORGANIZATION FOREIGN KEY (organization_id) REFERENCES `organization` (id);