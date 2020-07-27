package app.ticket.dao;

import app.ticket.entity.Ticket;
import app.ticket.entity.TicketDetail;
import app.ticket.repository.TicketDetailRepository;
import app.ticket.repository.TicketRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TicketDaoImpl implements TicketDao {

    private final TicketRepository ticketRepository;
    private final TicketDetailRepository ticketDetailRepository;

    public TicketDaoImpl(TicketRepository ticketRepository, TicketDetailRepository ticketDetailRepository) {
        this.ticketRepository = ticketRepository;
        this.ticketDetailRepository = ticketDetailRepository;
    }

    // TODO: attach image and intro to results
    @Override
    public List<Ticket> findAll() {
        return ticketRepository.findAll();
    }

    // TODO: attach image and intro
    @Override
    public Page<Ticket> findByPage(Pageable page) {
        return ticketRepository.findAll(page);
    }

    @Override
    public Ticket findOne(Integer id) {
        Ticket ticket = ticketRepository.getOne(id);
        TicketDetail detail = ticketDetailRepository.findByTid(id);
        if(detail != null){
            ticket.setImage(detail.getImg());
            ticket.setIntro(detail.getIntro());
        }
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
