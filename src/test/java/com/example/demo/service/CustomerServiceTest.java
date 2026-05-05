package com.example.demo.service;

import com.example.demo.entity.Customer;
import com.example.demo.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock CustomerRepository customerRepository;
    @Mock PasswordEncoder passwordEncoder;

    @InjectMocks CustomerService customerService;

    // ── helpers ───────────────────────────────────────────────────────────────

    private Customer sampleCustomer() {
        Customer customer = new Customer();
        customer.setFirstName("Alice");
        customer.setLastName("Smith");
        customer.setEmail("alice@test.com");
        customer.setPassword("plaintext");
        return customer;
    }

    // ── register ──────────────────────────────────────────────────────────────

    @Test
    void register_encodesPassword() {
        Customer customer = sampleCustomer();
        when(passwordEncoder.encode("plaintext")).thenReturn("hashed");
        when(customerRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        customerService.register(customer);

        assertThat(customer.getPassword()).isEqualTo("hashed");
    }

    @Test
    void register_savesCustomerToRepository() {
        Customer customer = sampleCustomer();
        when(passwordEncoder.encode(any())).thenReturn("hashed");
        when(customerRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Customer result = customerService.register(customer);

        verify(customerRepository).save(customer);
        assertThat(result).isEqualTo(customer);
    }

    @Test
    void register_doesNotStorePlaintextPassword() {
        Customer customer = sampleCustomer();
        when(passwordEncoder.encode("plaintext")).thenReturn("bcrypt$2a$...");
        when(customerRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        customerService.register(customer);

        assertThat(customer.getPassword()).isNotEqualTo("plaintext");
    }

    @Test
    void register_callsPasswordEncoderOnce() {
        Customer customer = sampleCustomer();
        when(passwordEncoder.encode(any())).thenReturn("hashed");
        when(customerRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        customerService.register(customer);

        verify(passwordEncoder, times(1)).encode("plaintext");
    }
}
