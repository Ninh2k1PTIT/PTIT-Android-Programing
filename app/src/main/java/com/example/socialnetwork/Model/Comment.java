package com.example.socialnetwork.Model;

import java.util.Date;

public class Comment {
    private Integer id;
    private String content;
    private Integer totalReact;
    private Boolean isReact;
    private Date createdAt;
    private Date updatedAt;
    private User user;
    private Post post;

    public Comment() {
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

    public Integer getTotalReact() {
        return totalReact;
    }

    public void setTotalReact(Integer totalReact) {
        this.totalReact = totalReact;
    }

    public Boolean getReact() {
        return isReact;
    }

    public void setReact(Boolean react) {
        isReact = react;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public User getUser() {
        return user;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }
}
