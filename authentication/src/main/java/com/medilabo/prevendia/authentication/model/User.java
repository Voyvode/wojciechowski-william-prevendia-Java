package com.medilabo.prevendia.authentication.model;

import java.util.HashSet;
import java.util.Set;

import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import static jakarta.persistence.FetchType.EAGER;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Data
@Table(name = "\"user\"")
public class User {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String username;

	@Column(nullable = false)
	private String password;

	@ElementCollection(fetch = EAGER)
	private Set<String> roles = new HashSet<>();

}