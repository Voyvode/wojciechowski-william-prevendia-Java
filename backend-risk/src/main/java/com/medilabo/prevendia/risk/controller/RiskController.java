package com.medilabo.prevendia.risk.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.medilabo.prevendia.risk.dto.PatientProfileDTO;
import com.medilabo.prevendia.risk.model.Risk;
import com.medilabo.prevendia.risk.service.RiskService;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/risk")
@RequiredArgsConstructor
@Slf4j
public class RiskController {

	private final RiskService riskService;

	@PostMapping
	@ResponseStatus(OK)
	public Risk assessRisk(@RequestBody PatientProfileDTO profile) {
		log.info("Request to assess risk");
		return riskService.assessRisk(profile);
	}

}
