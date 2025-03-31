package com.codeflow.toolflow.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.codeflow.toolflow.config.security.filter.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class HttpSecurityConfig {

    private final AuthenticationProvider daoAuthProvider;

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        SecurityFilterChain filterChain = http
                .csrf( csrfConfig -> csrfConfig.disable() )
                .sessionManagement( sessMagConfig -> sessMagConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS) )
                .authenticationProvider(daoAuthProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests( authReqConfig -> {
                    buildRequestMatchers(authReqConfig);
                } )
                .build();

        return filterChain;
    }

    @Bean
    public MethodSecurityExpressionHandler methodSecurityExpressionHandler() {
        DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
        // Al no establecer ningún prefijo, se compararán los roles exactamente como se definen (por ejemplo, "ADMINISTRATOR")
        expressionHandler.setDefaultRolePrefix("");
        return expressionHandler;
    }

    private static void buildRequestMatchers(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry authReqConfig) {
//    /*
//    Autorización de endpoints de products
//     */
//        authReqConfig.requestMatchers(HttpMethod.GET, "/products")
//                .hasAnyRole(Role.ADMINISTRATOR.name(), Role.ASSISTANT_ADMINISTRATOR.name());
//
////        authReqConfig.requestMatchers(HttpMethod.GET, "/products/{productId}")
//        authReqConfig.requestMatchers(RegexRequestMatcher.regexMatcher(HttpMethod.GET, "/products/[0-9]*"))
//                .hasAnyRole(Role.ADMINISTRATOR.name(), Role.ASSISTANT_ADMINISTRATOR.name());
//
//        authReqConfig.requestMatchers(HttpMethod.POST, "/products")
//                .hasRole(Role.ADMINISTRATOR.name());
//
//        authReqConfig.requestMatchers(HttpMethod.PUT, "/products/{productId}")
//                .hasAnyRole(Role.ADMINISTRATOR.name(), Role.ASSISTANT_ADMINISTRATOR.name());
//
//        authReqConfig.requestMatchers(HttpMethod.PUT, "/products/{productId}/disabled")
//                .hasRole(Role.ADMINISTRATOR.name());
//
//                    /*
//                    Autorización de endpoints de categories
//                     */
//
//        authReqConfig.requestMatchers(HttpMethod.GET, "/categories")
//                .hasAnyRole(Role.ADMINISTRATOR.name(), Role.ASSISTANT_ADMINISTRATOR.name());
//
//        authReqConfig.requestMatchers(HttpMethod.GET, "/categories/{categoryId}")
//                .hasAnyRole(Role.ADMINISTRATOR.name(), Role.ASSISTANT_ADMINISTRATOR.name());
//
//        authReqConfig.requestMatchers(HttpMethod.POST, "/categories")
//                .hasRole(Role.ADMINISTRATOR.name());
//
//        authReqConfig.requestMatchers(HttpMethod.PUT, "/categories/{categoryId}")
//                .hasAnyRole(Role.ADMINISTRATOR.name(), Role.ASSISTANT_ADMINISTRATOR.name());
//
//        authReqConfig.requestMatchers(HttpMethod.PUT, "/categories/{categoryId}/disabled")
//                .hasRole(Role.ADMINISTRATOR.name());
//
//        authReqConfig.requestMatchers(HttpMethod.GET, "/auth/profile")
//                .hasAnyRole(Role.ADMINISTRATOR.name(), Role.ASSISTANT_ADMINISTRATOR.name(),
//                        Role.CUSTOMER.name());

                    /*
                    Autorización de endpoints públicos
                     */
        authReqConfig.requestMatchers("/swagger-ui/**","/v3/api-docs/**").permitAll();

        authReqConfig.requestMatchers(HttpMethod.POST, "/auth/authenticate").permitAll();
        authReqConfig.requestMatchers(HttpMethod.GET, "/auth/validate-token").permitAll();

        authReqConfig.anyRequest().authenticated();
    }

}
