package pt.ipleiria.estg.dei.ei.dae.daebackend.dtos;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class EndConsumerDTO implements Serializable {
    private String username;
    private String password;
    private String name;
    private String email;
    private String address;
    private String phoneNumber;
    private List<OrderDTO> orders = new LinkedList<>();

    public EndConsumerDTO() {
    }

    public EndConsumerDTO(String username, String password, String name, String email, String address, String phoneNumber) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
        this.address = address;
        this.phoneNumber = phoneNumber;
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

    public List<OrderDTO> getOrders() {
        return orders;
    }

    public void setOrders(List<OrderDTO> orders) {
        this.orders = orders;
    }
}
