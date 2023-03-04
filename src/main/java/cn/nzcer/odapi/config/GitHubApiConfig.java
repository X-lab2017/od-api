package cn.nzcer.odapi.config;

import cn.nzcer.odapi.entity.GitHubToken;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @project: od-api
 * @ClassName: GitHubAPIConfig
 * @author: nzcer
 * @creat: 2023/2/14 19:51
 * @description: GitHub API 配置类
 */
@Component
@Data
@ConfigurationProperties(prefix = "github")
public class GitHubApiConfig {
    private List<GitHubToken> tokens;
}
