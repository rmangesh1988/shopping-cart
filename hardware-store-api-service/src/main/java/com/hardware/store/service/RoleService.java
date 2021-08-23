package com.hardware.store.service;

import com.hardware.store.domain.Role;
import com.hardware.store.repository.RoleRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
@AllArgsConstructor(onConstructor = @__({@Autowired}))
public class RoleService {

    private RoleRepository roleRepository;

    public Role save(Role role) {
        return roleRepository.save(role);
    }
}
