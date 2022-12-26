package cn.nzcer.odapi.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @project: od-api
 * @ClassName: Metric
 * @author: nzcer
 * @creat: 2022/12/21 18:11
 * @description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RepoMetric {
    private String orgName;
    private String repoName;
    private Date tMonth;
    private String type;
    private Double tValue;
}
