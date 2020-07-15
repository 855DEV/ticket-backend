package app.ticket.dao;

import app.ticket.entity.Ticket;

import java.util.List;

public interface TicketDao {

    List<Ticket> findAll();

    Ticket findOne(Integer id);

    Ticket insertOne(Ticket ticket);
}
