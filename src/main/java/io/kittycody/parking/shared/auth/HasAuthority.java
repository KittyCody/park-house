package io.kittycody.parking.shared.auth;

public abstract class HasAuthority {
    public static final String GATE_MACHINE_ROLE = "hasAuthority('ROLE_gate_machine')";
    public static final String ADMIN_ROLE = "hasAuthority('ROLE_parking_admin')";
}
