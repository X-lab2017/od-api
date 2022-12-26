package cn.nzcer.odapi.enums;

/**
 * @project: od-api
 * @ClassName: RepoMetricEnum
 * @author: nzcer
 * @creat: 2022/12/23 10:21
 * @description:
 */
public enum RepoMetricEnum {
    ACTIVITY("activity", "活跃度"),
    OPEN_RANK("openrank", "影响力"),
    ATTENTION("attention", "关注度"),
    TECHNICAL_FORK("technical_fork", "fork 新增量"),
    STARS("stars", "star 新增量"),
    ISSUES_NEW("issues_new", "新增打开的 issue 数量"),
    ISSUES_CLOSED("issues_closed", "新增关闭的 issue 数量"),
    CODE_CHANGE_LINES_ADD("code_change_lines_add", "代码新增行数"),
    CODE_CHANGE_LINES_REMOVE("code_change_lines_remove", "代码删除行数"),
    CODE_CHANGE_LINES_SUM("code_change_lines_sum", "代码变更总行数"),
    CHANGE_REQUESTS("change_requests", "新增打开的 PR 数量"),
    CHANGE_REQUESTS_ACCEPTED("change_requests_accepted", "新增合并的 PR 数量"),
    CHANGE_REQUESTS_REVIEWS("change_requests_reviews", "新增的 PR 评论数量"),
    ISSUE_COMMENTS("issue_comments", "新增的 issue 评论数量"),
    PARTICIPANTS("participants", "新增的项目参与者数量"),
    BUS_FACTOR("bus_factor", "巴士系数"),
    ;
    private final String metricName;
    private final String description;

    RepoMetricEnum(String metricName, String description) {
        this.metricName = metricName;
        this.description = description;
    }

    public String getMetricName() {
        return metricName;
    }

    public String getDescription() {
        return description;
    }
}
