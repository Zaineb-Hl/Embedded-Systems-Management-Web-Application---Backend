package bws.webdevintern.Embedded.system.PFE.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name="roles") 
public class Role implements Serializable{
	
    private static final long serialVersionUID = 1L;


	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="ROLE_ID")
	private Long id;
	private String name;
	
	@ManyToMany(mappedBy = "roles")
	@JsonIgnore
	private List <User> user = new ArrayList<>();

	public Role(String name, List<User> user) {
		super();
		this.name = name;
		this.user = user;
	}

	public Role() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public List<User> getUser() {
		return user;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setUser(List<User> user) {
		this.user = user;
	}
	
	
	
	

	
	
}
