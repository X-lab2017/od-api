package cn.nzcer.odapi.exception;

import cn.nzcer.odapi.dto.ResultData;
import cn.nzcer.odapi.enums.ReturnCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @project: od-api
 * @ClassName: GlobalExceptionHandler
 * @author: nzcer
 * @creat: 2022/12/6 14:38
 * @description: 全局异常处理类
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResultData<String> exception(Exception e) {
        log.error("全局异常信息 ex={}", e.getMessage(), e);
        return ResultData.fail(ReturnCode.RC404.getCode(), e.getMessage());
    }
}
