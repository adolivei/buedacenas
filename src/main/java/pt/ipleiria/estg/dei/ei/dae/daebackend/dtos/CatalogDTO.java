package pt.ipleiria.estg.dei.ei.dae.daebackend.dtos;

import pt.ipleiria.estg.dei.ei.dae.daebackend.entities.Catalog;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class CatalogDTO implements Serializable {
    private long code;
    private String name;
    private String manufacturerUsername;

    private List<ProductDTO> products = new LinkedList<>();

    public CatalogDTO() {
        this.products = new LinkedList<>();
    }

    public CatalogDTO(long code, String name, String manufacturerUsername) {
        this.code = code;
        this.name = name;
        this.manufacturerUsername = manufacturerUsername;
    }

    public long getCode() {
        return code;
    }

    public void setCode(long code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getManufacturerUsername() {
        return manufacturerUsername;
    }

    public void setManufacturerUsername(String manufacturerUsername) {
        this.manufacturerUsername = manufacturerUsername;
    }

    public List<ProductDTO> getProducts() {
        return products;
    }

    public void setProducts(List<ProductDTO> products) {
        this.products = products;
    }
}
