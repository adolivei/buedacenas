package pt.ipleiria.estg.dei.ei.dae.daebackend.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import pt.ipleiria.estg.dei.ei.dae.daebackend.enums.SensorStatus;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Entity
@NamedQueries({
        @NamedQuery(
                name = "getAllSensors",
                query = "SELECT s FROM Sensor s ORDER BY s.id"
        )
})
public class Sensor extends Versionable implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotNull
    private String type;
    @NotNull
    private String location;
    @NotNull
    private Date timestamp;
    @Enumerated(EnumType.STRING)
    @NotNull
    private SensorStatus status;

    @OneToMany(mappedBy = "sensor", cascade = CascadeType.REMOVE)
    private List<Measurement> measurements;

    @ManyToMany(mappedBy = "sensors" )
    private List<Package> packages;

    public Sensor() {
    }

    public Sensor(String type, String location, Date timestamp, SensorStatus status) {
        //this.id = id;
        this.type = type;
        this.location = location;
        this.timestamp = timestamp;
        this.status = status;
        this.measurements = new LinkedList<Measurement>();
        this.packages = new LinkedList<Package>();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public SensorStatus getStatus(){
        return status;
    }
    public void setStatus(SensorStatus status){
        this.status = status;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void addMeasurement(Measurement measurement){
        measurements.add(measurement);
    }

    public void removeMeasurement(Measurement measurement){
        measurements.remove(measurement);
    }

    public List<Measurement> getMeasurements(){
        return measurements;
    }

    public void setMeasurements(List<Measurement> measurements){
        this.measurements = measurements;
    }

    public List<Package> getPackages() {
        return packages;
    }

    public void setPackages(List<Package> packages) {
        this.packages = packages;
    }

    public void addPackage(Package p) {
        packages.add(p);
    }

    public void removePackage(Package p) {
        packages.remove(p);
    }
}
