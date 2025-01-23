package io.kittycody.parking.shared.auth;

import org.springframework.security.web.header.writers.StaticHeadersWriter;

public abstract class HasAuthority {
    public static final String GATE_MACHINE = "hasAuthority('gate_machine')";
    public static final String ADMIN = "hasAuthority('parking_admin')";
}
