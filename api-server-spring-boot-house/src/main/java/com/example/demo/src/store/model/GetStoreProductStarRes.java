package com.example.demo.src.store.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetStoreProductStarRes {
    private int productIdx;
    private String fiveNum;
    private String fourNum;
    private String threeNum;
    private String twoNum;
    private String oneNum;
}

/*
rs.getInt("idx"),
                        rs.getString("fiveNum"),
                        rs.getString("fourNum"),
                        rs.getString("threeNum"),
                        rs.getString("twoNum"),
                        rs.getString("oneNum"))
 */