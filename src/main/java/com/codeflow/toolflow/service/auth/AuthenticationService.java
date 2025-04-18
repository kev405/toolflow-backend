package com.codeflow.toolflow.service.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import static java.util.stream.Collectors.toList;
import java.util.HashMap;
import java.util.Map;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import com.codeflow.toolflow.dto.auth.AuthenticationRequest;
import com.codeflow.toolflow.dto.auth.AuthenticationResponse;
import com.codeflow.toolflow.dto.auth.UserLogin;
import com.codeflow.toolflow.persistence.user.entity.User;
import com.codeflow.toolflow.persistence.user.repository.UserRoleRepository;
import com.codeflow.toolflow.service.user.UserService;
import com.codeflow.toolflow.util.exception.ObjectNotFoundException;

@Service
@Log4j2
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserService userService;

    private final UserRoleRepository userRoleRepository;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    private Map<String, Object> generateExtraClaims(UserLogin userLogin) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("name", userLogin.getName());
        extraClaims.put("role", userLogin.getAuthorities());
        log.info("Extra claims: {}", extraClaims);

        return extraClaims;
    }

    public AuthenticationResponse login(AuthenticationRequest autRequest) {

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                autRequest.getUsername(), autRequest.getPassword()
        );

        authenticationManager.authenticate(authentication);
        User user = userService.findOneByUsername(autRequest.getUsername()).orElseThrow(() ->
                new ObjectNotFoundException("User not found. Username: " + autRequest.getUsername()));

        UserDetails userDetails = UserLogin.builder()
                .id(user.getId())
                .name(user.getName())
                .username(user.getUsername())
                .roles(userRoleRepository.findByToolflowUser(user).stream().map(userRole -> userRole.getRole().getEnumKey()).collect(toList()))
                .build();

        String jwt = jwtService.generateToken(userDetails, generateExtraClaims((UserLogin) userDetails));

        AuthenticationResponse authRsp = new AuthenticationResponse();
        authRsp.setJwt(jwt);

        return authRsp;
    }

    public boolean validateToken(String jwt) {

        try{
            jwtService.extractUsername(jwt);
            return true;
        }catch (Exception e){
            log.error(e.getMessage());
            return false;
        }

    }

    public UserLogin findLoggedInUser() {
        UsernamePasswordAuthenticationToken auth =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        String username = (String) auth.getPrincipal();
        User user = userService.findOneByUsername(username).orElseThrow(() ->
                new ObjectNotFoundException("User not found. Username: " + username));
        user.setPassword(null);
        UserLogin userLogin = UserLogin.builder()
                .id(user.getId())
                .name(user.getName())
                .username(user.getUsername())
                .roles(userRoleRepository.findByToolflowUser(user).stream().map(userRole -> userRole.getRole().getEnumKey()).collect(toList()))
                .build();
        return userLogin;
    }
}
