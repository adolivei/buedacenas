package pt.ipleiria.estg.dei.ei.dae.daebackend.dtos;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class LogisticOperatorDTO implements Serializable {
    private String username;
    private String password;
    private String name;
    private String email;
    private String company;
    private String vehicleType;
    private String licensePlate;
    private String contact;
    private List<OrderDTO> orders = new LinkedList<>();;

    public LogisticOperatorDTO() {
    }

    public LogisticOperatorDTO(String username, String password, String name, String email, String company, String vehicleType, String licensePlate, String contact) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
        this.company = company;
        this.vehicleType = vehicleType;
        this.licensePlate = licensePlate;
        this.contact = contact;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public List<OrderDTO> getOrders() {
        return orders;
    }

    public void setOrders(List<OrderDTO> orders) {
        this.orders = orders;
    }
}
