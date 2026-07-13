package bws.webdevintern.Embedded.system.PFE.models;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;


@Entity
@Table(name = "sensorInput")
public class SensorInput implements Serializable{

    private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="input_id")
	private Long id;

    @Column(name="value")
    private Double value;

    @CreationTimestamp
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;
    
    private String quality;
    
 // MANY INPUTS -> ONE SENSOR
    @ManyToOne
    @JoinColumn(name = "sensor_id")
    @JsonIgnoreProperties({"configurations", "rules", "alerts", "inputs"})
    private Sensor sensor;

    // ONE INPUT -> MANY ALERTS
    @OneToMany(mappedBy = "sensorInput")
    @JsonIgnoreProperties({"sensorInput", "sensor", "rule"})
    private List<Alert> alerts = new ArrayList<>();

	public SensorInput() {
		super();
		// TODO Auto-generated constructor stub
	}

	public SensorInput(Double value, LocalDateTime timestamp, String quality, Sensor sensor, List<Alert> alerts) {
		super();
		this.value = value;
		this.timestamp = timestamp;
		this.quality = quality;
		this.sensor = sensor;
		this.alerts = alerts;
	}

	public Long getId() {
		return id;
	}

	public Double getValue() {
		return value;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public String getQuality() {
		return quality;
	}

	public Sensor getSensor() {
		return sensor;
	}

	public List<Alert> getAlerts() {
		return alerts;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public void setQuality(String quality) {
		this.quality = quality;
	}

	public void setSensor(Sensor sensor) {
		this.sensor = sensor;
	}

	public void setAlerts(List<Alert> alerts) {
		this.alerts = alerts;
	}
    
    

 
}
