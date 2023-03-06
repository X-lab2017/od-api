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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
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
import java.util.concurrent.CyclicBarrier;
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

    // 每月 3 日凌晨 1 点启动定时任务更新仓库的所有指标数据
    @Scheduled(cron = "0 0 1 3 * ?")
    public void insertAllRepoMetrics() throws IOException, BrokenBarrierException, InterruptedException {
        log.info("Repo Metrics 定时任务启动:" + new Date());
        log.info("清空 repo_metric 表");
        repoMetricService.truncateRepoMetric();
        Set<String> allRepo = getAllRepo();
        log.info("获取到的 repo 数量为: " + allRepo.size());
        List<String> tokens = getTokens();
        CyclicBarrier cyclicBarrier = new CyclicBarrier(allRepo.size());
        int repoCnt = 0;
        for (String repo : allRepo) {
            String token = tokens.get(repoCnt % tokens.size());
            Thread thread = new Thread(() -> {
                try {
                    insertOneRepoMetric(repo, token);
                } catch (IOException e) {
                    log.warn(e.getMessage(), e);
                } finally {
                    try {
                        cyclicBarrier.await();
                    } catch (Exception e) {
                        log.warn(e.getMessage(), e);
                    }
                }
            });
            thread.start();
            repoCnt++;
        }
        cyclicBarrier.await();
        //int cnt = 0;
        //String reg = "(?<=/)\\w+(?=\\.json)";
        //for (String repo : allRepo) {
        //    String[] split = repo.split("/");
        //    String orgName = split[0];
        //    String repoName = split[1];
        //    List<String> urls = repoMetricService.getRepoMetricUrls(orgName,repoName);
        //    List<RepoMetric> repoMetricList = new ArrayList<>();
        //    for (String url : urls) {
        //        JSONObject jo = NetUtil.doGet(url);
        //        if (jo == null) {
        //            continue;
        //        }
        //        jo.forEach((key, value) -> {
        //            RepoMetric repoMetric = new RepoMetric();
        //            repoMetric.setOrgName(orgName);
        //            repoMetric.setRepoName(repoName);
        //            Date metricTime = DateUtil.parse(key);
        //            if (metricTime != null) {
        //                repoMetric.setTMonth(metricTime);
        //            } else {
        //                // 跳过本次循环
        //                return;
        //            }
        //            repoMetric.setType(repoMetricService.getRepoMetricTypeFromUrl(url, reg));
        //            if (value instanceof Integer) {
        //                repoMetric.setTValue(BigDecimal.valueOf((Integer)value).doubleValue());
        //            } else {
        //                repoMetric.setTValue(((BigDecimal)value).doubleValue());
        //            }
        //            //RepoMetric repoMetric = new RepoMetric(orgName,repoName, DateUtil.parse(key), repoMetricService.getRepoMetricTypeFromUrl(url,reg), ((BigDecimal)value).doubleValue());
        //            repoMetricList.add(repoMetric);
        //        });
        //    }
        //    if (repoMetricList.size() == 0) {
        //        continue;
        //    }
        //    log.info("插入第 " + cnt + " 个 repo 的数据：" + repo);
        //    repoMetricService.insertBatchRepoMetric(repoMetricList);
        //    cnt++;
        //}
        log.info("定时任务完成:" + new Date());
    }

    volatile int cnt = 1;

    // 每日凌晨 5 点启动定时任务更新所有仓库的 star 和 fork 数据
    @Scheduled(cron = "0 0 5 * * ?")
    public void insertAllRepoStarAndFork() throws InterruptedException, BrokenBarrierException {
        log.info("Repo Statistic 定时任务启动:" + new Date());
        log.info("清空  repo_statistic 表");
        repoStatisticService.truncateRepoStatistic();
        List<Map<String, String>> repoInfo = repoMetricService.getRepoInfo();
        log.info(String.valueOf(repoInfo.size()));
        List<String> tokens = getTokens();
        CyclicBarrier cyclicBarrier = new CyclicBarrier(repoInfo.size());
        int totalRepo = 0;
        for (Map<String, String> map : repoInfo) {
            totalRepo++;
            List<RepoStatistic> list = new ArrayList<>();
            int finalTotalRepo = totalRepo;
            Thread thread = new Thread(() -> {
                try {
                    insertOneRepoStarAndFork(map, tokens.get(finalTotalRepo % tokens.size()), list);
                } catch (IOException e) {
                    log.warn(e.getMessage(), e);
                } finally {
                    try {
                        cyclicBarrier.await();
                    } catch (Exception e) {
                        log.info(e.getMessage(), e);
                    }
                }
            });
            thread.start();
        }
        cyclicBarrier.await();
        log.info("所有子线程执行完毕");
    }


    private void insertOneRepoStarAndFork(Map<String, String> map, String token, List<RepoStatistic> list) throws IOException {
        String orgName = map.get("orgName");
        String repoName = map.get("repoName");
        String url = " https://api.github.com/repos/" + orgName + "/" + repoName;
        log.info(url);
        JSONObject jsonObject = NetUtil.doGetWithToken(url, token);
        if (jsonObject == null) {
            return;
        }
        Integer star = jsonObject.getInteger("stargazers_count");
        Integer fork = jsonObject.getInteger("forks_count");
        RepoStatistic r1 = new RepoStatistic(orgName, repoName, "stargazers_count", star);
        RepoStatistic r2 = new RepoStatistic(orgName, repoName, "forks_count", fork);
        list.add(r1);
        list.add(r2);
        repoStatisticService.insertBatchRepoStatistic(list);
        log.info("插入第 " + cnt + " 个 repo 的数据：" + orgName + "/" + repoName);
        list.clear();
        cnt++;
    }


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
        log.info("插入第 " + cnt + " 个 repo 的数据：" + orgName + "/" + repoName);
        cnt++;
    }

    public List<String> getTokens() {
        List<GitHubToken> tokens = gitHubApiConfig.getTokens();
        List<String> collect = tokens.stream().map(GitHubToken::getToken).collect(Collectors.toList());
        return collect;
    }
}
