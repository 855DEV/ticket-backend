package app.ticket.dao;

import app.ticket.entity.TicketItem;
import app.ticket.repository.TicketItemRepository;
import org.springframework.stereotype.Repository;

@Repository
public class TicketItemDaoImpl implements TicketItemDao {

    private final TicketItemRepository ticketItemRepository;

    public TicketItemDaoImpl(TicketItemRepository ticketItemRepository) {
        this.ticketItemRepository = ticketItemRepository;
    }

    @Override
    public TicketItem getOne(Integer id) {
        return ticketItemRepository.getOne(id);
    }
}
