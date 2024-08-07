package pt.ipleiria.estg.dei.ei.dae.daebackend.ws;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import pt.ipleiria.estg.dei.ei.dae.daebackend.dtos.MeasurementDTO;
import pt.ipleiria.estg.dei.ei.dae.daebackend.ejbs.MeasurementBean;
import pt.ipleiria.estg.dei.ei.dae.daebackend.ejbs.SensorBean;
import pt.ipleiria.estg.dei.ei.dae.daebackend.entities.Measurement;
import pt.ipleiria.estg.dei.ei.dae.daebackend.entities.Sensor;
import pt.ipleiria.estg.dei.ei.dae.daebackend.exceptions.MyConstraintViolationException;
import pt.ipleiria.estg.dei.ei.dae.daebackend.exceptions.MyEntityExistsException;
import pt.ipleiria.estg.dei.ei.dae.daebackend.exceptions.MyEntityNotFoundException;
import pt.ipleiria.estg.dei.ei.dae.daebackend.security.Authenticated;

import java.util.List;
import java.util.stream.Collectors;

@Path("measurements")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@Authenticated
public class MeasurementService {

    @EJB
    private MeasurementBean measurementBean;
    @EJB
    private SensorBean sensorBean;

    private MeasurementDTO toDTO(Measurement measurement) {
        return new MeasurementDTO(
                measurement.getType(),
                measurement.getValue(),
                measurement.getUnit(),
                measurement.getSensor().getId()
        );
    }

    private List<MeasurementDTO> toDTOs(List<Measurement> measurements) {
        return measurements.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @GET
    @Path("/")
    public List<MeasurementDTO> getAllMeasurements() {
        return toDTOs(measurementBean.getAllMeasurements());
    }

    @GET
    @Path("{id}")
    public Response getMeasurementDetails(@PathParam("id") long id)
            throws MyEntityNotFoundException{
        Measurement measurement = measurementBean.find(id);
        return Response.ok(toDTO(measurement)).build();
    }

    @POST
    @Path("/")
    public Response createNewMeasurement(MeasurementDTO measurementDTO)
            throws MyEntityNotFoundException, MyConstraintViolationException, MyEntityExistsException {
        Measurement newMeasurement = measurementBean.create(
                measurementDTO.getType(),
                measurementDTO.getValue(),
                measurementDTO.getUnit(),
                measurementDTO.getSensorId()
        );

        return Response.status(Response.Status.CREATED).entity(toDTO(newMeasurement)).build();
    }

    @PUT
    @Path("/")
    public Response updateMeasurement(MeasurementDTO measurementDTO)
            throws MyEntityNotFoundException, MyConstraintViolationException {
        Measurement existingMeasurement = measurementBean.find(measurementDTO.getId());

        existingMeasurement.setType(measurementDTO.getType());
        existingMeasurement.setValue(measurementDTO.getValue());
        existingMeasurement.setUnit(measurementDTO.getUnit());

        measurementBean.updateMeasurement(existingMeasurement);

        return Response.ok().entity("Measurement updated successfully").build();
    }

    @DELETE
    @Path("{id}")
    public Response deleteMeasurement(@PathParam("id") long id)
            throws MyEntityNotFoundException, MyConstraintViolationException{
        measurementBean.deleteMeasurement(id);
        return Response.status(Response.Status.NO_CONTENT).build();
    }
}
