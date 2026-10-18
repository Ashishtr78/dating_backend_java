# Dating Backend

Spring Boot 4, Java 25, PostgreSQL, Flyway, stateless JWT auth.

## Run

```bash
export JWT_SECRET=$(openssl rand -base64 48)
docker compose up --build
```

Without Docker: start PostgreSQL, then `JWT_SECRET=... ./mvnw spring-boot:run`.

| Variable | Default |
|---|---|
| `DB_URL` | `jdbc:postgresql://localhost:5432/dating` |
| `DB_USERNAME` / `DB_PASSWORD` | `dating` / `dating` |
| `JWT_SECRET` | required, 32+ characters |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:3000,http://localhost:5173` |

## API

All endpoints except `/api/auth/**` need `Authorization: Bearer <accessToken>`.
Errors are RFC 9457 problem details; validation errors include an `errors` map of field to message.

| Method | Path | Body / Query | Returns |
|---|---|---|---|
| POST | `/api/auth/register` | `email, password, displayName, birthDate, gender, interestedIn` | `accessToken, refreshToken, expiresIn` |
| POST | `/api/auth/login` | `email, password` | tokens |
| POST | `/api/auth/refresh` | `refreshToken` | new tokens (old refresh token is revoked) |
| POST | `/api/auth/logout` | `refreshToken` | 204 |
| GET | `/api/profile` | | own profile |
| PUT | `/api/profile` | `displayName, bio, gender, interestedIn, photoUrls[], minAge, maxAge, maxDistanceKm` | own profile |
| PUT | `/api/profile/location` | `latitude, longitude` | 204 |
| DELETE | `/api/profile` | | 204, deletes account and all data |
| GET | `/api/discovery` | `?limit=20` | `[{profile, distanceKm}]`, 409 until location is set |
| POST | `/api/swipes` | `targetId, liked` | `matched, matchId` |
| GET | `/api/matches` | `?page=0&size=50` | `[{id, profile, matchedAt}]` |
| DELETE | `/api/matches/{id}` | | 204 |
| GET | `/api/matches/{id}/messages` | `?before=<ISO instant>&size=50` | messages, newest first |
| POST | `/api/matches/{id}/messages` | `body` | message |

`gender`: `MALE`, `FEMALE`, `NON_BINARY`. `interestedIn`: the same plus `EVERYONE`.
Photos are HTTPS URLs; upload files to object storage from the client and send the URLs.
# dating_backend_java
