package app.ticket.controller;

import app.ticket.entity.Section;
import app.ticket.entity.Ticket;
import app.ticket.service.TicketService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/ticket")
public class TicketController {
    @Autowired
    TicketService ticketService;

    @GetMapping
    public List<JSONObject> findAll() {
        System.out.println("GET /ticket");
        return ticketService.findAll().parallelStream().map(this::wrapTicket).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public JSONObject findOne(@PathVariable("id") Integer id) {
        System.out.println("GET /ticket/");
        Ticket ticket = ticketService.findOne(id);
        return wrapTicket(ticket);
    }

    @PostMapping
    public JSONObject insertOne(@RequestBody JSONObject ticketJson) {
        System.out.println("POST /ticket\n" + ticketJson);
        Ticket ticket = ticketService.insertOne(ticketJson);
        System.out.println("insert successfully");
        System.out.println(ticket);
        return wrapTicket(ticket);
    }

    private JSONObject wrapTicket(Ticket ticket) {
        if (ticket == null) return null;
        JSONObject json = new JSONObject();
        json.put("id", ticket.getId());
        json.put("name", ticket.getName());
        json.put("startDate", ticket.getStartDate());
        json.put("endDate", ticket.getEndDate());
        json.put("providers", ticket.getTicketProviders().parallelStream()
                .map((tp) -> {
                    JSONObject tpJson = new JSONObject();
                    tpJson.put("id", tp.getProvider().getId());
                    tpJson.put("name", tp.getProvider().getName());
                    if (tp.getSectionList() != null)
                        tpJson.put("sections",
                                tp.getSectionList().parallelStream().map(this::wrapSection).collect(Collectors.toList()));
                    return tpJson;
                }).collect(Collectors.toList()));
        return json;
    }

    private JSONObject wrapSection(Section section) {
        JSONObject j = new JSONObject();
        j.put("description", section.getDescription());
        j.put("time", section.getTime());
        j.put("items", section.getTicketItemList());
        return j;
    }
}
