package com.example.demo.src.store.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetStoreProductImgRes {
    private int idx;
    private String imgUrl;
}

/*
rs.getInt("idx"),
                        rs.getString("imgUrl")
 */