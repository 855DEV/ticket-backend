package app.ticket.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "order_item")
@IdClass(OrderItemId.class)
public class OrderItem {
    private Orders order;
    private TicketItem ticketItem;
    private Integer amount;

    public OrderItem() {
    }

    public OrderItem(Orders order, TicketItem ticketItem, Integer amount) {
        this.order = order;
        this.ticketItem = ticketItem;
        this.amount = amount;
    }

    @Id
    @ManyToOne
    @JoinColumn(name = "order_id")
    @JsonIgnore
    public Orders getOrder() {
        return order;
    }

    public void setOrder(Orders order) {
        this.order = order;
    }

    @Id
    @ManyToOne
    @JoinColumn(name = "ticket_item_id")
    public TicketItem getTicketItem() {
        return ticketItem;
    }

    public void setTicketItem(TicketItem ticketItem) {
        this.ticketItem = ticketItem;
    }

    @Basic
    @Column(name = "amount")
    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }
}

class OrderItemId implements Serializable {
    private Integer order;
    private Integer ticketItem;

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Integer getTicketItem() {
        return ticketItem;
    }

    public void setTicketItem(Integer ticketItem) {
        this.ticketItem = ticketItem;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderItemId that = (OrderItemId) o;
        return Objects.equals(order, that.order) &&
                Objects.equals(ticketItem, that.ticketItem);
    }

    @Override
    public int hashCode() {
        return Objects.hash(order, ticketItem);
    }
}
