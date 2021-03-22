package com.example.demo.src.home.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter

public class GetPicturePostRes {
    private List<String> pictureUrl = new ArrayList<>();
    private String comment= new String();
    private List<GetPictureReviewRes> review = new ArrayList<>();
    public GetPicturePostRes(){
    }

}
