package com.medilabo.prevendia.frontend.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import com.medilabo.prevendia.frontend.dto.PatientProfileDTO;
import com.medilabo.prevendia.frontend.dto.RiskDTO;
import jakarta.validation.Valid;

@FeignClient(name = "riskClient", url = "http://localhost:8083")
//@FeignClient(name = "riskClient", url = "http://backend-risk:8083") TODO
public interface RiskClient {

	@PostMapping("/api/risk/")
	RiskDTO assessRisk(@Valid PatientProfileDTO profile);

}
