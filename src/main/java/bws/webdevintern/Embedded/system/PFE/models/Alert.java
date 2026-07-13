package bws.webdevintern.Embedded.system.PFE.models;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="alert")
public class Alert implements Serializable{

    	private static final long serialVersionUID = 1L;

	 	@Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    private String message;

	    @Enumerated(EnumType.STRING)
	    private Severity severity;

	    private double triggeredValue;
	    private LocalDateTime triggeredAt;
	    private boolean isResolved;

	    // MANY ALERTS -> ONE RULE
	    @ManyToOne
	    @JoinColumn(name = "rule_id")
	    @JsonIgnore
	    private Rule rule;

	    // MANY ALERTS -> ONE SENSOR
	    @ManyToOne
	    @JoinColumn(name = "sensor_id")
	    @JsonIgnoreProperties({"configurations", "rules", "alerts", "inputs"}) 
	    private Sensor sensor;

	    // MANY ALERTS -> ONE SENSOR INPUT
	    @ManyToOne
	    @JoinColumn(name = "sensor_input_id")
	    @JsonIgnoreProperties({"alerts", "sensor"}) 
	    private SensorInput sensorInput;

		public Alert() {
			super();
			// TODO Auto-generated constructor stub
		}

		public Alert(String message, Severity severity, double triggeredValue, LocalDateTime triggeredAt,
				boolean isResolved, Rule rule, Sensor sensor, SensorInput sensorInput) {
			super();
			this.message = message;
			this.severity = severity;
			this.triggeredValue = triggeredValue;
			this.triggeredAt = triggeredAt;
			this.isResolved = isResolved;
			this.rule = rule;
			this.sensor = sensor;
			this.sensorInput = sensorInput;
		}

		public Long getId() {
			return id;
		}

		public String getMessage() {
			return message;
		}

		public Severity getSeverity() {
			return severity;
		}

		public double getTriggeredValue() {
			return triggeredValue;
		}

		public LocalDateTime getTriggeredAt() {
			return triggeredAt;
		}

		public boolean isResolved() {
			return isResolved;
		}

		public Rule getRule() {
			return rule;
		}

		public Sensor getSensor() {
			return sensor;
		}

		public SensorInput getSensorInput() {
			return sensorInput;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		public void setSeverity(Severity severity) {
			this.severity = severity;
		}

		public void setTriggeredValue(double triggeredValue) {
			this.triggeredValue = triggeredValue;
		}

		public void setTriggeredAt(LocalDateTime triggeredAt) {
			this.triggeredAt = triggeredAt;
		}

		public void setResolved(boolean isResolved) {
			this.isResolved = isResolved;
		}

		public void setRule(Rule rule) {
			this.rule = rule;
		}

		public void setSensor(Sensor sensor) {
			this.sensor = sensor;
		}

		public void setSensorInput(SensorInput sensorInput) {
			this.sensorInput = sensorInput;
		}
	    
		
	    
	    

}
