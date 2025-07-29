package com.example.docgen.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.docgen.dto.user.BatchUserInsertResponseDTO;
import com.example.docgen.dto.user.UserRequestDTO;
import com.example.docgen.dto.user.UserResponseDTO;
import com.example.docgen.dto.user.UserUpdateDTO;
import com.example.docgen.entities.User;
import com.example.docgen.mappers.UserMapper;
import com.example.docgen.services.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/users")
public class UserController {

	// region Dependencies
	private final UserService userService;
	private final UserMapper userMapper;

	public UserController(UserService userService, UserMapper userMapper) {
		this.userService = userService;
		this.userMapper = userMapper;
	}
	// endregion

	// region GET Methods

	@GetMapping
	public ResponseEntity<List<UserResponseDTO>> findAll() {
		List<User> users = userService.findAll();
		return ResponseEntity.ok(userMapper.toDtoList(users));
	}

	@GetMapping("/{id}")
	public ResponseEntity<UserResponseDTO> findById(@PathVariable Long id) {
		User user = userService.findById(id);
		return ResponseEntity.ok(userMapper.toDto(user));
	}

	@GetMapping("/me")
	public ResponseEntity<String> getCurrentUser(Authentication authentication,
			@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {

		String token = null;

		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			token = authorizationHeader.substring(7);
		}
		return ResponseEntity.ok("Usuário logado: " + authentication.getName() + "\n" + "Token: " + token);

	}

	// endregion

	// region POST Methods

	@PostMapping
	public ResponseEntity<UserResponseDTO> insertUser(@RequestBody @Valid UserRequestDTO dto) {
		User createdUser = userService.insertUser(dto);
		return ResponseEntity.ok(userMapper.toDto(createdUser));
	}

	@PostMapping("/batch")
	public ResponseEntity<BatchUserInsertResponseDTO> insertMultiplerUsers(
			@RequestBody List<com.example.docgen.dto.user.UserRequestDTO> userDTOs) {

		BatchUserInsertResponseDTO result = userService.insertUsers(userDTOs);
		return ResponseEntity.status(HttpStatus.MULTI_STATUS).body(result);
	}

	// endregion

	// region PUT Method

	@PutMapping("/{id}")
	public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long id, @RequestBody @Valid UserUpdateDTO dto) {
		User updateUser = userService.updateUser(id, dto);
		return ResponseEntity.ok(userMapper.toDto(updateUser));
	}

	@PutMapping("/admin/reset-password/{id}")
	@PreAuthorize("hasRole('ADMIN')") // garante que só admin pode chamar
	public ResponseEntity<?> resetPassword(@PathVariable Long id) {
		userService.resetPassword(id);
		return ResponseEntity
				.ok(Map.of("message", "Senha redefinida com sucesso e campo passwordResetRequired Ativado"));

	}

	// endregion

	// region DELETE Method

	@DeleteMapping("/{id}")
	public ResponseEntity<UserResponseDTO> deleteUser(@PathVariable Long id) {
		userService.deleteUser(id);
		return ResponseEntity.noContent().build();
	}

	// endregion
}
