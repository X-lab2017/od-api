package cn.nzcer.odapi.service.impl;

import cn.nzcer.odapi.entity.RepoStatistic;
import cn.nzcer.odapi.mapper.RepoStatisticMapper;
import cn.nzcer.odapi.service.RepoStatisticService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @project: od-api
 * @ClassName: RepoStatisticServiceImpl
 * @author: nzcer
 * @creat: 2023/2/6 23:34
 * @description:
 */
@Service
public class RepoStatisticServiceImpl implements RepoStatisticService {
    @Resource
    private RepoStatisticMapper repoStatisticMapper;

    @Override
    public int truncateRepoStatistic() {
        int res = repoStatisticMapper.truncateRepoStatistic();
        return res;
    }

    @Override
    public int insertBatchRepoStatistic(List<RepoStatistic> metrics) {
        int res = repoStatisticMapper.insertBatchRepoStatistic(metrics);
        return res;
    }
}
