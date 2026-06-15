package com.mateo.wallet.wallet.dto;

import java.math.BigDecimal;

public class WalletResponse {

    private BigDecimal balance;
    private String cbu;
    private String alias;

    public WalletResponse(BigDecimal balance, String cbu, String alias) {
        this.balance = balance;
        this.cbu = cbu;
        this.alias = alias;
    }

    public BigDecimal getBalance() { return balance; }
    public String getCbu() { return cbu; }
    public String getAlias() { return alias; }
}