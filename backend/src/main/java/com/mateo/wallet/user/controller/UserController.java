package com.mateo.wallet.user.controller;

import com.mateo.wallet.user.dto.CheckAvailabilityRequest;
import com.mateo.wallet.user.dto.ResetPasswordRequest;
import com.mateo.wallet.user.dto.SendVerificationRequest;
import com.mateo.wallet.user.dto.UserRequest;
import com.mateo.wallet.user.dto.UserResponse;
import com.mateo.wallet.user.service.UserService;
import com.mateo.wallet.verification.dto.VerifyPinRequest;
import com.mateo.wallet.verification.service.EmailVerificationService;

import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final EmailVerificationService emailVerificationService;

    public UserController(UserService userService,
                          EmailVerificationService emailVerificationService) {
        this.userService = userService;
        this.emailVerificationService = emailVerificationService;
    }

    @SecurityRequirements
    @PostMapping("/send-verification")
    public ResponseEntity<Void> sendVerification(@RequestBody @Valid SendVerificationRequest request) {
        emailVerificationService.sendPin(request.getEmail());
        return ResponseEntity.ok().build();
    }

    @SecurityRequirements
    @PostMapping("/verify-pin")
    public ResponseEntity<Void> verifyPin(@RequestBody @Valid VerifyPinRequest request) {
        emailVerificationService.verifyPin(request.getEmail(), request.getPin());
        return ResponseEntity.ok().build();
    }

    @SecurityRequirements
    @PostMapping("/forgot-password/send-verification")
    public ResponseEntity<Void> sendForgotPasswordVerification(@RequestBody @Valid SendVerificationRequest request) {
        userService.findUserByEmail(request.getEmail()); 
        emailVerificationService.sendPin(request.getEmail());
        return ResponseEntity.ok().build();
    }

    @SecurityRequirements
    @PostMapping("/forgot-password/reset")
    public ResponseEntity<Void> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        emailVerificationService.assertEmailVerified(request.getEmail());
        userService.resetPassword(request.getEmail(), request.getNewPassword());
        return ResponseEntity.ok().build();
    }

    @SecurityRequirements
    @PostMapping("/check-availability")
    public ResponseEntity<Void> checkAvailability(@RequestBody CheckAvailabilityRequest request) {
        userService.checkAvailability(request.email(), request.dni());
        return ResponseEntity.ok().build();
    }

    @SecurityRequirements
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody @Valid UserRequest request) {
        emailVerificationService.assertEmailVerified(request.getEmail());
        return ResponseEntity.ok(userService.createUser(request));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMe(@AuthenticationPrincipal String email) {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }
}