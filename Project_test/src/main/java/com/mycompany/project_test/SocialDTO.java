package com.mycompany.project_test;

/**
 *
 * @author LeDinhTuan
 */
public class SocialDTO {

    private String url;
    private Integer count;

    public void setUrl(String url) {
        this.url = url;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getUrl() {
        return url;
    }

    public Integer getCount() {
        return count;
    }

    public SocialDTO() {
        this.url = "";
        this.count = 0;
    }
}
