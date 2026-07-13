package bws.webdevintern.Embedded.system.PFE.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table (name="user")
public class User implements Serializable{
	
    private static final long serialVersionUID = 1L;

	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="USER_ID")
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(name = "password", nullable = true)
    private String password;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "last_name", length = 50)
    private String lastName;

    @Column(name = "first_name", length = 50)
    private String firstName;
    
    @Column(nullable = false)
    private boolean isActive = true;

    @ManyToMany(fetch =FetchType.EAGER)
	@JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns =@JoinColumn(name = "role_id")) 
	private List<Role> roles = new ArrayList<>();
    
 // ONE USER -> MANY SYSTEMS
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<EmbeddedSystem> systems = new ArrayList<>();
    
   
public User() {
		super();
		// TODO Auto-generated constructor stub
	}

public User(String username, String password, String email, String lastName, String firstName, List<Role> roles,
		List<EmbeddedSystem> systems) {
	super();
	this.username = username;
	this.password = password;
	this.email = email;
	this.lastName = lastName;
	this.firstName = firstName;
	this.roles = roles;
	this.systems = systems;
}

public Long getId() {
	return id;
}
public void setId(Long id) {
	this.id = id;
}
public String getUsername() {
	return username;
}

public String getPassword() {
	return password;
}

public String getEmail() {
	return email;
}

public String getLastName() {
	return lastName;
}


public String getFirstName() {
	return firstName;
}

public boolean getIsActive() { 
	return isActive; 
}

public List<Role> getRoles() {
	return roles;
}

public List<EmbeddedSystem> getSystems() {
	return systems;
}

public void setUsername(String username) {
	this.username = username;
}

public void setPassword(String password) {
	this.password = password;
}

public void setEmail(String email) {
	this.email = email;
}

public void setLastName(String lastName) {
	this.lastName = lastName;
}

public void setFirstName(String firstName) {
	this.firstName = firstName;
}

public void setIsActive(boolean isActive) { 
	this.isActive = isActive; 
}


public void setRoles(List<Role> roles) {
	this.roles = roles;
}

public void setSystems(List<EmbeddedSystem> systems) {
	this.systems = systems;
}


    

}
