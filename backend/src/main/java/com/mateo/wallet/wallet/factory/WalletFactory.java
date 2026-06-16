package com.mateo.wallet.wallet.factory;

import com.mateo.wallet.user.model.User;
import com.mateo.wallet.wallet.generator.AliasGenerator;
import com.mateo.wallet.wallet.generator.CbuGenerator;
import com.mateo.wallet.wallet.model.Wallet;
import org.springframework.stereotype.Component;

@Component
public class WalletFactory {

    private final CbuGenerator cbuGenerator;
    private final AliasGenerator aliasGenerator;

    public WalletFactory(CbuGenerator cbuGenerator, AliasGenerator aliasGenerator) {
        this.cbuGenerator = cbuGenerator;
        this.aliasGenerator = aliasGenerator;
    }

    public Wallet createForUser(User user) {
        String cbu = cbuGenerator.generate();
        String alias = aliasGenerator.generate();
        return new Wallet(user, cbu, alias);
    }
}