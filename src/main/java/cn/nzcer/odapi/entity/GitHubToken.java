package cn.nzcer.odapi.entity;

import lombok.Data;

/**
 * @project: od-api
 * @ClassName: GitHubToken
 * @author: nzcer
 * @creat: 2023/3/4 11:10
 * @description: GitHub token 类
 */
@Data
public class GitHubToken {
    /**
     * 序列号
     */
    private int id;
    /**
     * token 提供者
     */
    private String owner;

    /**
     * token 值
     */
    private String token;
}
