package com.mateo.wallet.user.service;

import com.mateo.wallet.user.dto.UserRequest;
import com.mateo.wallet.user.dto.UserResponse;
import com.mateo.wallet.user.model.User;
import com.mateo.wallet.user.repository.UserRepository;
import com.mateo.wallet.wallet.model.Wallet;
import com.mateo.wallet.wallet.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;

    public UserServiceImpl(UserRepository userRepository,
                           WalletRepository walletRepository) {
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
    }

    @Override
    @Transactional
    public UserResponse createUser(UserRequest request) {

        // 1. crear user
        User user = new User(request.getEmail(), request.getPassword());
        User savedUser = userRepository.save(user);

        // 2. crear wallet automáticamente
        Wallet wallet = new Wallet(savedUser);
        walletRepository.save(wallet);

        return new UserResponse(savedUser.getId(), savedUser.getEmail());
    }

    @Override
    public UserResponse getUserById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return new UserResponse(user.getId(), user.getEmail());
    }
}