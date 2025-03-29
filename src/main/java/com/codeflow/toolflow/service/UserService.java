package com.codeflow.toolflow.service;

import java.util.Optional;
import com.codeflow.toolflow.dto.SaveUser;
import com.codeflow.toolflow.dto.auth.UserLogin;
import com.codeflow.toolflow.persistence.entity.User;

public interface UserService {
    User registrOneCustomer(SaveUser newUser);

    Optional<User> findOneByUsername(String username);
}
