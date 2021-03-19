package com.example.demo.src.store.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class GetStorePopPDRes {
    private int productIdx;
    private String imgUrl;
    private String scrapStatus;
    private String brandName;
    private String productName;
    private String discount;
    private String setProductStatus;
    private String price;
    private String starPoint;
    private String reviewNum;
    private String specialPriceStatus;
    public GetStorePopPDRes(int productIdx,String imgUrl,String scrapStatus,String brandName,String productName,String discount,String setProductStatus,String price, String starPoint,String reviewNum,String specialPriceStatus){
        this.productIdx=productIdx;
        this.imgUrl=imgUrl;
        this.scrapStatus=scrapStatus;
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
        this.specialPriceStatus=specialPriceStatus;
    }
}

/*
rs.getInt("idx"),
                        rs.getString("imgUrl"),
                        rs.getString("scrapStatus"),
                        rs.getString("brandName"),
                        "[오늘의딜]"+rs.getString("productName"),
                        rs.getString("percent")+"%",
                        rs.getString("setProductStatus"),
                        formatter.format(rs.getString("salePrice")),
                        rs.getString("starpoint"),
                        "리뷰 "+rs.getString("reviewNum"))
 */