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

/**
 * Entity representing a user in the authentication system.
 */
@Data
@Table(name = "\"user\"")
public class User {

	@Id
	private Long id;

	@Column
	private String username;

	@Column
	private String shownName;

	@Column
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