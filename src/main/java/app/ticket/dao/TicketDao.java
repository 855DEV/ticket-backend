package app.ticket.dao;

import app.ticket.entity.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TicketDao {

    List<Ticket> findAll();

    Page<Ticket> findByPage(Pageable page);

    Ticket findOne(Integer id);

    Ticket insertOne(Ticket ticket);
}
