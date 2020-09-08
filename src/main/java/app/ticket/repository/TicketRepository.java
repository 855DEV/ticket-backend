package app.ticket.repository;

import app.ticket.entity.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Integer> {
    @Override
    Page<Ticket> findAll(Pageable pageable);

    List<Ticket> findByNameAndCityAndCategory(String name, String city,
                                              String category);

    @Query("select ticket from Ticket ticket where ticket" +
            ".city = ?1 and ticket.category=?2 and ticket.startDate >= ?3 and" +
            " ticket.endDate <= ?4")
    List<Ticket> findInDate(String city, String category,
                            Date start, Date end);

    @Query(nativeQuery = true, value = "select * from ticket where " +
            "category=?1 order by rand() limit ?2")
    List<Ticket> randomGetByCategory(String category, int limit);
}
