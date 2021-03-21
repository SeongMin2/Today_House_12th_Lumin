package com.example.demo.src.store.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter

public class GetStoreProductRes {
    private List<String> productImg = new ArrayList<>();
    private GetStoreProductInfoRes productInfo;
    private List<GetStoreSetProductRes> setProduct = new ArrayList<>();
    private GetStoreProductStarRes starDistribution;
    private List<GetStoreProductReviewRes> review;
    public GetStoreProductRes(GetStoreProductInfoRes productInfo,GetStoreProductStarRes starDistribution,List<GetStoreProductReviewRes> review){
        this.productInfo=productInfo;
        this.starDistribution=starDistribution;
        this.review=review;
    }

}
