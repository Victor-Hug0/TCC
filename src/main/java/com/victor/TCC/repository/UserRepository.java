package com.victor.TCC.repository;

import com.victor.TCC.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<UserDetails> findUserByEmail(String email);

    User getUserById(Long id);

    Optional<User> findByEmail(String email);
}
