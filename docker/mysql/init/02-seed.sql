-- calto 로컬 개발용 더미 데이터
-- 시드 유저용 JWT 토큰은 docker-local-run.md 참고
SET NAMES utf8mb4;

-- ── user ─────────────────────────────────────────────────────────────
-- 1~3: 프로필 설정 완료 유저 / 4: OAuth 직후 프로필 미설정 유저 (온보딩 플로우 테스트용)
INSERT INTO `user` (`id`, `nickname`, `profile_image_url`, `is_profile_set`, `provider`, `provider_id`, `created_at`, `updated_at`) VALUES
  (1, '몽글이',   'https://picsum.photos/seed/calto-user1/200', 1, 'KAKAO',  'kakao_1001',  '2026-07-01 10:00:00', NULL),
  (2, '다이어리언', 'https://picsum.photos/seed/calto-user2/200', 1, 'GOOGLE', 'google_2002', '2026-07-02 11:30:00', NULL),
  (3, '별사탕',   'https://picsum.photos/seed/calto-user3/200', 1, 'KAKAO',  'kakao_3003',  '2026-07-10 09:15:00', NULL),
  (4, '새싹이',   NULL,                                          0, 'GOOGLE', 'google_4004', '2026-08-13 20:00:00', NULL);

-- ── blog ─────────────────────────────────────────────────────────────
INSERT INTO `blog` (`id`, `name`, `image_url`, `main_color`, `background_type`, `background_image_url`, `created_at`, `updated_at`) VALUES
  (1, '몽글몽글 다이어리', 'https://picsum.photos/seed/calto-blog1/400', 'PINK',   'COLOR', NULL,                                            '2026-07-01 10:05:00', NULL),
  (2, '우리들의 기록장',   'https://picsum.photos/seed/calto-blog2/400', 'PURPLE', 'IMAGE', 'https://picsum.photos/seed/calto-blog2-bg/800', '2026-07-02 12:00:00', NULL);

-- ── blog_member ──────────────────────────────────────────────────────
-- blog 1: user1(OWNER) + user2(ADMIN) + user3(MEMBER)
-- blog 2: user2(OWNER) + user1(MEMBER) + user3(탈퇴, deleted_at 세팅)
INSERT INTO `blog_member` (`id`, `blog_id`, `user_id`, `name`, `image_url`, `comments`, `role`, `created_at`, `updated_at`, `deleted_at`) VALUES
  (1, 1, 1, '몽글',     'https://picsum.photos/seed/calto-bm1/200', '블로그 주인장입니다',    'OWNER',  '2026-07-01 10:05:00', NULL, NULL),
  (2, 1, 2, '달빛',     'https://picsum.photos/seed/calto-bm2/200', '관리 담당이에요',        'ADMIN',  '2026-07-03 14:00:00', NULL, NULL),
  (3, 1, 3, '별사탕',   'https://picsum.photos/seed/calto-bm3/200', NULL,                     'MEMBER', '2026-07-11 18:20:00', NULL, NULL),
  (4, 2, 2, '기록왕',   'https://picsum.photos/seed/calto-bm4/200', '두 번째 블로그!',        'OWNER',  '2026-07-02 12:00:00', NULL, NULL),
  (5, 2, 1, '몽글몽글', 'https://picsum.photos/seed/calto-bm5/200', NULL,                     'MEMBER', '2026-07-05 16:40:00', NULL, NULL),
  (6, 2, 3, '떠난별',   'https://picsum.photos/seed/calto-bm6/200', '탈퇴한 멤버(soft delete)', 'MEMBER', '2026-07-06 10:00:00', '2026-08-01 09:00:00', '2026-08-01 09:00:00');

-- ── invite ───────────────────────────────────────────────────────────
-- 1: blog1 활성 코드 (user4 로 가입 테스트 가능: POST /blogs/1/invites/{code}/join)
-- 2: blog2 만료 코드 (만료 에러 케이스 테스트)
-- 3: blog1 사용 완료 코드 (user3 이 이미 사용)
INSERT INTO `invite` (`id`, `blog_id`, `code`, `invite_user_id`, `expires_at`, `used_user_id`, `used_at`, `created_at`) VALUES
  (1, 1, 'a1b2c3d4e5f60718293a4b5c6d7e8f90', 1, '2030-01-01 00:00:00', NULL, NULL,                  '2026-08-10 10:00:00'),
  (2, 2, 'b2c3d4e5f60718293a4b5c6d7e8f90a1', 2, '2026-08-01 00:00:00', NULL, NULL,                  '2026-07-30 10:00:00'),
  (3, 1, 'c3d4e5f60718293a4b5c6d7e8f90a1b2', 2, '2026-07-12 00:00:00', 3,    '2026-07-11 18:20:00', '2026-07-11 10:00:00');

-- ── refresh_token ────────────────────────────────────────────────────
-- 기본 JWT_SECRET(calto-local-dev-jwt-secret-key-0123456789abcdef)으로 서명된 refresh 토큰.
-- POST /auth/refresh 테스트 가능 (만료: 2028-08-13, 사용 시 rotate 되어 새 토큰이 내려감)
INSERT INTO `refresh_token` (`id`, `user_id`, `token`, `expires_at`, `created_at`) VALUES
  (1, 1, 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwidHlwZSI6InJlZnJlc2giLCJpYXQiOjE3ODY2NzE2NjcsImV4cCI6MTg0OTc0MzY2N30.h5VuuKL7clw4iNWszL9t9Nb7wCERN1FlPO0iOnLBOBs', '2028-08-13 10:41:07', '2026-08-14 00:00:00'),
  (2, 2, 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIyIiwidHlwZSI6InJlZnJlc2giLCJpYXQiOjE3ODY2NzE2NjcsImV4cCI6MTg0OTc0MzY2N30.hnA3RrQG5qUlEnbutfROTr5CErMA_YIhOFKMCV6rabM', '2028-08-13 10:41:07', '2026-08-14 00:00:00');
