package pt.ipleiria.estg.dei.ei.dae.daebackend.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

@Entity
@NamedQueries({
        @NamedQuery(
                name = "getAllEndConsumers",
                query = "SELECT ec FROM EndConsumer ec ORDER BY ec.name"
        )
})
public class EndConsumer extends User implements Serializable {
    @NotNull
    private String address;
    @NotNull
    private String phoneNumber;

    @OneToMany(mappedBy = "endConsumer", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<Order> orders;

    public EndConsumer() {
    }

    public EndConsumer(String username, String password, String name, String email, String address, String phoneNumber) {
        super(username, password, name, email);
        this.address = address;
        this.phoneNumber = phoneNumber;
        orders = new LinkedList<>();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void addOrder(Order order) {
        if (order == null) throw new IllegalArgumentException("Order cannot be null");
        orders.add(order);
    }

    public void removeOrder(Order order) {
        if (order == null) throw new IllegalArgumentException("Order cannot be null");
        if (!orders.contains(order)) throw new IllegalArgumentException("The user does not contain the order with the ID: " + order.getId());
        orders.remove(order);
    }
}
