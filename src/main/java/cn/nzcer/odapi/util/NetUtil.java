package cn.nzcer.odapi.util;

import com.alibaba.fastjson.JSONObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

/**
 * @project: od-api
 * @ClassName: NetUtil
 * @author: nzcer
 * @creat: 2022/12/1 16:22
 */
public class NetUtil {
    public static JSONObject doGet(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            String res = response.body().string();
            if (response.isSuccessful()) {
                return JSONObject.parseObject(res);
            } else {
                throw new IOException("Unexpected code " + response);
            }
        }
    }
}
