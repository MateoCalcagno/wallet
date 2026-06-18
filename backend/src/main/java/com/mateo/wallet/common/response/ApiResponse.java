package com.mateo.wallet.common.response;

import java.util.List;

public record ApiResponse(int status, String message, List<String> errors) {
    public ApiResponse(int status, String message) {
        this(status, message, null);
    }
}