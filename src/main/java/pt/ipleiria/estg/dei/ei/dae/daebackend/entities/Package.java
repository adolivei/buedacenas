package pt.ipleiria.estg.dei.ei.dae.daebackend.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import pt.ipleiria.estg.dei.ei.dae.daebackend.enums.PackageType;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Entity
@NamedQueries({
        @NamedQuery(
                name = "getAllPackages",
                query = "SELECT p FROM Package p ORDER BY p.id"
        )
})
public class Package extends Versionable implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Enumerated(EnumType.STRING)
    @NotNull
    private PackageType type;
    @NotNull
    private String material;
    @NotNull
    private float weight;
    @NotNull
    private double dimensions;

    @ManyToMany
    @JoinTable(
            name = "packages_sensors",
            joinColumns = @JoinColumn(
                    name = "package_id",
                    referencedColumnName = "id"
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "sensor_id",
                    referencedColumnName = "id"
            )
    )
    private List<Sensor> sensors;

    @ManyToMany
    @JoinTable(
            name = "packages_orders",
            joinColumns = @JoinColumn(name = "package_id"),
            inverseJoinColumns = @JoinColumn(name = "order_id")
    )
    private List<Order> orders;

    @ManyToMany
    @JoinTable(
            name = "packages_products",
            joinColumns = @JoinColumn(name = "package_id"),
            inverseJoinColumns = @JoinColumn(name = "product_code")
    )
    private List<Product> products;


    public Package() {
        sensors = new LinkedList<>();
        orders = new LinkedList<>();
        products = new LinkedList<>();
    }

    public Package(PackageType type, String material, float weight, double dimensions) {
        this.type = type;
        this.material = material;
        this.weight = weight;
        this.dimensions = dimensions;
        this.sensors = new LinkedList<Sensor>();
        orders = new LinkedList<>();
        products = new LinkedList<>();
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

    public List<Sensor> getSensors() {
        return sensors;
    }

    public void setSensors(List<Sensor> sensors) {
        this.sensors = sensors;
    }

    public void addSensor(Sensor sensor) {
        this.sensors.add(sensor);
    }

    public void removeSensor(Sensor sensor) {
        this.sensors.remove(sensor);
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
        if (!orders.contains(order)) throw new IllegalArgumentException("Order " + order.getId() + " is not assigned to this package");
        orders.remove(order);
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public void addProduct(Product product) {
        if (product == null) throw new IllegalArgumentException("Product cannot be null");
        products.add(product);
    }

    public void removeProduct(Product product) {
        if (product == null) throw new IllegalArgumentException("Product cannot be null");
        if (!products.contains(product)) throw new IllegalArgumentException("Product " + product.getCode() + " is not assigned to this package");
        products.remove(product);
    }
}
