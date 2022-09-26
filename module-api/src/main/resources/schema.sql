SET foreign_key_checks = 0;

DROP TABLE IF EXISTS categories CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS greenroom_question_answers CASCADE;
DROP TABLE IF EXISTS greenroom_question_scraps CASCADE;
DROP TABLE IF EXISTS greenroom_questions CASCADE;
DROP TABLE IF EXISTS question_groups CASCADE;
DROP TABLE IF EXISTS interview_questions CASCADE;

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
    oauth_id            varchar(100)    NOT NULL COMMENT 'oauth id',
    name                varchar(20)     DEFAULT NULL COMMENT '닉네임',
    profile_image       varchar(20)     DEFAULT NULL COMMENT '프로필 사진',
    is_deleted          boolean         NOT NULL DEFAULT FALSE COMMENT '회원탈퇴 여부',
    withdrawal_date     date            DEFAULT NULL COMMENT '탈퇴일',
    created_at          timestamp       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at          timestamp       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    category_id         bigint          DEFAULT NULL COMMENT '카테고리 id',
    PRIMARY KEY (id),
    KEY users_idx_name (name),
    KEY users_idx_oauthId_oauthType (oauth_id, oauth_type),
    CONSTRAINT fk_users_to_category FOREIGN KEY (category_id) REFERENCES categories (id) ON DELETE SET NULL ON UPDATE CASCADE
) COMMENT '유저 테이블';

CREATE TABLE greenroom_questions
(
    id                  bigint          NOT NULL AUTO_INCREMENT COMMENT 'id',
    question            varchar(50)     NOT NULL COMMENT '질문',
    is_deleted          boolean         NOT NULL DEFAULT FALSE COMMENT '삭제여부',
    created_at          timestamp       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    expired_at          timestamp       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '질문만료일시',
    category_id         bigint          DEFAULT NULL COMMENT '카테고리 id',
    user_id             bigint          DEFAULT NULL COMMENT '질문자 id',
    PRIMARY KEY (id),
    KEY greenroom_questions_idx_expired_at (expired_at),
    CONSTRAINT fk_greenroom_questions_to_category FOREIGN KEY (category_id) REFERENCES categories (id) ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT fk_greenroom_questions_to_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE SET NULL ON UPDATE CASCADE
) COMMENT '그린룸 질문 테이블';

CREATE TABLE greenroom_question_answers
(
    id                  bigint          NOT NULL AUTO_INCREMENT COMMENT 'id',
    answer              varchar(500)    NOT NULL COMMENT '답변',
    keywords            varchar(500)    DEFAULT NULL COMMENT '키워드',
    created_at          timestamp       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    question_id         bigint          DEFAULT NULL COMMENT '그린룸 질문 id',
    user_id             bigint          DEFAULT NULL COMMENT '답변자 id',
    PRIMARY KEY (id),
    KEY greenroom_question_answers_idx_greenroom_questions_user (question_id, user_id),
    CONSTRAINT fk_greenroom_question_answers_to_question FOREIGN KEY (question_id) REFERENCES greenroom_questions (id) ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT fk_greenroom_question_answers_to_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE SET NULL ON UPDATE CASCADE
) COMMENT '그린룸 질문 답변 테이블';

CREATE TABLE greenroom_question_scraps
(
    id                  bigint          NOT NULL AUTO_INCREMENT COMMENT 'id',
    is_deleted          boolean         NOT NULL DEFAULT FALSE COMMENT '삭제여부',
    created_at          timestamp       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at          timestamp       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    question_id         bigint          DEFAULT NULL COMMENT '그린룸 질문 id',
    user_id             bigint          DEFAULT NULL COMMENT '사용자 id',
    PRIMARY KEY (id),
    KEY greenroom_question_scraps_idx_greenroom_questions_user (question_id, user_id),
    CONSTRAINT fk_greenroom_question_scraps_to_question FOREIGN KEY (question_id) REFERENCES greenroom_questions (id) ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT fk_greenroom_question_scraps_to_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE SET NULL ON UPDATE CASCADE
) COMMENT '그린룸 질문 스크랩 테이블';

CREATE TABLE question_groups
(
    id                  bigint          NOT NULL AUTO_INCREMENT COMMENT 'id',
    is_deleted          boolean         NOT NULL DEFAULT FALSE COMMENT '삭제여부',
    name                varchar(10)     NOT NULL COMMENT '그룹명',
    created_at          timestamp       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at          timestamp       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    category_id         bigint          DEFAULT NULL COMMENT '카테고리 id',
    user_id             bigint          DEFAULT NULL COMMENT '사용자 id',
    PRIMARY KEY (id),
    CONSTRAINT fk_question_groups_to_category FOREIGN KEY (category_id) REFERENCES categories (id) ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT fk_question_groups_to_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE SET NULL ON UPDATE CASCADE
) COMMENT '질문 그룹 테이블';

CREATE TABLE interview_questions
(
    id                  bigint          NOT NULL AUTO_INCREMENT COMMENT 'id',
    question_type       int             NOT NULL COMMENT '질문 타입',
    is_deleted          boolean         NOT NULL DEFAULT FALSE COMMENT '삭제여부',
    question            varchar(60)     NOT NULL COMMENT '질문',
    answer              varchar(500)    DEFAULT NULL COMMENT '답변',
    keywords            varchar(500)    DEFAULT NULL COMMENT '키워드',
    created_at          timestamp       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at          timestamp       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    user_id             bigint          DEFAULT NULL COMMENT '사용자 id',
    group_id            bigint          DEFAULT NULL COMMENT '그룹 id',
    category_id         bigint          DEFAULT NULL COMMENT '카테고리 id',
    PRIMARY KEY (id),
    CONSTRAINT fk_interview_questions_to_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT fk_interview_questions_to_group FOREIGN KEY (group_id) REFERENCES question_groups (id) ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT fk_interview_questions_to_category FOREIGN KEY (category_id) REFERENCES categories (id) ON DELETE SET NULL ON UPDATE CASCADE
) COMMENT '면접 질문 테이블';

SET foreign_key_checks = 1;