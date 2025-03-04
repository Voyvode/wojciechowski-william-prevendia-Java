package com.medilabo.prevendia.frontend.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RiskDTO {

	NONE("Aucun"),
	BORDERLINE("Limité"),
	IN_DANGER("Danger"),
	EARLY_ONSET("Maladie déclarée");

	private final String assessment;

}
