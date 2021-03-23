package com.example.demo.src.home.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetPictureReviewRes {
    private int commentIdx;
    private int picturepostIdx;
    private int userIdx;
    private String userimageUrl;
    private String userName;
    private String comment;
    private String howmuchTime;
}
