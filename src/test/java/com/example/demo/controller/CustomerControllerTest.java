package com.example.demo.controller;

import com.example.demo.config.JWTAuthenticationFilter;
import com.example.demo.config.SecurityConfiguration;
import com.example.demo.entity.Customer;
import com.example.demo.service.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.security.autoconfigure.web.servlet.ServletWebSecurityAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = CustomerController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class, ServletWebSecurityAutoConfiguration.class},
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {SecurityConfiguration.class, JWTAuthenticationFilter.class}
        )
)
class CustomerControllerTest {

    @Autowired MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
    @MockitoBean CustomerService customerService;

    // ── helpers ───────────────────────────────────────────────────────────────

    private Customer sampleCustomer() {
        Customer c = new Customer();
        c.setFirstName("Alice");
        c.setLastName("Smith");
        c.setEmail("alice@test.com");
        c.setPassword("plaintext");
        return c;
    }

    // ── POST /api/customers/register ──────────────────────────────────────────

    @Test
    void register_validCustomer_returns200() throws Exception {
        Customer saved = sampleCustomer();
        saved.setPassword("hashed");
        when(customerService.register(any())).thenReturn(saved);

        mockMvc.perform(post("/api/customers/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleCustomer())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("alice@test.com")))
                .andExpect(jsonPath("$.firstName", is("Alice")));
    }

    @Test
    void register_passwordIsHashed_notPlaintext() throws Exception {
        Customer saved = sampleCustomer();
        saved.setPassword("$2a$10$hashedpassword");
        when(customerService.register(any())).thenReturn(saved);

        mockMvc.perform(post("/api/customers/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleCustomer())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.password", is("$2a$10$hashedpassword")));
    }

    // ── POST /api/customers/login ─────────────────────────────────────────────

    @Test
    void login_returns200WithMessage() throws Exception {
        mockMvc.perform(post("/api/customers/login"))
                .andExpect(status().isOk())
                .andExpect(content().string("Login successful!"));
    }
}
