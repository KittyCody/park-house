package io.kittycody.parking.shared.error;

import java.util.Map;

public class EntityNotPresent extends AppError {

    @SafeVarargs
    public EntityNotPresent(String entityName, Map.Entry<String, Object>... args) {

        super("%s:not_present".formatted(entityName), args);
    }

}