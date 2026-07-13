package bws.webdevintern.Embedded.system.PFE.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import bws.webdevintern.Embedded.system.PFE.services.RoleService;
import java.util.List;
import bws.webdevintern.Embedded.system.PFE.models.Role;


@RestController
@RequestMapping("/api/roles")
@CrossOrigin(origins = "http://localhost:4200")
public class RoleController {
	
	  @Autowired
	  private RoleService roleService;

	    @GetMapping
	    public List<Role> getAllRoles() {
	        return roleService.getAllRoles();
	    }

}
