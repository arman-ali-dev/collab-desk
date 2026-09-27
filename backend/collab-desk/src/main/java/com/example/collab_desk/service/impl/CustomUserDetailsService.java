package com.example.collab_desk.service.impl;

import com.example.collab_desk.entity.CustomUserDetails;
import com.example.collab_desk.entity.User;
import com.example.collab_desk.exception.ResourceNotFoundException;
import com.example.collab_desk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User Not found"
                ));

        return new CustomUserDetails(user);
    }
}
