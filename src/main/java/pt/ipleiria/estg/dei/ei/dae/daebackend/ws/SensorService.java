package pt.ipleiria.estg.dei.ei.dae.daebackend.ws;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import pt.ipleiria.estg.dei.ei.dae.daebackend.dtos.MeasurementDTO;
import pt.ipleiria.estg.dei.ei.dae.daebackend.dtos.SensorDTO;
import pt.ipleiria.estg.dei.ei.dae.daebackend.ejbs.MeasurementBean;
import pt.ipleiria.estg.dei.ei.dae.daebackend.ejbs.SensorBean;
import pt.ipleiria.estg.dei.ei.dae.daebackend.entities.Measurement;
import pt.ipleiria.estg.dei.ei.dae.daebackend.entities.Package;
import pt.ipleiria.estg.dei.ei.dae.daebackend.entities.Sensor;
import pt.ipleiria.estg.dei.ei.dae.daebackend.exceptions.MyConstraintViolationException;
import pt.ipleiria.estg.dei.ei.dae.daebackend.exceptions.MyEntityExistsException;
import pt.ipleiria.estg.dei.ei.dae.daebackend.exceptions.MyEntityNotFoundException;
import pt.ipleiria.estg.dei.ei.dae.daebackend.security.Authenticated;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Path("sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authenticated
@RolesAllowed({"Manufacturer", "LogisticOperator"})
public class SensorService {
    @EJB
    private SensorBean sensorBean;
    @EJB
    private MeasurementBean measurementBean;


    private SensorDTO toDTO(Sensor sensor) {
        SensorDTO  sensorDTO = new SensorDTO(
                sensor.getId(),
                sensor.getType(),
                sensor.getLocation(),
                sensor.getTimestamp(),
                sensor.getStatus()
        );
        sensorDTO.setMeasurements(measurementsToDTOs(sensor.getMeasurements()));

        return sensorDTO;
    }

    private MeasurementDTO measurementToDTO(Measurement measurement) {
        return new MeasurementDTO(
                measurement.getId(),
                measurement.getType(),
                measurement.getValue(),
                measurement.getUnit(),
                measurement.getSensor().getId()
        );
    }

    private List<SensorDTO> toDTOs(List<Sensor> sensors) {
        return sensors.stream().map(this::toDTO).collect(Collectors.toList());
    }

    private List<MeasurementDTO> measurementsToDTOs(List<Measurement> measurements) {
        return measurements.stream().map(this::measurementToDTO).collect(Collectors.toList());
    }

    @GET
    @Path("/")
    public List<SensorDTO> getAllSensors() {
        return toDTOs(sensorBean.getAllSensors());
    }

    @GET
    @Path("{id}")
    public Response getSensorDetails(@PathParam("id") long id)
            throws MyEntityNotFoundException{
        Sensor sensor = sensorBean.find(id);
            return Response.ok(toDTO(sensor)).build();
    }

    @POST
    @Path("/")
    public Response createNewSensor(SensorDTO sensorDTO)
            throws MyEntityExistsException, MyConstraintViolationException{
        Sensor newSensor = sensorBean.create(
                sensorDTO.getType(),
                sensorDTO.getLocation(),
                sensorDTO.getTimestamp(),
                sensorDTO.getStatus()
        );

        return Response.status(Response.Status.CREATED).entity(toDTO(newSensor)).build();
    }

    @PUT
    @Path("/")
    public Response updateSensor(SensorDTO sensorDTO)
            throws MyEntityNotFoundException, MyConstraintViolationException{
        Sensor existingSensor = sensorBean.find(sensorDTO.getId());

        existingSensor.setType(sensorDTO.getType());
        existingSensor.setLocation(sensorDTO.getLocation());
        existingSensor.setTimestamp(sensorDTO.getTimestamp());
        existingSensor.setStatus(sensorDTO.getStatus());

        sensorBean.updateSensor(existingSensor);

        return Response.ok().entity("Sensor updated successfully").build();
    }

    @DELETE
    @Path("{id}")
    public Response deleteSensor(@PathParam("id") long id)
            throws MyEntityNotFoundException, MyConstraintViolationException {

        measurementBean.deleteMeasurementsBySensorId(id);
        sensorBean.deleteSensor(id);
        return Response.ok().entity("Sensor deleted successfully").build();

    }

    @POST
    @Path("/{sensorId}/package/{packageId}")
    public Response addSensorToPackage(@PathParam("packageId") int packageId, @PathParam("sensorId") long sensorId) {
        try {
            sensorBean.addSensorToPackage(packageId, sensorId);
            return Response.ok("Sensor added to package successfully").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error adding sensor to package").build();
        }
    }

    @PATCH
    @Path("/{id}/toogleActive")
    public Response toggleCampo(@PathParam("id") long id)
            throws MyEntityNotFoundException, MyConstraintViolationException{
        sensorBean.updateStatus(id);

        return Response.ok().entity("Campo toggled successfully").build();
    }
}

