package bws.webdevintern.Embedded.system.PFE.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table (name="rule")
public class Rule implements Serializable{
	
    private static final long serialVersionUID = 1L;

	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	 
	private String name;

    @Enumerated(EnumType.STRING)
    private ComparisonOperator operator;

    private double thresholdValue;
    private Boolean isActive = true;
    private String notificationMessage;
    
    @Enumerated(EnumType.STRING)
    private Severity severity;

    // MANY RULES -> ONE SENSOR
    @ManyToOne
    @JoinColumn(name = "sensor_id")
    @JsonIgnoreProperties({"configurations", "rules", "alerts", "inputs"})
    private Sensor sensor;

    // ONE RULE -> MANY ALERTS
    @OneToMany(mappedBy = "rule")
    @JsonIgnoreProperties({"rule", "sensor", "sensorInput"}) 
    private List<Alert> alerts = new ArrayList<>();

	public Rule() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Rule(String name, ComparisonOperator operator, double thresholdValue, Boolean isActive,
			String notificationMessage, Severity severity, Sensor sensor, List<Alert> alerts) {
		super();
		this.name = name;
		this.operator = operator;
		this.thresholdValue = thresholdValue;
		this.isActive = isActive;
		this.notificationMessage = notificationMessage;
		this.severity = severity;
		this.sensor = sensor;
		this.alerts = alerts;
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public ComparisonOperator getOperator() {
		return operator;
	}

	public double getThresholdValue() {
		return thresholdValue;
	}

	public boolean isActive() {
		return isActive;
	}

	public String getNotificationMessage() {
		return notificationMessage;
	}
	
    public Severity getSeverity() { 
    	return severity; 
    }

	public Sensor getSensor() {
		return sensor;
	}

	public List<Alert> getAlerts() {
		return alerts;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setOperator(ComparisonOperator operator) {
		this.operator = operator;
	}

	public void setThresholdValue(double thresholdValue) {
		this.thresholdValue = thresholdValue;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public void setNotificationMessage(String notificationMessage) {
		this.notificationMessage = notificationMessage;
	}
	
    public void setSeverity(Severity severity) { 
    	this.severity = severity; 
    }


	public void setSensor(Sensor sensor) {
		this.sensor = sensor;
	}

	public void setAlerts(List<Alert> alerts) {
		this.alerts = alerts;
	}
    
    

}
