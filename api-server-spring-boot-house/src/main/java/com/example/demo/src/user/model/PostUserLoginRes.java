package com.example.demo.src.user.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class PostUserLoginRes {
    private String jwt;
    private int userIdx;
    private String status = "로그인 성공!";

    public PostUserLoginRes(String jwt,int userIdx){
        this.jwt=jwt;
        this.userIdx=userIdx;
    }
}