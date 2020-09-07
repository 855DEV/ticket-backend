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

    @Override
    public List<Ticket> findAll() {
        List<Ticket> tickets = ticketRepository.findAll();
        tickets.forEach(this::attachDetail);
        return tickets;
    }

    @Override
    public Page<Ticket> findByPage(Pageable page) {
        Page<Ticket> ticketPage = ticketRepository.findAll(page);
        ticketPage.getContent().parallelStream().forEach(this::attachDetail);
        return ticketPage;
    }

    @Override
    public Ticket findOne(Integer id) {
        Ticket ticket = ticketRepository.getOne(id);
        TicketDetail detail = ticketDetailRepository.findByTid(id);
        if (detail != null) {
            ticket.setImage(detail.getImg());
            ticket.setIntro(detail.getIntro());
        } else {
            System.err.println("Ticket " + id + " detail is null");
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

    @Override
    public List<Ticket> getRandomByCategory(String category, int limit) {
        List<Ticket> list = ticketRepository.randomGetByCategory(category, limit);
        list.forEach(this::attachDetail);
        return list;
    }

    /**
     * Attach detailed data, like image and introduction to `ticket`
     *
     * @param ticket target ticket
     */
    private void attachDetail(Ticket ticket) {
        if (ticket == null) return;
        TicketDetail detail =
                ticketDetailRepository.findByTid(ticket.getId());
        if (detail != null) {
            ticket.setImage(detail.getImg());
            ticket.setIntro(detail.getIntro());
        }
    }

}
