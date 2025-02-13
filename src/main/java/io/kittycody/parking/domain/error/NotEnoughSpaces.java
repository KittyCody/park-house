package io.kittycody.parking.domain.error;

import io.kittycody.parking.shared.error.InvalidOperation;

import java.util.Map;

public class NotEnoughSpaces extends InvalidOperation {

    @SafeVarargs
    public NotEnoughSpaces(Map.Entry<String, Object>... args) {
        super("entry", "capacity", args);
    }
}

