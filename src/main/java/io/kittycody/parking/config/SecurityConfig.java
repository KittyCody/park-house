package io.kittycody.parking.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.*;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    interface GrantedAuthoritiesConverter extends Converter<Jwt, Collection<GrantedAuthority>> {}

    @Bean
    GrantedAuthoritiesConverter grantedAuthoritiesConverter() {
        return jwt -> {
            final Map<String, Object> realmAccess = jwt.getClaim("realm_access");
            final var roles = realmAccess.get("roles");

            if (!(roles instanceof List<?>)) {
                throw new IllegalArgumentException("Unexpected instance type for 'roles'.");
            }

            final var realmRoles = ((List<?>) roles).stream()
                    .filter(e -> e instanceof String)
                    .map(e -> (String) e);

            return realmRoles
                    .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                    .collect(Collectors.toList());
        };
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter(
            Converter<Jwt, Collection<GrantedAuthority>> jwtGrantedAuthoritiesConverter
    ) {

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();

        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);

        return jwtAuthenticationConverter;
    }

    @Bean
    SecurityFilterChain resourceServerSecurityFilterChain(HttpSecurity http) throws Exception {

        http.oauth2ResourceServer(resourceServer ->
                resourceServer.jwt(jwtConfigurer -> {
        }));

        http.sessionManagement(sessions ->
                sessions.sessionCreationPolicy(SessionCreationPolicy.STATELESS)).csrf(AbstractHttpConfigurer::disable);

        http.authorizeHttpRequests(requests ->
                requests.anyRequest().authenticated());

        return http.build();
    }
}
