package com.medilabo.prevendia.risk.service;

import java.util.List;

import com.medilabo.prevendia.risk.dto.PatientProfileDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import com.medilabo.prevendia.risk.model.Risk;

import static com.medilabo.prevendia.risk.model.Risk.BORDERLINE;
import static com.medilabo.prevendia.risk.model.Risk.EARLY_ONSET;
import static com.medilabo.prevendia.risk.model.Risk.IN_DANGER;
import static com.medilabo.prevendia.risk.model.Risk.NONE;
import static java.text.Normalizer.Form.NFD;
import static java.text.Normalizer.normalize;

@Service
@Slf4j
@RequiredArgsConstructor
public class RiskService {

	private final List<String> triggerList;

	public Risk assessRisk(PatientProfileDTO profile) {
		log.debug("Age: {}", profile.age());

		long n = profile.notes().stream() // n triggers
				.map(note -> normalize(note, NFD).replaceAll("\\p{M}", "").toLowerCase())
				.flatMap(processedNote -> triggerList.stream().filter(processedNote::contains))
				.distinct()
				.count();
		log.debug("Triggers: {}", n);

		var risk = switch (profile.sex() + (profile.age() < 30 ? "30-" : "30+")) {
			case "M30-" -> (n <= 1) ? NONE : (n == 2) ? BORDERLINE : (n <= 4) ? IN_DANGER : EARLY_ONSET;
			case "F30-" -> (n <= 1) ? NONE : (n <= 3) ? BORDERLINE : (n <= 6) ? IN_DANGER : EARLY_ONSET;
			case "M30+" -> (n <= 1) ? NONE : (n <= 5) ? BORDERLINE : (n <= 7) ? IN_DANGER : EARLY_ONSET;
			case "F30+" -> (n <= 1) ? NONE : (n <= 6) ? BORDERLINE : (n == 7) ? IN_DANGER : EARLY_ONSET;
			default -> throw new IllegalArgumentException("Données patient invalides");
		};
		log.debug("Estimated risk: {}", risk);

		return risk;
	}

}
