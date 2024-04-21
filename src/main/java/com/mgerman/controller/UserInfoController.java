package com.mgerman.controller;

import com.mgerman.dto.UpdateUserInfoRecord;
import com.mgerman.dto.User;
import com.mgerman.service.api.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user-info")
@RequiredArgsConstructor
public class UserInfoController {

	private final UserService userService;

	@PostMapping
	public User updateUserInfo(@RequestBody UpdateUserInfoRecord updateUserInfo) {
		var updatedUser = userService.updateUserInfo(updateUserInfo.newPassword(), updateUserInfo.newAddress());
		return new User(updatedUser.getId(), updatedUser.getUsername(), updatedUser.getAddress());
	}

	@GetMapping
	public User getUserInfo() {
		var currentUser = userService.getCurrentUser();
		return new User(currentUser.getId(), currentUser.getUsername(), currentUser.getAddress());
	}
}
