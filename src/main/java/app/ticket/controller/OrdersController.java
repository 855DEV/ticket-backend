package app.ticket.controller;

import app.ticket.entity.OrderItem;
import app.ticket.entity.Orders;
import app.ticket.entity.Ticket;
import app.ticket.entity.User;
import app.ticket.service.OrdersService;
import app.ticket.service.TicketService;
import app.ticket.service.UserService;
import app.ticket.util.Message;
import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static app.ticket.util.TicketAdapter.wrapTicket;
import static app.ticket.util.TicketAdapter.wrapTicketItem;

@RestController
@RequestMapping("/order")
public class OrdersController {
    private final OrdersService ordersService;
    private final UserService userService;
    private final TicketService ticketService;

    public OrdersController(OrdersService ordersService,
                            UserService userService, TicketService ticketService) {
        this.ordersService = ordersService;
        this.userService = userService;
        this.ticketService = ticketService;
    }

    @GetMapping
    public List<JSONObject> getOrdersByUser() {
        User user = userService.getAuthedUser();
        System.out.println(user);
        List<Orders> orders = ordersService.getUserOrders(user.getId());
        List<JSONObject> data = new ArrayList<>();
        for (Orders o : orders)
            data.add(wrapOrder(o));
        return data;
    }

    public JSONObject wrapOrder(Orders order) {
        JSONObject json = new JSONObject();
        json.put("id", order.getId());
        json.put("time", order.getTime());
        json.put("price", order.getPrice());
        json.put("state", order.getState());
        // attach ticket info for every ticketItem
        List<JSONObject> orderList = new ArrayList<>();
        for (OrderItem item : order.getOrderItemList()) {
            Ticket ticket =
                    item.getTicketItem().getSection().getTicketProvider().getTicket();
            Ticket t = ticketService.findOne(ticket.getId());
            JSONObject i = new JSONObject();
            i.put("ticket", wrapTicket(t));
            i.put("amount", item.getAmount());
            i.put("ticketItem", wrapTicketItem(item.getTicketItem()));
            orderList.add(i);
        }
        json.put("orderItemList", orderList);
        return json;
    }

    @GetMapping("/{id}")
    public JSONObject getOrder(@PathVariable(name = "id") Integer id) {
        Orders order = ordersService.getOne(id);
        if (!userService.canDo(order.getUser().getId())) {
            return null;
        }
        System.out.println("Order:");
        System.out.println(order);
        for (OrderItem i : order.getOrderItemList()) {
            System.out.println(i);
        }
        return wrapOrder(order);
    }

    @GetMapping("/pay")
    public Message payOrder(@RequestParam("id") Integer id) {
        Orders order = ordersService.getOne(id);
        if (order == null)
            return new Message(1, "Order not exist.");
        if (!userService.canDo(order.getUser().getId()))
            return new Message(2, "Access denied.");
        if (order.getState() == 1)
            return new Message(3, "This order has been paid.");
        ordersService.payOrder(id);
        return new Message(0, "success");
    }

    @PostMapping
    public JSONObject createOrder(@RequestBody JSONObject info) {
        User user = userService.getAuthedUser();
        System.out.println("POST /orders" + user + " " + info);
        if (user == null)
            return new Message(1, "User not exist.");
        ordersService.addOne(user, info);
        return new Message(0, "Success");
    }
}
