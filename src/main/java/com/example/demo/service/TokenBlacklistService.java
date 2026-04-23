package com.example.demo.service;

import com.example.demo.entity.BlacklistedToken;
import com.example.demo.repository.BlacklistedTokenRepository;
import org.springframework.stereotype.Service;

@Service
public class TokenBlacklistService {

    private final BlacklistedTokenRepository repository;

    public TokenBlacklistService(BlacklistedTokenRepository repository) {
        this.repository = repository;
    }

    public void blacklist(String token) {
        repository.save(BlacklistedToken.builder().token(token).build());
    }

    public boolean isBlacklisted(String token) {
        return repository.existsByToken(token);
    }
}
