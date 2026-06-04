package com.mateo.wallet.auth.service;

import com.mateo.wallet.auth.dto.LoginRequest;
import com.mateo.wallet.auth.dto.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
}