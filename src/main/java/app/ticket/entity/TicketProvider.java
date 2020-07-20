package app.ticket.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * TicketProvider is a class holding all sections for a show/ticket from one
 * specific provider. The class is not just a relationship between Ticket
 * and Provider. For this reason, we need to turn this table into an entity.
 */

@Entity
@Table(name = "ticket_provider")
public class TicketProvider {
    private Integer id;
    private Provider provider;
    private Ticket ticket;
    private String link;
    private List<Section> sectionList = new ArrayList<>();

    public TicketProvider() {
    }

    public TicketProvider(Provider provider, Ticket ticket) {
        this.provider = provider;
        this.ticket = ticket;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "provider_id")
    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    @ManyToOne
    @JoinColumn(name = "ticket_id", nullable = false)
    @JsonIgnore // avoid mutual dependency
    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "tp_id")
    public List<Section> getSectionList() {
        return sectionList;
    }

    public void setSectionList(List<Section> sectionList) {
        this.sectionList = sectionList;
    }

    @Override
    public String toString() {
        return "TicketProvider{" +
                "id=" + id +
                ", provider=" + provider +
                ", link='" + link + '\'' +
                ", sectionList=" + sectionList +
                '}';
    }
}
