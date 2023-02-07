package cn.nzcer.odapi.mapper;

import cn.nzcer.odapi.entity.RepoMetric;
import cn.nzcer.odapi.entity.RepoStatistic;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @project: od-api
 * @ClassName: RepoStatisticMapper
 * @author: nzcer
 * @creat: 2023/2/6 23:26
 * @description:
 */
@Mapper
public interface RepoStatisticMapper {
    int insertBatchRepoStatistic(List<RepoStatistic> list);
    // 清空表可以将数据表的自增id重置为1
    int truncateRepoStatistic();
}
