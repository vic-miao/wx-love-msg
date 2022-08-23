package com.xiake.common.result;

import org.springframework.util.StringUtils;

import java.io.Serializable;


/**
 * <p>JsonInclude：忽略</p>
 * <p>JsonPropertyOrder：json结果按字母顺序排序</p>
 *
 * @ClassName ResponseApi
 * @Description 接口返回公共实体
 * @Date 2020/3/3 15:40
 * @Version 1.0
 */
public class ResponseApi<T> implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 结果说明
     */
    private String msg;
    /**
     * 状态码
     */
    private String code;
    /**
     * 时间戳
     */
    private Long resultTime;
    /**
     * 数据
     */
    private T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Long getResultTime() {
        return System.currentTimeMillis();
    }

    public void setResultTime(Long resultTime) {
        this.resultTime = resultTime;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }


    public ResponseApi() {
    }

    public static <T> ResponseApi<T> success() {
        return success(ErrorCodeEnum.SUCCESS);
    }

    public static <T> ResponseApi<T> success(T data) {
        return success(ErrorCodeEnum.SUCCESS, data);
    }

    public static <T> ResponseApi<T> success(String errorCode, T data) {

        errorCode = StringUtils.startsWithIgnoreCase(errorCode, "EC_") ? errorCode : "EC_" + errorCode;
        ErrorCodeEnum ece;
        try {
            ece = ErrorCodeEnum.valueOf(errorCode);
        } catch (IllegalArgumentException e) {
            ece = ErrorCodeEnum.A999999;
        }
        return success(ece, data);
    }

    public static <T> ResponseApi<T> success(ErrorCodeEnum ece) {
        return success(ece, null);
    }

    public static <T> ResponseApi<T> success(ErrorCodeEnum ece, T data) {
        String errorCode = ece.getErrorCode();
        String message = ece.getMessage();
        return success(errorCode, message, data);
    }

    public static <T> ResponseApi<T> success(String errorCode, String resultDetail, T data) {
        ResponseApi<T> responseApi = new ResponseApi<>();
        responseApi.setCode(errorCode);
        responseApi.setMsg(resultDetail);
        responseApi.setData(data);
        return responseApi;
    }

    public static <T> ResponseApi<T> fail() {
        return fail(ErrorCodeEnum.A999999);
    }

    public static <T> ResponseApi<T> fail(String errorMessage) {
        return fail(ErrorCodeEnum.A999999.getErrorCode(), errorMessage, null);
    }

    public static <T> ResponseApi<T> fail(T data) {
        return fail(ErrorCodeEnum.A999999, data);
    }


    public static <T> ResponseApi<T> error(T data) {
        return fail(ErrorCodeEnum.A999999, data);
    }


    public static <T> ResponseApi<T> fail(String errorCode, T data) {
        ErrorCodeEnum ece;
        try {
            ece = ErrorCodeEnum.valueOf(errorCode);
        } catch (IllegalArgumentException e) {
            ece = ErrorCodeEnum.A999999;
        }
        return fail(ece, data);
    }

    public static <T> ResponseApi<T> fail(ErrorCodeEnum ece) {
        return fail(ece, ece.getMessage());
    }

    public static <T> ResponseApi<T> fail(ErrorCodeEnum ece, String resultDetail) {
        return fail(ece.getErrorCode(), resultDetail, null);
    }

    public static <T> ResponseApi<T> fail(ErrorCodeEnum ece, T data) {
        String errorCode = ece.getErrorCode();
        String message = ece.getMessage();
        return fail(errorCode, message, data);
    }

    public static <T> ResponseApi<T> fail(String errorCode, String resultDetail, T data) {
        ResponseApi<T> responseApi = new ResponseApi<>();
        responseApi.setCode(errorCode);
        responseApi.setMsg(resultDetail);
        responseApi.setData(data);
        return responseApi;
    }
}
