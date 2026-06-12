package com.mateo.wallet.user.mapper;

import com.mateo.wallet.user.dto.UserRequest;
import com.mateo.wallet.user.dto.UserResponse;
import com.mateo.wallet.user.model.User;
import com.mateo.wallet.wallet.model.Wallet;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(UserRequest request, String encodedPassword) {
        return new User(
                request.getEmail(),
                encodedPassword,
                request.getFirstName(),
                request.getLastName(),
                request.getDni()
        );
    }

    public UserResponse toResponse(User user, Wallet wallet) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getDni(),
                wallet.getCbu(),
                wallet.getAlias()
        );
    }
}