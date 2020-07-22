package app.ticket.controller;

import app.ticket.entity.Section;
import app.ticket.entity.Ticket;
import app.ticket.service.TicketService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/search")
public class SearchController {
    private final TicketService ticketService;

    public SearchController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping
    public List<JSONObject> getSearch(@RequestParam(value="s") String text) {
        List<Ticket> ticketList = ticketService.findAll();
        String sText[] = text.split(" ");
        List<Ticket> res[] = new List[10];
        for (int i = 0; i < 10; i++)
            res[i] = new ArrayList<>();
        for (Ticket t : ticketList){
            Integer cnt = 0;
            for (String s : sText){
                if (s.length() == 0) continue;
                if (t.getName().contains(s) || t.getPlace().contains(s) || t.getCategory().contains(s) || t.getCity().contains(s))
                    cnt++;
            }
            res[cnt].add(t);
        }
        List<JSONObject> j = new ArrayList<>();
        for (int i = 9; i > 0; i--) {
            for (Ticket t : res[i])
                j.add(wrapTicket(t));
        }
        return j;
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
