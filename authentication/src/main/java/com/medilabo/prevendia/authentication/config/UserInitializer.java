package com.medilabo.prevendia.authentication.config;

import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.medilabo.prevendia.authentication.model.User;
import com.medilabo.prevendia.authentication.repository.UserRepository;

@Configuration
public class UserInitializer {

	@Bean
	CommandLineRunner initDatabase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			if (userRepository.count() == 0) {
				var doctor = new User();
				doctor.setUsername("doctor");
				doctor.setPassword(passwordEncoder.encode("doctor_password"));
				doctor.setRoles(Set.of("DOCTOR"));
				userRepository.save(doctor);

				var admin = new User();
				admin.setUsername("organizer");
				admin.setPassword(passwordEncoder.encode("organizer_password"));
				admin.setRoles(Set.of("ORGANIZER"));
				userRepository.save(admin);
			}
		};
	}

}
