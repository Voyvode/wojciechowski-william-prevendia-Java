package com.medilabo.prevendia.authentication.dto;

import java.util.Set;

public record AuthenticationResponse(String token, String username, Set<String> roles) { }
