package com.example.demo.src.home.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter

public class GetHomeMainRes {
    private List<GetHousewarmingRes> todayStory;
    private List<GetPictureRes> todayPicture;
    public GetHomeMainRes(List<GetHousewarmingRes> todayStory,List<GetPictureRes> todayPicture){
        this.todayStory=todayStory;
        this.todayPicture=todayPicture;
    }
}
