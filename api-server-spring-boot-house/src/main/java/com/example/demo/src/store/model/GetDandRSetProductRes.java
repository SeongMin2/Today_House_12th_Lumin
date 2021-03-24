package com.example.demo.src.store.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetDandRSetProductRes {
    private int productIdx;
    private String selectOrder;
    private String imgUrl;
    private String productName;
}

/*
rs.getString("idx"),
                        rs.getString("selectOrder"),
                        rs.getString("imgUrl"),
                        rs.getString("productName")
 */