package pt.ipleiria.estg.dei.ei.dae.daebackend.ejbs;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.validation.ConstraintViolationException;
import org.hibernate.Hibernate;
import pt.ipleiria.estg.dei.ei.dae.daebackend.entities.EndConsumer;
import pt.ipleiria.estg.dei.ei.dae.daebackend.entities.Package;
import pt.ipleiria.estg.dei.ei.dae.daebackend.exceptions.MyConstraintViolationException;
import pt.ipleiria.estg.dei.ei.dae.daebackend.exceptions.MyEntityExistsException;
import pt.ipleiria.estg.dei.ei.dae.daebackend.exceptions.MyEntityNotFoundException;
import pt.ipleiria.estg.dei.ei.dae.daebackend.security.Hasher;

import java.util.List;

@Stateless
public class EndConsumerBean {
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

    public void create(String username, String password, String name, String email, String address, String phoneNumber)
            throws MyEntityExistsException, MyConstraintViolationException {

        if (exists(username)) {
            throw new MyEntityExistsException(
                    "End consumer with username '" + username + "' already exists");
        }
        try {
            EndConsumer endConsumer = new EndConsumer(username, hasher.hash(password), name, email, address, phoneNumber);

            entityManager.persist(endConsumer);
            entityManager.flush();
        } catch (ConstraintViolationException e) {
            throw new MyConstraintViolationException(e);
        }
    }

    public List<EndConsumer> getAll() {
        List<EndConsumer> endConsumers = entityManager.createNamedQuery("getAllEndConsumers", EndConsumer.class).getResultList();
        for (EndConsumer endConsumer : endConsumers) {
            Hibernate.initialize(endConsumer.getOrders());
            for (var order : endConsumer.getOrders()) {
                Hibernate.initialize(order.getProducts());
                Hibernate.initialize(order.getPackages());
            }
        }
        return entityManager.createNamedQuery("getAllEndConsumers", EndConsumer.class).getResultList();
    }

    public EndConsumer find(String username)
            throws MyEntityNotFoundException {
        var endConsumer = entityManager.find(EndConsumer.class, username);
        if (endConsumer == null) {
            throw new MyEntityNotFoundException(
                    "End consumer with username '" + username + "' not found");
        }
        Hibernate.initialize(endConsumer.getOrders());
        for (var order : endConsumer.getOrders()) {
            Hibernate.initialize(order.getProducts());
            Hibernate.initialize(order.getPackages());
        }
        return endConsumer;
    }

    public void update(EndConsumer endConsumerToUpdate)
            throws MyEntityNotFoundException{
        EndConsumer endConsumer = entityManager.find(EndConsumer.class, endConsumerToUpdate.getUsername());

        if (endConsumer == null) {
            throw new MyEntityNotFoundException(
                    "End consumer with username '" + endConsumerToUpdate.getUsername() + "' not found");
        }
        entityManager.lock(endConsumer, LockModeType.OPTIMISTIC);
        endConsumer.setName(endConsumerToUpdate.getName());
        endConsumer.setEmail(endConsumerToUpdate.getEmail());
        endConsumer.setAddress(endConsumerToUpdate.getAddress());
        endConsumer.setPhoneNumber(endConsumerToUpdate.getPhoneNumber());
        entityManager.merge(endConsumer);
    }
}
