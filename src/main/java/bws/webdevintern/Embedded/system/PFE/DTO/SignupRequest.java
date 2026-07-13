package bws.webdevintern.Embedded.system.PFE.DTO;

import java.util.ArrayList;
import java.util.List;

import bws.webdevintern.Embedded.system.PFE.models.Role;

public class SignupRequest {
	public String username;
	public String firstName;
	public String lastName;
	public String email;
	public String password;
	private List<String> roles = new ArrayList<>();
	
	
	public String getUsername() {
		return username;
	}
	public String getFirstName() {
		return firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public String getEmail() {
		return email;
	}
	public String getPassword() {
		return password;
	}
	public List<String> getRoles() {
		return roles;
	}
	public void setRoles(List<String> roles) {
	    this.roles = roles;
	}

	


	

}
