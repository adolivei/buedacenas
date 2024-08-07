package pt.ipleiria.estg.dei.ei.dae.daebackend.dtos;

import pt.ipleiria.estg.dei.ei.dae.daebackend.enums.SensorStatus;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class SensorDTO implements Serializable {
    private long id;
    private String type;
    private String location;
    private Date timestamp;
    private SensorStatus status;
    private List<MeasurementDTO> measurements = new LinkedList<>();

    public SensorDTO() {
    }

    public SensorDTO(long id, String type, String location, Date timestamp, SensorStatus status) {
        this.id = id;
        this.type = type;
        this.location = location;
        this.timestamp = timestamp;
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public SensorStatus getStatus() {
        return status;
    }

    public void setStatus(SensorStatus status) {
        this.status = status;
    }

    public List<MeasurementDTO> getMeasurements() {
        return measurements;
    }

    public void setMeasurements(List<MeasurementDTO> measurements) {
        this.measurements = measurements;
    }
}