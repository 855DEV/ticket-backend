package app.ticket.entity;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "section")
public class Section {
    private Integer id;
    private Date time;
    private String description;
    private List<TicketItem> ticketItemList;

    public Section() {
    }

    public Section(Date time, String description) {
        this.time = time;
        this.description = description;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic
    @Column(name = "id")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Basic
    @Column(name = "time")
    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    @Basic
    @Column(name = "description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @OneToMany(cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "section_id")
    public List<TicketItem> getTicketItemList() {
        return ticketItemList;
    }

    public void setTicketItemList(List<TicketItem> ticketItemList) {
        this.ticketItemList = ticketItemList;
    }

    @Override
    public String toString() {
        return "Section{" +
                "id=" + id +
                ", time=" + time +
                ", description='" + description + '\'' +
                ", ticketItemList=" + ticketItemList +
                '}';
    }
}
