package app.user.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

public class Choice_Map implements Serializable {
    private Integer orderid;
    private Integer ticketItemid;

    public Choice_Map(){
    }

    public Choice_Map(Integer orderid,Integer ticketItemid){
        this.orderid = orderid;
        this.ticketItemid = ticketItemid;

    }
    public Integer getOrderid() {
        return orderid;
    }

    public void setOrderid(Integer orderid) {
        this.orderid = orderid;
    }

    public int getTicketItemid() {
        return ticketItemid;
    }

    public void setTicketItemid(Integer ticketItemid) {
        this.ticketItemid = ticketItemid;
    }
}

@Entity
@IdClass(Choice_Map.class)
@Table(name = "order_item")
public class OrderItme {
    private Integer orderid;
    private Integer ticketItemid;
    private Integer amount = new Integer(1);

    @Id
    @Basic
    @Column(name = "order_id")
    public Integer getOrderId() {
        return orderid;
    }

    public void setOrderId(Integer id) {
        this.orderid = id;
    }

    @Id
    @Basic
    @Column(name = "ticket_item_id")
    public Integer getTicketItemid() {
        return ticketItemid;
    }

    public void setTicketItemid(Integer id) {
        this.ticketItemid = id;
    }

    @Basic
    @Column(name = "amount")
    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }


    @Override
    public String toString() {
        return "OrderItem{" +
                "order_id=" + orderidid + '\'' +
                ", ticket_item_id='" + ticketItemid + '\'' +
                ", amount='" + amount + '\'' +
                '}';
    }
}
