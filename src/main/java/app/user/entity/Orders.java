package app.ticket.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.sql.Timestamp;
import java.util.List;
import java.math.BigDecimal;

@Entity
@Table(name = "orders")
public class Orders {
    private Integer id;
    private Integer uid;
    private Timestamp time;
    private BigDecimal price;
    private List<OrderItem> OrderItems = new ArrayList<>();

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
    @Column(name = "uid")
    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    @Basic
    @Column(name = "time")
    public String getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    @Basic
    @Column(name = "price")
    public String getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }


    @OneToMany(mappedBy = "orders", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    public List<TicketProvider> getOrderItems() {
        return OrderItems;
    }

    public void setOrderItems(List<OrderItem> OrderItems) {
        this.OrderItems = OrderItems;
    }

    @Override
    public String toString() {
        return "Orders{" +
                "id=" + id + '\'' +
                ", uid='" + uid + '\'' +
                ", time='" + time + '\'' +
                ", price='" + price + '\'' +
                '}';
    }
}
