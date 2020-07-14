package app.ticket.repository;

import app.ticket.entity.TicketItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketItemRepository extends JpaRepository<TicketItem, Integer> {
}
