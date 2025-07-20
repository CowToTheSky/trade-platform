package com.platform.common.handler;

import com.platform.common.exception.BusinessException;
import com.platform.common.vo.ResponseVO;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseVO<?> handleBusinessException(BusinessException ex) {
        return ResponseVO.error(ex.getCode(), ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseVO<?> handleException(Exception ex) {
        // 生产环境建议不要直接返回ex.getMessage()
        return ResponseVO.error(500, "服务器内部错误");
    }
} 