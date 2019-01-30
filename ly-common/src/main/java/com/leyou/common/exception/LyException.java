package com.leyou.common.exception;

import com.leyou.common.enums.ExceptionEnum;
import lombok.Getter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @author hftang
 * @date 2019-01-21 21:03
 * @desc
 */
@Getter
public class LyException extends RuntimeException {

    private ExceptionEnum exceptionEnum;


    public LyException(ExceptionEnum exceptionEnum) {
        this.exceptionEnum = exceptionEnum;
    }


}
