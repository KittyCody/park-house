package io.kittycody.parking.domain.error;

import io.kittycody.parking.shared.error.InvalidOperation;

import java.util.Map;

public class PendingPayment extends InvalidOperation {
    @SafeVarargs
    public PendingPayment(Map.Entry<String, Object>... args) {
        super("exit_attempt", "pending_payment", args);
    }
}
