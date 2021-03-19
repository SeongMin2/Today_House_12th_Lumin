package com.example.demo.src.store.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetStoreCategoryRes {
    private int pageIdx;
    private String categoryName;
    private String imageUrl;
    private String represented;

}

/*
rs.getInt("idx"),
                        rs.getString("name"),
                        rs.getString("imageUrl"),
                        rs.getString("categoryOut")
 */