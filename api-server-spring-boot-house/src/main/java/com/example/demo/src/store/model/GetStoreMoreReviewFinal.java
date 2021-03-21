package com.example.demo.src.store.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter

public class GetStoreMoreReviewFinal {
    private String reviewNum;
    private String starPoint;
    private GetStoreProductStarRes starDistribution;
    private List<GetMoreReviewRes> review=new ArrayList<>();
    private List<GetMoreSetReviewRes> setProductReview = new ArrayList<>();
    public GetStoreMoreReviewFinal(String reviewNum,String starPoint){
        this.reviewNum=reviewNum;
        this.starPoint=starPoint;
    }
}
