package cn.nzcer.odapi.mapper;

import cn.nzcer.odapi.entity.RepoMetric;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @project: od-api
 * @ClassName: RepoMetricMapper
 * @author: nzcer
 * @creat: 2022/12/22 13:43
 * @description:
 */
@Mapper
public interface RepoMetricMapper {
    int insertRepoMetric(RepoMetric repoMetric);

    // 清空表可以将数据表的自增id重置为1
    int truncateRepoMetric();

    // Mybatis 原生批量插入，提高效率
    int insertBatchRepoMetric(List<RepoMetric> list);
}
