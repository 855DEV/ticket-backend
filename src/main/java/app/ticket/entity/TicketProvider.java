package app.ticket.entity;

import javax.persistence.*;
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
    private String link;
    private List<Section> sectionList;

    public TicketProvider() {
    }

    public TicketProvider(Integer id) {
        this.id = id;
    }

    @Id
    @Column(name = "id")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @ManyToOne
    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    @OneToMany
    @JoinTable(name = "section", joinColumns = @JoinColumn(name = "id"),
            inverseJoinColumns = @JoinColumn(name = "tp_id"))
    public List<Section> getSectionList() {
        return sectionList;
    }

    public void setSectionList(List<Section> sectionList) {
        this.sectionList = sectionList;
    }
}
