package cn.nzcer.odapi.enums;

/**
 * @project: od-api
 * @ClassName: ReturnCode
 * @author: nzcer
 * @creat: 2022/12/1 15:29
 */
public enum ReturnCode {
    RC200(200, "操作成功"),
    RC444(444, "操作失败"),
    RC404(404, "资源不存在");
    private final Integer code;
    private final String message;

    ReturnCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
