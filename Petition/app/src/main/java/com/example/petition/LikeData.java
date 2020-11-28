package com.example.petition;

public class LikeData {
    private String id;
    private String category;
    private String title;
    private Integer agree;
    private Integer liked;


    public LikeData(String id, String category, String title, Integer agree, Integer liked) {
        super();
        this.id = id;
        this.category = category;
        this.title = title;
        this.agree = agree;
        this.liked = liked;
    }

    // GET 함수
    public String getID() {
        return id;
    }
    public String getCategory() {
        return category;
    }
    public String getTitle() {
        return title;
    }
    public Integer getAgree() {
        return agree;
    }
    public Integer getLiked() {
        return liked;
    }
}
