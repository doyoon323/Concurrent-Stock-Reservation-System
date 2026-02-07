package com.example.reservation_system.exception;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice // 모든 컨트롤러의 예외를 여기서 다 낚아챕니다.
@Slf4j
public class GlobalExceptionHandler {

    // 1. @Valid 검증 실패 시 발생하는 에러를 잡습니다.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationExceptions(MethodArgumentNotValidException e) {
        // 에러 메시지 중 첫 번째 것만 가져와서 프론트에게 던져줍니다.
        // 예: "휴대폰 번호 형식이 올바르지 않습니다."
        String errorMessage = e.getBindingResult()
                               .getAllErrors()
                               .get(0)
                               .getDefaultMessage();
        
        return ResponseEntity.badRequest().body(errorMessage);
    }

    

    // 우리가 서비스에서 던진 IllegalArgumentException을 여기서 잡습니다.
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        
        // 프론트에게 400 에러 코드와 서비스에서 설정한 메시지를 보냅니다.
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    // 그 외 예상치 못한 모든 에러(500)를 잡고 싶다면?
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAllException(Exception e) {
        log.error("서버에서 예상치 못한 에러가 발생",e.getMessage(),e);
        return ResponseEntity.internalServerError().body("서버 내부 오류가 발생했습니다.");
    }


}