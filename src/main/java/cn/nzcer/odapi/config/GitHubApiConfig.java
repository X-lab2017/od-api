package cn.nzcer.odapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @project: od-api
 * @ClassName: GitHubAPIConfig
 * @author: nzcer
 * @creat: 2023/2/14 19:51
 * @description: GitHub API 配置类
 */
@Component
@ConfigurationProperties(prefix = "github")
public class GitHubApiConfig {
    @Value("${github.token}")
    public static String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        GitHubApiConfig.token = token;
    }
}
