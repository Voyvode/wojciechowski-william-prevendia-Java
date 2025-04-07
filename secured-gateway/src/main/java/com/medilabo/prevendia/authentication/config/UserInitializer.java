package com.medilabo.prevendia.authentication.config;

import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.medilabo.prevendia.authentication.model.User;
import com.medilabo.prevendia.authentication.repository.UserRepository;

/**
 * Initializes default users in the system during application startup.
 */
@Configuration
public class UserInitializer {

	/**
	 * CommandLineRunner bean initializes the database with default users.
	 *
	 * @param userRepo repository for user data access
	 * @param passcoder password encoder for securing user passwords
	 * @return CommandLineRunner that creates the initial users
	 */
	@Bean
	CommandLineRunner initDatabase(UserRepository userRepo, PasswordEncoder passcoder) {
		return args -> {
			var doc = new User();
			doc.setUsername("g.maison");
			doc.setShownName("Dʳ Maison");
			doc.setPassword(passcoder.encode("doctor_password"));
			doc.setRoles(Set.of("DOCTOR"));

			var sec = new User();
			sec.setUsername("l.holloway");
			sec.setShownName("Holloway");
			sec.setPassword(passcoder.encode("secretary_password"));
			sec.setRoles(Set.of("ORGANIZER"));

			userRepo.save(doc)
					.then(userRepo.save(sec))
					.block();
		};
	}

}
