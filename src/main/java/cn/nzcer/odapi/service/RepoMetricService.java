package cn.nzcer.odapi.service;

import cn.nzcer.odapi.entity.RepoMetric;

import java.util.List;

/**
 * @project: od-api
 * @ClassName: RepoMetricService
 * @author: nzcer
 * @creat: 2022/12/22 13:51
 * @description:
 */
public interface RepoMetricService {
    int insertRepoMetric(RepoMetric metric);

    int truncateRepoMetric();

    /**
     * 批量插入数据
     * @param metrics
     * @return
     */
    int insertBatchRepoMetric(List<RepoMetric> metrics);

    /**
     * 获取仓库的所有指标url
     * @param repo
     * @return
     */
    List<String> getRepoMetricUrls(String repo);

    List<String> getRepoMetricUrls(String orgName, String repoName);

    String getRepoMetricTypeFromUrl(String url, String reg);
}
