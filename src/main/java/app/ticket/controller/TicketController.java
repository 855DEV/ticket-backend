package app.ticket.controller;

import app.ticket.entity.Ticket;
import app.ticket.entity.User;
import app.ticket.service.TicketService;
import app.ticket.service.UserService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static app.ticket.util.TicketAdapter.wrapTicket;

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
    public JSONObject findAll(@RequestParam(name = "page", required = false) Integer page,
                              @RequestParam(name = "size", required = false) Integer size) {
        System.out.println("GET /ticket?page=" + page + "&size=" + size);
        page = (page == null) ? 0 : page;
        size = (size == null) ? 10 : size;
        Page<Ticket> ticketPage = ticketService.findByPage(page, size);
        List<Ticket> ticketList = ticketPage.getContent();
        int total = ticketPage.getTotalPages();
        boolean next = ticketPage.hasNext();
        boolean last = ticketPage.isLast();
        List<JSONObject> content = new ArrayList<>();
        for (Ticket t: ticketList) {
            content.add(wrapTicket(t));
        }
        JSONObject data = new JSONObject();
        data.put("data", content);
        data.put("next", next);
        data.put("last", last);
        data.put("total", total);
        return data;
    }

    @GetMapping("/{id}")
    public JSONObject findOne(@PathVariable("id") Integer id) {
        System.out.println("GET /ticket/" + id);
        Ticket ticket = ticketService.findOne(id);
        return wrapTicket(ticket);
    }

    @GetMapping("/random")
    public List<JSONObject> getRandomByCategory(@RequestParam("category") String category,
            @RequestParam("limit") int limit) {
        List<Ticket> tickets = ticketService.getRandomByCategory(category, limit);
        List<JSONObject> resData = new ArrayList<>();
        for (Ticket t : tickets) {
            resData.add(wrapTicket(t));
        }
        return resData;
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
        Ticket ticket = ticketService.insertOne(ticketJson);
        if(ticket == null) {
            // insertion failed
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
        }
        System.out.println("insert successfully:");
        System.out.println(ticket);
        JSONObject wrappedTicket = wrapTicket(ticket);
        String response = wrappedTicket.toJSONString();
        return ResponseEntity.ok(response);
    }
}
