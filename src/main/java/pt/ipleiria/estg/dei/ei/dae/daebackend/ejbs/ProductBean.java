package pt.ipleiria.estg.dei.ei.dae.daebackend.ejbs;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.validation.ConstraintViolationException;
import org.hibernate.Hibernate;
import pt.ipleiria.estg.dei.ei.dae.daebackend.entities.Catalog;
import pt.ipleiria.estg.dei.ei.dae.daebackend.entities.Package;
import pt.ipleiria.estg.dei.ei.dae.daebackend.entities.Product;
import pt.ipleiria.estg.dei.ei.dae.daebackend.entities.Sensor;
import pt.ipleiria.estg.dei.ei.dae.daebackend.exceptions.MyEntityExistsException;
import pt.ipleiria.estg.dei.ei.dae.daebackend.exceptions.MyEntityNotFoundException;
import pt.ipleiria.estg.dei.ei.dae.daebackend.exceptions.MyConstraintViolationException;

import pt.ipleiria.estg.dei.ei.dae.daebackend.enums.ProductStatus;
import pt.ipleiria.estg.dei.ei.dae.daebackend.exceptions.MyIllegalArgumentException;

import java.util.List;

@Stateless
public class ProductBean {
    @PersistenceContext
    private EntityManager entityManager;

    public boolean exists(long code) {
        Query query = entityManager.createQuery(
                "SELECT COUNT(p.code) FROM Product p WHERE p.code = :code",
                Long.class
        );
        query.setParameter("code", code);
        return (Long)query.getSingleResult() > 0L;
    }

    public void create(long code, String name, double unit_price, double weight, long catalogCode)
            throws MyEntityExistsException, MyEntityNotFoundException, MyConstraintViolationException {
        if (exists(code)) {
            throw new MyEntityExistsException(
                    "Product with code '" + code + "' already exists");
        }

        Catalog catalog = entityManager.find(Catalog.class, catalogCode);

        if (catalog == null) {
            throw new MyEntityNotFoundException("Catalog with code '" + catalogCode + "' not found");
        }

        try {
            Product product = new Product(code, name, unit_price, weight, catalog);
            catalog.addProduct(product);
            entityManager.persist(product);
            entityManager.flush();
        } catch (ConstraintViolationException e) {
            throw new MyConstraintViolationException(e);
        }
    }

    public List<Product> getAll() {
        return entityManager.createNamedQuery("getAllProducts", Product.class).getResultList();
    }

    public Product find(long code)
            throws MyEntityNotFoundException{
        var product = entityManager.find(Product.class, code);

        if (product == null) {
            throw new MyEntityNotFoundException(
                    "Product with code '" + code + "' not found");
        }
        return product;
    }

    public void updateProduct(Product productToUpdate)
            throws MyEntityNotFoundException{

        Product product = entityManager.find(Product.class, productToUpdate.getCode());
        if (product == null) {
            throw new MyEntityNotFoundException(
                    "Product with code '" + productToUpdate.getCode() + "' not found");
        }
        entityManager.lock(productToUpdate, LockModeType.OPTIMISTIC);
        product.setName(productToUpdate.getName());
        product.setUnit_price(productToUpdate.getUnit_price());
        product.setWeight(productToUpdate.getWeight());
        entityManager.merge(product);
    }

    public void deleteProduct(long code)
            throws MyEntityNotFoundException {

        Product product = entityManager.find(Product.class, code);
        if (product == null) {
            throw new MyEntityNotFoundException(
                    "Product with code '" + code + "' not found");
        }
        Catalog catalog = product.getCatalog();
        catalog.removeProduct(product);
        entityManager.remove(product);

    }

    public void setStatus(long code, ProductStatus status)
            throws MyEntityNotFoundException, MyConstraintViolationException {
        Product product = entityManager.find(Product.class, code);
        if (product == null) {
            throw new MyEntityNotFoundException(
                    "Product with code '" + code + "' not found");
        }
        try {
            entityManager.lock(product, LockModeType.OPTIMISTIC);
            product.setStatus(status);
        } catch (ConstraintViolationException e) {
            throw new MyConstraintViolationException(e);
        }
    }
}
