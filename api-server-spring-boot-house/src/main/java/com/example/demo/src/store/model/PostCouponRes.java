package com.example.demo.src.store.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class PostCouponRes {
    private int couponStatusIdx;
    private String result = "쿠폰이 발급되었습니다.";
    public PostCouponRes(int couponStatusIdx){
        this.couponStatusIdx=couponStatusIdx;
    }
}
