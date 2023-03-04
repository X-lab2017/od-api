package cn.nzcer.odapi.cron;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.BrokenBarrierException;

/**
 * @project: od-api
 * @ClassName: SyncDataBaseTest
 * @author: nzcer
 * @creat: 2023/3/4 10:40
 * @description:
 */
@SpringBootTest
class SyncDataBaseTest {
    @Autowired
    SyncDataBase syncDataBase;

    @Test
    void insertAllRepoMetrics() {

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
}