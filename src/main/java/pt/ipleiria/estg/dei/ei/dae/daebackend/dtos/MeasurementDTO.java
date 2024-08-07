package pt.ipleiria.estg.dei.ei.dae.daebackend.dtos;

import java.io.Serializable;

public class MeasurementDTO implements Serializable {
    private long id;
    private String type;
    private double value;
    private String unit;
    private long sensorId;

    public MeasurementDTO() {
    }

    public MeasurementDTO(long id,String type, double value,String unit, long sensorId) {
        this.id = id;
        this.type = type;
        this.value = value;
        this.unit = unit;
        this.sensorId = sensorId;
    }

    public MeasurementDTO(String type, double value,String unit, long sensorId) {
        this.type = type;
        this.value = value;
        this.unit = unit;
        this.sensorId = sensorId;
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

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public long getSensorId() {
        return sensorId;
    }

    public void setSensorId(long sensorId) {
        this.sensorId = sensorId;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
