package app.ticket.service;

import app.ticket.entity.Ticket;
import app.ticket.repository.ProviderRepository;
import app.ticket.setup.TestContext;
import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
@DirtiesContext // reset db
public class TicketServiceTest {
    @Autowired
    private TicketService ticketService;

    @Autowired
    private ProviderRepository providerRepository;

    /**
     * Setup basic entities like providers to avoid unexpected failures.
     */
    @BeforeEach
    public void setup() {
        System.out.println("setup providers...");
        TestContext.setUpProvider(providerRepository, 20);
    }

    @Test
    @DirtiesContext
    public void findAll() {
        List<Ticket> initList = ticketService.findAll();
        assertNotNull(initList);
        assertEquals(0, initList.size());
    }

    @Test
    public void insertOneSimple() {
        JSONObject ticketJson = new JSONObject();
        ticketJson.put("name", "Test Ticket Name");
        ticketJson.put("startDate", "2077-01-07");
        ticketJson.put("endDate", "2077-07-07");
        ticketJson.put("place", "Somewhere");
        ticketJson.put("city", "Shanghai");
        Ticket actual = ticketService.insertOne(ticketJson);
        assertNotNull(actual);
        assertEquals("Test Ticket Name", actual.getName());
    }

    @Test
    @DirtiesContext
    public void insertOneNormal() {
        String example = "{\n" +
                "    \"name\": \"上海魔法世界\",\n" +
                "    \"startDate\": \"2020-07-18 10:42:00\",\n" +
                "    \"endDate\": \"2020-08-12 11:00:00\",\n" +
                "    \"providers\": [\n" +
                "        {\n" +
                "            \"id\": 1,\n" +
                "            \"sections\": [\n" +
                "                {\n" +
                "                    \"description\": \"日场\",\n" +
                "                    \"time\": \"2020-07-18 08:00:00\",\n" +
                "                    \"items\": [\n" +
                "                        {\n" +
                "                            \"description\": \"成人票\",\n" +
                "                            \"price\": 99\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"description\": \"学生票\",\n" +
                "                            \"price\": 49\n" +
                "                        }\n" +
                "                    ]\n" +
                "                },\n" +
                "                {\n" +
                "                    \"description\": \"夜场\",\n" +
                "                    \"time\": \"2020-07-18 21:30:00\",\n" +
                "                    \"items\": [\n" +
                "                        {\n" +
                "                            \"description\": \"成人票\",\n" +
                "                            \"price\": 49\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"description\": \"学生票\",\n" +
                "                            \"price\": 29\n" +
                "                        }\n" +
                "                    ]\n" +
                "                }\n" +
                "            ]\n" +
                "        }, {\n" +
                "            \"id\": 2,\n" +
                "            \"sections\": [\n" +
                "                {\n" +
                "                    \"description\": \"日间场\",\n" +
                "                    \"time\": \"2020-07-18 07:00:00\",\n" +
                "                    \"items\": [\n" +
                "                        {\n" +
                "                            \"description\": \"成人票\",\n" +
                "                            \"price\": 199\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"description\": \"学生票\",\n" +
                "                            \"price\": 149\n" +
                "                        }\n" +
                "                    ]\n" +
                "                },\n" +
                "                {\n" +
                "                    \"description\": \"晚间场\",\n" +
                "                    \"time\": \"2020-07-18 21:40:00\",\n" +
                "                    \"items\": [\n" +
                "                        {\n" +
                "                            \"description\": \"成人票\",\n" +
                "                            \"price\": 149\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"description\": \"学生票\",\n" +
                "                            \"price\": 129\n" +
                "                        }\n" +
                "                    ]\n" +
                "                }\n" +
                "            ]\n" +
                "        }\n" +
                "    ]\n" +
                "}";
        JSONObject ticketJson = JSONObject.parseObject(example);
        assertNotNull(ticketJson, "JSON parsing failed.");
        Ticket actual = ticketService.insertOne(ticketJson);
        assertNotNull(actual);
        assertEquals("上海魔法世界", actual.getName());
        assertEquals(2, actual.getTicketProviders().size());
    }
}
