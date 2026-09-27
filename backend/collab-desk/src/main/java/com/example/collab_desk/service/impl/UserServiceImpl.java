package com.example.collab_desk.service.impl;

import com.example.collab_desk.entity.User;
import com.example.collab_desk.exception.ResourceNotFoundException;
import com.example.collab_desk.exception.UnauthorizedException;
import com.example.collab_desk.repository.UserRepository;
import com.example.collab_desk.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email " + email));
    }

    @Override
    public Set<User> getUsersById(List<Long> ids) {
        List<User> users = userRepository.findAllById(ids);

        if (users.size() != ids.size()) {
            List<Long> foundIds = users
                    .stream()
                    .map(User::getId)
                    .toList();

            Set<Long> missingIds = new TreeSet<>(ids);
            missingIds.removeAll(foundIds);

            throw new ResourceNotFoundException("Users not found with ids: " + missingIds);
        }

        return new HashSet<>(users);
    }

    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            throw new UnauthorizedException("User is not authenticated");
        }

        String email = authentication.getName();
        return getUserByEmail(email);
    }
}
