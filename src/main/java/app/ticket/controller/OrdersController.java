package app.ticket.controller;

import app.ticket.entity.User;
import app.ticket.service.OrdersService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrdersController {
    @Autowired
    OrdersService ordersService;

    //TODO:
    @GetMapping
    public Object getOrdersByUser() {
        String username =
                (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println(username);
        return username;
    }

    @PostMapping
    public JSONObject createOrder(@RequestBody JSONObject info) {
        String user =
                (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println(user);
        // TODO:
//        ordersService.addOne(info);
        return null;
    }
}
