package org.example.qnrassignment.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.qnrassignment.model.BlacklistedToken;
import org.example.qnrassignment.repository.BlacklistTokenRepo;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenBlacklistService {

    private final BlacklistTokenRepo blacklistTokenRepo;

    @Transactional
    public void blacklistToken(String token, String username, Date expiryDate) {
        if (blacklistTokenRepo.existsByToken(token)) {
            log.warn("Token is already blacklisted.");
            return;
        }

        BlacklistedToken blacklistedToken = BlacklistedToken.builder()
                .token(token)
                .username(username)
                .blacklistedAt(LocalDateTime.now())
                .build();

        blacklistTokenRepo.save(blacklistedToken);
        log.info("Token blacklisted successfully for user: {}", username);
    }

    @Transactional
    public boolean isTokenBlacklisted(String token) {
        return blacklistTokenRepo.existsByToken(token);
    }
}
