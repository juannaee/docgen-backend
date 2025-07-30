package com.example.docgen.config.security;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.docgen.entities.User;

public class CustomUserDetails implements UserDetails {

	private static final long serialVersionUID = 1L;
	private final User user;

	public CustomUserDetails(User user) {
		this.user = user;
	}

	public User getUser() {
		return user;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// Se quiser usar papéis, pode mapear user.getRole() aqui
		return Collections.emptyList(); // Ou use List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
	}

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getEmail(); // Ou getUsername(), depende de qual campo representa o login
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}
