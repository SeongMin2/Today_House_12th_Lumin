package com.example.demo.src.mypage.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter

public class GetMypageProfileRes {
    private GetMypageUserRes user;
    private GetMypagePictureRes picture;
    public GetMypageProfileRes(GetMypageUserRes user ,GetMypagePictureRes picture){
        this.user=user;
        this.picture=picture;
    }
}
