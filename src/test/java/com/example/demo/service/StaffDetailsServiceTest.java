package com.example.demo.service;

import com.example.demo.entity.Staff;
import com.example.demo.repository.StaffRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StaffDetailsServiceTest {

    @Mock StaffRepository staffRepository;

    @InjectMocks StaffDetailsService staffDetailsService;

    // ── helpers ───────────────────────────────────────────────────────────────

    private Staff sampleStaff(String username, String password, String role) {
        Staff staff = new Staff();
        staff.setUsername(username);
        staff.setPassword(password);
        staff.setRole(role);
        return staff;
    }

    // ── loadUserByUsername ────────────────────────────────────────────────────

    @Test
    void loadUserByUsername_found_returnsUserDetails() {
        Staff staff = sampleStaff("johndoe", "secret", "MANAGER");
        when(staffRepository.findByUsername("johndoe")).thenReturn(Optional.of(staff));

        UserDetails result = staffDetailsService.loadUserByUsername("johndoe");

        assertThat(result.getUsername()).isEqualTo("johndoe");
        assertThat(result.getPassword()).isEqualTo("secret");
    }

    @Test
    void loadUserByUsername_grantsCorrectRole() {
        Staff staff = sampleStaff("johndoe", "secret", "MANAGER");
        when(staffRepository.findByUsername("johndoe")).thenReturn(Optional.of(staff));

        UserDetails result = staffDetailsService.loadUserByUsername("johndoe");

        assertThat(result.getAuthorities())
                .anyMatch(a -> a.getAuthority().equals("ROLE_MANAGER"));
    }

    @Test
    void loadUserByUsername_notFound_throwsUsernameNotFoundException() {
        when(staffRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> staffDetailsService.loadUserByUsername("ghost"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("ghost");
    }

    @Test
    void loadUserByUsername_differentRoles_reflectedInAuthorities() {
        Staff staff = sampleStaff("barista", "pass", "SLAVE");
        when(staffRepository.findByUsername("barista")).thenReturn(Optional.of(staff));

        UserDetails result = staffDetailsService.loadUserByUsername("barista");

        assertThat(result.getAuthorities())
                .anyMatch(a -> a.getAuthority().equals("ROLE_SLAVE"));
    }
}
