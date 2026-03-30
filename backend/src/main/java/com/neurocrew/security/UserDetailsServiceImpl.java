package com.neurocrew.security;

import com.neurocrew.model.User;
import com.neurocrew.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor                                              // ← handles injection via constructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;                      // ← removed "= null" bug fix ✅

    @Override
    public UserDetails loadUserByUsername(String userId)
            throws UsernameNotFoundException {

        Long id;
        try {
            id = Long.parseLong(userId);                              // ← parse String to Long
        } catch (NumberFormatException e) {
            throw new UsernameNotFoundException("Invalid user ID: " + userId);
        }

        User user = userRepository.findById(id)                       // ← Long id now
                .orElseThrow(() ->
                    new UsernameNotFoundException("User not found: " + userId));

        return new org.springframework.security.core.userdetails.User(
                String.valueOf(user.getId()),                         // ← convert Long back to String
                user.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }
}