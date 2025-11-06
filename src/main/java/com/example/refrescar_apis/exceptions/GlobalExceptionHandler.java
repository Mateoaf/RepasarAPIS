package com.example.refrescar_apis.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

// @RestControllerAdvice le dice a Spring que esta clase
// "vigilará" a todos los @RestController por si lanzan una excepción.
@RestControllerAdvice
public class GlobalExceptionHandler {

    // @ExceptionHandler le dice: "Si alguien lanza una MethodArgumentNotValidException,
    // NO dejes que Spring la maneje. Ejecuta ESTE método en su lugar."
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        // Recorremos todos los errores de campo que ocurrieron
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        // Devolvemos el mapa de errores con un estado 400 Bad Request
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}