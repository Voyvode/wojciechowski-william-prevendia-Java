package com.medilabo.prevendia.authentication.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import reactor.core.publisher.Mono;

import com.medilabo.prevendia.authentication.model.User;

/**
 * Repository for authentication user management.
 */
public interface UserRepository extends ReactiveCrudRepository<User, Long> {

	Mono<User> findByUsername(String username);

}
