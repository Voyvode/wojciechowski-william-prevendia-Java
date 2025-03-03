package com.medilabo.prevendia.risk.config;

import java.io.IOException;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.text.Normalizer.Form.NFD;
import static java.text.Normalizer.normalize;

@Configuration
public class TriggersConfig {

	@Bean
	public List<String> triggerList() throws IOException {
		ClassPathResource resource = new ClassPathResource("/triggers.txt");
		System.out.println(resource);
		return resource.getContentAsString(UTF_8)
				.lines()
				.map(this::normalizeTrigger)
				.toList();
	}

	private String normalizeTrigger(String trigger) {
		return normalize(trigger, NFD)
				.replaceAll("\\p{M}", "")
				.toLowerCase();
	}

}