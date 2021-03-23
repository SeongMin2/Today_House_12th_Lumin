package com.example.demo.src.store.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class GetProductCouponRes {
    private int couponIdx;
    private String discount;
    private String couponName;
    private String condition;
    private String receivable;
    public GetProductCouponRes(int couponIdx,int discount,String couponName,String maxPrice,String leftDay,String receivable){
        this.couponIdx=couponIdx;
        if(discount>=100){
            this.discount="￦"+discount;
        }
        else{
            this.discount=discount+"%";
        }
        this.couponName=couponName;
        this.condition=maxPrice+"·"+leftDay;
        this.receivable=receivable;
    }
}
/*
rs.getInt("idx"),
                        rs.getString("saleRange"),
                        rs.getString("name"),
                        formatter.format(rs.getInt("maxPrice"))+"원 이상 구매시",
                        rs.getString("leftDay")+"일 남음")
 */