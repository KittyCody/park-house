package io.kittycody.parking;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class AuthUtil {

    record RealmAccess(List<String> roles) {}

    public static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor generateJwt(String sub, String... roles) {
        final Collection<GrantedAuthority> grantedAuthorities = Arrays.stream(roles)
                .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                .collect(Collectors.toList());

        return SecurityMockMvcRequestPostProcessors.jwt()
                .jwt(jwt -> {
                    jwt.claim("sub", sub);
                    jwt.claim("realm_access", new RealmAccess(List.of(roles)));
                })
                .authorities(grantedAuthorities);
    }
}