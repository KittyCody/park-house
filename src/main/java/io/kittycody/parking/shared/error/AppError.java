package io.kittycody.parking.shared.error;

import java.util.Map;


public abstract class AppError extends RuntimeException {

    private final String code;
    private final Map.Entry<String, Object>[] args;

    @SafeVarargs
    protected AppError(String code, Map.Entry<String, Object>... args) {

        super(code);

        this.code = code;
        this.args = args;
    }

    public String getCode() {
        return code;
    }

    public Map.Entry<String, Object>[] getArgs() {
        return args;
    }
}