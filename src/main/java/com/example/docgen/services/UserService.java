package com.example.docgen.services;

import java.util.ArrayList;
import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.docgen.config.security.CustomUserDetails;
import com.example.docgen.dto.user.BatchUserInsertResponseDTO;
import com.example.docgen.dto.user.FailedUserDTO;
import com.example.docgen.dto.user.PasswordResetRequestDTO;
import com.example.docgen.dto.user.UserRequestDTO;
import com.example.docgen.dto.user.UserResponseDTO;
import com.example.docgen.dto.user.UserUpdateDTO;
import com.example.docgen.entities.User;
import com.example.docgen.exceptions.CpfValidationException;
import com.example.docgen.exceptions.ResourceNotFoundException;
import com.example.docgen.mappers.UserMapper;
import com.example.docgen.repositories.UserRepository;
import br.com.caelum.stella.validation.CPFValidator;
import br.com.caelum.stella.validation.InvalidStateException;

@Service
public class UserService implements UserDetailsService {

	// region Dependencies
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final UserMapper userMapper;
	private final JwtService jwtService;

	public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper,
			JwtService jwtService) {
		this.passwordEncoder = passwordEncoder;
		this.userRepository = userRepository;
		this.userMapper = userMapper;
		this.jwtService = jwtService;
	}
	// endregion

	// region CRUD Operations

	public List<User> findAll() {
		return userRepository.findAll();
	}

	public User findById(Long id) {
		return userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Usuário com ID " + id + " não encontrado."));
	}

	public User insertUser(UserRequestDTO dto) {
		validateUser(dto);
		User user = userMapper.toEntity(dto);
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		return userRepository.save(user);
	}

	public User updateUser(Long id, UserUpdateDTO dto) {
		User user = userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Usuário com ID: " + id + " não encontrado."));

		if (dto.getName() != null) {
			user.setName(dto.getName());
		}
		if (dto.getPhone() != null) {
			user.setPhone(dto.getPhone());
		}
		return userRepository.save(user);
	}

	public void deleteUser(Long id) {
		if (!userRepository.existsById(id)) {
			throw new ResourceNotFoundException("Usuário com ID: " + id + "não encontrado");
		}
		userRepository.deleteById(id);
	}

	// Logica pra resetar a senha (fazendo com que o username vire a senha email ==
	// senha
	public void resetPassword(Long userId) {
		User user = findById(userId);

		String newPassword = user.getEmail();
		user.setPassword(passwordEncoder.encode(newPassword));
		user.setPasswordResetRequired(true);

		userRepository.save(user);
	}

	// serviço de nova senha após resete da mesma
	public void newPassword(String token, PasswordResetRequestDTO request) {
		if (!request.getNewPassword().equals(request.getConfirmPassword())) {
			throw new IllegalArgumentException("As senhas não coincidem");
		}

		String userEmail = jwtService.extractUsername(token);
		User user = userRepository.findByEmail(userEmail)
				.orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

		user.setPassword(passwordEncoder.encode(request.getNewPassword()));
		user.setPasswordResetRequired(false);
		userRepository.save(user);
	}

	// endregion

	// region Batch Insert

	public BatchUserInsertResponseDTO insertUsers(List<UserRequestDTO> userDTOs) {
		List<UserResponseDTO> successUsers = new ArrayList<>();
		List<FailedUserDTO> failedUsers = new ArrayList<>();

		for (UserRequestDTO dto : userDTOs) {
			try {
				validateUser(dto);
				User user = userMapper.toEntity(dto);
				user.setPassword(passwordEncoder.encode(user.getPassword()));
				User saved = userRepository.save(user);
				successUsers.add(userMapper.toDto(saved));
			} catch (Exception e) {
				failedUsers.add(new FailedUserDTO(dto.getEmail(), e.getMessage()));
			}
		}

		return new BatchUserInsertResponseDTO(successUsers, failedUsers);
	}

	// endregion

	// region Validation

	public void validateUser(UserRequestDTO userDTO) {
		userRepository.findByEmail(userDTO.getEmail()).ifPresent(u -> {
			throw new DataIntegrityViolationException("Email já cadastrado: " + u.getEmail());
		});

		String cleanCpf = userDTO.getCpf().replaceAll("[^\\d]", "");
		CPFValidator cpfValidator = new CPFValidator();

		try {
			cpfValidator.assertValid(cleanCpf);
		} catch (InvalidStateException e) {
			throw new CpfValidationException("CPF inválido: " + userDTO.getCpf());
		}
	}

	// endregion

	// region Spring Security Integration

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		System.out.println("Buscando usuário por email: " + email);
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

		return new CustomUserDetails(user);
	}

	// endregion
}
