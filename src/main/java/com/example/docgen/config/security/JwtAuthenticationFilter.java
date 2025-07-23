package com.example.docgen.config.security;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
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

		// Pegando valor do cabeçalho (teste pra ver se vai da certo)
		final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
		final String jwt;
		final String username;

		// Se não tiver Bearer no inicio passa direto ( Se tiver vazio também )
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}

		// Extraindo o token
		jwt = authHeader.substring(7); // Remover o Barear
		username = jwtService.extractUsername(jwt); // pega o email ( username )

		// Se tiver usuário e ainda não está autenticado
		if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			var user = userRepository.findByEmail(username).orElse(null);

			// puxando do service metodo de validação
			if (user != null && jwtService.isTokenValid(jwt, user)) {
				var authToken = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

				authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

				// Autentica no contexto do Spring
				SecurityContextHolder.getContext().setAuthentication(authToken);
			}

		}

		filterChain.doFilter(request, response);

	}

}
