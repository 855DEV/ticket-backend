package app.ticket.dao;

import app.ticket.entity.TicketItem;
import app.ticket.repository.TicketItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class TicketItemDaoImpl implements TicketItemDao {
    @Autowired
    TicketItemRepository ticketItemRepository;

    @Override
    public TicketItem getOne(Integer id) {
        return ticketItemRepository.getOne(id);
    }
}
