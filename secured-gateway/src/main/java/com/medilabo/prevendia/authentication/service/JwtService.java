package com.medilabo.prevendia.authentication.service;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.HashSet;
import java.util.List;
import java.util.Arrays;
import java.util.Set;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

import com.medilabo.prevendia.authentication.model.User;
import com.medilabo.prevendia.authentication.repository.UserRepository;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Service handling JWT token operations including generation, validation, and claims extraction.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class JwtService {

	@Value("${jwt.secret}")
	private String secret;

	@Value("${jwt.expiration}")
	private Long expiration;

	private final UserRepository userRepository;

	/**
	 * Generates a JWT token for a user with optional additional claims.
	 *
	 * @param username user identifier to be included in the token
	 * @return signed JWT token string
	 */
	public Mono<String> generateToken(String username) {
		log.info("Generating JWT token for user {}", username);
		return userRepository.findByUsername(username)
				.map(User::getRoles)
				.map(roles -> {
					Map<String, Object> claims = new HashMap<>();
					claims.put("roles", roles);
					return createToken(claims, username);
				});
	}

	/**
	 * Validates a JWT token's signature and expiration.
	 *
	 * @param token JWT token to validate
	 * @return Mono<Boolean> true if token is valid, false otherwise
	 */
	public Mono<Boolean> validateToken(String token) {
		log.info("Validating JWT token");
		return Mono.fromCallable(() -> {
			// Check token signature and structure
			Jwts.parserBuilder()
					.setSigningKey(getSignKey())
					.build()
					.parseClaimsJws(token);

			Instant expiration = extractClaim(token, Claims::getExpiration).toInstant();
			return Instant.now().isBefore(expiration);
		});
	}

	/**
	 * Extracts the username from a JWT token.
	 *
	 * @param token JWT token
	 * @return the username stored in the token
	 */
	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	/**
	 * Extracts the role from a JWT token.
	 *
	 * @param token JWT token
	 * @return the role stored in the token
	 */
	public String extractRole(String token) {
		return extractClaim(token, claims -> claims.get("role", String.class));
	}

	/**
	 * Extracts the roles from a JWT token.
	 *
	 * @param token JWT token
	 * @return the roles stored in the token
	 */
	@SuppressWarnings("unchecked")
	public Set<String> extractRoles(String token) {
		Object rolesObj = extractClaim(token, claims -> claims.get("roles"));
		log.info("Roles object from token: {}", rolesObj);
		if (rolesObj instanceof List) {
			Set<String> roles = new HashSet<>((List<String>) rolesObj);
			log.info("Extracted roles from List: {}", roles);
			return roles;
		} else if (rolesObj instanceof String) {
			Set<String> roles = new HashSet<>(Arrays.asList(((String) rolesObj).split(",")));
			log.info("Extracted roles from String: {}", roles);
			return roles;
		}
		log.warn("No roles found in token");
		return new HashSet<>();
	}

	/**
	 * Extracts all claims from a JWT token.
	 *
	 * @param token JWT token
	 * @return the claims stored in the token
	 */
	private Claims extractAllClaims(String token) {
		return Jwts.parserBuilder()
				.setSigningKey(getSignKey())
				.build()
				.parseClaimsJws(token)
				.getBody();
	}

	/**
	 * Extracts a specific claim from a JWT token using the provided claims resolver function.
	 *
	 * @param token JWT token
	 * @param claimsResolver function to extract the desired claim
	 * @return the extracted claim value
	 */
	private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	/**
	 * Creates a JWT token with specified claims and subject.
	 *
	 * @param claims token claims
	 * @param subject token subject (username)
	 * @return signed JWT token string
	 */
	private String createToken(Map<String, Object> claims, String subject) {
		return Jwts.builder()
				.setClaims(claims)
				.setSubject(subject)
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + expiration))
				.signWith(getSignKey())
				.compact();
	}

	/**
	 * Gets the signing key used for JWT token operations.
	 *
	 * @return signing key derived from the configured secret
	 */
	private Key getSignKey() {
		byte[] keyBytes = secret.getBytes(UTF_8);
		return Keys.hmacShaKeyFor(keyBytes);
	}

}