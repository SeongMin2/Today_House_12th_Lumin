package com.example.demo.src.store.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetOptionSetProductRes {
    private int productIdx;
    private String order;
    private String imgUrl;
    private String brandName;
    private String productName;
    private String percent;
    private String salePrice;
    private String specialStatus;
    private String freeDeliveryStatus;
}

/*
rs.getInt("idx"),
                        rs.getString("selectOrder"),
                        rs.getString("imgUrl"),
                        rs.getString("brandName"),
                        rs.getString("productName"),
                        rs.getString("percent")+"%",
                        formatter.format(rs.getInt("salePrice")),
                        rs.getString("specialStatus"),
                        rs.getString("freeDeliveryStatus")
 */