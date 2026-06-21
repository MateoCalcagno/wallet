package com.mateo.wallet.user.service;

import com.mateo.wallet.user.dto.UserRequest;
import com.mateo.wallet.user.dto.UserResponse;
import com.mateo.wallet.user.model.User;

public interface UserService {
    UserResponse createUser(UserRequest request);
    UserResponse getUserByEmail(String email);
    void resetPassword(String email, String newPassword);
    User findUserByEmail(String email);
    void checkAvailability(String email, String dni);
}