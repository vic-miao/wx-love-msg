package com.xiake.common.result;


/**
 * 全局错误码
 */


/**
 * 全局错误码
 * <p>A开头的为系统通用错误，B开头的是业务错误，C开头的是卡包错误</p>
 */
public enum ErrorCodeEnum {
    /**
     * 通用错误
     */
    SUCCESS("0000", "成功"),
    AUTHFAIL("401", "认证失败"),
    NOAUTHORITY("403", "无操作权限"),
    GATEWAYERROR("503", "网关异常"),
    A999999("A999999", "系统异常,请稍后重试"),
    A100001("A100001", "请求（参数）错误!");

    private String errorCode;
    private String message;

    ErrorCodeEnum(String errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return this.message;
    }


    public static ErrorCodeEnum getErrorCodeEnum(String errorCode) {
        for (ErrorCodeEnum value : ErrorCodeEnum.values()) {
            if (value.errorCode.equals(errorCode)) {
                return value;
            }
        }
        return ErrorCodeEnum.A999999;
    }


    public static void main(String[] args) {
        System.out.println("| 错误码 | 描述 |");
        System.out.println("| ----- | ------ |");
        for (ErrorCodeEnum value : ErrorCodeEnum.values()) {
            System.out.println("| " + value.errorCode + " | " + value.getMessage() + " | ");
        }
    }

}
