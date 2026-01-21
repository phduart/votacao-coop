package com.br.duarte.votacao.api.exception;

import com.br.duarte.votacao.api.v2.dto.response.RespostaApi;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

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
