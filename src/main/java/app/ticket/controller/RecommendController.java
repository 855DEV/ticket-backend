package app.ticket.controller;

import app.ticket.entity.*;
import app.ticket.service.OrdersService;
import app.ticket.service.UserService;
import app.ticket.service.TicketService;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.*;

@RestController
@RequestMapping("/recommend")
@CacheConfig(cacheNames = {"lastResult"})
public class RecommendController {
    private final UserService userService;
    private final TicketService ticketService;
    private final OrdersService ordersService;

    public RecommendController(OrdersService ordersService, UserService userService, TicketService ticketService) {
        this.ordersService = ordersService;
        this.userService = userService;
        this.ticketService = ticketService;
    }

    @GetMapping
    public List<JSONObject> getRecommendList(@RequestParam(value="n",defaultValue="10") Integer n) {
        User user = userService.getAuthedUser();
        List<Ticket> ticketList = ticketService.findAll();
        //System.out.println(user);
        //System.out.println(ticketList);
        //for (Ticket t : ticketList)
        //    System.out.println(t);
        List<Orders> orderList = ordersService.getUserOrders(user.getId());

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
            if (user == null || orderList.isEmpty())
                orderCnt.put(key, 1);
            orderSize += orderCnt.get(key);
        }
        System.out.println("orderCnt: " + orderCnt);

        List<Ticket> result = new ArrayList<>();
        Integer dif = 0;
        Integer currentTicketSize = 0, currentParsedOrder = 0;
        for (String key : orderCnt.keySet()){
            Integer need = currentTicketSize;
            currentParsedOrder += orderCnt.get(key);
            while (need.doubleValue() / n < currentParsedOrder.doubleValue() / orderSize && need - currentTicketSize < ticketTable.get(key).size())
                need ++;

            List<Integer> randomInt = randomNumberGenerator(ticketTable.get(key).size(), need - currentTicketSize);
            currentTicketSize = need;
            List<Ticket> tmp = ticketTable.get(key);
            for (Integer i : randomInt)
                result.add(tmp.get(i));
            System.out.println("dif = " + dif + "; curP = " + currentParsedOrder + "; curT = " + currentTicketSize);
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

    @Cacheable
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
