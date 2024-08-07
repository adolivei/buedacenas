package pt.ipleiria.estg.dei.ei.dae.daebackend.ejbs;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.validation.ConstraintViolationException;
import pt.ipleiria.estg.dei.ei.dae.daebackend.entities.Measurement;
import pt.ipleiria.estg.dei.ei.dae.daebackend.entities.Sensor;
import pt.ipleiria.estg.dei.ei.dae.daebackend.exceptions.MyConstraintViolationException;
import pt.ipleiria.estg.dei.ei.dae.daebackend.exceptions.MyEntityExistsException;
import pt.ipleiria.estg.dei.ei.dae.daebackend.exceptions.MyEntityNotFoundException;

import java.util.List;

@Stateless
public class MeasurementBean {

    @PersistenceContext
    private EntityManager entityManager;

    public boolean exists(long id) {
        Query query = entityManager.createQuery(
                "SELECT COUNT(m.id) FROM Measurement m WHERE m.id = :id",
                Long.class
        );
        query.setParameter("id", id);
        return (Long)query.getSingleResult() > 0L;
    }

    public Measurement create(String type, double value, String unit, long sensorId)
            throws MyEntityExistsException, MyConstraintViolationException, MyEntityNotFoundException {
        Sensor sensor = entityManager.find(Sensor.class, sensorId);
        if (sensor == null) {
            throw new MyEntityNotFoundException(
                    "Sensor with id '" + sensorId + "' not found");
        }
        try {
            Measurement measurement = new Measurement(type, value, unit, sensor);
            if (exists(measurement.getId())) {
                throw new MyEntityExistsException(
                        "Measurement with id '" + measurement.getId() + "' already exists");
            }
            entityManager.persist(measurement);
            entityManager.merge(sensor);

            sensor.addMeasurement(measurement);

            return measurement;
        } catch (ConstraintViolationException e) {
            throw new MyConstraintViolationException(e);
        }
    }

    public List<Measurement> getAllMeasurements() {
        return entityManager.createNamedQuery("getAllMeasurements", Measurement.class).getResultList();
    }

    public Measurement find(long id)
            throws MyEntityNotFoundException{
        Measurement measurement = entityManager.find(Measurement.class, id);

        if (measurement == null) {
            throw new MyEntityNotFoundException(
                    "Measurement with id '" + id + "' not found");
        }

        return measurement;
    }

    public void updateMeasurement(Measurement updatedMeasurement)
            throws MyEntityNotFoundException, MyConstraintViolationException{

        Measurement existingMeasurement = find(updatedMeasurement.getId());
        if (existingMeasurement == null) {
            throw new MyEntityNotFoundException(
                    "Measurement with id '" + updatedMeasurement.getId() + "' not found");
        }
        try {
            entityManager.lock(existingMeasurement, LockModeType.OPTIMISTIC);
            existingMeasurement.setType(updatedMeasurement.getType());
            existingMeasurement.setValue(updatedMeasurement.getValue());
            existingMeasurement.setUnit(updatedMeasurement.getUnit());
            existingMeasurement.setSensor(updatedMeasurement.getSensor());
        } catch (ConstraintViolationException e) {
            throw new MyConstraintViolationException(e);
        }
    }

    public void deleteMeasurement(long id)
            throws MyEntityNotFoundException, MyConstraintViolationException{
        Measurement measurement = entityManager.find(Measurement.class, id);

        if (measurement == null) {
            throw new MyEntityNotFoundException(
                    "Measurement with id '" + id + "' not found");
        }
        try {
            entityManager.remove(measurement);
        } catch (ConstraintViolationException e) {
            throw new MyConstraintViolationException(e);
        }
    }

    public void deleteMeasurementsBySensorId(long sensorId)
            throws MyEntityNotFoundException, MyConstraintViolationException{
        Sensor sensor = entityManager.find(Sensor.class, sensorId);
        if (sensor == null) {
            throw new MyEntityNotFoundException(
                    "Sensor with id '" + sensorId + "' not found");
        }
        try {
            Query query = entityManager.createQuery("DELETE FROM Measurement WHERE sensor.id = :sensorId");
            query.setParameter("sensorId", sensorId);
            query.executeUpdate();
        } catch (ConstraintViolationException e) {
            throw new MyConstraintViolationException(e);
        }
    }

}
