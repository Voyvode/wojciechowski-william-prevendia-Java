package com.medilabo.prevendia.patients.security;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import static java.nio.charset.StandardCharsets.UTF_8;

@Component
@Slf4j
public class JwtUtil {

	@Value("${jwt.secret}")
	private String secret;

	public boolean validateToken(String token) {
		var signKey = Keys.hmacShaKeyFor(secret.getBytes(UTF_8));

		Claims claims = Jwts.parserBuilder()
				.setSigningKey(signKey)
				.build()
				.parseClaimsJws(token)
				.getBody();

		// Vérification de l'expiration
		Date expiration = claims.getExpiration();
		return !expiration.before(new Date());
	}

	public String extractUsername(String token) {
		var signKey = Keys.hmacShaKeyFor(secret.getBytes(UTF_8));
		return Jwts.parserBuilder()
				.setSigningKey(signKey)
				.build()
				.parseClaimsJws(token)
				.getBody()
				.getSubject();
	}

	@SuppressWarnings("unchecked")
	public Set<String> extractRoles(String token) {
		var signKey = Keys.hmacShaKeyFor(secret.getBytes(UTF_8));
		Claims claims = Jwts.parserBuilder()
				.setSigningKey(signKey)
				.build()
				.parseClaimsJws(token)
				.getBody();

		Object rolesObj = claims.get("roles");
		log.debug("Roles object from token: {}", rolesObj);
		if (rolesObj instanceof List) {
			Set<String> roles = new HashSet<>((List<String>) rolesObj);
			log.debug("Extracted roles from List: {}", roles);
			return roles;
		}
		log.warn("No roles found in token");
		return new HashSet<>();
	}
}
