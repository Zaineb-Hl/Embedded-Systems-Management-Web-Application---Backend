package bws.webdevintern.Embedded.system.PFE.servicesImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bws.webdevintern.Embedded.system.PFE.models.Role;
import bws.webdevintern.Embedded.system.PFE.repositories.RoleRepository;
import bws.webdevintern.Embedded.system.PFE.services.RoleService;

@Service
public class RoleServiceImpl implements RoleService{

	@Autowired
    private RoleRepository roleRepository;

    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }
}
