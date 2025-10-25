package com.victor.TCC.controller;

import com.victor.TCC.config.TokenConfig;
import com.victor.TCC.dto.request.LoginRequest;
import com.victor.TCC.dto.request.RegisterUserRequest;
import com.victor.TCC.dto.response.LoginResponse;
import com.victor.TCC.dto.response.RegisterUserResponse;
import com.victor.TCC.entity.User;
import com.victor.TCC.entity.UserRole;
import com.victor.TCC.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenConfig tokenConfig;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, TokenConfig tokenConfig) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenConfig = tokenConfig;
    }


    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password());
        Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);

        User user = (User) authentication.getPrincipal();
        String token = tokenConfig.generateToken(user);

        return ResponseEntity.ok(new LoginResponse(token));
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterUserResponse> registerUser(@Valid @RequestBody RegisterUserRequest registerUserRequest) {


        System.out.println("registerUserRequest: " + registerUserRequest);

        User user = new User();
        user.setName(registerUserRequest.name());
        user.setEmail(registerUserRequest.email());
        user.setPassword(passwordEncoder.encode(registerUserRequest.password()));
        user.setRoles(Set.of(UserRole.CUSTOMER));

        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(new RegisterUserResponse(user.getId(), user.getName(), user.getEmail(), user.getRoles().stream().toList()));
    }
}
