package com.voleksiienko.specforgeapi.infra.adapter.in.web;

import static com.voleksiienko.specforgeapi.core.domain.model.error.DomainErrorCode.*;
import static com.voleksiienko.specforgeapi.infra.adapter.in.web.dto.response.ApiErrorCode.INTERNAL;
import static com.voleksiienko.specforgeapi.infra.adapter.in.web.dto.response.ApiErrorCode.INVALID_REQUEST_FORMAT;

import com.voleksiienko.specforgeapi.core.application.exception.ConversionException;
import com.voleksiienko.specforgeapi.core.domain.exception.ConfigValidationException;
import com.voleksiienko.specforgeapi.core.domain.exception.JavaModelValidationException;
import com.voleksiienko.specforgeapi.core.domain.exception.SpecModelValidationException;
import com.voleksiienko.specforgeapi.core.domain.exception.TsModelValidationException;
import com.voleksiienko.specforgeapi.infra.adapter.in.web.dto.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RuntimeException.class)
    public ErrorResponse handle(RuntimeException e, HttpServletRequest request) {
        log.error("Error on URI [{}]: {}", request.getRequestURI(), e.getMessage(), e);
        return new ErrorResponse(INTERNAL.name(), "Something went wrong");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ErrorResponse handleJsonError(HttpMessageNotReadableException e, HttpServletRequest request) {
        log.error("Malformed JSON on URI [{}]", request.getRequestURI(), e);
        return new ErrorResponse(INVALID_REQUEST_FORMAT.name(), "Malformed JSON request");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ErrorResponse handleMethodError(HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        log.error("Method not supported on URI [{}]: {}", request.getRequestURI(), e.getMessage(), e);
        return new ErrorResponse(
                INVALID_REQUEST_FORMAT.name(), "HTTP method not supported: %s".formatted(e.getMethod()));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handleDtoValidation(MethodArgumentNotValidException e, HttpServletRequest request) {
        log.error("Validation failed on URI [{}]: {}", request.getRequestURI(), e.getMessage(), e);
        String devMessage = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> "Request validation failed for field [%s] because [%s]"
                        .formatted(error.getField(), error.getDefaultMessage()))
                .orElse("Request validation failed");
        return new ErrorResponse(INVALID_REQUEST_FORMAT.name(), devMessage);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(SpecModelValidationException.class)
    public ErrorResponse handle(SpecModelValidationException e, HttpServletRequest request) {
        log.error("Spec Validation failed on URI [{}]", request.getRequestURI(), e);
        return new ErrorResponse(SPEC_MODEL_VALIDATION_FAILED.name(), e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(JavaModelValidationException.class)
    public ErrorResponse handle(JavaModelValidationException e, HttpServletRequest request) {
        log.error("Java Model Validation failed on URI [{}]", request.getRequestURI(), e);
        return new ErrorResponse(JAVA_MODEL_VALIDATION_FAILED.name(), e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(TsModelValidationException.class)
    public ErrorResponse handle(TsModelValidationException e, HttpServletRequest request) {
        log.error("TS Model Validation failed on URI [{}]", request.getRequestURI(), e);
        return new ErrorResponse(TS_MODEL_VALIDATION_FAILED.name(), e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConfigValidationException.class)
    public ErrorResponse handle(ConfigValidationException e, HttpServletRequest request) {
        log.error("Config Validation failed on URI [{}]", request.getRequestURI(), e);
        return new ErrorResponse(CONFIG_VALIDATION_FAILED.name(), e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConversionException.class)
    public ErrorResponse handle(ConversionException e, HttpServletRequest request) {
        log.error("Conversion failed on URI [{}]", request.getRequestURI(), e);
        return new ErrorResponse(e.getErrorCode().getName(), e.getMessage());
    }
}
