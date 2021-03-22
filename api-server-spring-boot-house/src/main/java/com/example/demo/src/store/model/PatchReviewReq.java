package com.example.demo.src.store.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PatchReviewReq {
    private int starPoint;
    private String imgUrl;  // 없으면 F로 넣어주기
    private String content;
}
