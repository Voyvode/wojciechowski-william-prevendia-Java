package com.medilabo.prevendia.frontend.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthenticationInterceptor implements HandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
		String requestURI = request.getRequestURI();
		if (requestURI.startsWith("/login") || requestURI.contains("/css/") ||
				requestURI.contains("/js/") || requestURI.contains("/images/")) {
			return true;
		}

		var session = request.getSession(false);
		if (session != null && session.getAttribute("token") != null) {
			return true;
		}

		response.sendRedirect("/login");
		return false;
	}

}