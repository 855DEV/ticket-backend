package app.ticket.controller;

import app.ticket.entity.*;
import app.ticket.service.OrdersService;
import app.ticket.service.UserService;
import app.ticket.service.TicketService;

import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.*;

@RestController
@RequestMapping("/recommend")
public class RecommendController {
    private final UserService userService;
    private final TicketService ticketService;
    private final OrdersService ordersService;

    public RecommendController(OrdersService ordersService, UserService userService, TicketService ticketService) {
        this.ordersService = ordersService;
        this.userService = userService;
        this.ticketService = ticketService;
    }

    @GetMapping("/{searchText}")
    public List<JSONObject> getSearch(@PathVariable("searchText") String text) {
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

    @GetMapping("/{n}")
    public List<JSONObject> getRecommendList(@PathVariable("n") Integer n) {
        User user = userService.getAuthedUser();
        List<Ticket> ticketList = ticketService.findAll();
        List<Orders> orderList = user.getOrders();

        Map<String, List<Ticket>> ticketTable = new HashMap<>();
        Map<String, Integer> orderCnt = new HashMap<>();
        for (Ticket t : ticketList){
            String category = t.getCategory();
            if (!ticketTable.containsKey(category)) {
                ticketTable.put(category, new ArrayList<>());
                orderCnt.put(category, 0);
            }
            ticketTable.get(category).add(t);
        }
        for (Orders o : orderList){
            List<Ticket> tl = getTickets(o);
            for (Ticket t : tl){
                String category = t.getCategory();
                if (!orderCnt.containsKey(category)) {
                    ticketTable.put(category, new ArrayList<>());
                    orderCnt.put(category, 0);
                }
                Integer c = orderCnt.get(category);
                orderCnt.put(category, c + 1);
            }
        }

        Integer ticketSize = ticketList.size();
        Integer orderSize = 0;
        for (String key : orderCnt.keySet()){
            if (user == null)
                orderCnt.put(key, 1);
            orderSize += orderCnt.get(key);
        }

        List<Ticket> result = new ArrayList<>();
        Integer dif = 0;
        Integer currentTicketSize = 0, currentParsedOrder = 0;
        for (String key : orderCnt.keySet()){
            Integer need = currentTicketSize;
            currentParsedOrder += orderCnt.get(key);
            while (need.doubleValue() / n < currentParsedOrder.doubleValue() / orderSize)
                need ++;
            Integer g = ticketTable.get(key).size(); // maxValue
            Integer f = need - currentTicketSize + dif; // numberNeeded

            List<Integer> randomInt;
            if (g < f){
                randomInt = randomNumberGenerator(g, g);
                dif += (need - currentTicketSize) - g;
            }
            else{
                randomInt = randomNumberGenerator(g, f);
                dif = 0;
            }
            currentTicketSize += g;

            List<Ticket> tmp = ticketTable.get(key);
            for (Integer i : randomInt)
                result.add(tmp.get(i));
        }

        List<JSONObject> j = new ArrayList<>();
        for (Ticket t : result)
            j.add(wrapTicket(t));
        return j;
    }

    private List<Ticket> getTickets(Orders order){
        List<OrderItem> oi = order.getOrderItemList();
        List<Ticket> ret = new ArrayList<>();
        for (OrderItem it : oi){
            TicketItem ti = it.getTicketItem();
            Section s = ti.getSection();
            TicketProvider tp = s.getTicketProvider();
            Ticket t = tp.getTicket();
            ret.add(t);
        }
        return ret;
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

    private List<Integer> randomNumberGenerator(Integer maxValue, Integer number){ // get number values between [0, maxValue)
        List<Integer> l = new ArrayList<>();
        Random rand = new Random(114514);
        for (int i = 0; i < maxValue; i++)
            l.add(i);
        Collections.shuffle(l, rand);
        List<Integer> ret = new ArrayList<>();
        for (int i = 0; i < number; i++)
            ret.add(l.get(i));
        return ret;
    }
}
