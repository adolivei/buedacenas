package pt.ipleiria.estg.dei.ei.dae.daebackend.ejbs;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.validation.ConstraintViolationException;
import pt.ipleiria.estg.dei.ei.dae.daebackend.entities.Manufacturer;
import pt.ipleiria.estg.dei.ei.dae.daebackend.exceptions.MyConstraintViolationException;
import pt.ipleiria.estg.dei.ei.dae.daebackend.exceptions.MyEntityExistsException;
import pt.ipleiria.estg.dei.ei.dae.daebackend.exceptions.MyEntityNotFoundException;
import pt.ipleiria.estg.dei.ei.dae.daebackend.security.Hasher;

import java.util.List;

@Stateless
public class ManufacturerBean {
    @PersistenceContext
    private EntityManager entityManager;

    private Hasher hasher = new Hasher();

    public boolean exists(String username) {
        Query query = entityManager.createQuery(
                "SELECT COUNT(u.username) FROM User u WHERE u.username = :username",
                String.class
        );
        query.setParameter("username", username);
        return (Long)query.getSingleResult() > 0L;
    }

    public void create(String username, String password, String name, String email, String industry, String location, String contact)
            throws MyEntityExistsException, MyConstraintViolationException {
        if (exists(username)) {
            throw new MyEntityExistsException(
                    "Manufacturer with username '" + username + "' already exists");
        }
        try {
            Manufacturer manufacturer = new Manufacturer(username, hasher.hash(password), name, email, industry, location, contact);
            entityManager.persist(manufacturer);
            entityManager.flush();
        } catch (ConstraintViolationException e) {
            throw new MyConstraintViolationException(e);
        }
    }

    public List<Manufacturer> getAll() {
        return entityManager.createNamedQuery("getAllManufacturers", Manufacturer.class).getResultList();
    }

    public Manufacturer find(String username)
            throws MyEntityNotFoundException{
        Manufacturer manufacturer = entityManager.find(Manufacturer.class, username);

        if (manufacturer == null) {
            throw new MyEntityNotFoundException(
                    "Manufacturer with username '" + username + "' not found");
        }

        return manufacturer;
    }

    public void update(Manufacturer manufacturerToUpdate)
            throws MyEntityNotFoundException{
        Manufacturer manufacturer = entityManager.find(Manufacturer.class, manufacturerToUpdate.getUsername());

        if (manufacturer == null) {
            throw new MyEntityNotFoundException(
                    "Manufacturer with username '" + manufacturerToUpdate.getUsername() + "' not found");
        }
        entityManager.lock(manufacturer, LockModeType.OPTIMISTIC);
        manufacturer.setName(manufacturerToUpdate.getName());
        manufacturer.setEmail(manufacturerToUpdate.getEmail());
        manufacturer.setIndustry(manufacturerToUpdate.getIndustry());
        manufacturer.setLocation(manufacturerToUpdate.getLocation());
        manufacturer.setContact(manufacturerToUpdate.getContact());
        entityManager.merge(manufacturer);
    }
}
