package com.medilabo.prevendia.risk;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.medilabo.prevendia.risk.config.TestConfig;
import com.medilabo.prevendia.risk.dto.PatientProfileDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.medilabo.prevendia.risk.service.RiskService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static com.medilabo.prevendia.risk.model.Risk.NONE;
import static com.medilabo.prevendia.risk.model.Risk.BORDERLINE;
import static com.medilabo.prevendia.risk.model.Risk.IN_DANGER;
import static com.medilabo.prevendia.risk.model.Risk.EARLY_ONSET;

@SpringJUnitConfig(TestConfig.class)
class RiskServiceTest {

	@Autowired
	private List<String> triggerList;

	private RiskService riskService;

	@BeforeEach
	void setUp() {
		riskService = new RiskService(triggerList);
	}

	@Test
	void assessRisk_None() {
		var profile = new PatientProfileDTO(35, "M", List.of(
				"En pleine forme !"
		));
		var risk = riskService.assessRisk(profile);
		assertEquals(NONE, risk);
	}

	@Test
	void assessRisk_Borderline() {
		var profile = new PatientProfileDTO(30, "F", List.of(
				"Microalbuminurie anormale."
		));
		var risk = riskService.assessRisk(profile);
		assertEquals(BORDERLINE, risk);
	}

	@Test
	void assessRisk_EarlyOnset() {
		var profile = new PatientProfileDTO(50, "F", List.of(
				"Cholestérolémie, microalbuminurie.",
				"Réaction, anticorps.",
				"Fume.",
				"Vertiges.",
				"Hémoglobine A1C anormale."
		));
		var risk = riskService.assessRisk(profile);
		assertEquals(EARLY_ONSET, risk);
	}

	@Test
	void assessRisk_Normalization_Success() {
		var profile = new PatientProfileDTO(25, "M", List.of(
				"RÉaCtIoN aNtiCorps HémoGlobIne aNoRMAL."
		));
		var risk = riskService.assessRisk(profile);
		assertEquals(IN_DANGER, risk);
	}

	@Test
	void testAssessRisk_InvalidPatient_Exception() {
		var profile = new PatientProfileDTO(40, "X", List.of());
		assertThrows(IllegalArgumentException.class, () -> riskService.assessRisk(profile));
	}

}
