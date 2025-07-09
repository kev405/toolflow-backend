package com.codeflow.toolflow.util.exception;

import com.codeflow.toolflow.dto.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Log4j2
public class GlobalExceptionHandler {
    @ExceptionHandler(ObjectNotFoundException.class)
    public ResponseEntity<ApiError> handleObjectNotFound(HttpServletRequest request,
                                                         ObjectNotFoundException exception) {
        ApiError apiError = ApiError.builder()
                .message("El recurso solicitado no existe.")
                .backendMessage(exception.getLocalizedMessage())
                .url(request.getRequestURL().toString())
                .method(request.getMethod())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolationException(HttpServletRequest request,
                                                                       ConstraintViolationException exception) {
        List<String> violations = exception.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());

        ApiError apiError = ApiError.builder()
                .message("Datos inválidos")
                .backendMessage(String.join("; ", violations))
                .url(request.getRequestURL().toString())
                .method(request.getMethod())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handlerGenericException(HttpServletRequest request, Exception exception) {

        ApiError apiError = new ApiError();
        apiError.setBackendMessage(exception.getLocalizedMessage());
        apiError.setUrl(request.getRequestURL().toString());
        apiError.setMethod(request.getMethod());
        apiError.setMessage("Error interno en el servidor, vuelva a intentarlo");
        apiError.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiError);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValidException(HttpServletRequest request,
                                                                          MethodArgumentNotValidException exception) {
        List<String> validationMessages = exception.getBindingResult()
                .getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        ApiError apiError = ApiError.builder()
                .message(String.join("; ", validationMessages))
                .backendMessage(exception.getLocalizedMessage())
                .url(request.getRequestURL().toString())
                .method(request.getMethod())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> handlerUserNotFoundException(HttpServletRequest request, UserNotFoundException exception) {
        ApiError apiError = new ApiError();
        apiError.setBackendMessage(exception.getLocalizedMessage());
        apiError.setUrl(request.getRequestURL().toString());
        apiError.setMethod(request.getMethod());
        apiError.setTimestamp(LocalDateTime.now());
        apiError.setMessage("El usuario no existe");

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
    }

    @ExceptionHandler(InvalidRoleAssignmentException.class)
    public ResponseEntity<?> handlerInvalidRoleAssignmentException(HttpServletRequest request, InvalidRoleAssignmentException exception) {
        ApiError apiError = new ApiError();
        apiError.setBackendMessage(exception.getLocalizedMessage());
        apiError.setUrl(request.getRequestURL().toString());
        apiError.setMethod(request.getMethod());
        apiError.setTimestamp(LocalDateTime.now());
        apiError.setMessage("Error con los roles asignados");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<?> handlerUserAlreadyExistsException(HttpServletRequest request, UserAlreadyExistsException exception) {
        ApiError apiError = new ApiError();
        apiError.setBackendMessage(exception.getLocalizedMessage());
        apiError.setUrl(request.getRequestURL().toString());
        apiError.setMethod(request.getMethod());
        apiError.setTimestamp(LocalDateTime.now());
        apiError.setMessage("El usuario ya existe");

        return ResponseEntity.status(HttpStatus.CONFLICT).body(apiError);
    }

    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleHttpMessageNotReadable(HttpServletRequest request,
                                                                 org.springframework.http.converter.HttpMessageNotReadableException exception) {
        ApiError apiError = ApiError.builder()
                .message("Formato de solicitud inválido. Verifica la estructura del JSON.")
                .backendMessage(exception.getLocalizedMessage())
                .url(request.getRequestURL().toString())
                .method(request.getMethod())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }

    @ExceptionHandler(org.springframework.web.HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiError> handleMethodNotSupported(HttpServletRequest request,
                                                             org.springframework.web.HttpRequestMethodNotSupportedException exception) {
        ApiError apiError = ApiError.builder()
                .message("Método HTTP no permitido para este endpoint.")
                .backendMessage(exception.getMessage())
                .url(request.getRequestURL().toString())
                .method(request.getMethod())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(apiError);
    }

    @ExceptionHandler(org.springframework.web.bind.MissingServletRequestParameterException.class)
    public ResponseEntity<ApiError> handleMissingParameter(HttpServletRequest request,
                                                           org.springframework.web.bind.MissingServletRequestParameterException exception) {
        ApiError apiError = ApiError.builder()
                .message("Falta un parámetro obligatorio en la solicitud: " + exception.getParameterName())
                .backendMessage(exception.getMessage())
                .url(request.getRequestURL().toString())
                .method(request.getMethod())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(HttpServletRequest request,
                                                       org.springframework.security.access.AccessDeniedException exception) {
        ApiError apiError = ApiError.builder()
                .message(exception.getMessage() != null && !exception.getMessage().isEmpty() ? exception.getMessage() : "No tienes permisos para acceder a este recurso.")
                .backendMessage(exception.getMessage())
                .url(request.getRequestURL().toString())
                .method(request.getMethod())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiError);
    }

    @ExceptionHandler(InvalidSearchColumnException.class)
    public ResponseEntity<ApiError> handleInvalidSearchColumnException(HttpServletRequest request,
                                                                       InvalidSearchColumnException exception) {
        ApiError apiError = ApiError.builder()
                .message("La columna de búsqueda especificada no es válida.")
                .backendMessage(exception.getLocalizedMessage())
                .url(request.getRequestURL().toString())
                .method(request.getMethod())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }

    @ExceptionHandler(ToolNotFoundException.class)
    public ResponseEntity<?> handlerToolNotFoundException(HttpServletRequest request, ToolNotFoundException exception) {
        ApiError apiError = new ApiError();
        apiError.setBackendMessage(exception.getLocalizedMessage());
        apiError.setUrl(request.getRequestURL().toString());
        apiError.setMethod(request.getMethod());
        apiError.setTimestamp(LocalDateTime.now());
        apiError.setMessage("La herramienta no existe");

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleBadCredentials(HttpServletRequest request,
                                                         BadCredentialsException exception) {
        ApiError apiError = ApiError.builder()
                .message("Credenciales inválidas. Verifica tu usuario y contraseña.")
                .backendMessage(exception.getLocalizedMessage())
                .url(request.getRequestURL().toString())
                .method(request.getMethod())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiError);
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ApiError> handleDisabledAccount(HttpServletRequest request,
                                                          DisabledException exception) {
        ApiError apiError = ApiError.builder()
                .message("La cuenta está deshabilitada. Contacta al administrador.")
                .backendMessage(exception.getLocalizedMessage())
                .url(request.getRequestURL().toString())
                .method(request.getMethod())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiError);
    }

    @ExceptionHandler(MainHeadquarterDeletionException.class)
    public ResponseEntity<ApiError> handleMainHeadquarterDeletionException(
            HttpServletRequest request,
            MainHeadquarterDeletionException exception) {

        ApiError apiError = ApiError.builder()
                .message("No es posible eliminar la sede principal.")
                .backendMessage(exception.getLocalizedMessage())
                .url(request.getRequestURL().toString())
                .method(request.getMethod())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }

    @ExceptionHandler(AssociatedEntitiesExistException.class)
    public ResponseEntity<ApiError> handleAssociatedEntitiesExistException(
            HttpServletRequest request,
            AssociatedEntitiesExistException exception) {

        ApiError apiError = ApiError.builder()
                .message(exception.getMessage())
                .backendMessage(exception.getLocalizedMessage())
                .url(request.getRequestURL().toString())
                .method(request.getMethod())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(apiError);
    }
}
