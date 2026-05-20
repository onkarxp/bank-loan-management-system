package com.BankLoanManagement.exceptions;
 
import java.time.LocalDateTime;
 
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
 
@RestControllerAdvice // Tells Spring this class intercepts exceptions globally
public class GlobalExceptionHandler {
 
    // 1. Handle our specific custom exception
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFoundException(ResourceNotFoundException exception) {
        
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }
 
    // 2. Handle generic/unexpected exceptions (The "Catch-All")
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGlobalException(Exception exception) {
        
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    // 3. Arithmetic exception
    @ExceptionHandler(ArithmeticException.class)
    public ResponseEntity<String> handleArithmeticException(ArithmeticException exception){
    	    return new ResponseEntity <>(exception.getMessage() , HttpStatus.NOT_FOUND) ; 
    }
}