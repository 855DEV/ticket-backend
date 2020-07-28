package app.ticket.controller;

import app.ticket.entity.Section;
import app.ticket.entity.Ticket;
import app.ticket.entity.TicketItem;
import app.ticket.entity.User;
import app.ticket.service.TicketService;
import app.ticket.service.UserService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
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
        System.out.println("insert successfully:");
        System.out.println(ticket);
        JSONObject wrappedTicket = wrapTicket(ticket);
        String response = wrappedTicket.toJSONString();
        return ResponseEntity.ok(response);
    }

    private JSONObject wrapTicket(Ticket ticket) {
        if (ticket == null) return null;
        JSONObject json = new JSONObject();
        json.put("id", ticket.getId());
        json.put("name", ticket.getName());
        json.put("startDate", ticket.getStartDate());
        json.put("endDate", ticket.getEndDate());
        List<JSONObject> providersJson =
                ticket.getTicketProviders().stream()
                        .map((tp) -> {
                            JSONObject tpJson = new JSONObject();
                            tpJson.put("id", tp.getProvider().getId());
                            tpJson.put("name", tp.getProvider().getName());
                            List<JSONObject> sectionJson =
                                    tp.getSectionList().stream().map(this::wrapSection).collect(Collectors.toList());
                            JSONArray sections = new JSONArray();
                            sections.addAll(sectionJson);
                            tpJson.put("sections", sections);
                            return tpJson;
                        }).collect(Collectors.toList());
        JSONArray providers = new JSONArray(Collections.singletonList(providersJson));
        json.put("providers", providers);
        json.put("image", ticket.getImage());
        json.put("intro", ticket.getIntro());
        return json;
    }

    private JSONObject wrapSection(Section section) {
        JSONObject j = new JSONObject();
        j.put("description", section.getDescription());
        j.put("time", section.getTime());
        List<JSONObject> ticketItemList = new ArrayList<>();
        if (section.getTicketItemList() != null)
            ticketItemList =
                    section.getTicketItemList().stream().map(this::wrapTicketItem).collect(Collectors.toList());
        JSONArray ticketItems =
                new JSONArray(Collections.singletonList(ticketItemList));
        j.put("items", ticketItems);
        return j;
    }

    private JSONObject wrapTicketItem(TicketItem ticketItem) {
        JSONObject j = new JSONObject();
        j.put("price", ticketItem.getPrice());
        j.put("description", ticketItem.getDescription());
        return j;
    }
}
