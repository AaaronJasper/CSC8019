package com.example.demo.service;

import com.example.demo.entity.Staff;
import com.example.demo.repository.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StaffDetailsService implements UserDetailsService {

    @Autowired
    private StaffRepository staffRepository;

    // Load a staff member by username for Spring Security authentication
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Staff staff = staffRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Staff not found: " + username));

        return new User(
                staff.getUsername(),
                staff.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + staff.getRole().toUpperCase()))
        );
    }
}
