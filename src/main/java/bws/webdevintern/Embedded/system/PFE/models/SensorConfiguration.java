package bws.webdevintern.Embedded.system.PFE.models;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="SensorConfiguration")
public class SensorConfiguration implements Serializable{
	
    	private static final long serialVersionUID = 1L;

	
	 	@Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    private double minRange;
	    private double maxRange;
	    private int samplingRate;
	    private Boolean isActive;

	    // MANY CONFIGS -> ONE SENSOR
	    @ManyToOne
	    @JoinColumn(name = "sensor_id")
	    @JsonIgnoreProperties({"configurations", "rules", "alerts", "inputs"}) // était {"configurations"}
	    private Sensor sensor;
	    
	    
		public SensorConfiguration() {
			super();
			// TODO Auto-generated constructor stub
		}

		public SensorConfiguration(double minRange, double maxRange, int samplingRate, boolean isActive,
				Sensor sensor) {
			super();
			this.minRange = minRange;
			this.maxRange = maxRange;
			this.samplingRate = samplingRate;
			this.isActive = isActive;
			this.sensor = sensor;
		}

		public Long getId() {
			return id;
		}

		public double getMinRange() {
			return minRange;
		}

		public double getMaxRange() {
			return maxRange;
		}

		public int getSamplingRate() {
			return samplingRate;
		}

		public boolean isActive() {
			return isActive;
		}

		public Sensor getSensor() {
			return sensor;
		}

		public void setMinRange(double minRange) {
			this.minRange = minRange;
		}

		public void setMaxRange(double maxRange) {
			this.maxRange = maxRange;
		}

		public void setSamplingRate(int samplingRate) {
			this.samplingRate = samplingRate;
		}

		public void setActive(boolean isActive) {
			this.isActive = isActive;
		}

		public void setSensor(Sensor sensor) {
			this.sensor = sensor;
		}
		
		
	    
	    

}
