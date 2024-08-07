package pt.ipleiria.estg.dei.ei.dae.daebackend.dtos;

import java.io.Serializable;

public class AdminDTO implements Serializable {
    private String username;
    private String name;

    public AdminDTO() {
    }

    public AdminDTO(String username, String name) {
        this.username = username;
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
