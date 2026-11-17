package cl.duocuc.ferreteria_auth.exception;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AuthExceptionHandler {

  @ExceptionHandler(BadCredentialsException.class)
  ResponseEntity<Map<String, String>> credencialesInvalidas(BadCredentialsException e) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
  }

  @ExceptionHandler(ReglaNegocioException.class)
  ResponseEntity<Map<String, String>> reglaNegocio(ReglaNegocioException e) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  ResponseEntity<Map<String, String>> datosInvalidos(MethodArgumentNotValidException e) {
    String mensaje = e.getBindingResult().getFieldErrors().stream()
        .findFirst()
        .map(error -> error.getField() + ": " + error.getDefaultMessage())
        .orElse("Datos inválidos");
    return ResponseEntity.badRequest().body(Map.of("error", mensaje));
  }
}
