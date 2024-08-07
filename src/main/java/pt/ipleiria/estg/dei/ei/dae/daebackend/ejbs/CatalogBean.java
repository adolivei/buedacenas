package pt.ipleiria.estg.dei.ei.dae.daebackend.ejbs;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import jdk.swing.interop.SwingInterOpUtils;
import org.hibernate.Hibernate;
import pt.ipleiria.estg.dei.ei.dae.daebackend.entities.Catalog;
import pt.ipleiria.estg.dei.ei.dae.daebackend.entities.Manufacturer;
import pt.ipleiria.estg.dei.ei.dae.daebackend.entities.Product;
import pt.ipleiria.estg.dei.ei.dae.daebackend.exceptions.MyEntityExistsException;
import pt.ipleiria.estg.dei.ei.dae.daebackend.exceptions.MyEntityNotFoundException;
import pt.ipleiria.estg.dei.ei.dae.daebackend.exceptions.MyConstraintViolationException;

import java.util.List;

@Stateless
public class CatalogBean {
    @PersistenceContext
    private EntityManager entityManager;

    public boolean exists(long code) {
        Query query = entityManager.createQuery(
                "SELECT COUNT(c.code) FROM Catalog c WHERE c.code = :code",
                Long.class
        );
        query.setParameter("code", code);
        return (Long)query.getSingleResult() > 0L;
    }

    public void create(long code, String name, String manufacturerUsername)
            throws MyEntityExistsException, MyEntityNotFoundException, MyConstraintViolationException{

        if (exists(code)) {
            throw new MyEntityExistsException(
                    "Catalog with code '" + code + "' already exists");
        }

        Manufacturer manufacturer = entityManager.find(Manufacturer.class, manufacturerUsername);

        if (manufacturer == null) {
            throw new MyEntityNotFoundException("Manufacturer with name '" + manufacturerUsername + "' not found");
        }

        try {
            Catalog catalog = new Catalog(code, name, manufacturer);
            manufacturer.addCatalog(catalog);
            entityManager.persist(catalog);
        } catch (ConstraintViolationException e) {
            throw new MyConstraintViolationException(e);
        }
    }

    public List<Catalog> getAll() {
        List<Catalog> catalogs = entityManager.createNamedQuery("getAllCatalogs", Catalog.class).getResultList();
        catalogs.forEach(catalog -> Hibernate.initialize(catalog.getProducts()));
        return entityManager.createNamedQuery("getAllCatalogs", Catalog.class).getResultList();
    }

    public Catalog find(long code)
            throws MyEntityNotFoundException{
        Catalog catalog = entityManager.find(Catalog.class, code);

        if (catalog == null) {
            throw new MyEntityNotFoundException(
                    "Catalog with code '" + code + "' not found");
        }

        if(!Hibernate.isInitialized(catalog.getProducts())) {
            Hibernate.initialize(catalog.getProducts());
        }
        return catalog;
    }

    public void updateCatalog(Catalog catalogToUpdate)
            throws MyEntityNotFoundException{

        Catalog catalog = entityManager.find(Catalog.class, catalogToUpdate.getCode());

        if (catalog == null) {
            throw new MyEntityNotFoundException(
                    "Catalog with code '" + catalogToUpdate.getCode() + "' not found");
        }
        entityManager.lock(catalog, LockModeType.OPTIMISTIC);
        catalog.setName(catalogToUpdate.getName());
        catalog.setProducts(catalogToUpdate.getProducts());
        catalog.setManufacturer(catalogToUpdate.getManufacturer());

        entityManager.merge(catalog);
    }

    public void deleteCatalog(long code)
            throws MyEntityNotFoundException {
        Catalog catalog = entityManager.find(Catalog.class, code);

        if (catalog == null) {
            throw new MyEntityNotFoundException(
                    "Catalog with code '" + code + "' not found");
        }
        entityManager.remove(catalog);
    }
    public Catalog getCatalogProducts(long catalogCode)
            throws MyEntityNotFoundException {
        Catalog catalog = this.find(catalogCode);

        if (catalog == null) {
            throw new MyEntityNotFoundException(
                    "Catalog with code '" + catalogCode + "' not found");
        }

        if (!Hibernate.isInitialized(catalog.getProducts())) {
            Hibernate.initialize(catalog.getProducts());
        }

        return catalog;
    }
}
