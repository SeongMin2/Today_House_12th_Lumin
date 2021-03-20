package com.example.demo.src.store.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter

public class GetStoreHomeRes {
    //private List<GetStoreAdRes> storeAdvertisement;
    private List<String> storeAdvertisement= new ArrayList<>();
    private List<GetStoreCategoryRes> storeCategory;
    private List<GetStoreTDPDRes> todayDeal;
    private List<GetStorePopPDRes> popular;
    public GetStoreHomeRes(List<GetStoreCategoryRes> storeCategory,List<GetStoreTDPDRes> todayDeal, List<GetStorePopPDRes> popular){
        this.storeCategory=storeCategory;
        this.todayDeal=todayDeal;
        this.popular=popular;
    }
}
