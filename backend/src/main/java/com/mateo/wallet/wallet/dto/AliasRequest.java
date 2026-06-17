package com.mateo.wallet.wallet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class AliasRequest {

    @NotBlank(message = "Alias is required")
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "Alias no válido")
    private String alias;

    public AliasRequest() {}

    public void setAlias(String alias){ this.alias = alias; } // para tests
    public String getAlias() { return alias; }
}