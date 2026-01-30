package com.techie.microservices.order.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.techie.microservices.order.dto.ApiResponse;
import feign.FeignException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.util.stream.Collectors;


@RestControllerAdvice
public class GlobalExceptionHandler {

    // JSON invalide
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidJson(HttpMessageNotReadableException ex) {
        String message = "JSON invalide";
        if (ex.getCause() instanceof InvalidFormatException invalidFormatException) {
            String fieldName = invalidFormatException.getPath().get(0).getFieldName();
            message = "Le champ '" + fieldName + "' doit être un nombre valide";
        }
        return ResponseEntity.badRequest().body(new ApiResponse<>(false, message, null));
    }

    // Validation DTO
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationErrors(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining("; "));
        return ResponseEntity.badRequest().body(new ApiResponse<>(false, errorMessage, null));
    }


    // IllegalArgument
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(new ApiResponse<>(false, ex.getMessage(), null));
    }
     // Fegn Exception
     @ExceptionHandler(FeignException.class)
     public ResponseEntity<ApiResponse<Void>> handleFeignException(FeignException ex) {
         // Convert Inventory response body into your ApiResponse
         String message = ex.contentUTF8(); // This is the JSON string from inventory
         return ResponseEntity.status(ex.status())
                 .body(new ApiResponse<>(false, message, null));
     }

     //Order Exception
     @ExceptionHandler(OrderException.class)
     public ResponseEntity<ApiResponse<Void>> handleOrderException(OrderException ex) {
         return ResponseEntity.badRequest()
                 .body(new ApiResponse<>(false, ex.getMessage(), null));
     }


    // Autres exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneral(Exception ex) {
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, "Internal server error", null));
    }
}
