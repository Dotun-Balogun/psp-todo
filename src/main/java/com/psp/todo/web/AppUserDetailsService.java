package com.psp.todo.web;

import com.psp.todo.model.User;
import com.psp.todo.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

/** Lets people sign in with either their username or their email. */
@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {
    private final UserRepository users;

    @Override
    public UserDetails loadUserByUsername(String input) throws UsernameNotFoundException {
        String key = input.trim().toLowerCase();
        User u = users.findByUsername(key).or(() -> users.findByEmail(key))
                .orElseThrow(() -> new UsernameNotFoundException("Unknown user"));
        return org.springframework.security.core.userdetails.User.withUsername(u.getUsername())
                .password(u.getPasswordHash()).roles(u.getRole()).build();
    }
}
