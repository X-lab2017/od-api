package cn.nzcer.odapi.util;

/**
 * @project: od-api
 * @ClassName: StringUtil
 * @author: nzcer
 * @creat: 2022/12/1 16:16
 */
public class StringUtil {
    private static final String urlPrefix = "https://oss.x-lab.info/open_digger/github/";

    public static String getRepoUrl(String orgName, String repoName, String metricName) {
        StringBuilder sb = new StringBuilder(urlPrefix);
        sb.append(orgName);
        sb.append("/");
        sb.append(repoName);
        sb.append("/");
        sb.append(metricName);
        sb.append(".json");
        return sb.toString();
    }

    public static String getUserUrl(String userName, String metricName) {
        StringBuilder sb = new StringBuilder(urlPrefix);
        sb.append(userName);
        sb.append("/");
        sb.append(metricName);
        sb.append(".json");
        return sb.toString();
    }

}
