package cn.nzcer.odapi.util;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @project: od-api
 * @ClassName: NetUtil
 * @author: nzcer
 * @creat: 2022/12/1 16:22
 */
@Slf4j
public class NetUtil {
    //读取超时为500s
    private static final long READ_TIMEOUT = 500;
    //写入超时为500s
    private static final long WRITE_TIMEOUT = 500;
    //连接超时为500s
    private static final long CONNECT_TIMEOUT = 500;
    static OkHttpClient client = new OkHttpClient.Builder()
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .build();

    // 同步请求
    public static JSONObject doGet(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            String res = response.body().string();
            if (response.isSuccessful()) {
                return JSONObject.parseObject(res);
            } else {
                //throw new IOException("Unexpected code " + response);
                //log.error("Unexpected code " + response);
                return null;
            }
        }
    }

    public static JSONObject doGetWithToken(String url, String token) throws IOException {
        Request request = new Request.Builder().header("Authorization", "token " + token)
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            String res = response.body().string();
            if (response.isSuccessful()) {
                return JSONObject.parseObject(res);
            } else {
                //throw new IOException("Unexpected code " + response);
                //log.error("Unexpected code " + response);
                return null;
            }
        }
    }

}
