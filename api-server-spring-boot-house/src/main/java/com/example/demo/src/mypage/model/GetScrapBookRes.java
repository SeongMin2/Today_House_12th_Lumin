package com.example.demo.src.mypage.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter

public class GetScrapBookRes {
    private int scrapHWCount;
    private List<GetScrapHWRes> housewarming;
    private int scrapPictureCount;
    private List<GetScrapPictureRes> picture;
    public GetScrapBookRes(int scrapPictureCount,List<GetScrapPictureRes> picture,int scrapHWCount,List<GetScrapHWRes> housewarming ){
        this.scrapPictureCount=scrapPictureCount;
        this.picture=picture;
        this.scrapHWCount=scrapHWCount;
        this.housewarming=housewarming;
    }
}
