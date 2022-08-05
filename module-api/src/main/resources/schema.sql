SET foreign_key_checks = 0;

DROP TABLE IF EXISTS categories CASCADE;
DROP TABLE IF EXISTS users CASCADE;

CREATE TABLE categories
(
    id                  bigint          NOT NULL AUTO_INCREMENT COMMENT 'id',
    name                varchar(20)     NOT NULL COMMENT '카테고리명',
    created_at          timestamp       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at          timestamp       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    PRIMARY KEY (id),
    KEY categories_idx_name (name)
) COMMENT '면접 항목 테이블';

CREATE TABLE users
(
    id                  bigint          NOT NULL AUTO_INCREMENT COMMENT 'id',
    oauth_type          int             NOT NULL COMMENT 'oauth type',
    oauth_id            varchar(20)     NOT NULL COMMENT 'oauth id',
    name                varchar(20)     DEFAULT NULL COMMENT '닉네임',
    profile_image       varchar(20)     DEFAULT NULL COMMENT '프로필 사진',
    created_at          timestamp       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at          timestamp       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    category_id         bigint          DEFAULT NULL COMMENT '카테고리 id',
    PRIMARY KEY (id),
    KEY users_idx_name (name),
    CONSTRAINT fk_users_to_category FOREIGN KEY (category_id) REFERENCES categories (id) ON DELETE SET NULL ON UPDATE CASCADE
) COMMENT '유저 테이블';

SET foreign_key_checks = 1;