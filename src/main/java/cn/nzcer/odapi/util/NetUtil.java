package cn.nzcer.odapi.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @project: od-api
 * @ClassName: NetUtil
 * @author: nzcer
 * @creat: 2022/12/1 16:22
 */
public class NetUtil {
    static OkHttpClient client = new OkHttpClient.Builder().readTimeout(100, TimeUnit.SECONDS).build();
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
                throw new IOException("Unexpected code " + response);
            }
        }
    }

    // 异步请求
    //public static void asyncRequest(String url) {
    //    Request request = new Request.Builder().url(url).build();
    //    client.newCall(request).enqueue(new Callback() {
    //        @Override
    //        public void onFailure(@NotNull Call call, @NotNull IOException e) {
    //            e.printStackTrace();
    //        }
    //
    //        @Override
    //        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
    //            if (!response.isSuccessful()) {
    //                throw new IOException("Unexpected code " + response);
    //            } else {
    //                String res = response.body().string();
    //                System.out.println(JSONObject.parseObject(res));
    //            }
    //        }
    //    });
    //}

}
