package com.example.demo.src.mypage.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetHeartPictureRes {
    private int heartIdx;
    private int pictureIdx;
    private String thumbnailImageUrl;
}