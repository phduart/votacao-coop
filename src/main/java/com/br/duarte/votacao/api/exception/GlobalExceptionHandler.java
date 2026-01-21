package com.br.duarte.votacao.api.exception;

import com.br.duarte.votacao.api.v2.dto.response.RespostaApi;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RespostaApi<Object>> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex) {

        String mensagem = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse("Dados inválidos");

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(RespostaApi.erro(mensagem));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<RespostaApi<Object>> handleConstraintViolation(
            ConstraintViolationException ex) {

        String mensagem = ex.getConstraintViolations()
                .stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .findFirst()
                .orElse("Dados inválidos");

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(RespostaApi.erro(mensagem));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<RespostaApi<Object>> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(RespostaApi.erro("Payload inválido ou valor incompatível com o tipo esperado."));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<RespostaApi<Object>> handleBusinessException(BusinessException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(RespostaApi.erro("Erro de negócio: " + ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RespostaApi<Object>> handleGeneralException(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(RespostaApi.erro("Ocorreu um erro interno no servidor."));
    }
}