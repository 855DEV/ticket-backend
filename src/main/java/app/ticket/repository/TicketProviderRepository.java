package app.ticket.repository;

import app.ticket.entity.TicketProvider;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketProviderRepository extends JpaRepository<TicketProvider, Integer> {

}
