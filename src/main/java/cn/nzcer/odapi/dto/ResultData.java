package cn.nzcer.odapi.dto;

import cn.nzcer.odapi.enums.ReturnCode;
import lombok.Data;

/**
 * @project: od-api
 * @ClassName: ResultData
 * @author: nzcer
 * @creat: 2022/12/1 15:35
 */
@Data
public class ResultData<T> {
    private Integer code;
    private String message;
    private T data;
    private long timestamp;

    public ResultData() {
        this.timestamp = System.currentTimeMillis();
    }

    public static <T> ResultData<T> success(T data) {
        ResultData<T> resultData = new ResultData<>();
        resultData.setCode(ReturnCode.RC200.getCode());
        resultData.setMessage(ReturnCode.RC200.getMessage());
        resultData.setData(data);
        return resultData;
    }

    public static <T> ResultData<T> fail(Integer code, String message) {
        ResultData<T> resultData = new ResultData<>();
        resultData.setCode(code);
        resultData.setMessage(message);
        return resultData;
    }

    public static <T> ResultData<T> fail() {
        ResultData<T> resultData = new ResultData<>();
        resultData.setCode(ReturnCode.RC444.getCode());
        resultData.setMessage(ReturnCode.RC444.getMessage());
        return resultData;
    }

    public static <T> ResultData<T> fail(ReturnCode returnCode) {
        return fail(returnCode.getCode(), returnCode.getMessage());
    }
}
