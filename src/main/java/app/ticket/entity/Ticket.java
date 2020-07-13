package app.ticket.entity;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "ticket")
public class Ticket {
    private Integer id;
    private String name;
    private String place;
    private String city;
    private Date startDate;
    private Date endDate;
    private List<Provider> provider;
    private List<TicketProvider> ticketProviderList;

    @Id
    @Basic
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Basic
    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "place")
    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    @Basic
    @Column(name = "city")
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Column(name = "start_date")
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @Column(name = "end_date")
    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @ManyToMany
    @JoinTable(name = "ticket_provider",
            joinColumns = @JoinColumn(name = "ticket_id"),
            inverseJoinColumns = @JoinColumn(name = "provider_id"))
    public List<Provider> getProvider() {
        return provider;
    }

    public void setProvider(List<Provider> provider) {
        this.provider = provider;
    }

    @OneToMany
    @JoinTable(name = "ticket_provider",
            joinColumns = @JoinColumn(name = "ticket_id"),
            inverseJoinColumns = @JoinColumn(name = "provider_id"))
    public List<TicketProvider> getTicketProviderList() {
        return ticketProviderList;
    }

    public void setTicketProviderList(List<TicketProvider> ticketProviderList) {
        this.ticketProviderList = ticketProviderList;
    }
}
