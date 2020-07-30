package app.ticket.service;

import app.ticket.dao.ProviderDao;
import app.ticket.dao.TicketDao;
import app.ticket.entity.*;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TicketServiceImpl implements TicketService {

    private final TicketDao ticketDao;
    private final ProviderDao providerDao;

    public TicketServiceImpl(TicketDao ticketDao, ProviderDao providerDao) {
        this.ticketDao = ticketDao;
        this.providerDao = providerDao;
    }

    @Override
    public List<Ticket> findAll() {
        return ticketDao.findAll();
    }

    @Override
    public Ticket findOne(Integer id) {
        return ticketDao.findOne(id);
    }

    @Override
    public Page<Ticket> findByPage(int pageId, int size) {
        Pageable page = PageRequest.of(pageId, size);
        return ticketDao.findByPage(page);
    }

    @Override
    public Ticket insertOne(JSONObject ticketJson) {
        Ticket ticket = new Ticket();
        String name = ticketJson.getString("name");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate;
        Date endDate;
        try {
            startDate = format.parse(ticketJson.getString("startDate"));
            endDate = format.parse(ticketJson.getString("endDate"));
        } catch (ParseException e) {
            e.printStackTrace();
            System.err.println("ERROR: Failed to parse date string");
            return null;
        }
        String place = ticketJson.getString("place");
        String city = ticketJson.getString("city");
        JSONArray providers = ticketJson.getJSONArray("providers");
        // iterate through all providers
        List<TicketProvider> ticketProviderList = (providers == null) ?
                new ArrayList<>() :
                providers.parallelStream()
                        .map((p) -> {
                            JSONObject jsonProvider = (JSONObject) p;
                            Integer id = jsonProvider.getInteger("id");
                            JSONArray sections = jsonProvider.getJSONArray("sections");
                            TicketProvider tp = new TicketProvider();
                            // iterate through all sections
                            List<Section> sectionList = sections.parallelStream()
                                    .map((sec) -> parseSectionFromJson((JSONObject) sec, tp))
                                    .collect(Collectors.toList());
                            tp.setTicket(ticket);
                            Provider provider = providerDao.getOne(id);
                            if (provider == null)
                                System.err.println("Provider not exist!");
                            tp.setProvider(provider);
                            tp.setSectionList(sectionList);
                            return tp;
                        }).collect(Collectors.toList());
        ticket.setName(name);
        ticket.setStartDate(startDate);
        ticket.setEndDate(endDate);
        ticket.setPlace(place);
        ticket.setCity(city);
        ticket.setTicketProviders(ticketProviderList);
        return ticketDao.insertOne(ticket);
    }

    @Override
    public Ticket insertOne(Ticket ticket) {
        return ticketDao.insertOne(ticket);
    }

    private Section parseSectionFromJson(JSONObject json, TicketProvider tp) {
        String description = json.getString("description");
        String timeString = json.getString("time");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date time = null;
        try {
            time = format.parse(timeString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        JSONArray jsonItems = json.getJSONArray("items");
        Section section = new Section(time, description);
        // iterate through ticket items
        List<TicketItem> ticketItems = jsonItems.parallelStream()
                .map((item) -> parseTicketItemFromJson((JSONObject) item, section))
                .collect(Collectors.toList());
        section.setTicketItemList(ticketItems);
        section.setTicketProvider(tp);
        return section;
    }

    private TicketItem parseTicketItemFromJson(JSONObject json, Section section) {
        String description = json.getString("description");
        BigDecimal price = json.getBigDecimal("price");
        return new TicketItem(price, description, section);
    }
}
