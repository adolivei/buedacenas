package pt.ipleiria.estg.dei.ei.dae.daebackend.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@NamedQueries({
        @NamedQuery(
                name = "getAllManufacturers",
                query = "SELECT m FROM Manufacturer m ORDER BY m.name"
        )
})
public class Manufacturer extends User implements Serializable{
    @NotNull
    private String industry;
    @NotNull
    private String location;
    @NotNull
    private String contact;
    @OneToMany(mappedBy = "manufacturer", cascade = CascadeType.REMOVE)
    private List<Catalog> catalogs;

    public Manufacturer() {
        this.catalogs = new ArrayList<>();
    }

    public Manufacturer(String username, String password, String name, String email, String industry, String location, String contact) {
        super(username, password, name, email);
        this.industry = industry;
        this.location = location;
        this.contact = contact;
        this.catalogs = new ArrayList<>();
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

    public List<Catalog> getCatalogs() {
        return catalogs;
    }

    public void setCatalogs(List<Catalog> catalogs) {
        this.catalogs = catalogs;
    }

    public void addCatalog(Catalog catalog) {
        this.catalogs.add(catalog);
    }

    public void removeCatalog(Catalog catalog) {
        this.catalogs.remove(catalog);
    }
}
