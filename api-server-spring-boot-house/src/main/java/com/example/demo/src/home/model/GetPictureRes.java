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
    private int scrapCount;
    private String scrapStaus;
    private int heartCount;
    private String heartStatus;
    private int reviewCount;
    private String userimageUrl;
    private String userName;
    private String comment;
    private String pictureUrl;
}

/*
//코트님 미반영코드
private int picturepostIdx;
    private int userIdx;
    private int scrapCount;
    private String scrapStaus;
    private String userimageUrl;
    private String userName;
    private String comment;
    private String pictureUrl;
 */

/*
rs.getInt("picturepostIdx"),
                        rs.getInt("userIdx"),
                        rs.getInt("scrapCount"),
                        rs.getString("scrapStatus"),
                        rs.getInt("heartCount"),
                        rs.getString("heartStatus"),
                        rs.getInt("reviewCount"),
                        rs.getString("userimageUrl"),
                        rs.getString("userName"),
                        rs.getString("comment"),
                        rs.getString("pictureUrl")),
 */