package com.medilabo.prevendia.authentication.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.medilabo.prevendia.authentication.model.User;

/**
 * Repository for authentication user management.
 */
public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByUsername(String username);

}
