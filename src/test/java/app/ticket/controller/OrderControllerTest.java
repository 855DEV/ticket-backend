package app.ticket.controller;

import app.ticket.entity.Ticket;
import app.ticket.entity.TicketItem;
import app.ticket.repository.*;
import app.ticket.service.UserService;
import app.ticket.setup.TestContext;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class OrderControllerTest {
    @Autowired
    RecommendController recommendController;

    @Autowired
    TicketRepository ticketRepository;

    @Autowired
    ProviderRepository providerRepository;

    @Autowired
    TicketProviderRepository ticketProviderRepository;

    @Autowired
    SectionRepository sectionRepository;

    @Autowired
    TicketItemRepository ticketItemRepository;

    @Autowired
    OrdersRepository ordersRepository;

    @Autowired
    UserService userService;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MockMvc mockMvc;
    private String authToken;

    @BeforeEach
    public void setup() throws Exception {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        TestContext.setUpProvider(providerRepository, 20);
        TestContext.saveTickets(providerRepository, ticketRepository, 10, 10);
        Map<String, String> mm = TestContext.createUserAuth(mockMvc);
        authToken = mm.get("auth");
        System.out.println("Auth token: " + authToken);

    }

    @Test
    public void CreateOrder_and_getOrdersByUser() throws Exception{

        List<TicketItem> tl = ticketItemRepository.findAll();

        JSONArray items = new JSONArray();
        for (TicketItem it : tl){
            JSONObject itemJson = new JSONObject();
            itemJson.put("ticketItemId", it.getId());
            itemJson.put("amount", 1);
            items.add(itemJson);
        }
        System.out.println("items: " + items);
        JSONObject postJson = new JSONObject();
        postJson.put("items", items);

        // Test: POST an order
        System.out.println("POST an order");
        RequestBuilder authedRequest = post("/order")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(postJson.toJSONString());
        String result = mockMvc.perform(authedRequest)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONObject res = JSONObject.parseObject(result);
        System.out.println(result);
        System.out.println("__________________OK____________________");


        // Test: GET the order
        System.out.println();
        result = mockMvc.perform(get("/order").header("Authorization", authToken)).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        System.out.println(result);
        System.out.println("__________________OK____________________");
    }
}
