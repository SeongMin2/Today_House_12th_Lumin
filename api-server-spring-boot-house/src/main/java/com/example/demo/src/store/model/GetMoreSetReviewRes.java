package com.example.demo.src.store.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetMoreSetReviewRes {
    private int reviewIdx;
    private int userIdx;
    private int productIdx;
    private String productName;
    private String userImageUrl;
    private String userName;
    private String starPoint;
    private String dayAndStatus;
    private String imgUrl;
    private String content;
    private String helpfulNum;   // 0명이면 F
    private String helpfulStatus;
    public GetMoreSetReviewRes(int reviewIdx,int userIdx,int productIdx,String selectOrder,String productName,String userImageUrl,String userName,String starPoint,String createdAt,String fromLocal,String imgUrl,String content,String helpful,String helpfulStatus){
        this.reviewIdx=reviewIdx;
        this.userIdx=userIdx;
        this.productIdx=productIdx;
        this.productName=selectOrder+productName;
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
        if(helpful.equals("0")){
            this.helpfulNum="F";
        }
        else{
            this.helpfulNum = helpful+"명에게 도움이 되었습니다.";
        }
        this.helpfulStatus=helpfulStatus;
    }
}

/*
rs.getInt("idx"),
                        rs.getInt("userIdx"),
                        rs.getInt("productIdx"),
                        rs.getInt("selectOrder"),
                        rs.getString("name"),
                        rs.getString("userimageUrl"),
                        rs.getString("userName"),
                        rs.getString("starPoint"),
                        rs.getString("createdAt"),
                        rs.getString("fromLocal"),
                        rs.getString("imgUrl"),
                        rs.getString("content"),
                        rs.getString("helpful"),
                        rs.getString("helpfulStatus")
 */