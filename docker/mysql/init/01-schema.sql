-- calto 로컬 개발용 스키마 (운영 DB 스키마 덤프 기반)
-- mysql 컨테이너 최초 기동 시 docker-entrypoint-initdb.d 에 의해 1회 실행됨
SET NAMES utf8mb4;

CREATE TABLE `blog` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `background_image_url` varchar(255) DEFAULT NULL COMMENT '배경 이미지 URL',
  `background_type` enum('COLOR','IMAGE') NOT NULL DEFAULT 'COLOR' COMMENT '배경 모드 (COLOR/IMAGE)',
  `created_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '생성 시각',
  `deleted_at` datetime(6) DEFAULT NULL COMMENT '삭제 시각',
  `image_url` varchar(255) NOT NULL COMMENT '블로그 대표 이미지',
  `main_color` enum('PINK','YELLOW','PURPLE','WHITE') NOT NULL DEFAULT 'WHITE' COMMENT '메인 컬러 (PINK/YELLOW/PURPLE/WHITE)',
  `name` varchar(255) NOT NULL COMMENT '블로그 이름',
  `updated_at` datetime(6) DEFAULT NULL COMMENT '수정 시각',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `blog_member` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `blog_id` bigint NOT NULL COMMENT '소속된 블로그 ID',
  `comments` varchar(255) DEFAULT NULL COMMENT '프로필 코멘트',
  `created_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '가입 시각',
  `deleted_at` datetime(6) DEFAULT NULL COMMENT '탈퇴/추방 시각',
  `image_url` varchar(255) NOT NULL COMMENT '블로그용 프로필 이미지 URL',
  `name` varchar(255) NOT NULL COMMENT '블로그용 닉네임(블로그 내 중복 불가)',
  `role` enum('OWNER','ADMIN','MEMBER') NOT NULL COMMENT '블로그 내 역할(OWNER/ADMIN/MEMBER)',
  `updated_at` datetime(6) DEFAULT NULL COMMENT '수정 시각',
  `user_id` bigint NOT NULL COMMENT 'OAuth 유저 PK',
  PRIMARY KEY (`id`),
  KEY `idx_blog_member_blog_active` (`blog_id`,`deleted_at`),
  KEY `idx_blog_member_user_active` (`user_id`,`deleted_at`),
  KEY `idx_blog_member_blog_user_active` (`blog_id`,`user_id`,`deleted_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `invite` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `blog_id` bigint NOT NULL COMMENT '초대 대상 블로그',
  `code` varchar(64) NOT NULL COMMENT '초대 코드 (UUID, unique)',
  `created_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '발급 시각',
  `expires_at` datetime(6) NOT NULL COMMENT '만료 시각',
  `invite_user_id` bigint NOT NULL COMMENT '코드 발급자 user_id',
  `used_at` datetime(6) DEFAULT NULL COMMENT '코드 사용 시각',
  `used_user_id` bigint DEFAULT NULL COMMENT '코드 사용자 user_id (NULL 이면 미사용)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_invite_code` (`code`),
  KEY `idx_invite_creator` (`blog_id`,`invite_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `refresh_token` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '발급 시각',
  `expires_at` datetime(6) NOT NULL COMMENT '토큰 만료 시각',
  `token` varchar(255) DEFAULT NULL COMMENT 'JWT refresh 토큰',
  `user_id` bigint NOT NULL COMMENT '토큰 소유 user_id',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_refresh_token_token` (`token`),
  KEY `idx_refresh_token_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '가입 시각',
  `deleted_at` datetime(6) DEFAULT NULL COMMENT '탈퇴 시각',
  `is_profile_set` tinyint(1) NOT NULL DEFAULT '0' COMMENT '최초 프로필 설정 완료 여부',
  `nickname` varchar(255) NOT NULL COMMENT '프로필 닉네임 (unique)',
  `profile_image_url` varchar(255) DEFAULT NULL COMMENT '프로필 이미지 URL',
  `provider` enum('KAKAO','GOOGLE') NOT NULL COMMENT 'OAuth 제공자',
  `provider_id` varchar(255) NOT NULL COMMENT 'OAuth 제공자 내 유저 식별자',
  `updated_at` datetime(6) DEFAULT NULL COMMENT '수정 시각',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_provider_identity` (`provider`,`provider_id`),
  UNIQUE KEY `uk_user_nickname` (`nickname`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
