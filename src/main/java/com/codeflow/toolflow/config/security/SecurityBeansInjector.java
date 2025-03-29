package com.codeflow.toolflow.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.codeflow.toolflow.dto.auth.UserLogin;
import com.codeflow.toolflow.persistence.repository.UserRepository;
import com.codeflow.toolflow.persistence.repository.UserRoleRepository;
import com.codeflow.toolflow.util.exception.ObjectNotFoundException;

@Configuration
@RequiredArgsConstructor
public class SecurityBeansInjector {

    private final UserRepository userRepository;

    private final UserRoleRepository userRoleRepository;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authenticationStrategy = new DaoAuthenticationProvider();
        authenticationStrategy.setPasswordEncoder( passwordEncoder() );
        authenticationStrategy.setUserDetailsService( userDetailsService() );

        return authenticationStrategy;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(){
        return (username) -> {
            return userRepository.findByUsername(username).map(user -> {
                        UserLogin userLogin = UserLogin.builder()
                                .id(user.getId())
                                .name(user.getName())
                                .username(user.getUsername())
                                .password(user.getPassword())
                                .roles(userRoleRepository.findByToolflowUser(user).stream().map(userRole -> userRole.getRole().getEnumKey()).toList())
                                .build();

                        return userLogin;
                    })
                    .orElseThrow(() -> new ObjectNotFoundException("User not found with username " + username));
        };
    }

}
