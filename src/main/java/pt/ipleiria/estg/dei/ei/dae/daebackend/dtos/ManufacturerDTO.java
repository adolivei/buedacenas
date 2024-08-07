package pt.ipleiria.estg.dei.ei.dae.daebackend.dtos;

import java.io.Serializable;

public class ManufacturerDTO implements Serializable {
    private String username;
    private String password;
    private String name;
    private String email;
    private String industry;
    private String location;
    private String contact;

    public ManufacturerDTO() {
    }

    public ManufacturerDTO(String username, String password, String name, String email, String industry, String location, String contact) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
        this.industry = industry;
        this.location = location;
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

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }
}
