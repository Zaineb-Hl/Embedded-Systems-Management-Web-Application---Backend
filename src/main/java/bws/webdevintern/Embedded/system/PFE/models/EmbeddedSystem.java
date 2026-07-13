package bws.webdevintern.Embedded.system.PFE.models;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import bws.webdevintern.Embedded.system.PFE.models.SystemStatus;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
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
@Table(name = "embeddedSystem")
public class EmbeddedSystem implements Serializable{

    private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "system_id")
    private Long id;
    private String name;
    private String description;
    @Column(unique = true, nullable = false)
    private String uniqueIdentifier;
    
    @Enumerated(EnumType.STRING)
    private SystemStatus status;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // ONE SYSTEM -> MANY SENSORS
    @OneToMany(mappedBy = "embeddedSystem",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @JsonManagedReference
    private List<Sensor> sensors = new ArrayList<>();
    
    // MANY SYSTEMS -> ONE USER
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    
    public EmbeddedSystem() {
		super();
		// TODO Auto-generated constructor stub
	}

	public EmbeddedSystem(String name, String description, String uniqueIdentifier, SystemStatus status,
			LocalDateTime createdAt, LocalDateTime updatedAt, List<Sensor> sensors, User owner) {
		super();
		this.name = name;
		this.description = description;
		this.uniqueIdentifier = uniqueIdentifier;
		this.status = status;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.sensors = sensors;
		this.owner = owner;
	}

	public Long getId() {
		return id;
	}


	public String getName() {
		return name;
	}


	public String getDescription() {
		return description;
	}


	public String getUniqueIdentifier() {
		return uniqueIdentifier;
	}


	public SystemStatus getStatus() {
		return status;
	}


	public LocalDateTime getCreatedAt() {
		return createdAt;
	}


	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}


	public List<Sensor> getSensors() {
		return sensors;
	}


	public User getOwner() {
		return owner;
	}


	public void setName(String name) {
		this.name = name;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public void setUniqueIdentifier(String uniqueIdentifier) {
		this.uniqueIdentifier = uniqueIdentifier;
	}


	public void setStatus(SystemStatus status) {
		this.status = status;
	}


	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}


	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}


	public void setSensors(List<Sensor> sensors) {
		this.sensors = sensors;
	}


	public void setOwner(User owner) {
		this.owner = owner;
	}

	

	
    
    

}
