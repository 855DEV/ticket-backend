package app.ticket.controller;

import app.ticket.entity.Section;
import app.ticket.entity.Ticket;
import app.ticket.service.TicketService;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSON;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;
import java.text.SimpleDateFormat;

@RestController
@RequestMapping("/search")
public class SearchController {
    private final TicketService ticketService;

    public SearchController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    private class myClass{
        public Ticket t;
        public Integer matchDeg;
        public myClass(Ticket t, Integer matchDeg){
            super();
            this.t = t;
            this.matchDeg = matchDeg;
        }
    }
    private class SortByMatchDeg implements Comparator<myClass> {
        @Override
        public int compare(myClass o1, myClass o2) {
            return -(o1.matchDeg > o2.matchDeg ? 1 : o1.matchDeg == o2.matchDeg? 0 : -1); //descending order
        }
    }
    private class SortByStartDate implements Comparator<myClass> {
        @Override
        public int compare(myClass o1, myClass o2) {
            return (o1.t.getStartDate().compareTo(o2.t.getStartDate())); //descending order
        }
    }

    @GetMapping
    public ResponseEntity<String> getSearch(@RequestParam(value = "s") String text,
                                            @RequestParam(value = "city", defaultValue = "") String city,
                                            @RequestParam(value = "cat", defaultValue = "") String category,
                                            @RequestParam(value = "st", defaultValue = "") String startDate,
                                            @RequestParam(value = "en", defaultValue = "") String endDate,
                                            @RequestParam(value = "o", defaultValue = "2") Integer orderChosen) {
        System.out.println("Search : " + text);
        if (orderChosen < 1 || orderChosen > 3){
            JSONObject res = new JSONObject();
            res.put("code", -1);
            res.put("message", "Bad Request");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res.toJSONString());
        }

        List<Ticket> ticketList = ticketService.findAll();
        System.out.println("total ticket number: " + ticketList.size());
        String sText[] = text.split(" ");
        SimpleDateFormat ft = new SimpleDateFormat ("yyyyMMdd");
        Date st = new Date(), en = new Date();
        if (!startDate.isBlank() && !endDate.isBlank()) {
            //System.out.println("st = " + startDate + ";  en = " + endDate);
            try {
                st = ft.parse(startDate);
                en = ft.parse(endDate);
            } catch (ParseException e) {
                e.printStackTrace();
                System.err.println("ERROR: Failed to parse date string");
            }
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(en);
        calendar.add(Calendar.HOUR, 23);
        calendar.add(Calendar.MINUTE, 59);
        calendar.add(Calendar.SECOND, 59);
        en = calendar.getTime();

        List<myClass> res = new ArrayList<>();
        for (Ticket t : ticketList){
            if (!city.isBlank() && !t.getCity().equals(city)) continue;
            if (!category.isBlank() && !t.getCategory().equals(category)) continue;
            if (!startDate.isBlank() && !endDate.isBlank()){
                Date tst = t.getStartDate(), ten = t.getEndDate();
                if (!((tst.compareTo(st) >= 0 && tst.compareTo(en) <= 0) ||
                      (ten.compareTo(st) >= 0 && ten.compareTo(en) <= 0) ||
                      (st.compareTo(tst) >= 0 && st.compareTo(ten) <= 0) ||
                      (en.compareTo(tst) >= 0 && en.compareTo(ten) <= 0)) )
                    continue;
            }

            Integer cnt = 0;
            //System.out.println("Parse " + t.getName() + ": ");
            for (String s : sText){
                if (s.length() == 0) continue;
                //System.out.println("word " + s + ": " + t.getCategory().contains(s));
                if (t.getName().contains(s) || t.getPlace().contains(s) || t.getCategory().contains(s) || t.getCity().contains(s))
                    cnt++;
            }
            //System.out.println("res = " + cnt);
            if (cnt == 0) continue;
            res.add(new myClass(t, cnt));
        }

        if (orderChosen == 1)
            Collections.sort(res, new SortByMatchDeg());
        else if (orderChosen == 3)
            Collections.sort(res, new SortByStartDate());

        List<JSONObject> j = new ArrayList<>();
        for (myClass r : res)
            j.add(wrapTicket(r.t));
        return ResponseEntity.ok(JSON.toJSONString(j));
    }

    private JSONObject wrapTicket(Ticket ticket) {
        if (ticket == null) return null;
        JSONObject json = new JSONObject();
        json.put("id", ticket.getId());
        json.put("name", ticket.getName());
        json.put("startDate", ticket.getStartDate());
        json.put("endDate", ticket.getEndDate());
        json.put("providers", ticket.getTicketProviders().stream()
                .map((tp) -> {
                    JSONObject tpJson = new JSONObject();
                    tpJson.put("id", tp.getProvider().getId());
                    tpJson.put("name", tp.getProvider().getName());
                    if (tp.getSectionList() != null)
                        tpJson.put("sections",
                                tp.getSectionList().stream().map(this::wrapSection).collect(Collectors.toList()));
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
