package app.ticket.util;

import app.ticket.entity.Section;
import app.ticket.entity.Ticket;
import app.ticket.entity.TicketItem;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TicketAdapter {
    public static JSONObject wrapTicket(Ticket ticket) {
        if (ticket == null) return null;
        JSONObject json = new JSONObject();
        json.put("id", ticket.getId());
        json.put("name", ticket.getName());
        json.put("startDate", ticket.getStartDate());
        json.put("endDate", ticket.getEndDate());
        json.put("category", ticket.getCategory());
        json.put("city", ticket.getCity());
        json.put("place", ticket.getPlace());
        List<JSONObject> providersJson =
                ticket.getTicketProviders().stream()
                        .map((tp) -> {
                            JSONObject tpJson = new JSONObject();
                            tpJson.put("id", tp.getProvider().getId());
                            tpJson.put("name", tp.getProvider().getName());
                            List<JSONObject> sectionJson =
                                    tp.getSectionList().stream().map(TicketAdapter::wrapSection).collect(Collectors.toList());
                            JSONArray sections = new JSONArray();
                            sections.addAll(sectionJson);
                            tpJson.put("sections", sections);
                            return tpJson;
                        }).collect(Collectors.toList());
        JSONArray providers = new JSONArray();
        providers.addAll(providersJson);
        json.put("providers", providers);
        json.put("image", ticket.getImage());
        json.put("intro", ticket.getIntro());
        return json;
    }

    public static JSONObject wrapSection(Section section) {
        JSONObject j = new JSONObject();
        j.put("description", section.getDescription());
        j.put("time", section.getTime());
        List<JSONObject> ticketItemList = new ArrayList<>();
        if (section.getTicketItemList() != null)
            ticketItemList =
                    section.getTicketItemList().stream().map(TicketAdapter::wrapTicketItem).collect(Collectors.toList());
        JSONArray ticketItems =
                new JSONArray();
        ticketItems.addAll(ticketItemList);
        j.put("items", ticketItems);
        return j;
    }

    public static JSONObject wrapTicketItem(TicketItem ticketItem) {
        JSONObject j = new JSONObject();
        j.put("price", ticketItem.getPrice());
        j.put("description", ticketItem.getDescription());
        return j;
    }
}
