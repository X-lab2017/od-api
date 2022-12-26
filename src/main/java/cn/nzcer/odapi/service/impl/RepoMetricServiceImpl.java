package cn.nzcer.odapi.service.impl;

import cn.nzcer.odapi.entity.RepoMetric;
import cn.nzcer.odapi.enums.RepoMetricEnum;
import cn.nzcer.odapi.mapper.RepoMetricMapper;
import cn.nzcer.odapi.service.RepoMetricService;
import cn.nzcer.odapi.util.StringUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @project: od-api
 * @ClassName: RepoMetricServiceImpl
 * @author: nzcer
 * @creat: 2022/12/22 13:51
 * @description:
 */
@Service
public class RepoMetricServiceImpl implements RepoMetricService {
    @Resource
    private RepoMetricMapper repoMetricMapper;
    @Override
    public int insertRepoMetric(RepoMetric repoMetric) {
        int res = repoMetricMapper.insertRepoMetric(repoMetric);
        return res;
    }

    @Override
    public int insertBatchRepoMetric(List<RepoMetric> metrics) {
        int res = repoMetricMapper.insertBatchRepoMetric(metrics);
        return res;
    }

    @Override
    public int truncateRepoMetric() {
        int res = repoMetricMapper.truncateRepoMetric();
        return res;
    }

    @Override
    public List<String> getRepoMetricUrls(String repo) {
        String[] strs = repo.split("/");
        return this.getRepoMetricUrls(strs[0], strs[1]);
    }

    @Override
    public List<String> getRepoMetricUrls(String orgName, String repoName) {
        List<String> urls = new ArrayList<>();
        Arrays.stream(RepoMetricEnum.values()).forEach(repoMetricEnum -> {
            urls.add(StringUtil.getRepoUrl(orgName, repoName, repoMetricEnum.getMetricName()));
        });
        return urls;
    }

    @Override
    public String getRepoMetricTypeFromUrl(String url, String reg) {
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }
}
