package app.ticket.controller;

import app.ticket.entity.*;
import app.ticket.service.OrdersService;
import app.ticket.service.UserService;
import app.ticket.service.TicketService;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

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

    @GetMapping("/{n}")
    public List<JSONObject> getRecommendList(@PathVariable("n") Integer n) {
        User user = userService.getAuthedUser();
        List<Ticket> ticketList = ticketService.findAll();
        List<Orders> orderList = ordersService.getOrdersByUser();

        Map<String, List<Ticket>> ticketTable = new HashMap<>();
        Map<String, Integer> orderCnt;
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
        for (String key : orderCnt){
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
            TicketItem ti = it2.getTicketItem();
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

    private List<Integer> randomNumberGenerator(Integer maxValue, Integer number){ // get number values between [0, maxValue)
        List<Integer> l = new ArrayList<>();
        Random rand = new Random(114514);
        for (int i = 0; i < maxValue; i++)
            l.add(i);
        Collections.shuffle(l, rand);
        List<Integer> ret;
        for (int i = 0; i < number; i++)
            ret.add(l.get(i));
        return ret;
    }
}
