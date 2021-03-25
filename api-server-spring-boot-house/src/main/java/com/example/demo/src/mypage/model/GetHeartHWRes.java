package com.example.demo.src.mypage.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetHeartHWRes {
    private int heartIdx;
    private int HWIdx;
    private String thumbnailImageUrl;
    private String title;
    private String userName;
}