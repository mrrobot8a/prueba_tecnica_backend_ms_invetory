package com.inventario.pruebaTecnica.exception;

import com.inventario.pruebaTecnica.dtos.ErrorResponse;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler(CustomException.class)
        public ResponseEntity<ErrorResponse> handleCustomException(
                        CustomException ex,
                        HttpServletRequest request) {
                ErrorResponse response = ErrorResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .status(ex.getStatus().value())
                                .error(ex.getStatus().getReasonPhrase())
                                .message(ex.getMessage())
                                .errorCode(ex.getErrorCode())
                                .path(request.getRequestURI())
                                .build();
                return new ResponseEntity<>(response, ex.getStatus());
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ErrorResponse> handleValidationExceptions(
                        MethodArgumentNotValidException ex,
                        HttpServletRequest request) {
                String errors = ex.getBindingResult().getFieldErrors().stream()
                                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                                .collect(Collectors.joining(", "));

                ErrorResponse response = ErrorResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.BAD_REQUEST.value())
                                .error("Validation Error")
                                .message(errors)
                                .errorCode("INVALID_DATA")
                                .path(request.getRequestURI())
                                .build();

                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(DataAccessException.class)
        public ResponseEntity<ErrorResponse> handleDataAccessException(
                        DataAccessException ex,
                        HttpServletRequest request) {
                ErrorResponse response = ErrorResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.SERVICE_UNAVAILABLE.value())
                                .error("Error de base de datos")
                                .message("Ocurrió un error al acceder a los datos. Intente más tarde.")
                                .errorCode("DATABASE_ERROR")
                                .path(request.getRequestURI())
                                .build();

                return new ResponseEntity<>(response, HttpStatus.SERVICE_UNAVAILABLE);
        }

        /**
         * Handles constraint violations (e.g., unique product_id)
         */
        @ExceptionHandler(DataIntegrityViolationException.class)
        public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(
                        DataIntegrityViolationException ex,
                        HttpServletRequest request) {

                String message = "Data integrity violation";
                if (ex.getCause() instanceof ConstraintViolationException) {
                        message = "Duplicate product ID not allowed";
                }

                ErrorResponse response = ErrorResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.CONFLICT.value())
                                .error("Data Conflict")
                                .message(message)
                                .errorCode("DATA_CONFLICT")
                                .path(request.getRequestURI())
                                .build();

                return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }

        @ExceptionHandler(HttpMessageNotReadableException.class)
        public ResponseEntity<Map<String, String>> handleMissingRequestBody(HttpMessageNotReadableException ex) {
                return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(Map.of(
                                                "error", "Cuerpo de la solicitud faltante",
                                                "message", "Se requiere un cuerpo JSON válido para esta operación",
                                                "detalle",
                                                "Por favor, incluya los datos del inventario en formato JSON"));
        }

        @ExceptionHandler(InventoryAlreadyExistsException.class)
        public ResponseEntity<ErrorResponse> handleInventoryAlreadyExists(InventoryAlreadyExistsException ex, HttpServletRequest request) {
                ErrorResponse response = ErrorResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.CONFLICT.value())
                                .error("Inventory Exists")
                                .message(ex.getMessage())
                                .errorCode("INVENTORY_EXISTS")
                                .path(request.getRequestURI())
                                .build();
                return ResponseEntity.status(HttpStatus.CONFLICT)
                                .body(response);
        }

        @ExceptionHandler(InventoryNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleInventoryNotFound(InventoryNotFoundException ex) {
                ErrorResponse response = ErrorResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.NOT_FOUND.value())
                                .error("Inventory Not Found")
                                .message(ex.getMessage())
                                .errorCode("INVENTORY_NOT_FOUND")
                                .path(null)
                                .build();
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(response);
        }
}