package com.victor.TCC.controller;

import com.victor.TCC.dto.request.RegisterUserRequest;
import com.victor.TCC.dto.response.RegisterUserResponse;
import com.victor.TCC.entity.User;
import com.victor.TCC.entity.UserRole;
import com.victor.TCC.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Collections;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/admin")
public class VulnerableAdminController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public VulnerableAdminController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping
    public ResponseEntity<RegisterUserResponse> createAdmin(@RequestBody RegisterUserRequest request) {

        User newAdmin = new User();
        newAdmin.setName(request.name());
        newAdmin.setEmail(request.email());
        newAdmin.setPassword(passwordEncoder.encode(request.password()));
        // Forcing the role to ADMIN
        newAdmin.setRoles(Set.of(UserRole.ADMIN));

        userRepository.save(newAdmin);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(newAdmin.getId()).toUri();

        RegisterUserResponse response = new RegisterUserResponse(newAdmin.getId(), newAdmin.getName(), newAdmin.getEmail(), newAdmin.getRoles().stream().toList());

        return ResponseEntity.created(location).body(response);
    }
}
