package pt.ipleiria.estg.dei.ei.dae.daebackend.dtos;

import pt.ipleiria.estg.dei.ei.dae.daebackend.enums.PackageType;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class PackageDTO implements Serializable {
    private int id;
    private PackageType type;
    private String material;
    private float weight;
    private double dimensions;

    private List<OrderDTO> orders = new LinkedList<>();
    private List<ProductDTO> products = new LinkedList<>();

    public PackageDTO() {
    }

    public PackageDTO(int id, PackageType type, String material, float weight, double dimensions) {
        this.id = id;
        this.type = type;
        this.material = material;
        this.weight = weight;
        this.dimensions = dimensions;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public PackageType getType() {
        return type;
    }

    public void setType(PackageType type) {
        this.type = type;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public double getDimensions() {
        return dimensions;
    }

    public void setDimensions(double dimensions) {
        this.dimensions = dimensions;
    }

    public List<OrderDTO> getOrders() {
        return orders;
    }

    public void setOrders(List<OrderDTO> orders) {
        this.orders = orders;
    }

    public List<ProductDTO> getProducts() {
        return products;
    }

    public void setProducts(List<ProductDTO> products) {
        this.products = products;
    }
}
