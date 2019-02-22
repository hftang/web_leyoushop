package com.leyou.common.exception;

import com.leyou.common.enums.ExceptionEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author hftang
 * @date 2019-01-21 21:03
 * @desc
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class LyException extends RuntimeException {

    private ExceptionEnum exceptionEnum;




}
