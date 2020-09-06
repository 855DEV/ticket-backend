package app.ticket.controller;

import app.ticket.entity.OrderItem;
import app.ticket.entity.Orders;
import app.ticket.entity.User;
import app.ticket.service.OrdersService;
import app.ticket.service.UserService;
import com.alibaba.fastjson.JSONObject;
import org.hibernate.criterion.Order;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
    public Orders getOrder(@PathVariable(name = "id") Integer id){
        Orders order = ordersService.getOne(id);
        if(!userService.canDo(order.getUser().getId())) {
            return null;
        }
        System.out.println("Order:");
        System.out.println(order);
        for (OrderItem i : order.getOrderItemList()) {
            System.out.println(i);
        }
        return order;
    }

    @PostMapping
    public JSONObject createOrder(@RequestBody JSONObject info) {
        User user = userService.getAuthedUser();
        System.out.println("POST /orders" + user + " " + info);

        ordersService.addOne(user, info);
        return null;
    }
}
