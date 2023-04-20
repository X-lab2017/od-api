package cn.nzcer.odapi.cron;

import cn.nzcer.odapi.config.GitHubApiConfig;
import cn.nzcer.odapi.entity.GitHubToken;
import cn.nzcer.odapi.entity.RepoMetric;
import cn.nzcer.odapi.entity.RepoStatistic;
import cn.nzcer.odapi.service.RepoMetricService;
import cn.nzcer.odapi.service.RepoStatisticService;
import cn.nzcer.odapi.util.DateUtil;
import cn.nzcer.odapi.util.NetUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @project: od-api
 * @ClassName: SyncDataBase
 * @author: nzcer
 * @creat: 2022/12/12 16:31
 * @description:
 */
@Slf4j
@Component
public class SyncDataBase {
    @Resource
    RepoMetricService repoMetricService;

    @Resource
    RepoStatisticService repoStatisticService;

    @Resource
    GitHubApiConfig gitHubApiConfig;

    /**
     * 获取 https://open-leaderboard.x-lab.info/ 中上榜的 repo name
     *
     * @return
     */
    public Set<String> getAllRepo() throws IOException {
        String urlPrefix = "https://xlab-open-source.oss-cn-beijing.aliyuncs.com/open_leaderboard/";
        String[] metrics = {"activity", "open_rank"};
        String[] regions = {"chinese", "global"};
        List<String> months = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        int curYear = calendar.get(Calendar.YEAR);
        int curMonth = calendar.get(Calendar.MONTH);
        int stYear = 2015;
        for (int year = stYear; year < curYear; year++) {
            months.add(String.valueOf(year));
            for (int month = 1; month <= 12; month++) {
                months.add(year+""+month);
            }
        }
        // 非某一年的第一个月份
        if (curMonth != 0) {
            months.add(String.valueOf(curYear));
            for (int month = 1; month <= curMonth; month++) {
                months.add(curYear+""+month);
            }
        }
        StringBuilder sb = new StringBuilder();
        Set<String> repoNames = new HashSet<>();
        List<String> allUrls = new ArrayList<>();
        for (String metric : metrics) {
            for (String region : regions) {
                for (String month : months) {
                    sb.append(urlPrefix).append(metric).append("/").append("repo").append("/").append(region).append("/").append(month).append(".json");
                    allUrls.add(sb.toString());
                    sb.setLength(0);
                }
            }
        }
        for (String url : allUrls) {
            repoNames.addAll(parseRepo(url,300));
        }
        return repoNames;
    }
    public Set<String> parseRepo(String url) throws IOException {
        JSONObject jsonObject = NetUtil.doGet(url);
        //JSONObject jsonObject = NetUtil.asyncRequest(url);
        Set<String> repoNames = new HashSet<>();
        JSONArray jsonArray = jsonObject.getJSONArray("data");
        for (int i = 0; i < jsonArray.size(); i++) {
            String repoName = jsonArray.getJSONObject(i).getJSONObject("item").getString("name");
            repoNames.add(repoName);
        }
        return repoNames;
    }

    /**
     * 从 OSS 上获取每个文件中的前 count 个 repo
     * @param url
     * @param count
     * @return
     * @throws IOException
     */
    public Set<String> parseRepo(String url, int count) throws IOException {
        JSONObject jsonObject = NetUtil.doGet(url);
        Set<String> repoNames = new HashSet<>();
        JSONArray jsonArray = jsonObject.getJSONArray("data");
        int cnt = 0;
        for (int i = 0; i < jsonArray.size(); i++) {
            String repoName = jsonArray.getJSONObject(i).getJSONObject("item").getString("name");
            repoNames.add(repoName);
            cnt++;
            if (cnt == count) {
                break;
            }
        }
        return repoNames;
    }

    /**
     * 插入所有仓库的指标数据
     * @throws IOException
     * @throws BrokenBarrierException
     * @throws InterruptedException
     */
    // 每月 3 日凌晨 1 点启动定时任务更新仓库的所有指标数据
    @Scheduled(cron = "0 0 1 3 * ?")
    public void insertAllRepoMetrics() throws IOException, BrokenBarrierException, InterruptedException {
        // RepoMetrics 线程池
        ThreadPoolExecutor executor = new ThreadPoolExecutor(20,
                100,
                10,
                TimeUnit.MICROSECONDS,
                new LinkedBlockingQueue<Runnable>());
        log.info("Repo Metrics 定时任务启动:" + new Date());
        log.info("清空 repo_metric 表");
        repoMetricService.truncateRepoMetric();
        Set<String> allRepo = getAllRepo();
        List<String> tokens = getTokens();
        List<String> errorRepos = new ArrayList<>();
        CountDownLatch countDownLatch = new CountDownLatch(allRepo.size());
        int repoCnt = 0;
        for (String repo : allRepo) {
            String token = tokens.get(repoCnt % tokens.size());
            executor.execute(() -> {
                try {
                    insertOneRepoMetric(repo, token);
                } catch (Exception e) {
                    errorRepos.add(repo + ":" + e.getMessage());
                    throw new RuntimeException(e);
                } finally {
                    countDownLatch.countDown();
                }
            });
            repoCnt++;
        }
        countDownLatch.await();
        log.info("仓库的数量: " + String.valueOf(allRepo.size()));
        log.info("已执行完的仓库数量：" + executor.getCompletedTaskCount());
        log.info("所有子线程执行完毕");
        FileWriter writer = new FileWriter("output.txt");
        for (String str : errorRepos) {
            writer.write(str + System.lineSeparator());
        }
        writer.close();
        executor.shutdown();
    }



    /**
     * 插入所有仓库的 star 和 fork 数据
     * @throws InterruptedException
     * @throws BrokenBarrierException
     */
    @Scheduled(cron = "0 0 5 * * ?")
    public void insertAllRepoStarAndFork() throws InterruptedException, BrokenBarrierException {
        // RepoStarAndFork 线程池
        ThreadPoolExecutor executor = new ThreadPoolExecutor(20,
                100,
                10,
                TimeUnit.MICROSECONDS,
                new LinkedBlockingQueue<Runnable>());
        log.info("Repo Statistic 定时任务启动:" + new Date());
        log.info("清空  repo_statistic 表");
        repoStatisticService.truncateRepoStatistic();
        List<Map<String, String>> repoInfo = repoMetricService.getRepoInfo();
        List<String> tokens = getTokens();
        CountDownLatch countDownLatch = new CountDownLatch(repoInfo.size());
        int totalRepo = 0;
        for (Map<String, String> map : repoInfo) {
            totalRepo++;
            List<RepoStatistic> list = new ArrayList<>();
            int finalTotalRepo = totalRepo;
            executor.execute(() -> {
                try {
                    insertOneRepoStarAndFork(map, tokens.get(finalTotalRepo % tokens.size()), list);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();
        log.info("仓库的数量: " + String.valueOf(repoInfo.size()));
        log.info("已执行完的仓库数量：" + executor.getCompletedTaskCount());
        log.info("所有子线程执行完毕");
        executor.shutdown();
    }

    /**
     * 插入单个仓库的 star 和 fork 数据
     * @param map
     * @param token
     * @param list
     * @throws IOException
     */
    private void insertOneRepoStarAndFork(Map<String, String> map, String token, List<RepoStatistic> list) throws IOException {
        String orgName = map.get("orgName");
        String repoName = map.get("repoName");
        String url = " https://api.github.com/repos/" + orgName + "/" + repoName;
        //log.info(url);
        JSONObject jsonObject = NetUtil.doGetWithToken(url, token);
        if (jsonObject != null) {
            Integer star = jsonObject.getInteger("stargazers_count");
            Integer fork = jsonObject.getInteger("forks_count");
            if (star == null || fork == null) {
                log.info(Thread.currentThread().getName() + " 当前仓库" + orgName + "/" + repoName + "无 star 或 fork");
                return;
            }
            RepoStatistic r1 = new RepoStatistic(orgName, repoName, "stargazers_count", star);
            RepoStatistic r2 = new RepoStatistic(orgName, repoName, "forks_count", fork);
            list.add(r1);
            list.add(r2);
            repoStatisticService.insertBatchRepoStatistic(list);
            log.info(Thread.currentThread().getName() + " 成功插入 star/fork 数据：" + orgName + "/" + repoName);
            list.clear();
        }
    }


    /**
     * 插入单个仓库的指标数据
     * @param repo
     * @param token
     * @throws IOException
     */
    private void insertOneRepoMetric(String repo, String token) throws IOException {
        String reg = "(?<=/)\\w+(?=\\.json)";
        String[] split = repo.split("/");
        String orgName = split[0];
        String repoName = split[1];
        List<String> urls = repoMetricService.getRepoMetricUrls(orgName, repoName);
        List<RepoMetric> repoMetricList = new ArrayList<>();
        for (String url : urls) {
            JSONObject jo = NetUtil.doGetWithToken(url, token);
            if (jo == null) {
                continue;
            }
            jo.forEach((key, value) -> {
                RepoMetric repoMetric = new RepoMetric();
                repoMetric.setOrgName(orgName);
                repoMetric.setRepoName(repoName);
                Date metricTime = DateUtil.parse(key);
                if (metricTime != null) {
                    repoMetric.setTMonth(metricTime);
                } else {
                    // 跳过本次循环
                    return;
                }
                repoMetric.setType(repoMetricService.getRepoMetricTypeFromUrl(url, reg));
                if (value instanceof Integer) {
                    repoMetric.setTValue(BigDecimal.valueOf((Integer) value).doubleValue());
                } else {
                    repoMetric.setTValue(((BigDecimal) value).doubleValue());
                }
                //RepoMetric repoMetric = new RepoMetric(orgName,repoName, DateUtil.parse(key), repoMetricService.getRepoMetricTypeFromUrl(url,reg), ((BigDecimal)value).doubleValue());
                repoMetricList.add(repoMetric);
            });
        }
        if (repoMetricList.size() == 0) {
            return;
        }
        repoMetricService.insertBatchRepoMetric(repoMetricList);
        log.info(Thread.currentThread().getName() + " 成功插入指标数据：" + orgName + "/" + repoName);
    }

    /**
     * 获取 token 列表
     * @return
     */
    public List<String> getTokens() {
        List<GitHubToken> tokens = gitHubApiConfig.getTokens();
        List<String> collect = tokens.stream().map(GitHubToken::getToken).collect(Collectors.toList());
        return collect;
    }
}
