package com.medilabo.prevendia.risk.dto;

import java.util.List;

public record PatientProfileDTO(int age, String sex, List<String> notes) { }
