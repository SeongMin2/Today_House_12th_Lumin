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
    private List<GetStoreProductReviewRes> review = new ArrayList<>();
    private List<GetStoreSetProductReviewRes> setProductReview =new ArrayList<>();
    public GetStoreProductRes(GetStoreProductInfoRes productInfo,GetStoreProductStarRes starDistribution){
        this.productInfo=productInfo;
        this.starDistribution=starDistribution;
    }

}
