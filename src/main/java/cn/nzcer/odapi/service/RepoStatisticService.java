package cn.nzcer.odapi.service;

import cn.nzcer.odapi.entity.RepoMetric;
import cn.nzcer.odapi.entity.RepoStatistic;

import java.util.List;

/**
 * @project: od-api
 * @ClassName: RepoStatisticService
 * @author: nzcer
 * @creat: 2023/2/6 23:34
 * @description:
 */
public interface RepoStatisticService {
    int truncateRepoStatistic();

    int insertBatchRepoStatistic(List<RepoStatistic> metrics);
}
