package com.medilabo.prevendia.frontend.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import com.medilabo.prevendia.frontend.dto.PatientProfileDTO;
import com.medilabo.prevendia.frontend.dto.RiskDTO;
import jakarta.validation.Valid;

@FeignClient(name = "riskClient", url = "${clients.risk.url}")
public interface RiskClient {

	@PostMapping("/api/risk/")
	RiskDTO assessRisk(@Valid PatientProfileDTO profile);

}
