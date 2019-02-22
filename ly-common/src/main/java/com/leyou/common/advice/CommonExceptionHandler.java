package com.leyou.common.advice;

import com.leyou.common.exception.LyException;
import com.leyou.common.vo.ExceptionResult;
import lombok.val;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @author hftang
 * @date 2019-02-21 16:43
 * @desc
 */
public class CommonExceptionHandler {

    @ExceptionHandler(LyException.class)
    public ResponseEntity<ExceptionResult> handleException(LyException e) {
        val em = e.getExceptionEnum();
        return ResponseEntity.status(em.getCode()).body(new ExceptionResult(em));
    }
}
