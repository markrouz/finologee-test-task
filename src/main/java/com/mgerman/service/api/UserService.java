package com.mgerman.service.api;

import com.mgerman.entity.User;

public interface UserService {

	User getCurrentUser();

	User updateUserInfo(String newUsername, String newPassword);

}
