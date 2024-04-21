package com.mgerman.service;

import com.mgerman.entity.User;
import com.mgerman.repository.UserRepository;
import com.mgerman.service.api.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	public User updateUserInfo(String newPassword, String newAddress) {
		if (newPassword == null && newAddress == null) {
			throw new IllegalArgumentException("Neither new password or new address aren't provided. Nothing to update");
		}
		User currentUser = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
		if (newAddress != null) {
			currentUser.setAddress(newAddress);
		}
		if (newPassword != null) {
			if (passwordEncoder.matches(newPassword, currentUser.getPassword())) {
				throw new IllegalArgumentException("The new password matches with default one");
			}
			currentUser.setPassword(passwordEncoder.encode(newPassword));
			logout();
		}
		return userRepository.save(currentUser);
	}

	@Override
	public User getCurrentUser() {
		return userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
	}

	private void logout() {
		HttpServletRequest request =
				((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
						.getRequest();
		new SecurityContextLogoutHandler().logout(request, null, null);
	}

}
