package app.ticket.setup;

import app.ticket.entity.*;
import app.ticket.repository.*;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.assertj.core.internal.bytebuddy.utility.RandomString;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class TestContext {
    // For tests only, exists only in memory database
    public static final String adminInitUsername = "admin";
    public static final String adminInitPassword = "init$pass";
    public static final String adminInitPasswordEncrypted = "$2a$10$dZ5AZRC.RtrRNswLTipOueIqZCLeCeEKMtPYTetTodelOwQ5m5Zou";

    public static final String AUTH_STRING = "Authorization";

    public static void setUpProvider(ProviderRepository providerRepository,
                                     int amount) {
        System.out.println("Setting up: providers");
        if (providerRepository == null) {
            fail();
            return;
        }
        for (int i = 0; i < amount; i++) {
            providerRepository.save(new Provider("Test Provider " + i, "www.test.com"));
        }
    }

    /**
     * Save x tickets of category "mo" and y tickets of category "ha"
     */
    public static void saveTickets(ProviderRepository providerRepository, TicketRepository ticketRepository, Integer x, Integer y){
        for (int i = 1; i <= x; i++){
            Ticket t = TestContext.createTicket(providerRepository, Integer.toString(i), "SJTU", "上海", "19890604", "20200718", "mo", false);
            ticketRepository.save(t);
        }
        for (int i = 1; i <= y; i++){
            Ticket t = TestContext.createTicket(providerRepository, Integer.toString(i + x), "SJTU", "上海", "19890604", "20200718", "ha", false);
            ticketRepository.save(t);
        }
    }

    /**
     * create a JSONObject as requestbody in POST "/order"
     */
    public static JSONObject getOrderPOSTJSON(List<TicketItem> tl){
        JSONArray items = new JSONArray();
        for (TicketItem it : tl){
            JSONObject itemJson = new JSONObject();
            itemJson.put("ticketItemId", it.getId());
            itemJson.put("amount", 1);
            items.add(itemJson);
        }
        JSONObject postJson = new JSONObject();
        postJson.put("items", items);
        return postJson;
    }

    /**
     * Create a simple Ticket object
     * The caller must prepare providers before calling the function.
     *
     * @param providerRepository a provider repository with some initialized
     *                           provider
     * @return Created Ticket object
     */
    public static Ticket createTicket(ProviderRepository providerRepository, String name, String place, String city, String st, String en, String cat, Boolean emptyProvider) {
        SimpleDateFormat ft = new SimpleDateFormat("yyyyMMdd");
        Date startDate = new Date(), endDate = new Date();
        try {
            startDate = ft.parse(st);
            endDate = ft.parse(en);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Ticket ticket = new Ticket(name, place, city, startDate, endDate, cat);
        List<TicketProvider> tps = new ArrayList<>();
        if (emptyProvider) {
            ticket.setTicketProviders(tps);
            return ticket;
        }
        List<Provider> providers = providerRepository.findAll();
        assertNotNull(providers.get(0));
        TicketProvider tp = new TicketProvider(providers.get(0), ticket);
        Section sec = new Section(new Date(), "A sample section");
        TicketItem ticketItem = new TicketItem(new BigDecimal("100.00"), "A ticket");
        ticketItem.setSection(sec);
        List<TicketItem> ticketItemList = new ArrayList<>();
        ticketItemList.add(ticketItem);
        sec.setTicketItemList(ticketItemList);
        sec.setTicketProvider(tp);
        List<Section> sectionList = new ArrayList<>();
        sectionList.add(sec);
        tp.setSectionList(sectionList);
        tps.add(tp);
        ticket.setTicketProviders(tps);
        return ticket;
    }


    public static Ticket createTicket(ProviderRepository providerRepository) {
        return TestContext.createTicket(providerRepository, "name", "place", "city", "19260817", "19890604", "mo", false);
    }

    /**
     * Register and return user with Authorization header.
     * The result map includes two entries:
     * user: JSONString of registered user, with password encoded.
     * auth: Authorization token, can be put in request header to get
     * authorization.
     *
     * @param mockMvc MockMvc Object in context
     * @throws Exception some exception
     */
    public static Map<String, String> createUserAuth(MockMvc mockMvc) throws Exception {
        JSONObject registerAdminJson = new JSONObject();
        registerAdminJson.put("username", "admin");
        registerAdminJson.put("password", "dsi2#35*kx&x3^2@4x%cv$12#3");
        String result =
                mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerAdminJson.toJSONString()))
                        .andReturn().getResponse().getContentAsString();
        System.out.println(result);
        JSONObject loginJson = new JSONObject();
        loginJson.put("username", "admin");
        loginJson.put("password", "dsi2#35*kx&x3^2@4x%cv$12#3");
        String auth =
                mockMvc.perform(post("/login").contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson.toJSONString())).andReturn().getResponse().getHeader("Authorization");
        System.out.println("Auth: " + auth);
        Map<String, String> map = new HashMap<>();
        map.put("user", result);
        map.put("auth", auth);
        return map;
    }

    /**
     * Create the first admin user to enable further tests
     *
     * @param userRepository UserRepository in context
     */
    public static void createGodAdmin(UserRepository userRepository) {
        User user = new User();
        user.setUsername(adminInitUsername);
        user.setPassword(adminInitPasswordEncrypted);
        user.setType(0);
        userRepository.save(user);
        System.out.println(userRepository.findByUsername(adminInitUsername));
    }

    /**
     * Emulate god admin login and return an authorization token.
     * Call createGodAdmin before call this function.
     *
     * @param mockMvc MockMvc in context
     */
    public static String getGodAdminAuth(MockMvc mockMvc) throws Exception {
        JSONObject loginJson = new JSONObject();
        loginJson.put("username", adminInitUsername);
        loginJson.put("password", adminInitPassword);
        return mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson.toJSONString()))
                .andReturn()
                .getResponse().getHeader("Authorization");
    }

    /**
     * Simply create a user and insert it into database.
     * @param userRepository caller's user repository
     * @return user inserted to database. Available entries are User user,
     * String rawPassword
     */
    public static Map<String, Object> createOneUser(UserRepository userRepository) {
        User user = new User();
        user.setUsername(RandomString.make());
        String rawPassword = RandomString.make();
        BCryptPasswordEncoder bCryptPasswordEncoder =
                new BCryptPasswordEncoder();
        String encrypted = bCryptPasswordEncoder.encode(rawPassword);
        user.setPassword(encrypted);
        User saved = userRepository.save(user);
        Map<String, Object> data = new HashMap<>();
        data.put("password", rawPassword);
        data.put("user", saved);
        return data;
    }

    /**
     * Given a user, return its auth token.
     * Note that the user should be newly created, and cannot be fetched from
     * database because we have to know its raw password to perform oprations.
     * @param mockMvc caller's MockMvc
     * @param username username
     * @param password password
     * @return auth token string
     * @throws Exception in mock MVC operations
     */
    public static String getUserAuth(MockMvc mockMvc, String username,
                                     String password) throws Exception {
        JSONObject loginJson = new JSONObject();
        loginJson.put("username", username);
        loginJson.put("password", password);
        return mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson.toJSONString()))
                .andReturn()
                .getResponse().getHeader("Authorization");
    }
}
