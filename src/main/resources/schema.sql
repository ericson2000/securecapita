CREATE SCHEMA IF NOT EXISTS securecap;
SET NAMES 'UTF8MB4';
-- SET TIME_ZONE = 'MST';
-- SET TIME_ZONE = '-4:00';

USE
securecap;

DROP TABLE IF EXISTS users;

CREATE TABLE Users
(
    id         BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50),
    last_name  VARCHAR(50),
    email      VARCHAR(100),
    password   VARCHAR(255) DEFAULT NULL,
    address    VARCHAR(255) DEFAULT NULL,
    phone      VARCHAR(30)  DEFAULT NULL,
    title      VARCHAR(50)  DEFAULT NULL,
    bio        VARCHAR(255) DEFAULT NULL,
    enabled    BOOLEAN      DEFAULT FALSE,
    non_locked BOOLEAN      DEFAULT TRUE,
    using_mfa  BOOLEAN      DEFAULT FALSE,
    image_url  VARCHAR(255) DEFAULT 'https://cdn-icons-png.flaticon.com/512/149/149071.png',
    created_at DATETIME     DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT UQ_Users_Email UNIQUE (email)

);

DROP TABLE IF EXISTS roles;

CREATE TABLE Roles
(
    id         BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(50),
    permission VARCHAR(255), -- user:read, user;delete,customer:read
    CONSTRAINT UQ_Roles_Name UNIQUE (name)
);

DROP TABLE IF EXISTS userroles;

CREATE TABLE UserRoles
(
    id      BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNSIGNED NOT NULL,
    role_id BIGINT UNSIGNED NOT NULL,
    FOREIGN KEY (user_id) REFERENCES Users (id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (role_id) REFERENCES Roles (id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT UQ_UserRoles_User_Id UNIQUE (user_id) -- ca remplace onToMany- approche

);

DROP TABLE IF EXISTS Events;

CREATE TABLE Events
(
    id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    type        VARCHAR(50) NOT NULL CHECK ( type IN
                                             ('LOGIN_ATTEMPT', 'LOGIN_ATTEMPT_FAILURE', 'LOGIN_ATTEMPT_SUCCESS',
                                              'PROFILE_UPDATE', 'PROFILE_PICTURE_UPDATE', 'ROLE_UPDATE',
                                              'ACCOUNT_SETTINGS_UPDATE', 'PASSWORD_UPDATE', 'MFA_UPDATE')),
    description VARCHAR(255),
    CONSTRAINT UQ_Events_type UNIQUE (type)
);

DROP TABLE IF EXISTS UserEvents;

CREATE TABLE UserEvents
(
    id         BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id    BIGINT UNSIGNED NOT NULL,
    event_id   BIGINT UNSIGNED NOT NULL,
    device     VARCHAR(100) DEFAULT NULL,
    ip_address VARCHAR(100) DEFAULT NULL,
    created_at DATETIME     DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES Users (id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (event_id) REFERENCES Events (id) ON DELETE RESTRICT ON UPDATE CASCADE
);

DROP TABLE IF EXISTS AccountVerifications;

CREATE TABLE AccountVerifications
(
    id      BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNSIGNED NOT NULL,
    url     VARCHAR(255) NOT NULL,
    -- date     DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES Users (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT UQ_AccountVerifications_User_Id UNIQUE (user_id),
    CONSTRAINT UQ_AccountVerifications_url UNIQUE (url)

);

DROP TABLE IF EXISTS ResetPasswordVerifications;

CREATE TABLE ResetPasswordVerifications
(
    id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT UNSIGNED NOT NULL,
    url             VARCHAR(255) NOT NULL,
    expiration_date DATETIME     NOT NULL,
    FOREIGN KEY (user_id) REFERENCES Users (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT UQ_ResetPasswordVerifications_User_Id UNIQUE (user_id),
    CONSTRAINT UQ_ResetPasswordVerifications_url UNIQUE (url)

);

DROP TABLE IF EXISTS TwoFactorVerifications;

CREATE TABLE TwoFactorVerifications
(
    id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT UNSIGNED NOT NULL,
    code            VARCHAR(10) NOT NULL,
    expiration_date DATETIME    NOT NULL,
    FOREIGN KEY (user_id) REFERENCES Users (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT UQ_TwoFactorVerifications_User_Id UNIQUE (user_id),
    CONSTRAINT UQ_TwoFactorVerifications_Code UNIQUE (code)

);


INSERT INTO Roles (name, permission)
VALUES ('ROLE_USER', 'READ:USER,READ:CUSTOMER'),
       ('ROLE_MANAGER', 'READ:USER,READ:CUSTOMER,UPDATE:USER,UPDATE:CUSTOMER'),
       ('ROLE_ADMIN', 'READ:USER,READ:CUSTOMER,CREATE:USER,CREATE:CUSTOMER,UPDATE:USER,UPDATE:CUSTOMER'),
       ('ROLE_SYSADMIN',
        'READ:USER,READ:CUSTOMER,CREATE:USER,CREATE:CUSTOMER,UPDATE:USER,UPDATE:CUSTOMER,DELETE:USER,DELETE:CUSTOMER');


INSERT INTO Events (type, description)
VALUES ('LOGIN_ATTEMPT', 'You tried to log in'),
       ('LOGIN_ATTEMPT_FAILURE', 'You tried to log in and you failed'),
       ('LOGIN_ATTEMPT_SUCCESS', 'You tried to log in and you succeeded'),
       ('PROFILE_UPDATE', 'You updated your profile information'),
       ('PROFILE_PICTURE_UPDATE', 'You updated your profile picture'),
       ('ROLE_UPDATE', 'You updated your role and permissions'),
       ('ACCOUNT_SETTINGS_UPDATE', 'You updated your account settings'),
       ('PASSWORD_UPDATE', 'You updated your password'),
       ('MFA_UPDATE', 'You updated your MFA settings');




