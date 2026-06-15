package com.mateo.wallet.user.service;

import com.mateo.wallet.common.exception.EmailAlreadyExistsException;
import com.mateo.wallet.common.exception.ResourceNotFoundException;
import com.mateo.wallet.user.dto.UserRequest;
import com.mateo.wallet.user.dto.UserResponse;
import com.mateo.wallet.user.mapper.UserMapper;
import com.mateo.wallet.user.model.User;
import com.mateo.wallet.user.repository.UserRepository;
import com.mateo.wallet.wallet.model.Wallet;
import com.mateo.wallet.wallet.service.WalletService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final WalletService walletService;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository,
                           WalletService walletService,
                           PasswordEncoder passwordEncoder,
                           UserMapper userMapper) {
        this.userRepository = userRepository;
        this.walletService = walletService;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional
    public UserResponse createUser(UserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException();
        }
        if (userRepository.existsByDni(request.getDni())) {
            throw new IllegalArgumentException("DNI already in use");
        }

        User user = userMapper.toEntity(request, passwordEncoder.encode(request.getPassword()));
        User savedUser = userRepository.save(user);

        Wallet wallet = walletService.createForUser(savedUser);

        return userMapper.toResponse(savedUser, wallet);
    }

    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Wallet wallet = walletService.getByUserId(user.getId());
        return userMapper.toResponse(user, wallet);
    }

    @Override
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Wallet wallet = walletService.getByUserId(user.getId());
        return userMapper.toResponse(user, wallet);
    }
}