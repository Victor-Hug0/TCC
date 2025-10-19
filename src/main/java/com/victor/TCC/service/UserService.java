package com.victor.TCC.service;

import com.victor.TCC.dto.response.UserResponseDTO;
import com.victor.TCC.entity.User;
import com.victor.TCC.exception.ResourceNotFoundException;
import com.victor.TCC.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponseDTO getUserById(Long id) {

        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return UserResponseDTO.fromEntity(user);
    }

    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream().map(UserResponseDTO::fromEntity).collect(Collectors.toList());
    }
}
