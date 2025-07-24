package com.example.docgen.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.docgen.entities.User;
import com.example.docgen.entities.enums.UserRole;
import com.example.docgen.repositories.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private UserService userService;

	@Test
	void whenFindByIdExistingUser_thenReturnUser() {
		Long id = 1l;
		User user = new User(id, "testejunit", "testejunint@", "testejunit", LocalDate.of(1990, 1, 1), "phone", "cpf",
				UserRole.USER);

		// quando userRepository.findById(id) for chamado, retorna optional com user
		when(userRepository.findById(id)).thenReturn(Optional.of(user));

		// chamad o metodo do service
		User found = userService.findById(id);

		// validações
		assertNotNull(found);
		assertEquals(id, found.getId());
		assertEquals("testejunit", found.getName());

		// verifica se o método findById foi chamado exatamente uma vez
		verify(userRepository).findById(id);

	}

	@Test
	void whenFindByIdNotExistingUser_thenThrowException() {
		Long id = 2L;

		// quando userRepository.findById(id) for chamado, retorna Optional vazio
		when(userRepository.findById(id)).thenReturn(Optional.empty());

		// espera que o método findById lance RuntimeException
		RuntimeException exception = assertThrows(RuntimeException.class, () -> {
			userService.findById(id);
		});

		assertEquals("Usuário com ID " + id + " não encontrado.", exception.getMessage());

		verify(userRepository).findById(id);
	}

}
