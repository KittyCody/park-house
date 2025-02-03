package io.kittycody.parking.config;

import io.kittycody.parking.shared.error.AppError;
import io.kittycody.parking.shared.error.EntityAlreadyPresent;
import io.kittycody.parking.shared.error.EntityNotPresent;
import io.kittycody.parking.shared.error.InvalidOperation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class GlobalErrorHandler {

    private final Logger logger = LogManager.getLogger(GlobalErrorHandler.class);

    @ExceptionHandler(AppError.class)
    ProblemDetail handleAppError(AppError err) {

        final var status = switch (err) {
            case EntityNotPresent ignored -> HttpStatus.NOT_FOUND;
            case EntityAlreadyPresent ignored -> HttpStatus.CONFLICT;
            case InvalidOperation ignored -> HttpStatus.NOT_ACCEPTABLE;

            default -> throw new IllegalStateException("unexpected value: " + err);
        };

        return ProblemDetail.forStatusAndDetail(status, err.getCode());
    }

    @ExceptionHandler(Exception.class)
    ProblemDetail handleInternalError(Exception err) {

        logger.error(err);

        return ProblemDetail
                .forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "server:internal_error");
    }
}
