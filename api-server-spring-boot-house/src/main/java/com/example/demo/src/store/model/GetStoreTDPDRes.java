package com.example.demo.src.store.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class GetStoreTDPDRes {
    private int productIdx;
    private int evalableIdx;
    private String imgUrl;
    private String scrapStatus;
    private String leftTime;
    private String brandName;
    private String productName;
    private String discount;
    private String setProductStatus;
    private String price;
    private String starPoint;
    private String reviewNum;
    private String freeDeliveryStatus;
    public GetStoreTDPDRes(int productIdx, int evalableIdx,String imgUrl,String scrapStatus,String leftTime,String brandName,String productName,String discount,String setProductStatus,String price, String starPoint,String reviewNum,String freeDeliveryStatus){
        this.productIdx=productIdx;
        this.evalableIdx=evalableIdx;
        this.imgUrl=imgUrl;
        this.scrapStatus=scrapStatus;
        if(leftTime.length()<=2){
            this.leftTime=leftTime+"일";
        }
        else{
            this.leftTime=leftTime;
        }
        this.brandName=brandName;
        this.productName=productName;
        this.discount=discount;
        this.setProductStatus=setProductStatus;
        if(setProductStatus.equals("T")){
            this.price=price+" 외";
        }
        else{
            this.price=price;
        }
        this.starPoint=starPoint;
        this.reviewNum=reviewNum;
        this.freeDeliveryStatus=freeDeliveryStatus;
    }
}
/*
rs.getInt("idx"),
                        rs.getString("imgUrl"),
                        rs.getString("scrapStatus"),
                        rs.getString("leftTime")+"일",
                        rs.getString("brandName"),
                        "[오늘의딜]"+rs.getString("productName"),
                        rs.getString("percent")+"%",
                        rs.getString("setProductStatus"),
                        formatter.format(rs.getString("salePrice")),
                        rs.getString("starpoint"),
                        "리뷰 "+rs.getString("reviewNum"))
 */