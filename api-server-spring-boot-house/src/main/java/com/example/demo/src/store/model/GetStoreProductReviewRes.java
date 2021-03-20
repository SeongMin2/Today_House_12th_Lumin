package com.example.demo.src.store.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class GetStoreProductReviewRes {
    private int reviewIdx;
    private int userIdx;
    private String userImageUrl;
    private String userName;
    private String starPoint;
    private String dayAndStatus;
    private String imgUrl;
    private String content;
    public GetStoreProductReviewRes(int reviewIdx,int userIdx,String userImageUrl,String userName,String starPoint,String createdAt,String fromLocal,String imgUrl,String content){
        this.reviewIdx=reviewIdx;
        this.userIdx=userIdx;
        this.userImageUrl=userImageUrl;
        this.userName=userName;
        this.starPoint=starPoint;

        if(fromLocal.equals("T")){
            this.dayAndStatus= createdAt+"·"+"오늘의집 구매";
        }
        else{
            this.dayAndStatus=createdAt+"·"+"다른 쇼핑몰 구매";
        }
        this.imgUrl=imgUrl;
        this.content=content;
    }
}

/*
rs.getInt("idx"),
                        rs.getInt("userIdx"),
                        rs.getString("userimageUrl"),
                        rs.getString("userName"),
                        rs.getString("starPoint"),
                        rs.getString("createdAt"),
                        rs.getString("fromLocal"),
                        rs.getString("imgUrl"),
                        rs.getString("content")
 */