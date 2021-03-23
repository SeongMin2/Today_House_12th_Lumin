package com.example.demo.src.home.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetHousewarmingRes {
   private String thumbnailImageUrl;
   private int scrapCount;
   private String scrapStaus;
   private int userIdx;
   private String userName;
   private String title;
   private String newContent;
   private String userimageUrl;
}
