# Ticket App Backend

![Tests](https://github.com/855DEV/ticket-backend/workflows/Tests/badge.svg)

## Configuration

For safety concerns, `application.properties` file is ignored by `git` and any changes should be updated into `application.properties.example` and discard any sensitive information like passwords.

## Documentation

### Spring Profiles

Spring profiles are at `src/main/resources`, `application-local` is for local debug and build and `application-prod
` is for production release. `pom.xml`  declares these profiles.
Run with flag like `-Dspring.profiles.active=local` to enable a local configuration.

### Ticket Data

#### Insertion

Inserting a new ticket involves 3 tables: `ticket`, `ticket_provider`, `ticket_item`.  
First, a new tuple `ticket` should be inserted. Then, insert its providers to `ticket_provider`. Finally, insert every
 section
 in every provider to `ticket_items`.
