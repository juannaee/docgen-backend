package com.example.docgen.controllers;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.docgen.config.TestConfig;
import com.example.docgen.config.security.CustomUserDetails;
import com.example.docgen.dto.LoginRequestDTO;
import com.example.docgen.entities.User;
import com.example.docgen.services.JwtService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

	@SuppressWarnings("unused")
	private final TestConfig testConfig;

	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;

	public AuthController(AuthenticationManager authenticationManager, JwtService jwtService, TestConfig testConfig) {
		this.authenticationManager = authenticationManager;
		this.jwtService = jwtService;
		this.testConfig = testConfig;
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody @Valid LoginRequestDTO request) {

		// Cria o "pacote de login" com o email e senha fornecidos
		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(request.getEmail(),
				request.getPassword());

		// Faz a autenticação de fato (verifica se existe, se a senha está certa, etc)
		Authentication authentication = authenticationManager.authenticate(authToken);

		// Depois de autenticar pega os dados do usuário
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		User user = userDetails.getUser();

		System.out.println(user.getPasswordResetRequired());

		// gerando o token jwt com base nesse usuário

		String jwt = jwtService.generateToken(userDetails);

		return ResponseEntity.ok(Map.of("token", jwt, "passwordResetRequired", user.getPasswordResetRequired()));

	}

}
