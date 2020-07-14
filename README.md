# Ticket App Backend

## Configuration

For safety concerns, `application.properties` file is ignored by git and any changes should be updated into `application.properties.example` and discard any sensitive information like passwords.

## Documentation

### Ticket Data

#### Insertion

Inserting a new ticket involves 3 tables: `ticket`, `ticket_provider`, `ticket_item`.  
First, a new tuple `ticket` should be inserted. Then, insert its providers to `ticket_provider`. Finally, insert every
 section
 in every provider to `ticket_items`.
