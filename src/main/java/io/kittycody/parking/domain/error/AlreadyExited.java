package io.kittycody.parking.domain.error;

import io.kittycody.parking.shared.error.InvalidOperation;

import java.util.Map;

public class AlreadyExited extends InvalidOperation {
    @SafeVarargs
    public AlreadyExited(Map.Entry<String, Object>... args) {
        super("exit_attempt", "already_exited", args);
    }
}
