package com.medilabo.prevendia.authentication.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.medilabo.prevendia.authentication.model.User;
import com.medilabo.prevendia.authentication.repository.UserRepository;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private JwtService jwtService;

    private static final String SECRET = "testSecretKeyMustBeAtLeast32BytesLong1234567890";
    private static final Long EXPIRATION = 86400000L; // 24 heures
    private static final String USERNAME = "testUser";
    private static final Set<String> ROLES = Set.of("ROLE_USER", "ROLE_ADMIN");
    private String validToken;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtService, "secret", SECRET);
        ReflectionTestUtils.setField(jwtService, "expiration", EXPIRATION);
        
        var user = new User();
        user.setUsername(USERNAME);
        user.setRoles(ROLES);
        
        when(userRepository.findByUsername(USERNAME)).thenReturn(Mono.just(user));
        
        StepVerifier.create(jwtService.generateToken(USERNAME))
            .consumeNextWith(token -> validToken = token)
            .verifyComplete();
    }

    @Test
    void generateToken_ShouldGenerateValidToken() {
        var user = new User();
        user.setUsername(USERNAME);
        user.setRoles(ROLES);
        
        when(userRepository.findByUsername(USERNAME)).thenReturn(Mono.just(user));

        StepVerifier.create(jwtService.generateToken(USERNAME))
            .assertNext(token -> {
                assertNotNull(token);
				assertFalse(token.isEmpty());
            })
            .verifyComplete();
    }

    @Test
    void validateToken_WithValidToken_ShouldReturnTrue() {
        StepVerifier.create(jwtService.validateToken(validToken))
            .expectNext(true)
            .verifyComplete();
    }
    
    @Test
    void validateToken_WithExpiredToken_ShouldReturnFalse() {
        String expiredToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0VXNlciIsImlhdCI6MTYwMDAwMDAwMCwiZXhwIjoxNjAwMDAwMDAxfQ.BnMv9Q6XjU7KQoXgxmZjczOI4y-SCEixLI7-VXD5wHM";
        
        StepVerifier.create(jwtService.validateToken(expiredToken))
            .expectError()
            .verify();
    }
    
    @Test
    void extractUsername_ShouldReturnCorrectUsername() {
        String username = jwtService.extractUsername(validToken);
        assertEquals(USERNAME, username);
    }
    
    @Test
    void extractRoles_ShouldReturnCorrectRoles() {
        Set<String> roles = jwtService.extractRoles(validToken);
        assertEquals(ROLES, roles);
    }
} 