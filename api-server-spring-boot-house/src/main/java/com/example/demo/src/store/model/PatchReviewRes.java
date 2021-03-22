package com.example.demo.src.store.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class PatchReviewRes {
    private int reviewIdx;
    private String result="수정이 완료되었습니다.";
    public PatchReviewRes(int reviewIdx){
        this.reviewIdx=reviewIdx;
    }

}
