package pt.ipleiria.estg.dei.ei.dae.daebackend.ejbs;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.validation.ConstraintViolationException;
import org.hibernate.Hibernate;
import pt.ipleiria.estg.dei.ei.dae.daebackend.entities.Measurement;
import pt.ipleiria.estg.dei.ei.dae.daebackend.entities.Package;
import pt.ipleiria.estg.dei.ei.dae.daebackend.entities.Sensor;
import pt.ipleiria.estg.dei.ei.dae.daebackend.enums.SensorStatus;
import pt.ipleiria.estg.dei.ei.dae.daebackend.exceptions.MyConstraintViolationException;
import pt.ipleiria.estg.dei.ei.dae.daebackend.exceptions.MyEntityExistsException;
import pt.ipleiria.estg.dei.ei.dae.daebackend.exceptions.MyEntityNotFoundException;

import java.util.Date;
import java.util.List;

@Stateless
public class SensorBean {

    @PersistenceContext
    private EntityManager entityManager;

    @EJB
    private PackageBean packageBean;

    public boolean exists(long id) {
        Query query = entityManager.createQuery(
                "SELECT COUNT(s.id) FROM Sensor s WHERE s.id = :id",
                Long.class
        );
        query.setParameter("id", id);
        return (Long) query.getSingleResult() > 0L;
    }

    public Sensor create(String type, String location, Date timestamp, SensorStatus status)
            throws MyEntityExistsException, MyConstraintViolationException {
        try {
            Sensor sensor = new Sensor(type, location, timestamp, status);
            if (exists(sensor.getId())) {
                throw new MyEntityExistsException(
                        "Sensor with id '" + sensor.getId() + "' already exists");
            }
            entityManager.persist(sensor);
            return sensor;
        } catch (ConstraintViolationException e) {
            throw new MyConstraintViolationException(e);
        }
    }

    public List<Sensor> getAllSensors() {
        //so para testes
        List<Sensor> sensors = entityManager.createNamedQuery("getAllSensors", Sensor.class).getResultList();
        for (Sensor sensor : sensors) {
            if (!Hibernate.isInitialized(sensor.getMeasurements())) {
                Hibernate.initialize(sensor.getMeasurements());
            }
        }
        return entityManager.createNamedQuery("getAllSensors", Sensor.class).getResultList();
    }

    public Sensor find(long id)
            throws MyEntityNotFoundException {
        Sensor sensor = entityManager.find(Sensor.class, id);
        if (sensor == null) {
            throw new MyEntityNotFoundException(
                    "Sensor with id '" + id + "' not found");
        }
        if (!Hibernate.isInitialized(sensor.getMeasurements())) {
            Hibernate.initialize(sensor.getMeasurements());
        }
        return sensor;
    }

    public void updateSensor(Sensor updatedSensor)
            throws MyEntityNotFoundException, MyConstraintViolationException{
        Sensor existingSensor = entityManager.find(Sensor.class, updatedSensor.getId());
        if (existingSensor == null) {
            throw new MyEntityNotFoundException(
                    "Sensor with id '" + updatedSensor.getId() + "' not found");
        }
        entityManager.lock(existingSensor, LockModeType.OPTIMISTIC);
        try {
            existingSensor.setType(updatedSensor.getType());
            existingSensor.setLocation(updatedSensor.getLocation());
            existingSensor.setTimestamp(updatedSensor.getTimestamp());
            existingSensor.setStatus(updatedSensor.getStatus());
        } catch (ConstraintViolationException e) {
            throw new MyConstraintViolationException(e);
        }
    }

    public void deleteSensor(long id)
            throws MyEntityNotFoundException, MyConstraintViolationException{
        Sensor sensor = entityManager.find(Sensor.class, id);
        if (sensor == null) {
            throw new MyEntityNotFoundException(
                    "Sensor with id '" + id + "' not found");
        }
        try {
            entityManager.remove(sensor);
        } catch (ConstraintViolationException e) {
            throw new MyConstraintViolationException(e);
        }
    }

    public void addSensorToPackage(int packageId, long sensorId)
            throws MyEntityNotFoundException, MyConstraintViolationException {
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

        try {
            entityManager.lock(pack, LockModeType.OPTIMISTIC);
            entityManager.lock(sensor, LockModeType.OPTIMISTIC);
            packageBean.enrollSensorInPackage(pack.getId(), sensor.getId());
        } catch (ConstraintViolationException e) {
            throw new MyConstraintViolationException(e);
        }
    }

    public void removeSensorFromPackage(long packageId, long sensorId)
            throws MyEntityNotFoundException, MyConstraintViolationException {
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
        try {
            entityManager.lock(pack, LockModeType.OPTIMISTIC);
            entityManager.lock(sensor, LockModeType.OPTIMISTIC);
            packageBean.unenrollSensorInPackage(pack.getId(), sensor.getId());
        } catch (ConstraintViolationException e) {
            throw new MyConstraintViolationException(e);
        }
    }


    public void updateStatus(long id)
            throws MyEntityNotFoundException, MyConstraintViolationException{
        Sensor sensor = entityManager.find(Sensor.class, id);
        if (sensor == null) {
            throw new MyEntityNotFoundException(
                    "Sensor with id '" + id + "' not found");
        }
        try {
            entityManager.lock(sensor, LockModeType.OPTIMISTIC);
            if (sensor.getStatus() == SensorStatus.ACTIVE) {
                sensor.setStatus(SensorStatus.INACTIVE);
            } else {
                sensor.setStatus(SensorStatus.ACTIVE);
                for (Measurement measurement : sensor.getMeasurements()) {
                    if (measurement.getUnit().equals("Theft") || measurement.getUnit().equals("Boolean")) {
                        entityManager.lock(measurement, LockModeType.OPTIMISTIC);
                        measurement.setValue(Math.random() > 0.5 ? 1: 0);
                    } else {
                        entityManager.lock(measurement, LockModeType.OPTIMISTIC);
                        measurement.setValue(Math.round(Math.random() * 100 * 10.0) / 10.0);
                    }
                }
            }
            entityManager.merge(sensor);
        } catch (ConstraintViolationException e) {
            throw new MyConstraintViolationException(e);
        }
    }
}
