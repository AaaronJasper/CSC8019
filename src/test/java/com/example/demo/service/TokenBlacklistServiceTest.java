package com.example.demo.service;

import com.example.demo.entity.BlacklistedToken;
import com.example.demo.repository.BlacklistedTokenRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenBlacklistServiceTest {

    @Mock BlacklistedTokenRepository repository;

    @InjectMocks TokenBlacklistService tokenBlacklistService;

    @Test
    void blacklist_savesTokenToRepository() {
        tokenBlacklistService.blacklist("some.jwt.token");

        ArgumentCaptor<BlacklistedToken> captor = ArgumentCaptor.forClass(BlacklistedToken.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getToken()).isEqualTo("some.jwt.token");
    }

    @Test
    void isBlacklisted_tokenExists_returnsTrue() {
        when(repository.existsByToken("some.jwt.token")).thenReturn(true);

        assertThat(tokenBlacklistService.isBlacklisted("some.jwt.token")).isTrue();
    }

    @Test
    void isBlacklisted_tokenAbsent_returnsFalse() {
        when(repository.existsByToken("unknown.token")).thenReturn(false);

        assertThat(tokenBlacklistService.isBlacklisted("unknown.token")).isFalse();
    }

    @Test
    void blacklist_differentTokens_savesEachSeparately() {
        tokenBlacklistService.blacklist("token.one");
        tokenBlacklistService.blacklist("token.two");

        verify(repository, times(2)).save(any(BlacklistedToken.class));
    }
}
