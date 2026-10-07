package com.mateo.wallet.wallet.resolver;

import com.mateo.wallet.common.exception.ResourceNotFoundException;
import com.mateo.wallet.wallet.model.Wallet;
import com.mateo.wallet.wallet.repository.WalletRepository;
import org.springframework.stereotype.Component;

@Component
public class WalletResolver {

    private final WalletRepository walletRepository;

    public WalletResolver(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    public Wallet resolveByEmail(String email) {
        return walletRepository.findByUserEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));
    }

    public Wallet resolveByUserId(Long userId) {
        return walletRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));
    }

    public Wallet resolveByIdentifier(String identifier){
        return walletRepository.findByCbu(identifier)
                .or(() -> walletRepository.findByAlias(identifier))
                .orElseThrow(() -> new ResourceNotFoundException("Destination wallet not found"));
    }

    public Long resolveIdByEmail(String email) {
        return walletRepository.findIdByUserEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));
    }

    public Long resolveIdByIdentifier(String identifier) {
        return walletRepository.findIdByCbu(identifier)
                .or(() -> walletRepository.findIdByAlias(identifier))
                .orElseThrow(() -> new ResourceNotFoundException("Destination wallet not found"));
    }

    public Wallet lockById(Long id) {
        return walletRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));
    }

    public Wallet resolveByEmailForUpdate(String email) {
        return lockById(resolveIdByEmail(email));
    }
}