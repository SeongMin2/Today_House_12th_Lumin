package com.example.demo.src.store.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetStoreHomeRes {
    private List<GetStoreAdRes> storeAdvertisement;
    private List<GetStoreCategoryRes> storeCategory;
    private List<GetStoreTDPDRes> todayDeal;
    private List<GetStorePopPDRes> popular;
}
