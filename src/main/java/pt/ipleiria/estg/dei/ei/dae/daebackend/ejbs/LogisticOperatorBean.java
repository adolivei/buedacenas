package pt.ipleiria.estg.dei.ei.dae.daebackend.ejbs;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.validation.ConstraintViolationException;
import org.hibernate.Hibernate;
import pt.ipleiria.estg.dei.ei.dae.daebackend.entities.LogisticOperator;
import pt.ipleiria.estg.dei.ei.dae.daebackend.exceptions.MyConstraintViolationException;
import pt.ipleiria.estg.dei.ei.dae.daebackend.exceptions.MyEntityExistsException;
import pt.ipleiria.estg.dei.ei.dae.daebackend.exceptions.MyEntityNotFoundException;
import pt.ipleiria.estg.dei.ei.dae.daebackend.security.Hasher;

import java.util.List;

@Stateless
public class LogisticOperatorBean {
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

    public void create(String username, String password, String name, String email, String company, String vehicleType, String licensePlate, String contact)
            throws MyEntityExistsException, MyConstraintViolationException {

        if (exists(username)) {
            throw new MyEntityExistsException(
                    "Logistic operator with username '" + username + "' already exists");
        }
        try {
            LogisticOperator logisticOperator = new LogisticOperator(username, hasher.hash(password), name, email, company, vehicleType, licensePlate, contact);
            entityManager.persist(logisticOperator);
        } catch (ConstraintViolationException e) {
            throw new MyConstraintViolationException(e);
        }
    }

    public List<LogisticOperator> getAll() {
        var logisticOperators = entityManager.createNamedQuery("getAllLogisticOperators", LogisticOperator.class).getResultList();
        for (var logisticOperator : logisticOperators) {
            Hibernate.initialize(logisticOperator.getOrders());
            for (var order : logisticOperator.getOrders()) {
                Hibernate.initialize(order.getProducts());
                Hibernate.initialize(order.getPackages());
            }
        }
        return entityManager.createNamedQuery("getAllLogisticOperators", LogisticOperator.class).getResultList();
    }

    public LogisticOperator find(String username)
            throws MyEntityNotFoundException {
        var logisticOperator = entityManager.find(LogisticOperator.class, username);
        if (logisticOperator == null) {
            throw new MyEntityNotFoundException(
                    "Logistic operator with username '" + username + "' not found");
        }
        Hibernate.initialize(logisticOperator.getOrders());
        for (var order : logisticOperator.getOrders()) {
            Hibernate.initialize(order.getProducts());
            Hibernate.initialize(order.getPackages());
        }
        return logisticOperator;
    }

    public void update(LogisticOperator logisticOperatorToUpdate)
            throws MyEntityNotFoundException{
        LogisticOperator logisticOperator = entityManager.find(LogisticOperator.class, logisticOperatorToUpdate.getUsername());

        if (logisticOperator == null) {
            throw new MyEntityNotFoundException(
                    "Logistic operator with username '" + logisticOperatorToUpdate.getUsername() + "' not found");
        }
        entityManager.lock(logisticOperator, LockModeType.OPTIMISTIC);
        logisticOperator.setName(logisticOperatorToUpdate.getName());
        logisticOperator.setEmail(logisticOperatorToUpdate.getEmail());
        logisticOperator.setCompany(logisticOperatorToUpdate.getCompany());
        logisticOperator.setVehicleType(logisticOperatorToUpdate.getVehicleType());
        logisticOperator.setLicensePlate(logisticOperatorToUpdate.getLicensePlate());
        logisticOperator.setContact(logisticOperatorToUpdate.getContact());
        entityManager.merge(logisticOperator);
    }
}
