package com.robustel.adapter.resource.dispatching;

import com.robustel.ddd.core.DomainException;
import com.robustel.utils.RestResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author YangXuehong
 * @date 2021/11/17
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandlerDispatching {

    @ExceptionHandler(DomainException.class)
    public RestResponse<Void> runtimeExceptionHandler(HttpServletRequest request, final DomainException exception, HttpServletResponse response) {
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        exception.printStackTrace();
        return RestResponse.ofFailure(exception.getClass().getSimpleName(), exception.getMessage());
    }

}
