package com.example.demo.src.store.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostReviewRes {
    private int reviewIdx;
    private String status="리뷰가 등록이 완료되었습니다.";
    public PostReviewRes(int reviewIdx){
        this.reviewIdx=reviewIdx;
    }
}
