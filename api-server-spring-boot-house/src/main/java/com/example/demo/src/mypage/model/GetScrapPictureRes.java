package com.example.demo.src.mypage.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetScrapPictureRes {
    private int scrapIdx;
    private int picturepostIdx;
    private String thumbnailImageUrl;
    private String comment;
}