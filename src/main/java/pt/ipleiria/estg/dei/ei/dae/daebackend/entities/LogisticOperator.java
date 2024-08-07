package pt.ipleiria.estg.dei.ei.dae.daebackend.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

@Entity
@NamedQueries({
        @NamedQuery(
                name = "getAllLogisticOperators",
                query = "SELECT lo FROM LogisticOperator lo ORDER BY lo.name"
        )
})
public class LogisticOperator extends User implements Serializable{
    @NotNull
    private String company;
    @NotNull
    private String vehicleType;
    @NotNull
    private String licensePlate;
    @NotNull
    private String contact;

    @OneToMany(mappedBy = "logisticOperator", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<Order> orders;

    public LogisticOperator() {
    }

    public LogisticOperator(String username, String password, String name, String email, String company, String vehicleType, String licensePlate, String contact) {
        super(username, password, name, email);
        this.company = company;
        this.vehicleType = vehicleType;
        this.licensePlate = licensePlate;
        this.contact = contact;
        orders = new LinkedList<>();
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
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
