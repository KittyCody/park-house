package io.kittycody.parking.shared.result;

import io.kittycody.parking.shared.error.AppError;

public class Result<T> {
    private final T value;
    private final AppError error;

    private Result(T value, AppError error) {
        this.value = value;
        this.error = error;
    }

    // Factory method for success
    public static <T> Result<T> success(T value) {
        return new Result<>(value, null);
    }

    // Factory method for failure
    public static <T> Result<T> failure(AppError error) {
        return new Result<>(null, error);
    }

    public boolean isSuccess() {
        return error == null;
    }

    public boolean isFailure() {
        return error != null;
    }

    public T valueOrThrow() {
        if (isFailure()) {
            throw new IllegalStateException("Cannot get value from a failed result.");
        }

        return value;
    }

    public AppError errorOrThrow() {
        if (isSuccess()) {
            throw new IllegalStateException("Cannot get error from a successful result.");
        }

        return error;
    }

    @Override
    public String toString() {
        return isSuccess()
                ? "Success[value=" + value + "]"
                : "Failure[error=" + error + "]";
    }
}
