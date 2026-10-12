package com.mateo.wallet.transaction.service;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

final class TransferRequestHash {

    private TransferRequestHash() {}

    static String of(String identifier, BigDecimal amount) {
        // stripTrailingZeros: así 100 y 100.00 dan la misma huella
        String canonical = identifier + "|" + amount.stripTrailingZeros().toPlainString();
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(canonical.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}