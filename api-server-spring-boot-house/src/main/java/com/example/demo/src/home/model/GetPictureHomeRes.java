package com.example.demo.src.home.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter

public class GetPictureHomeRes {
    private List<GetPictureRes> homeinfo = new ArrayList<>();
    private List<String> pictureUrl = new ArrayList<>();
    public GetPictureHomeRes(){
    }

}