package pt.ipleiria.estg.dei.ei.dae.daebackend.ejbs;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.validation.ConstraintViolationException;
import pt.ipleiria.estg.dei.ei.dae.daebackend.entities.Admin;
import pt.ipleiria.estg.dei.ei.dae.daebackend.exceptions.MyConstraintViolationException;
import pt.ipleiria.estg.dei.ei.dae.daebackend.exceptions.MyEntityExistsException;
import pt.ipleiria.estg.dei.ei.dae.daebackend.security.Hasher;

@Stateless
public class AdminBean {
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

    public void create(String username, String password, String name, String email)
            throws MyEntityExistsException, MyConstraintViolationException {

        if (exists(username)) {
            throw new MyEntityExistsException(
                    "Admin with username '" + username + "' already exists");
        }
        try{
            Admin admin = new Admin(username, hasher.hash(password), name, email);
            entityManager.persist(admin);
        } catch (ConstraintViolationException e) {
            throw new MyConstraintViolationException(e);
        }
    }
}
