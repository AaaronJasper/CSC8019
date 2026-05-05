package com.example.demo.controller;

import com.example.demo.auth.AuthenticationController;
import com.example.demo.auth.AuthenticationResponse;
import com.example.demo.auth.AuthenticationService;
import com.example.demo.auth.RegisterRequest;
import com.example.demo.config.JWTAuthenticationFilter;
import com.example.demo.config.SecurityConfiguration;
import com.example.demo.entity.Role;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.security.autoconfigure.web.servlet.ServletWebSecurityAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = AuthenticationController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class, ServletWebSecurityAutoConfiguration.class},
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {SecurityConfiguration.class, JWTAuthenticationFilter.class}
        )
)
class AuthenticationControllerTest {

    @Autowired MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
    @MockitoBean AuthenticationService authenticationService;

    // ── helpers ───────────────────────────────────────────────────────────────

    private RegisterRequest registerRequest() {
        return RegisterRequest.builder()
                .firstname("Alice")
                .lastname("Smith")
                .email("alice@test.com")
                .password("password123")
                .role(Role.CUSTOMER)
                .build();
    }

    private AuthenticationResponse authResponse() {
        return AuthenticationResponse.builder()
                .token("eyJhbGciOiJIUzI1NiJ9.mock.token")
                .role("CUSTOMER")
                .name("Alice Smith")
                .build();
    }

    // ── POST /api/v1/auth/register ────────────────────────────────────────────

    @Test
    void register_validRequest_returns200WithToken() throws Exception {
        when(authenticationService.register(any())).thenReturn(authResponse());

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.role", is("CUSTOMER")))
                .andExpect(jsonPath("$.name", is("Alice Smith")));
    }

    @Test
    void register_callsServiceOnce() throws Exception {
        when(authenticationService.register(any())).thenReturn(authResponse());

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest())));

        verify(authenticationService, times(1)).register(any());
    }

    // ── POST /api/v1/auth/authenticate ────────────────────────────────────────

    @Test
    void authenticate_validCredentials_returns200WithToken() throws Exception {
        when(authenticationService.authenticate(any())).thenReturn(authResponse());

        mockMvc.perform(post("/api/v1/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.role", is("CUSTOMER")));
    }

    @Test
    void authenticate_badCredentials_throwsRuntimeException() throws Exception {
        when(authenticationService.authenticate(any()))
                .thenThrow(new RuntimeException("Bad credentials"));

        assertThatThrownBy(() ->
                mockMvc.perform(post("/api/v1/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest()))))
                .hasMessageContaining("Bad credentials");
    }

    // ── DELETE /api/v1/auth/logout ────────────────────────────────────────────

    @Test
    void logout_withBearerToken_returns204() throws Exception {
        doNothing().when(authenticationService).logout(any());

        mockMvc.perform(delete("/api/v1/auth/logout")
                        .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.mock.token"))
                .andExpect(status().isNoContent());
    }

    @Test
    void logout_callsServiceWithHeader() throws Exception {
        doNothing().when(authenticationService).logout(any());

        mockMvc.perform(delete("/api/v1/auth/logout")
                        .header("Authorization", "Bearer my.jwt.token"));

        verify(authenticationService).logout("Bearer my.jwt.token");
    }

    @Test
    void logout_withoutAuthHeader_returns204() throws Exception {
        doNothing().when(authenticationService).logout(any());

        mockMvc.perform(delete("/api/v1/auth/logout"))
                .andExpect(status().is4xxClientError());
    }
}
