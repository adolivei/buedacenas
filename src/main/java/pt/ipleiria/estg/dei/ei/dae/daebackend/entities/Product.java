package pt.ipleiria.estg.dei.ei.dae.daebackend.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import pt.ipleiria.estg.dei.ei.dae.daebackend.enums.ProductStatus;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

@Entity
@NamedQueries({
        @NamedQuery(
                name = "getAllProducts",
                query = "SELECT p FROM Product p ORDER BY p.name" // JPQL
        )
})
public class Product extends Versionable implements Serializable {
    @Id
    private long code;
    @NotNull
    private String name;
    @NotNull
    private double unit_price;
    @NotNull
    private double weight;
    @Enumerated(EnumType.STRING)
    @NotNull
    private ProductStatus status;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "catalog_code")
    private Catalog catalog;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    public Product() {
    }

    public Product(long code, String name, double unit_price, double weight, Catalog catalog) {
        this.code = code;
        this.name = name;
        this.unit_price = unit_price;
        this.weight = weight;
        this.catalog = catalog;
        this.status = ProductStatus.AVAILABLE;
        order = null;
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

    public Catalog getCatalog() {
        return catalog;
    }

    public void setCatalog(Catalog catalog) {
        this.catalog = catalog;
    }

    public void removeCatalog() {
        this.catalog = null;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public ProductStatus getStatus() {
        return status;
    }

    public void setStatus(ProductStatus status) {
        this.status = status;
    }

//    public List<Package> getPackages() {
//        return packages;
//    }
//
//    public void setPackages(List<Package> packages) {
//        this.packages = packages;
//    }
//
//    public void addPackage(Package pack) {
//        if (pack == null) throw new IllegalArgumentException("Package cannot be null");
//        packages.add(pack);
//    }
//
//    public void removePackage(Package pack) {
//        if (pack == null) throw new IllegalArgumentException("Package cannot be null");
//        if (!packages.contains(pack))  throw new IllegalArgumentException("Package " + pack.getId() + " is not assigned to this product");
//        packages.remove(pack);
//    }
}
