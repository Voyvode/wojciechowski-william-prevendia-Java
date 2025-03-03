package com.medilabo.prevendia.risk.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(TriggersConfig.class)
public class TestConfig { }