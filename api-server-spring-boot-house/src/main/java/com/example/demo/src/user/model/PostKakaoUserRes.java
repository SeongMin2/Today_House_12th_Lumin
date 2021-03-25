package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class PostKakaoUserRes {
    private int userIdx;
    private String jwt;
    private String status="카카오 계정으로 가입 완료되었습니다.";
    public PostKakaoUserRes(int userIdx,String jwt){
        this.userIdx=userIdx;
        this.jwt=jwt;
    }
}
