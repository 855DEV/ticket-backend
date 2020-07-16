package app.ticket.dao;

import app.ticket.entity.TicketItem;

public interface TicketItemDao {
    TicketItem getOne(Integer id);
}
