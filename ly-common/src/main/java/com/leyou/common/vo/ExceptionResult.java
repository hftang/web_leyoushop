package com.leyou.common.vo;

/**
 * @author hftang
 * @date 2019-02-21 16:45
 * @desc
 */
import com.leyou.common.enums.ExceptionEnum;
import lombok.Data;

/**
 * @Author: cuzz
 * @Date: 2018/10/31 19:32
 * @Description: 异常结果处理对象
 */
@Data
public class ExceptionResult {
    private int status;
    private String message;
    private Long timestamp;

    public ExceptionResult(ExceptionEnum exceptionEnum) {
        this.status = exceptionEnum.getCode();
        this.message = exceptionEnum.getMsg();
        this.timestamp = System.currentTimeMillis();
    }
}