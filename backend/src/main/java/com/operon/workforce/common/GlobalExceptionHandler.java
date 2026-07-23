package com.operon.workforce.common;

import com.operon.workforce.auth.InvalidLoginException;
import com.operon.workforce.auth.UserNotApprovedException;
import com.operon.workforce.availability.AvailabilityNotFoundException;
import com.operon.workforce.availability.InvalidAvailabilityTimeRangeException;
import com.operon.workforce.shift.InvalidShiftTimeRangeException;
import com.operon.workforce.shift.ShiftNotFoundException;
import com.operon.workforce.shiftassignment.ShiftAssignmentAlreadyExistsException;
import com.operon.workforce.shiftassignment.ShiftAssignmentCapacityExceededException;
import com.operon.workforce.shiftassignment.ShiftAssignmentNotFoundException;
import com.operon.workforce.shiftassignment.ShiftAssignmentOverlapException;
import com.operon.workforce.user.DuplicateEmailException;
import com.operon.workforce.user.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicateEmail(DuplicateEmailException exception, HttpServletRequest request) {

        HttpStatus status = HttpStatus.CONFLICT;
        ApiErrorResponse errorResponse = new ApiErrorResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                exception.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationError(HttpServletRequest request) {

        HttpStatus status = HttpStatus.BAD_REQUEST;
        ApiErrorResponse errorResponse = new ApiErrorResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                "Validation failed",
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(errorResponse);

    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleUserNotFound(UserNotFoundException exception, HttpServletRequest request) {

        HttpStatus status = HttpStatus.NOT_FOUND;
        ApiErrorResponse errorResponse = new ApiErrorResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                exception.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(InvalidLoginException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidLogin(InvalidLoginException exception, HttpServletRequest request) {

        HttpStatus status = HttpStatus.UNAUTHORIZED;
        ApiErrorResponse errorResponse = new ApiErrorResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                exception.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(UserNotApprovedException.class)
    public ResponseEntity<ApiErrorResponse> handleUserNotApproved(UserNotApprovedException exception, HttpServletRequest request) {

        HttpStatus status = HttpStatus.FORBIDDEN;
        ApiErrorResponse errorResponse = new ApiErrorResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                exception.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(InvalidAvailabilityTimeRangeException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidAvailabilityTimeRange(InvalidAvailabilityTimeRangeException exception, HttpServletRequest request) {

        HttpStatus status = HttpStatus.BAD_REQUEST;
        ApiErrorResponse errorResponse = new ApiErrorResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                exception.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(AvailabilityNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleAvailabilityNotFound(AvailabilityNotFoundException exception, HttpServletRequest request) {

        HttpStatus status = HttpStatus.NOT_FOUND;
        ApiErrorResponse errorResponse = new ApiErrorResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                exception.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(InvalidShiftTimeRangeException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidShiftTimeRange(InvalidShiftTimeRangeException exception, HttpServletRequest request) {

        HttpStatus status = HttpStatus.BAD_REQUEST;
        ApiErrorResponse errorResponse = new ApiErrorResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                exception.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(ShiftNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleShiftNotFound(ShiftNotFoundException exception, HttpServletRequest request) {

        HttpStatus status = HttpStatus.NOT_FOUND;
        ApiErrorResponse errorResponse = new ApiErrorResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                exception.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(ShiftAssignmentAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleShiftAssignmentAlreadyExists(ShiftAssignmentAlreadyExistsException exception, HttpServletRequest request) {

        HttpStatus status = HttpStatus.CONFLICT;
        ApiErrorResponse errorResponse = new ApiErrorResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                exception.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(ShiftAssignmentNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleShiftAssignmentNotFound(ShiftAssignmentNotFoundException exception, HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ApiErrorResponse errorResponse = new ApiErrorResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                exception.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(ShiftAssignmentCapacityExceededException.class)
    public ResponseEntity<ApiErrorResponse> handleShiftAssignmentCapacityExceededException(ShiftAssignmentCapacityExceededException exception, HttpServletRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;
        ApiErrorResponse errorResponse = new ApiErrorResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                exception.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(ShiftAssignmentOverlapException.class)
    public ResponseEntity<ApiErrorResponse> handleShiftAssignmentOverlapException(ShiftAssignmentOverlapException exception, HttpServletRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;
        ApiErrorResponse errorResponse = new ApiErrorResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                exception.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(errorResponse);
    }
}
