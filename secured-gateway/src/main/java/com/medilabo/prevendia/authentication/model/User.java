package com.medilabo.prevendia.authentication.model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.Data;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Entity representing a user in the authentication system.
 */
@Data
@Table(name = "\"user\"")
public class User {

	@Id
	private Long id;

	@Column
	@NotBlank
	private String username;

	@Column
	@NotBlank
	private String shownName;

	@Column
	@NotBlank
	private String password;

	@Column
	private String roles;

	public Set<String> getRoles() {
		return Optional.ofNullable(roles)
				.map(s -> Arrays.stream(s.split(","))
						.map(String::trim)
						.filter(role -> !role.isEmpty())
						.collect(Collectors.toSet()))
				.orElseGet(HashSet::new);
	}

	public void setRoles(Set<String> roles) {
		this.roles = Optional.ofNullable(roles)
				.filter(r -> !r.isEmpty())
				.map(r -> String.join(",", r))
				.orElse("");
	}

}