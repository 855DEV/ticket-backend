package app.ticket.controller;

import app.ticket.entity.User;
import app.ticket.service.OrdersService;
import app.ticket.service.UserService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

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
        //System.out.println("GET /orders " + user + "; " + user.getOrders().size());
        //System.out.println("getOrders: " + ordersService.getUserOrders(user.getId()).size());
        //return user.getOrders();
        return ordersService.getUserOrders(user.getId());
    }

    @PostMapping
    public JSONObject createOrder(@RequestBody JSONObject info) {
        User user = userService.getAuthedUser();
        System.out.println("POST /orders" + user + " " + info);

        ordersService.addOne(user, info);
        return null;
    }
}
