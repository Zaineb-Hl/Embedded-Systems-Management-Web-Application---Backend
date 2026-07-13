package bws.webdevintern.Embedded.system.PFE.exceptions;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
	 @ExceptionHandler(ResourceNotFoundException.class)
	    public ResponseEntity<Map<String, String>> handleNotFound(ResourceNotFoundException ex) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                .body(Map.of("message", ex.getMessage()));
	    }

	    @ExceptionHandler(ForbiddenAccessException.class)
	    public ResponseEntity<Map<String, String>> handleForbidden(ForbiddenAccessException ex) {
	        return ResponseEntity.status(HttpStatus.FORBIDDEN)
	                .body(Map.of("message", ex.getMessage()));
	    }

}
