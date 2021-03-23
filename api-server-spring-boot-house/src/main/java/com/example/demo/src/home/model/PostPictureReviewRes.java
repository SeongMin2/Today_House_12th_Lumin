package com.example.demo.src.home.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostPictureReviewRes {
    private int reviewIdx;
    private String status="댓글 등록 성공!";
    public PostPictureReviewRes(int reviewIdx){
        this.reviewIdx=reviewIdx;
    }
}
