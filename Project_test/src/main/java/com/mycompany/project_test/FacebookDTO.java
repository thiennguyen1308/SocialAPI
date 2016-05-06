package com.mycompany.project_test;

/**
 *
 * @author LeDinhTuan
 */
public class FacebookDTO {

    private String url;
    private Integer share;
    private Integer like;
    private Integer comment;
    private Integer click;

    public void setUrl(String url) {
        this.url = url;
    }

    public void setShare(Integer share) {
        this.share = share;
    }

    public void setLike(Integer like) {
        this.like = like;
    }

    public void setComment(Integer comment) {
        this.comment = comment;
    }

    public void setClick(Integer click) {
        this.click = click;
    }

    public String getUrl() {
        return url;
    }

    public Integer getShare() {
        return share;
    }

    public Integer getLike() {
        return like;
    }

    public Integer getComment() {
        return comment;
    }

    public Integer getClick() {
        return click;
    }

    public FacebookDTO() {
        this.url = "";
        this.share = 0;
        this.like = 0;
        this.comment = 0;
        this.click = 0;
    }
}
