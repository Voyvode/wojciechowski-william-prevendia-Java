package com.medilabo.prevendia.frontend.controller;

import lombok.RequiredArgsConstructor;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.medilabo.prevendia.frontend.client.AuthClient;
import com.medilabo.prevendia.frontend.dto.AuthenticationRequest;

@Controller
@RequiredArgsConstructor
public class LoginController {

	private final AuthClient authClient;

	@GetMapping("/login")
	public String login(Model model) {
		model.addAttribute("authRequest", new AuthenticationRequest("", ""));
		return "login";
	}

	@PostMapping("/login")
	public String processLogin(@ModelAttribute AuthenticationRequest authRequest,
							   HttpSession session) {
		var authResponse = authClient.authenticate(authRequest);

		session.setAttribute("token", authResponse.token());
		session.setAttribute("username", authResponse.username());
		session.setAttribute("shownName", authResponse.shownName());
		session.setAttribute("roles", authResponse.roles());

		return "redirect:/";
	}

	@PostMapping("/logout")
	public String performLogout(HttpSession session) {
		session.invalidate();
		return "redirect:/login";
	}

}
