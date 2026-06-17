package com.mateo.wallet.verification.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class VerifyPinRequest {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 6, max = 6)
    private String pin;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPin() { return pin; }
    public void setPin(String pin) { this.pin = pin; }
}