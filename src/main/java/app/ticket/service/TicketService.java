package app.ticket.service;

import app.ticket.entity.Ticket;
import com.alibaba.fastjson.JSONObject;
import org.springframework.data.domain.Page;

import java.util.List;

public interface TicketService {
    List<Ticket> findAll();

    Page<Ticket> findByPage(int pageId, int size);

    Ticket findOne(Integer id);

    List<Ticket> getRandomByCategory(String category, int limit);

    Ticket insertOne(JSONObject ticket);

    Ticket insertOne(Ticket ticket);
}
