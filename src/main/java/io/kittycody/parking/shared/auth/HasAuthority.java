package io.kittycody.parking.shared.auth;

public abstract class HasAuthority {
    public static final String GATE_MACHINE = "hasAuthority('ROLE_gate_machine')";
    public static final String ADMIN = "hasAuthority('ROLE_parking_admin')";
}
