package io.kittycody.parking.config;

import io.kittycody.parking.shared.error.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;


@ControllerAdvice
public class GlobalErrorHandler {

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

    @ExceptionHandler(NoResourceFoundException.class)
    ProblemDetail handleNoResourceFoundException(NoResourceFoundException ignoredEx) {

        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, "resource:not_found");
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    ProblemDetail handleAuthorizationDeniedException(AuthorizationDeniedException ignoredEx) {

        return ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, "access:denied");
    }

    @ExceptionHandler(Exception.class)
    ProblemDetail handleInternalError(Exception ignoredErr) {

        return ProblemDetail
                .forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "server:internal_error");
    }

    @ExceptionHandler(InvalidInputData.class)
    ProblemDetail handleBadRequest(InvalidInputData ignoredEx) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "bad_request");
    }
}
