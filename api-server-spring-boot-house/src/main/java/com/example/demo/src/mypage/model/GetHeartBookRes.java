package com.example.demo.src.mypage.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter

public class GetHeartBookRes {
    private int heartHWCount;
    private List<GetHeartHWRes> housewarming;
    private int heartPictureCount;
    private List<GetHeartPictureRes> picture;
    public GetHeartBookRes(int heartPictureCount,List<GetHeartPictureRes> picture,int heartHWCount,List<GetHeartHWRes> housewarming ){
        this.heartPictureCount=heartPictureCount;
        this.picture=picture;
        this.heartHWCount=heartHWCount;
        this.housewarming=housewarming;
    }
}