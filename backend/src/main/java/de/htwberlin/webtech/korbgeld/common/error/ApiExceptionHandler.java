package de.htwberlin.webtech.korbgeld.common.error;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;
import java.util.Map;

/**
 * Zentrale Fehlerbehandlung: Jede Ausnahme wird zu einer Antwort im Format ProblemDetail (RFC 9457).
 * Die Basisklasse kümmert sich schon um Standardfehler wie kaputtes JSON (400).
 */
@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(NotFoundException.class)
    ProblemDetail handleNotFound(NotFoundException e, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
        problem.setProperty("path", request.getRequestURI());
        return problem;
    }

    @ExceptionHandler(FeatureNotAvailableException.class)
    ProblemDetail handleNotAvailable(FeatureNotAvailableException e) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_IMPLEMENTED, e.getMessage());
        problem.setProperty("feature", e.getFeature());
        problem.setProperty("milestone", e.getMilestone());
        return problem;
    }

    @ExceptionHandler(ConflictException.class)
    ProblemDetail handleConflict(ConflictException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler(BadRequestException.class)
    ProblemDetail handleBadRequest(BadRequestException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(ExternalServiceException.class)
    ProblemDetail handleExternal(ExternalServiceException e) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_GATEWAY, e.getMessage());
        problem.setProperty("source", e.getSource());
        return problem;
    }

    @ExceptionHandler(DemoLimitReachedException.class)
    ProblemDetail handleDemoLimit(DemoLimitReachedException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.TOO_MANY_REQUESTS, e.getMessage());
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    ProblemDetail handleInvalidCredentials(InvalidCredentialsException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, e.getMessage());
    }

    // Alles Unerwartete: ins Log schreiben, dem Nutzer nur eine allgemeine Meldung zeigen (keine Interna)
    @ExceptionHandler(Exception.class)
    ProblemDetail handleUnexpected(Exception e, HttpServletRequest request) {
        log.error("Unerwarteter Fehler bei {} {}", request.getMethod(), request.getRequestURI(), e);
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                "Da ist etwas schiefgelaufen. Versuch es gleich noch einmal.");
    }

    // Unbekannter Pfad, z. B. GET /api/gibt-es-nicht
    @Override
    protected ResponseEntity<Object> handleNoResourceFoundException(NoResourceFoundException e, HttpHeaders headers,
                                                                    HttpStatusCode status, WebRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, "Diesen Pfad gibt es nicht.");
        problem.setProperty("path", ((ServletWebRequest) request).getRequest().getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem);
    }

    // Bean Validation (@Valid) schlägt fehl: Liste aus Feld und Meldung
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException e, HttpHeaders headers,
                                                                  HttpStatusCode status, WebRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Einige Angaben sind ungültig.");
        List<Map<String, String>> errors = e.getBindingResult().getFieldErrors().stream()
                .map(error -> Map.of("field", error.getField(), "message", String.valueOf(error.getDefaultMessage())))
                .toList();
        problem.setProperty("errors", errors);
        return ResponseEntity.badRequest().body(problem);
    }
}
