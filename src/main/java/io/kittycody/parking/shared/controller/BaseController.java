package io.kittycody.parking.shared.controller;

import io.kittycody.parking.shared.result.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public abstract class BaseController {

    protected <T> ResponseEntity<T> toResponse(HttpStatus status, Result<T> result) {
        if (result.isFailure()) {
            throw result.errorOrThrow();
        }

        return ResponseEntity.status(status).body(result.valueOrThrow());
    }
}
