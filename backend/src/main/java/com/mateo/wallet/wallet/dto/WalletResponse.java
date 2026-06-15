package com.mateo.wallet.wallet.dto;

import java.math.BigDecimal;

public record WalletResponse(BigDecimal balance, String cbu, String alias) {}