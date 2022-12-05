package cn.nzcer.odapi.controller.repo;

import cn.nzcer.odapi.dto.ResultData;
import cn.nzcer.odapi.util.NetUtil;
import cn.nzcer.odapi.util.StringUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @project: od-api
 * @ClassName: RepoMetricController
 * @author: nzcer
 * @creat: 2022/12/1 17:03
 */
@RestController
@RequestMapping("/{org_name}/{repo_name}")
public class RepoMetricController {
    @Resource
    private RedisTemplate<String,JSONObject> redisTemplate;

    @GetMapping("/{metric}")
    public JSONArray  getRepoMetric(@PathVariable("org_name") String orgName, @PathVariable("repo_name") String repoName, @PathVariable("metric") String metric) throws IOException {
        String repoUrl = StringUtil.getRepoUrl(orgName, repoName, metric);
        JSONObject jo = redisTemplate.opsForValue().get(repoUrl);
        if (jo == null) {
            jo = NetUtil.doGet(repoUrl);
        }
        JSONArray ja = new JSONArray();
        jo.forEach((key, value) -> {
            JSONObject cur = new JSONObject();
            cur.put("org_name", orgName);
            cur.put("repo_name", repoName);
            cur.put("type", metric);
            cur.put("month", key);
            cur.put("value", value);
            ja.add(cur);
        });
        redisTemplate.opsForValue().set(repoUrl,jo,12L, TimeUnit.HOURS);
        return ja;
        //return ResultData.success(ja);
    }
}
