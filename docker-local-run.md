# calto 로컬 개발 환경 (FE 작업용)

docker-compose 로 백엔드 API + MySQL 을 한 번에 띄웁니다. 더미 데이터와 장기 유효 JWT 가 미리 준비되어 있어 **OAuth 로그인 없이 바로 API 를 호출**할 수 있습니다.

## 실행

```bash
docker compose up -d --build
```

- API: `http://localhost:8080` (헬스체크: `GET /health`)
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- MySQL: `localhost:13306` / db `calto` / user `calto` / pw `calto1234`

컨테이너는 `app(Spring Boot, :8080)` + `mysql(:13306)` 두 개이며, 설정(DB 접속·JWT·초대 정책)은 전부 `docker-compose.yml` 의 환경변수로 주입됩니다.

> **CORS 참고**: 백엔드에 CORS 설정이 없어 브라우저에서 직접 `localhost:8080` 을 호출하면 CORS 에 막힐 수 있습니다. FE 개발 서버의 proxy 기능(Next.js `rewrites`, Vite `server.proxy` 등)으로 `/api` → `http://localhost:8080` 을 프록시해서 사용하세요.

최초 기동 시 MySQL 볼륨이 비어 있으면 `docker/mysql/init/*.sql` 이 자동 실행되어 스키마 + 더미 데이터가 적재됩니다. **데이터를 초기 상태로 리셋**하려면:

```bash
docker compose down -v && docker compose up -d --build
```

## 인증 (개발용 토큰)

모든 API(`/auth/**`, `/health`, swagger 제외)는 `Authorization: Bearer <accessToken>` 헤더가 필요합니다. 아래 토큰은 시드 유저용 개발 토큰으로 **2028-08-13 까지(2년) 유효**합니다.

| userId | 닉네임 | 상태 | accessToken |
|---|---|---|---|
| 1 | 몽글이 | blog1 OWNER, blog2 MEMBER | `eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwidHlwZSI6ImFjY2VzcyIsImlhdCI6MTc4NjY3MTY2NywiZXhwIjoxODQ5NzQzNjY3fQ.zBM_OtAs2hzNwVwnCdRvynBQywd2EcGo7Mp4W7-ymm8` |
| 2 | 다이어리언 | blog1 ADMIN, blog2 OWNER | `eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIyIiwidHlwZSI6ImFjY2VzcyIsImlhdCI6MTc4NjY3MTY2NywiZXhwIjoxODQ5NzQzNjY3fQ.nHPkJLj22te48d3neTKBao2DWekueznUJPDxbtrZKxk` |
| 3 | 별사탕 | blog1 MEMBER | `eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIzIiwidHlwZSI6ImFjY2VzcyIsImlhdCI6MTc4NjY3MTY2NywiZXhwIjoxODQ5NzQzNjY3fQ.6mqxQLArFOHOXFsuj_yQGGs-7bM7vd4lp5FX51a-Zlk` |
| 4 | 새싹이 | 프로필 미설정(온보딩 테스트용), 블로그 없음 | `eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI0IiwidHlwZSI6ImFjY2VzcyIsImlhdCI6MTc4NjY3MTY2NywiZXhwIjoxODQ5NzQzNjY3fQ.ngJ6y2HMpNhq7z-RlISu7SVhsiumegWPlx6JpKJb5nw` |

refresh 토큰(user 1, 2)은 `docker/mysql/init/02-seed.sql` 의 `refresh_token` 시드에 있으며 `POST /auth/refresh` 로 rotate 테스트가 가능합니다. (`JWT_SECRET` 을 바꾸거나 다른 유저의 토큰이 필요하면 [jwt.io](https://jwt.io) 에서 HS256 + compose 의 `JWT_SECRET` 값으로 `{"sub":"<userId>","type":"access","exp":<epoch>}` payload 를 서명해 만들면 됩니다.)

사용 예:

```bash
curl -H "Authorization: Bearer <user1 accessToken>" http://localhost:8080/blogs
```

실제 카카오/구글 로그인(`POST /auth/oauth2/{provider}/callback`)을 붙이려면 `docker-compose.yml` 의 주석 처리된 OAuth 환경변수를 해제하고 키를 채운 뒤 `docker compose up -d` 로 재기동하세요. 키가 없어도 앱은 정상 동작하며 해당 엔드포인트만 `OAUTH_PROVIDER_NOT_REGISTERED` 를 반환합니다.

## 응답 포맷

모든 응답은 아래 envelope 로 감싸집니다. 시큐리티 단계에서 거절돼도 동일 포맷입니다 (인증 없음/잘못된 토큰 → 401 `UNAUTHORIZED`, 권한 부족 → 403 `FORBIDDEN`).

```json
{ "data": { }, "code": 200, "errorCode": null, "message": "성공" }
{ "data": null, "code": 404, "errorCode": "BLOG_NOT_FOUND", "message": "..." }
```

## API 요약

인증 (`/auth/**` 는 토큰 불필요):

| 메서드 | 경로 | 설명 |
|---|---|---|
| POST | `/auth/oauth2/{KAKAO\|GOOGLE}/callback` | 인가 코드로 로그인, `{accessToken, refreshToken, userId, isNewUser}` 반환 |
| POST | `/auth/refresh` | body `{refreshToken}` — 토큰 재발급 (rotate) |
| POST | `/auth/logout` | body `{refreshToken}` — refresh 토큰 폐기 |

유저 (`/users`):

| 메서드 | 경로 | 설명 |
|---|---|---|
| POST | `/users/profile` | 최초 프로필 설정 (`isProfileSet` 전환) — user 4 로 테스트 |
| GET | `/users/nickname/check?nickname=` | 닉네임 중복 확인 |
| GET | `/users/me` | 내 프로필 조회 |
| PUT | `/users/me` | 내 프로필 수정 |
| DELETE | `/users/me` | 회원 탈퇴 (soft delete) |

블로그 (`/blogs`):

| 메서드 | 경로 | 설명 |
|---|---|---|
| GET | `/blogs` | 내가 속한 블로그 목록 |
| GET | `/blogs/{blogId}` | 블로그 상세 |
| POST | `/blogs` | 블로그 생성 (생성자가 OWNER) |
| PUT | `/blogs/{blogId}` | 블로그 수정 |
| PUT | `/blogs/{blogId}/background/mainColor` | 메인 컬러 변경 (`PINK/YELLOW/PURPLE/WHITE`) |
| PUT | `/blogs/{blogId}/background/image` | 배경 이미지 변경 |

블로그 멤버 (`/blogs/{blogId}/members`):

| 메서드 | 경로 | 설명 |
|---|---|---|
| GET | `/blogs/{blogId}/members` | 멤버 목록 |
| GET | `/blogs/{blogId}/members/me` | 내 멤버 프로필 |
| GET | `/blogs/{blogId}/members/me/nickname/check?name=` | 블로그 내 닉네임 중복 확인 |
| GET | `/blogs/{blogId}/members/{blogMemberId}` | 멤버 상세 |
| PUT | `/blogs/{blogId}/members/me` | 내 멤버 프로필 수정 |
| PUT | `/blogs/{blogId}/members/{blogMemberId}` | 역할 변경 (`OWNER/ADMIN/MEMBER`) |
| DELETE | `/blogs/{blogId}/members/me` | 블로그 탈퇴 |
| DELETE | `/blogs/{blogId}/members/{blogMemberId}` | 멤버 추방 (OWNER) |

초대 (`/blogs/{blogId}/invites`):

| 메서드 | 경로 | 설명 |
|---|---|---|
| POST | `/blogs/{blogId}/invites` | 초대 코드 발급 (OWNER/ADMIN, TTL 24h) — 초대 URL 반환 |
| GET | `/blogs/{blogId}/invites` | 내가 발급한 활성 코드 조회 |
| DELETE | `/blogs/{blogId}/invites/{code}` | 코드 삭제 |
| POST | `/blogs/{blogId}/invites/{code}/join` | 코드로 블로그 가입 (body 에 블로그용 닉네임 등) |

## 더미 데이터 시나리오

- **blog 1 「몽글몽글 다이어리」** (PINK/COLOR): 몽글이(OWNER) · 다이어리언(ADMIN) · 별사탕(MEMBER)
- **blog 2 「우리들의 기록장」** (PURPLE/IMAGE 배경): 다이어리언(OWNER) · 몽글이(MEMBER) · 떠난별(soft delete 된 탈퇴 멤버)
- **초대 코드**
  - 활성: `a1b2c3d4e5f60718293a4b5c6d7e8f90` (blog 1, 2030년 만료) → user 4 토큰으로 `POST /blogs/1/invites/a1b2c3d4e5f60718293a4b5c6d7e8f90/join` 가입 플로우 테스트 가능
  - 만료: `b2c3d4e5f60718293a4b5c6d7e8f90a1` (blog 2) → 만료 에러 케이스
  - 사용됨: `c3d4e5f60718293a4b5c6d7e8f90a1b2` (blog 1) → 이미 사용된 코드 에러 케이스
- **user 4 「새싹이」**: `isProfileSet=false` — 온보딩(프로필 설정) 플로우 테스트용

> ⚠️ 이 디렉터리의 시크릿/비밀번호는 전부 로컬 개발 전용입니다. 운영 환경에 사용하지 마세요.
