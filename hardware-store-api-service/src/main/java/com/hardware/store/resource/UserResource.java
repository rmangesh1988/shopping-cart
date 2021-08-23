package com.hardware.store.resource;

import com.hardware.store.domain.User;
import com.hardware.store.service.UserService;
import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/users")
@AllArgsConstructor(onConstructor = @__({@Autowired}))
public class UserResource {

    private UserService userService;

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<User> findUser(@PathVariable @NotNull Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }
}
