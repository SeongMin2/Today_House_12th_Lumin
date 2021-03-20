package com.example.demo.src.store.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetStoreSetProductRes {
    private int productIdx;
    private String selection;
    private String imgUrl;
    private String scrapStatus;
    private String brandName;
    private String productName;
    private String starPoint;
    private String reviewNum;
    private String percent;
    private String originalPrice;
    private String salePrice;
    private String specialStatus;
    private String freeDeliveryStatus;
}

/*
rs.getInt("idx"),
                        "선택 "+rs.getString("selectOrder"),
                        rs.getString("imgUrl"),
                        rs.getString("scrapStatus"),
                        rs.getString("brandName"),
                        rs.getString("productName"),
                        rs.getString("starpoint"),
                        "리뷰 "+rs.getString("reviewNum"),
                        rs.getString("percent")+"%",
                        formatter.format(rs.getInt("originalPrice")),
                        formatter.format(rs.getInt("salePrice")),
                        rs.getString("specialStatus"),
                        rs.getString("freeDeliveryStatus")
 */