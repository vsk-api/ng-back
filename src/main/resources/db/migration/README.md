# Database Migrations with Flyway

This directory contains Flyway database migration scripts for the PT application.

## Migration Naming Convention

Migration files must follow the naming pattern: `V{version}__{description}.sql`

Examples:
- `V1__Initial_schema.sql` - Initial database schema
- `V2__Add_user_table.sql` - Adding a new user table
- `V3__Update_product_indexes.sql` - Updating indexes on product table

## Creating New Migrations

1. Create a new SQL file in this directory following the naming convention
2. Use the next sequential version number
3. Write your SQL DDL/DML statements
4. Test the migration locally before committing

## Migration Best Practices

- Always use `IF NOT EXISTS` for CREATE statements when possible
- Use transactions for complex migrations
- Test rollback scenarios
- Keep migrations small and focused
- Never modify existing migration files once they've been applied to production

## Running Migrations

Migrations run automatically when the Spring Boot application starts.

To run migrations manually:
```bash
mvn flyway:migrate
```

To check migration status:
```bash
mvn flyway:info
```

## Rollback Considerations

Flyway doesn't support automatic rollbacks. For rollbacks:
1. Create a new migration that undoes the changes
2. Use version numbers higher than the migration being rolled back
3. Test rollback migrations thoroughly
