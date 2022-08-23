package com.xiake.common.exception;


import com.xiake.common.result.ErrorCodeEnum;
import com.xiake.common.result.ResponseApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.yaml.snakeyaml.constructor.DuplicateKeyException;

import javax.servlet.http.HttpServletRequest;
import java.net.SocketTimeoutException;
import java.util.Arrays;

/**
 * 异常处理器
 */
@RestControllerAdvice
public class MyExceptionHandler {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private HttpServletRequest request;


    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseApi handleDuplicateKeyException(DuplicateKeyException e) {
        logger.error(e.getMessage(), e);
        return ResponseApi.fail("系统中已存在重复数据");
    }


    @ExceptionHandler(SocketTimeoutException.class)
    public ResponseApi handleSocketTimeoutException(SocketTimeoutException e) {
        logger.error(e.getMessage(), e);
        return ResponseApi.fail("请求超时");
    }

    @ExceptionHandler(ResourceAccessException.class)
    public ResponseApi handleResourceAccessException(ResourceAccessException e) {
        logger.error(e.getMessage(), e);
        return ResponseApi.fail("网络请求异常");
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseApi handleHttpClientErrorException(HttpClientErrorException e) {
        logger.error(e.getMessage(), e);
        return ResponseApi.fail("网络请求错误,http响应状态码:" + e.getStatusCode().value());
    }

    @ExceptionHandler(BindException.class)
    public ResponseApi handleBindException(BindException e) {
        logger.error(e.getMessage(), e);
        return ResponseApi.fail("参数类型不正确,请检查请求参数");
    }

    /**
     * 类型转换异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(NumberFormatException.class)
    public ResponseApi handleNumberFormatException(NumberFormatException e) {
        logger.error(e.getMessage(), e);
        return ResponseApi.fail("参数类型不正确,请检查请求参数类型");
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseApi handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        return ResponseApi.fail("请求方式不支持,该接口支持的请求方式是:" + Arrays.toString(e.getSupportedMethods()));
    }


    /**
     * 参数丢失
     *
     * @param e
     * @return
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseApi handleClientAbortException(MissingServletRequestParameterException e) {
        String parameterName = e.getParameterName();
        String parameterType = e.getParameterType();
        return ResponseApi.fail("请求参数" + parameterName + "(" + parameterType + ")不能为空");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseApi handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        e.printStackTrace();
        return ResponseApi.fail(ErrorCodeEnum.A100001, "请检查请求参数");
    }

    @ExceptionHandler(Exception.class)
    public ResponseApi handleException(Exception e) {
        e.printStackTrace();
        return ResponseApi.fail("系统错误");
    }


}
