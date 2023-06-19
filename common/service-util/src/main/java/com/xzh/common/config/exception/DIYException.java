package com.xzh.common.config.exception;

import com.xzh.common.result.ResultCodeEnum;
import lombok.Data;

/**
 * @Author:Xzh_
 * @Date:2023/3/15
 */
@Data
public class DIYException extends RuntimeException{
    private Integer code;
    private String message;

    /**
     * 通过状态码和错误消息创建异常对象
     * @param code
     * @param message
     */
    public DIYException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    /**
     * 接收枚举类型对象
     * @param resultCodeEnum
     */
    public DIYException(ResultCodeEnum resultCodeEnum) {
        super(resultCodeEnum.getMessage());
        this.code = resultCodeEnum.getCode();
        this.message = resultCodeEnum.getMessage();
    }

    @Override
    public String toString() {
        return "GuliException{" +
                "code=" + code +
                ", message=" + this.getMessage() +
                '}';
    }
}
