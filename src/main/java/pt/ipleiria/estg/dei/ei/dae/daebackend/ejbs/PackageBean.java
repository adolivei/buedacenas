package pt.ipleiria.estg.dei.ei.dae.daebackend.ejbs;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.validation.ConstraintViolationException;
import org.hibernate.Hibernate;
import pt.ipleiria.estg.dei.ei.dae.daebackend.entities.Package;
import pt.ipleiria.estg.dei.ei.dae.daebackend.entities.Product;
import pt.ipleiria.estg.dei.ei.dae.daebackend.entities.Sensor;
import pt.ipleiria.estg.dei.ei.dae.daebackend.enums.PackageType;
import pt.ipleiria.estg.dei.ei.dae.daebackend.exceptions.MyEntityExistsException;
import pt.ipleiria.estg.dei.ei.dae.daebackend.exceptions.MyEntityNotFoundException;
import pt.ipleiria.estg.dei.ei.dae.daebackend.exceptions.MyConstraintViolationException;

import java.util.List;

@Stateless
public class PackageBean {

    @PersistenceContext
    private EntityManager entityManager;

    public boolean exists(int id) {
        Query query = entityManager.createQuery(
                "SELECT COUNT(p.id) FROM Package p WHERE p.id = :id",
                Long.class
        );
        query.setParameter("id", id);
        return (Long) query.getSingleResult() > 0L;
    }

    public Package create(PackageType type, String material, float weight, double dimensions)
            throws MyEntityExistsException, MyConstraintViolationException {
        try {
            Package pack = new Package(type, material, weight, dimensions);
            if (exists(pack.getId())) {
                throw new MyEntityExistsException(
                        "Package with id '" + pack.getId() + "' already exists");
            }

            entityManager.persist(pack);
            entityManager.flush();
            return pack;
        } catch (ConstraintViolationException e) {
            throw new MyConstraintViolationException(e);
        }
    }

    public List<Package> getAll() {
        var packages = entityManager.createNamedQuery("getAllPackages", Package.class).getResultList();
        for (var pack : packages) {
            Hibernate.initialize(pack.getOrders());
            Hibernate.initialize(pack.getProducts());
            for (var order : pack.getOrders()){
                Hibernate.initialize(order.getProducts());
            }
        }
        return entityManager.createNamedQuery("getAllPackages", Package.class).getResultList();
    }

    public Package find(int id)
            throws MyEntityNotFoundException{

        var pack = entityManager.find(Package.class, id);

        if (pack == null) {
            throw new MyEntityNotFoundException(
                    "Package with id '" + id + "' not found");
        }

        Hibernate.initialize(pack.getOrders());
        Hibernate.initialize(pack.getProducts());
        for (var order : pack.getOrders()) {
            Hibernate.initialize(order.getProducts());
        }
        return pack;
    }

    public void updatePackage(Package packageToUpdate)
            throws MyEntityNotFoundException, MyConstraintViolationException{

        Package pack = entityManager.find(Package.class, packageToUpdate.getId());

        if (pack == null) {
            throw new MyEntityNotFoundException(
                    "Package with id '" + packageToUpdate.getId() + "' not found");
        }
        try {
            entityManager.lock(pack, LockModeType.OPTIMISTIC);
            pack.setType(packageToUpdate.getType());
            pack.setMaterial(packageToUpdate.getMaterial());
            pack.setWeight(packageToUpdate.getWeight());
            pack.setDimensions(packageToUpdate.getDimensions());
        } catch (ConstraintViolationException e) {
            throw new MyConstraintViolationException(e);
        }
        entityManager.merge(pack);
    }

    public void deletePackage(int id)
            throws MyEntityNotFoundException {

        Package pack = entityManager.find(Package.class, id);

        if (pack == null) {
            throw new MyEntityNotFoundException(
                    "Package with id '" + id + "' not found");
        }

        entityManager.remove(pack);
    }

    public void enrollSensorInPackage(int packageId, long sensorId)
            throws MyEntityNotFoundException {
        Package pack = entityManager.find(Package.class, packageId);

        if (pack == null) {
            throw new MyEntityNotFoundException(
                    "Package with id '" + packageId + "' not found");
        }

        Sensor sensor = entityManager.find(Sensor.class, sensorId);

        if (sensor == null) {
            throw new MyEntityNotFoundException(
                    "Sensor with id '" + sensorId + "' not found");
        }
        entityManager.lock(pack, LockModeType.OPTIMISTIC);
        entityManager.lock(sensor, LockModeType.OPTIMISTIC);
        pack.addSensor(sensor);
        sensor.addPackage(pack);
        entityManager.merge(pack);
    }

    public void unenrollSensorInPackage(int packageId, long sensorId)
            throws MyEntityNotFoundException {
        Package pack = entityManager.find(Package.class, packageId);

        if (pack == null) {
            throw new MyEntityNotFoundException(
                    "Package with id '" + packageId + "' not found");
        }

        Sensor sensor = entityManager.find(Sensor.class, sensorId);

        if (sensor == null) {
            throw new MyEntityNotFoundException(
                    "Sensor with id '" + sensorId + "' not found");
        }
        entityManager.lock(pack, LockModeType.OPTIMISTIC);
        entityManager.lock(sensor, LockModeType.OPTIMISTIC);
        pack.removeSensor(sensor);
        sensor.removePackage(pack);
        entityManager.merge(pack);
    }

    public Package getPackageSensors(int packageId)
            throws MyEntityNotFoundException{
        Package pack = entityManager.find(Package.class, packageId);

        if (pack == null) {
            throw new MyEntityNotFoundException(
                    "Package with id '" + packageId + "' not found");
        }

        if (!Hibernate.isInitialized(pack.getSensors())) {
            Hibernate.initialize(pack.getSensors());
            for (Sensor sensor : pack.getSensors()) {
                if (!Hibernate.isInitialized(sensor.getMeasurements())) {
                    Hibernate.initialize(sensor.getMeasurements());
                }
            }
        }

        return pack;
    }

    public void addProductToPackage(long code, int id)
            throws MyEntityNotFoundException{
        Product product = entityManager.find(Product.class, code);
        if (product == null) {
            throw new MyEntityNotFoundException(
                    "Product with code '" + code + "' not found");
        }
        Package pack = entityManager.find(Package.class, id);

        if (pack == null) {
            throw new MyEntityNotFoundException(
                    "Package with id '" + id + "' not found");
        }

        Hibernate.initialize(pack.getProducts());
        Hibernate.initialize(pack.getOrders());
        Hibernate.initialize(pack.getSensors());
        entityManager.lock(pack, LockModeType.OPTIMISTIC);
        pack.addProduct(product);
    }

    public void removeProductFromPackage(long code, int id)
            throws MyEntityNotFoundException{
        Product product = entityManager.find(Product.class, code);
        if (product == null) {
            throw new MyEntityNotFoundException(
                    "Product with code '" + code + "' not found");
        }
        Package pack = entityManager.find(Package.class, id);

        if (pack == null) {
            throw new MyEntityNotFoundException(
                    "Package with id '" + id + "' not found");
        }

        Hibernate.initialize(pack.getProducts());
        Hibernate.initialize(pack.getOrders());
        Hibernate.initialize(pack.getSensors());
        entityManager.lock(pack, LockModeType.OPTIMISTIC);
        pack.removeProduct(product);
    }
}