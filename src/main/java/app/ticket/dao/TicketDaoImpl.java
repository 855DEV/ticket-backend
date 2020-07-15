package app.ticket.dao;

import app.ticket.entity.Ticket;
import app.ticket.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TicketDaoImpl implements TicketDao {

    @Autowired
    private TicketRepository ticketRepository;

    @Override
    public List<Ticket> findAll() {
        return ticketRepository.findAll();
    }

    @Override
    public Ticket findOne(Integer id) {
        return ticketRepository.getOne(id);
    }

    @Override
    public Ticket insertOne(Ticket ticket) {
        try {
            return ticketRepository.save(ticket);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
