package app.ticket.service;

import app.ticket.entity.Ticket;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

public interface TicketService {
    List<Ticket> findAll();

    Ticket insertOne(JSONObject ticket);
}
