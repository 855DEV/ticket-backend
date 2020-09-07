package app.ticket.controller;

import app.ticket.entity.OrderItem;
import app.ticket.entity.Orders;
import app.ticket.entity.User;
import app.ticket.service.OrdersService;
import app.ticket.service.UserService;
import app.ticket.util.Message;
import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
public class OrdersController {
    private final OrdersService ordersService;
    private final UserService userService;

    public OrdersController(OrdersService ordersService, UserService userService) {
        this.ordersService = ordersService;
        this.userService = userService;
    }

    @GetMapping
    public Object getOrdersByUser() {
        User user = userService.getAuthedUser();
        System.out.println(user);
        return ordersService.getUserOrders(user.getId());
    }

    @GetMapping("/{id}")
    public Orders getOrder(@PathVariable(name = "id") Integer id) {
        Orders order = ordersService.getOne(id);
        if (!userService.canDo(order.getUser().getId())) {
            return null;
        }
        System.out.println("Order:");
        System.out.println(order);
        for (OrderItem i : order.getOrderItemList()) {
            System.out.println(i);
        }
        return order;
    }

    @GetMapping("/pay")
    public Message payOrder(@RequestParam("id") Integer id) {
        Orders order = ordersService.getOne(id);
        if (order == null)
            return new Message(1, "Order not exist.");
        if (!userService.canDo(order.getUser().getId()))
            return new Message(2, "Access denied.");
        ordersService.payOrder(id);
        return new Message(0, "success");
    }

    @PostMapping
    public JSONObject createOrder(@RequestBody JSONObject info) {
        User user = userService.getAuthedUser();
        System.out.println("POST /orders" + user + " " + info);

        ordersService.addOne(user, info);
        return null;
    }
}
