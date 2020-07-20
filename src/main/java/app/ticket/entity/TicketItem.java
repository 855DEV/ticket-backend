package app.ticket.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "ticket_item")
public class TicketItem {
    private Integer id;
    private BigDecimal price;
    private String description;
    private Section section;

    public TicketItem() {
    }

    public TicketItem(BigDecimal price, String description) {
        this.price = price;
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
    @Column(name = "price")
    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Basic
    @Column(name = "description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @ManyToOne
    @JoinColumn(name = "section_id", nullable = false)
    @JsonIgnore
    public Section getSection() {
        return section;
    }

    public void setSection(Section section) {
        this.section = section;
    }

    @Override
    public String toString() {
        return "TicketItem{" +
                "id=" + id +
                ", price=" + price +
                ", description='" + description + '\'' +
                '}';
    }
}
