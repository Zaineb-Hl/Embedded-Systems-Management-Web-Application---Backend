package bws.webdevintern.Embedded.system.PFE.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table (name="sensor_Type")
public class SensorType implements Serializable{
	
    private static final long serialVersionUID = 1L;

    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;
    private String description;
    private String unit;

    @OneToMany(mappedBy = "sensorType")
    @JsonManagedReference
    private List<Sensor> sensors = new ArrayList<>();
    
	public SensorType() {
		super();
		// TODO Auto-generated constructor stub
	}

	public SensorType(String code, String description, String unit, List<Sensor> sensors) {
		super();
		this.code = code;
		this.description = description;
		this.unit = unit;
		this.sensors = sensors;
	}

	public Long getId() {
		return id;
	}

	public String getCode() {
		return code;
	}

	public String getDescription() {
		return description;
	}

	public String getUnit() {
		return unit;
	}

	public List<Sensor> getSensors() {
		return sensors;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public void setSensors(List<Sensor> sensors) {
		this.sensors = sensors;
	}
	
	
	
    
	
    

}
