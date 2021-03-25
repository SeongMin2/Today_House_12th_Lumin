package com.example.demo.src.mypage.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter

public class GetMypagePictureRes {
    private int countPicture;
    private List<GetPictureDetailRes> pictureDetail= new ArrayList<>();
    public GetMypagePictureRes(int countPicture,List<GetPictureDetailRes> pictureDetail){
        this.countPicture=countPicture;
        this.pictureDetail=pictureDetail;

    }
}