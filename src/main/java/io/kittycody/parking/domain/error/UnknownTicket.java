package io.kittycody.parking.domain.error;

import io.kittycody.parking.shared.error.EntityNotPresent;

import java.util.Map;

public class UnknownTicket extends EntityNotPresent {
    @SafeVarargs
    public UnknownTicket(Map.Entry<String, Object>... args) {
        super("ticket", args);
    }
}
