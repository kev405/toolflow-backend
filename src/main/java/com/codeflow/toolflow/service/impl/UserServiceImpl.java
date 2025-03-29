package com.codeflow.toolflow.service.impl;

import lombok.RequiredArgsConstructor;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import com.codeflow.toolflow.dto.SaveUser;
import com.codeflow.toolflow.persistence.entity.User;
import com.codeflow.toolflow.persistence.entity.UserRole;
import com.codeflow.toolflow.persistence.repository.UserRepository;
import com.codeflow.toolflow.persistence.repository.UserRoleRepository;
import com.codeflow.toolflow.service.UserService;
import com.codeflow.toolflow.util.enums.Role;
import com.codeflow.toolflow.util.exception.InvalidPasswordException;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserRoleRepository userRoleRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public User registrOneCustomer(SaveUser newUser) {
        validatePassword(newUser);

        User user = new User();
        user.setPassword(passwordEncoder.encode(newUser.getPassword()));
        user.setUsername(newUser.getUsername());
        user.setName(newUser.getName());
        user.setLastName(newUser.getLastName());
        user.setEmail(newUser.getEmail());
        user.setPhone(newUser.getPhone());
        user.setCreatedBy(newUser.getCreatedBy());
        user.setCreatedAt(newUser.getCreatedAt());
        user.setUpdatedAt(newUser.getUpdatedAt());
        user.setUpdatedBy(newUser.getUpdatedBy());
        user.setStatus(true);

        User userSaved = userRepository.save(user);

        UserRole userRole = UserRole.builder()
                .role(Role.STUDENT)
                .toolflowUser(userSaved)
                .createdAt(userSaved.getCreatedAt())
                .createdBy(userSaved.getCreatedBy())
                .build();

        userRoleRepository.save(userRole);

        return userSaved;
    }

    @Override
    public Optional<User> findOneByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    private void validatePassword(SaveUser dto) {

        if(!StringUtils.hasText(dto.getPassword()) || !StringUtils.hasText(dto.getRepeatedPassword())){
            throw new InvalidPasswordException("Passwords don't match");
        }

        if(!dto.getPassword().equals(dto.getRepeatedPassword())){
            throw new InvalidPasswordException("Passwords don't match");
        }

    }

}
