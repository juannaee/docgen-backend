package com.example.docgen.config.security;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.docgen.repositories.UserRepository;
import com.example.docgen.services.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtService jwtService;
	private final UserRepository userRepository;

	public JwtAuthenticationFilter(JwtService jwtService, UserRepository userRepository) {
		this.jwtService = jwtService;
		this.userRepository = userRepository;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String path = request.getServletPath();
		if (path.equals("/auth/login")) {
			filterChain.doFilter(request, response);
			return;
		}

		final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

		// Verifica se o token existe e está bem formado
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			throw new BadCredentialsException("Token ausente ou mal formado");
		}

		final String jwt = authHeader.substring(7); // Remove "Bearer "
		final String username = jwtService.extractUsername(jwt);

		if (username == null) {
			throw new BadCredentialsException("Token inválido");
		}

		// Só autentica se ainda não houver um usuário autenticado no contexto
		if (SecurityContextHolder.getContext().getAuthentication() == null) {
			var user = userRepository.findByEmail(username).orElse(null);

			if (user == null || !jwtService.isTokenValid(jwt, user)) {
				throw new BadCredentialsException("Token inválido");
			}

			var authToken = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
			authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			SecurityContextHolder.getContext().setAuthentication(authToken);
		}

		filterChain.doFilter(request, response);
	}
}
