package com.example.demo.src.store.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class GetReviewPageRes {
    private int userIdx;
    private int productIdx;
    private String name;
    private String imgUrl;
    private String givenPoint="포토250P";
    public GetReviewPageRes(int productIdx,String brandName,String todayDeal,String name,String imgUrl){
        this.productIdx=productIdx;
        if(todayDeal.equals("T")){
            this.name=brandName+" [오늘의딜] "+name;
        }
        else{
            this.name=brandName+" "+name;
        }
        this.name=name;
        this.imgUrl=imgUrl;
    }

}
/*
 rs.getInt("idx"),
                        rs.getString("brandName"),
                        rs.getString("todayDeal"),
                        rs.getString("name"),
                        rs.getString("imgUrl")
 */