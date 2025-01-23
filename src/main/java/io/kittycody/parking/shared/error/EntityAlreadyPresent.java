package io.kittycody.parking.shared.error;

import java.util.Map;

public class EntityAlreadyPresent extends AppError {

    @SafeVarargs
    public EntityAlreadyPresent(String entityName, Map.Entry<String, Object>... args) {

        super("%s:already_present".formatted(entityName), args);
    }

}
