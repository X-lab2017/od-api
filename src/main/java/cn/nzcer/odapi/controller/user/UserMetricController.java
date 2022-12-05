package cn.nzcer.odapi.controller.user;

import cn.nzcer.odapi.dto.ResultData;
import cn.nzcer.odapi.util.NetUtil;
import cn.nzcer.odapi.util.StringUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @project: od-api
 * @ClassName: UserMetricController
 * @author: nzcer
 * @creat: 2022/12/1 17:07
 */
@RestController
@RequestMapping("/{user_name}")
public class UserMetricController {
    @Resource
    private RedisTemplate<String, JSONObject> redisTemplate;
    @GetMapping("/{metric}")
    public ResultData<JSONArray> getUserMetric(@PathVariable("user_name") String userName, @PathVariable("metric") String userMetric) throws IOException {
        String userUrl = StringUtil.getUserUrl(userName, userMetric);
        JSONObject jo = redisTemplate.opsForValue().get(userUrl);
        if (jo == null) {
            jo = NetUtil.doGet(userUrl);
        }
        JSONArray ja = new JSONArray();
        jo.forEach((key, value) -> {
            JSONObject cur = new JSONObject();
            cur.put("user", userName);
            cur.put("type", userMetric);
            cur.put("month", key);
            cur.put("value", value);
            ja.add(cur);
        });
        redisTemplate.opsForValue().set(userUrl,jo,12L, TimeUnit.HOURS);
        return ResultData.success(ja);
    }
}
