package com.example.socialnetwork.Model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Post {
    private Integer id;
    private String content;
    private Date createdAt;
    private Date updatedAt;
    private EAudience audience;
    private User user;
    private List<Photo> photos = new ArrayList<>();
    private Integer totalReact;
    private Integer totalComment;
    private Boolean isReact;

    public Post() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public EAudience getAudience() {
        return audience;
    }

    public void setAudience(EAudience audience) {
        this.audience = audience;
    }

    public User getUser() {
        return user;
    }

    public Integer getTotalReact() {
        return totalReact;
    }

    public void setTotalReact(Integer totalReact) {
        this.totalReact = totalReact;
    }

    public Integer getTotalComment() {
        return totalComment;
    }

    public void setTotalComment(Integer totalComment) {
        this.totalComment = totalComment;
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public Boolean getReact() {
        return isReact;
    }

    public void setReact(Boolean react) {
        isReact = react;
    }
}
