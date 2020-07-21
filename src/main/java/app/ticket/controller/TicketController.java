package app.ticket.controller;

import app.ticket.entity.Section;
import app.ticket.entity.Ticket;
import app.ticket.entity.User;
import app.ticket.service.TicketService;
import app.ticket.service.UserService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/ticket")
public class TicketController {
    TicketService ticketService;
    UserService userService;

    public TicketController(TicketService ticketService, UserService userService) {
        this.ticketService = ticketService;
        this.userService = userService;
    }

    @GetMapping
    public List<JSONObject> findAll() {
        System.out.println("GET /ticket");
        return ticketService.findAll().parallelStream().map(this::wrapTicket).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public JSONObject findOne(@PathVariable("id") Integer id) {
        System.out.println("GET /ticket/" + id);
        Ticket ticket = ticketService.findOne(id);
        return wrapTicket(ticket);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> insertOne(@RequestBody JSONObject ticketJson) {
        User user = userService.getAuthedUser();
        System.out.println("POST /ticket\n" + user + "\n" + ticketJson);
        if (user == null || user.getType() != 0) {
            JSONObject res = new JSONObject();
            res.put("code", -1);
            res.put("message", "Access denied.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(res.toJSONString());
        }
        System.out.println("POST /ticket\n" + ticketJson);
        Ticket ticket = ticketService.insertOne(ticketJson);
        System.out.println("insert successfully");
        System.out.println(ticket);
        return ResponseEntity.ok(wrapTicket(ticket).toJSONString());
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
        json.put("image", ticket.getImage());
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
