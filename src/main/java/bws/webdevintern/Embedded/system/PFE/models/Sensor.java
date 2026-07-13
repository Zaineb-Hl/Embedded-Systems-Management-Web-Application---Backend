package bws.webdevintern.Embedded.system.PFE.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;


@Entity
@Table(name = "sensor")
public class Sensor implements Serializable{

    private static final long serialVersionUID = 1L;


	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sensor_id")
    private Long id;
    
    @Column(name="name", nullable=false)
    private  String name;
    
    @Column(name= "isActive")
    private Boolean isActive;
    
    // MANY SENSORS -> ONE SYSTEM
    @ManyToOne
    @JoinColumn(name = "system_id")
    @JsonIgnoreProperties({"sensors", "owner"})  
    private EmbeddedSystem embeddedSystem;
    
    // MANY SENSORS -> ONE TYPE
    @ManyToOne
    @JoinColumn(name = "sensor_type_id")
    @JsonIgnoreProperties({"sensors"})
    private SensorType sensorType;
    
    // ONE SENSOR -> MANY CONFIGS
    @OneToMany(mappedBy = "sensor",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @JsonIgnoreProperties({"sensor"})
    private List<SensorConfiguration> configurations = new ArrayList<>();
    
    // ONE SENSOR -> MANY RULES
    @OneToMany(mappedBy = "sensor",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @JsonIgnoreProperties({"sensor", "alerts"})
    private List<Rule> rules = new ArrayList<>();
    
    // ONE SENSOR -> MANY ALERTS
    @OneToMany(mappedBy = "sensor")
    @JsonIgnoreProperties({"sensor", "alerts"})
    private List<Alert> alerts = new ArrayList<>();
    
    // ONE SENSOR -> MANY INPUTS
    @OneToMany(mappedBy = "sensor",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @JsonIgnoreProperties({"sensor", "alerts"})
    private List<SensorInput> inputs = new ArrayList<>();

	public Sensor() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Sensor(String name, Boolean isActive, EmbeddedSystem embeddedSystem, SensorType sensorType,
			List<SensorConfiguration> configurations, List<Rule> rules, List<Alert> alerts, List<SensorInput> inputs) {
		super();
		this.name = name;
		this.isActive = isActive;
		this.embeddedSystem = embeddedSystem;
		this.sensorType = sensorType;
		this.configurations = configurations;
		this.rules = rules;
		this.alerts = alerts;
		this.inputs = inputs;
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public EmbeddedSystem getEmbeddedSystem() {
		return embeddedSystem;
	}

	public SensorType getSensorType() {
		return sensorType;
	}

	public List<SensorConfiguration> getConfigurations() {
		return configurations;
	}

	public List<Rule> getRules() {
		return rules;
	}

	public List<Alert> getAlerts() {
		return alerts;
	}

	public List<SensorInput> getInputs() {
		return inputs;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public void setEmbeddedSystem(EmbeddedSystem embeddedSystem) {
		this.embeddedSystem = embeddedSystem;
	}

	public void setSensorType(SensorType sensorType) {
		this.sensorType = sensorType;
	}

	public void setConfigurations(List<SensorConfiguration> configurations) {
		this.configurations = configurations;
	}

	public void setRules(List<Rule> rules) {
		this.rules = rules;
	}

	public void setAlerts(List<Alert> alerts) {
		this.alerts = alerts;
	}

	public void setInputs(List<SensorInput> inputs) {
		this.inputs = inputs;
	}
    
    
    
    
    
}
