package cn.nzcer.odapi.cron;

import cn.nzcer.odapi.util.DateUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BrokenBarrierException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @project: od-api
 * @ClassName: SyncDataBaseTest
 * @author: nzcer
 * @creat: 2023/3/4 10:40
 * @description:
 */
@SpringBootTest
@Slf4j
class SyncDataBaseTest {
    @Autowired
    SyncDataBase syncDataBase;

    @Test
    void insertAllRepoMetrics() throws BrokenBarrierException, IOException, InterruptedException {
        // 更新所有仓库的 OpenDigger 指标数据
        syncDataBase.insertAllRepoMetrics();
    }

    @Test
    void insertAllRepoStarAndFork() throws BrokenBarrierException, InterruptedException {
        // 更新所有仓库的 star 和 fork 总数
        syncDataBase.insertAllRepoStarAndFork();
    }

    @Test
    void name() {
        System.out.println(syncDataBase.getTokens());
    }

    @Test
    void testPattern() {
        String[] arr = {"2021-10-raw", "2021-10", "2021-10-otherKey", "other-key-2021-10-otherKey"};
        for (String content : arr) {
            String pattern = "^\\d{4}-\\d{2}$";
            Pattern r = Pattern.compile(pattern);
            Matcher matcher = r.matcher(content);
            if (matcher.find()) {
                log.info("原始 content：{}，匹配结果：{}", content, matcher.group(0).toString());
            } else {
                log.info("原始 content：{}，匹配结果：{}", content, "匹配失败");
            }
        }
    }

    @Test
    public void testJsonObjectForEach() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", "zhicheng");
        map.put("age", 18);
        JSONObject jo = new JSONObject(map);
        jo.forEach((key, value) -> {
            if (key.equals("name")) {
                return;
            }
            System.out.println(jo.get(key));
        });
    }

}