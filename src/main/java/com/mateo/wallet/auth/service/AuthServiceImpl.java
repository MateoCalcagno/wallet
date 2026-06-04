package com.mateo.wallet.auth.service;

import com.mateo.wallet.auth.dto.LoginRequest;
import com.mateo.wallet.auth.dto.LoginResponse;
import com.mateo.wallet.auth.util.JwtUtil;
import com.mateo.wallet.common.exception.ResourceNotFoundException;
import com.mateo.wallet.user.model.User;
import com.mateo.wallet.user.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public AuthServiceImpl(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!user.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtUtil.generateToken(user.getEmail());
        return new LoginResponse(token);
    }
}