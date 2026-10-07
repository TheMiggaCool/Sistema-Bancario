package ar.edu.unju.fi.arquitecturas.tp2daas.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Recurso no encontrado (404)
     */
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleRecursoNoEncontrado(
            RecursoNoEncontradoException ex, HttpServletRequest request
    ) {
        ErrorResponse respuesta = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
    }

    /**
     * Saldo insuficiente (422 Unprocessable Entity o 400 Bad Request)
     */
    @ExceptionHandler(SaldoInsuficienteException.class)
    public ResponseEntity<ErrorResponse> handleSaldoInsuficiente(
            SaldoInsuficienteException ex, HttpServletRequest request
    ) {
        ErrorResponse respuesta = new ErrorResponse(
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                "Regla del negocio violada",
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(respuesta);
    }

    /**
     * Límite diario de extracción superado (400 Bad Request o 422 Unprocessable Entity)
     */
    @ExceptionHandler(LimiteDiarioExcedidoException.class)
    public ResponseEntity<ErrorResponse> handleLimiteDiarioExcedido(
            LimiteDiarioExcedidoException ex, HttpServletRequest request
    ) {
        ErrorResponse respuesta = new ErrorResponse(
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                "Límite diario superado",
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(respuesta);
    }

    /**
     * Recurso ya existente / Duplicado (409 Conflict)
     */
    @ExceptionHandler(RecursoYaExistenteException.class)
    public ResponseEntity<ErrorResponse> handleRecursoYaExistnte(
            RecursoYaExistenteException ex, HttpServletRequest request
    ) {
        ErrorResponse respuesta = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(respuesta);
    }

    /**
     * Validación de DTOs con Jakarta Bean Validation (@Valid)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidacionDto(
            MethodArgumentNotValidException ex, HttpServletRequest request
    ) {
        ErrorResponse response = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Uno o más campos contienen datos inválidos",
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Captura general de excepciones no controladas (500)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleExceptionGlobal(
            Exception ex, HttpServletRequest request
    ) {
        ErrorResponse response = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                "Ocurrió un error interno inesperado. Por favor intente más tarde.",
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}