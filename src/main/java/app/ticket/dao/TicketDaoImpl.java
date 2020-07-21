package app.ticket.dao;

import app.ticket.entity.Ticket;
import app.ticket.entity.TicketDetail;
import app.ticket.repository.TicketDetailRepository;
import app.ticket.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TicketDaoImpl implements TicketDao {

    private TicketRepository ticketRepository;
    private TicketDetailRepository ticketDetailRepository;

    public TicketDaoImpl(TicketRepository ticketRepository, TicketDetailRepository ticketDetailRepository) {
        this.ticketRepository = ticketRepository;
        this.ticketDetailRepository = ticketDetailRepository;
    }

    // TODO: attach image and intro to results
    // TODO: Pageable query
    @Override
    public List<Ticket> findAll() {
        return ticketRepository.findAll();
    }

    @Override
    public Ticket findOne(Integer id) {
        Ticket ticket = ticketRepository.getOne(id);
        TicketDetail detail = ticketDetailRepository.findByTid(id);
        ticket.setImage(detail.getImg());
        ticket.setIntro(detail.getIntro());
        return ticket;
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
