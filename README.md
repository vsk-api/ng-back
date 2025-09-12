## Spring Boot Backend (ru.pt)

### Requirements
- Java 17+
- Maven 3.8+
- PostgreSQL 13+

### Run locally
1. Create database:
```
createdb pt_db
```
2. Configure credentials in `src/main/resources/application.yml` if different from defaults (`postgres`/`postgres`).
3. Build and run:
```
mvn spring-boot:run
```

App listens on `http://localhost:8080`.

### REST API
- `GET /api/items` – list in-memory items
- `POST /api/items` – create in-memory item (raw string body)

- `GET /api/notes` – list notes
- `POST /api/notes` – create note. Example body:
```json
{
  "title": "Test",
  "content": "Hello"
}
```
- `GET /api/notes/{id}` – get note
- `PUT /api/notes/{id}` – update note
- `DELETE /api/notes/{id}` – delete note

### Database config
Default JDBC URL: `jdbc:postgresql://localhost:5432/pt_db`

Override via environment variables:
```
SPRING_DATASOURCE_URL
SPRING_DATASOURCE_USERNAME
SPRING_DATASOURCE_PASSWORD
```
