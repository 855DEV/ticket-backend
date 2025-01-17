package app.ticket.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "ticket")
public class Ticket {
    private Integer id;
    private String name;
    private String place;
    private String category;
    private String city;
    private Date startDate;
    private Date endDate;
    private List<TicketProvider> ticketProviders = new ArrayList<>();
    private String image;
    private String intro;

    public Ticket() {
    }

    public Ticket(String name, String place, String city, Date startDate, Date endDate, String category) {
        this.name = name;
        this.place = place;
        this.city = city;
        this.startDate = startDate;
        this.endDate = endDate;
        this.category = category;
    }

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
    @Column(name = "category")
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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


    @OneToMany(mappedBy = "ticket", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    public List<TicketProvider> getTicketProviders() {
        return ticketProviders;
    }

    public void setTicketProviders(List<TicketProvider> ticketProviderList) {
        this.ticketProviders = ticketProviderList;
    }

    @Transient
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Transient
    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", place='" + place + '\'' +
                ", city='" + city + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", ticketProviders=" + ticketProviders +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ticket ticket = (Ticket) o;
        return id.equals(ticket.id);
    }
}
