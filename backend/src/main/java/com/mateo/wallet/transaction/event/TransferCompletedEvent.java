package com.mateo.wallet.transaction.event;

import com.mateo.wallet.wallet.model.Wallet;
import java.math.BigDecimal;

public record TransferCompletedEvent(
        Wallet fromWallet,
        Wallet toWallet,
        BigDecimal amount
) {}