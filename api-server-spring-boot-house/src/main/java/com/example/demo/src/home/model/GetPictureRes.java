package com.example.demo.src.home.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetPictureRes {
    private int picturepostIdx;
    private int userIdx;
    private String userimageUrl;
    private String userName;
    private String comment;
    private String pictureUrl;
}
