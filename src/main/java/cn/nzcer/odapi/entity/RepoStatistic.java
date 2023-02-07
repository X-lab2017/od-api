package cn.nzcer.odapi.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @project: od-api
 * @ClassName: RepoStatistic
 * @author: nzcer
 * @creat: 2023/2/6 23:25
 * @description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RepoStatistic {
    private String orgName;
    private String repoName;
    private String type;
    private Integer value;
}
