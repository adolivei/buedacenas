package pt.ipleiria.estg.dei.ei.dae.daebackend.dtos;

import pt.ipleiria.estg.dei.ei.dae.daebackend.entities.Product;
import pt.ipleiria.estg.dei.ei.dae.daebackend.enums.ProductStatus;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class ProductDTO implements Serializable {
    private long code;
    private String name;
    private double unit_price;
    private double weight;
    private long catalogCode;
    private String catalogName;
    private ProductStatus status;


    public ProductDTO() {
    }

    public ProductDTO(long code, String name, double unit_price,
                      double weight, long catalogCode, String catalogName, ProductStatus productStatus) {
        this.code = code;
        this.name = name;
        this.unit_price = unit_price;
        this.weight = weight;
        this.catalogCode = catalogCode;
        this.catalogName = catalogName;
        this.status = productStatus;
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

    public double getUnit_price() {
        return unit_price;
    }

    public void setUnit_price(double unit_price) {
        this.unit_price = unit_price;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public long getCatalogCode() {
        return catalogCode;
    }

    public void setCatalogCode(long catalogCode) {
        this.catalogCode = catalogCode;
    }

    public String getCatalogName() {
        return catalogName;
    }

    public void setCatalogName(String catalogName) {
        this.catalogName = catalogName;
    }

    public ProductStatus getStatus() {
        return status;
    }

    public void setStatus(ProductStatus status) {
        this.status = status;
    }
}
