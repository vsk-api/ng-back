# Flyway Database Migration Setup

## Overview
This project now includes Flyway database migration support for managing database schema changes in a controlled and versioned manner.

## What Was Implemented

### 1. Dependencies Added
- `flyway-core` - Core Flyway functionality
- `flyway-database-postgresql` - PostgreSQL-specific Flyway support
- `flyway-maven-plugin` - Maven plugin for command-line migration management

### 2. Configuration Changes
- **application.yml**: Added Flyway configuration with proper settings
- **JPA Configuration**: Changed from `ddl-auto: update` to `ddl-auto: validate` to prevent Hibernate from auto-generating schema
- **Flyway Settings**:
  - `baseline-on-migrate: true` - Allows Flyway to work with existing databases
  - `baseline-version: 0` - Sets baseline version for existing schemas
  - `locations: classpath:db/migration` - Migration files location
  - `validate-on-migrate: true` - Validates migrations before applying

### 3. Migration Structure
```
src/main/resources/db/migration/
├── README.md                           # Migration documentation
├── V1__Initial_schema.sql             # Initial database schema
└── V2__Add_sample_migration.sql       # Sample migration template
```

### 4. Files Created
- **V1__Initial_schema.sql**: Contains all initial database tables and sequences
- **V2__Add_sample_migration.sql**: Example migration showing best practices
- **FlywayConfig.java**: Spring configuration for Flyway
- **application-dev.yml**: Development profile with Flyway debugging enabled

### 5. Files Modified
- **pom.xml**: Added Flyway dependencies and Maven plugin
- **application.yml**: Updated JPA and added Flyway configuration
- **Removed**: `schema.sql` (replaced by Flyway migrations)

## Usage

### Running Migrations
Migrations run automatically when the Spring Boot application starts.

### Manual Migration Commands (if Maven is available)
```bash
# Check migration status
mvn flyway:info

# Run pending migrations
mvn flyway:migrate

# Validate applied migrations
mvn flyway:validate

# Clean database (development only)
mvn flyway:clean
```

### Creating New Migrations
1. Create a new SQL file in `src/main/resources/db/migration/`
2. Use naming convention: `V{version}__{description}.sql`
3. Use next sequential version number
4. Write your DDL/DML statements
5. Test locally before committing

### Example Migration
```sql
-- V3__Add_user_audit_table.sql
CREATE TABLE IF NOT EXISTS pt_user_audit (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(100) NOT NULL,
    action VARCHAR(50) NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS pt_user_audit_user_id_idx ON pt_user_audit(user_id);
```

## Benefits
- **Version Control**: Database changes are versioned and tracked
- **Team Collaboration**: All team members get consistent database schema
- **Deployment Safety**: Controlled, tested schema changes
- **Rollback Support**: Easy to create rollback migrations
- **Environment Consistency**: Same schema across dev/staging/production

## Migration Best Practices
- Always use `IF NOT EXISTS` for CREATE statements
- Keep migrations small and focused
- Test migrations thoroughly
- Never modify existing migration files once applied
- Use transactions for complex migrations
- Document breaking changes

## Troubleshooting
- Check `application-dev.yml` for development-specific Flyway settings
- Enable Flyway debug logging: `logging.level.org.flywaydb: DEBUG`
- Use `baseline-on-migrate: true` for existing databases
- Validate schema with `ddl-auto: validate`
