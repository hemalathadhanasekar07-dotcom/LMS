CREATE TABLE courses
(
    id              BIGINT AUTO_INCREMENT NOT NULL,
    code            VARCHAR(255) NOT NULL,
    title           VARCHAR(255) NOT NULL,
    status          VARCHAR(255) NULL,
    active          BIT(1) NULL,
    created_by      BIGINT NULL,
    updated_by      BIGINT NULL,
    organization_id BIGINT NULL,
    visibility      VARCHAR(255) NULL,
    created_at      datetime NULL,
    updated_at      datetime NULL,
    CONSTRAINT pk_courses PRIMARY KEY (id)
);

CREATE TABLE modules
(
    id           BIGINT AUTO_INCREMENT NOT NULL,
    course_id    BIGINT NULL,
    name         VARCHAR(255) NULL,
    module_order INT NULL,
    created_at   datetime NULL,
    updated_at   datetime NULL,
    CONSTRAINT pk_modules PRIMARY KEY (id)
);

CREATE TABLE organizations
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    code       VARCHAR(255) NOT NULL,
    name       VARCHAR(255) NOT NULL,
    created_at datetime NULL,
    updated_at datetime NULL,
    CONSTRAINT pk_organizations PRIMARY KEY (id)
);

CREATE TABLE roles
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    name       VARCHAR(255) NOT NULL,
    created_at datetime NULL,
    updated_at datetime NULL,
    CONSTRAINT pk_roles PRIMARY KEY (id)
);

CREATE TABLE topics
(
    id          BIGINT AUTO_INCREMENT NOT NULL,
    module_id   BIGINT NULL,
    name        VARCHAR(255) NULL,
    topic_order INT NULL,
    created_at  datetime NULL,
    updated_at  datetime NULL,
    CONSTRAINT pk_topics PRIMARY KEY (id)
);

CREATE TABLE users
(
    id              BIGINT AUTO_INCREMENT NOT NULL,
    username        VARCHAR(255) NOT NULL,
    name            VARCHAR(255) NOT NULL,
    email           VARCHAR(255) NOT NULL,
    password        VARCHAR(255) NOT NULL,
    role_id         BIGINT       NOT NULL,
    organization_id BIGINT       NOT NULL,
    status          VARCHAR(255) NOT NULL,
    created_at      datetime     NOT NULL,
    updated_at      datetime     NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

ALTER TABLE modules
    ADD CONSTRAINT uc_5eb325d308cc261a1475b5fb6 UNIQUE (course_id, name);

ALTER TABLE users
    ADD CONSTRAINT uc_74165e195b2f7b25de690d14a UNIQUE (email);

ALTER TABLE users
    ADD CONSTRAINT uc_77584fbe74cc86922be2a3560 UNIQUE (username);

ALTER TABLE topics
    ADD CONSTRAINT uc_ab2a03896da7f22be386ee7e8 UNIQUE (module_id, name);

ALTER TABLE courses
    ADD CONSTRAINT uc_courses_code UNIQUE (code);

ALTER TABLE organizations
    ADD CONSTRAINT uc_organizations_code UNIQUE (code);

ALTER TABLE roles
    ADD CONSTRAINT uc_roles_name UNIQUE (name);

ALTER TABLE users
    ADD CONSTRAINT FK_USERS_ON_ORGANIZATION FOREIGN KEY (organization_id) REFERENCES organizations (id);

ALTER TABLE users
    ADD CONSTRAINT FK_USERS_ON_ROLE FOREIGN KEY (role_id) REFERENCES roles (id);