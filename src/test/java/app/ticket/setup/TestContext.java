package app.ticket.setup;

import app.ticket.entity.*;
import app.ticket.repository.ProviderRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TestContext {

    public static void setUpProvider(ProviderRepository providerRepository) {
        System.out.println("Setting up tests context");
        if (providerRepository != null)
            providerRepository.save(new Provider("Test Provider", "www.test.com"));
    }

    /**
     * Create a simple Ticket object
     * @param providerRepository a provider repository with some initialized
     *                           provider
     * @return Created Ticket object
     */
    public static Ticket createTicket(ProviderRepository providerRepository) {
        Ticket ticket = new Ticket("Ticket Name", "place", "city",
                new Date(), new Date());
        List<TicketProvider> tps = new ArrayList<>();
        TicketProvider tp =
                new TicketProvider(providerRepository.findAll().get(0), ticket);
        Section sec = new Section(new Date(), "A sample section");
        TicketItem ticketItem = new TicketItem(new BigDecimal("100.00"), "A " +
                "ticket");
        List<TicketItem> ticketItemList = new ArrayList<>();
        ticketItemList.add(ticketItem);
        sec.setTicketItemList(ticketItemList);
        List<Section> sectionList = new ArrayList<>();
        sectionList.add(sec);
        tp.setSectionList(sectionList);
        tps.add(tp);
        ticket.setTicketProviders(tps);
        return ticket;
    }
}
