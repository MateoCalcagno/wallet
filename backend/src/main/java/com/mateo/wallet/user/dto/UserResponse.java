package com.mateo.wallet.user.dto;

public record UserResponse(
        Long id,
        String email,
        String firstName,
        String lastName,
        String dni,
        String cbu,
        String alias
) {}