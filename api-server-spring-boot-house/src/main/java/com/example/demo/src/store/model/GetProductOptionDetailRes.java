package com.example.demo.src.store.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetProductOptionDetailRes {
    private int detailidx;
    private int optionIdx;
    private int productIdx;
    private String name;
    private int priceForCalculate;
    private String price;
    private String status;  // 품절 여부
}

/*
rs.getInt("idx"),
                        rs.getInt("productIdx"),
                        rs.getString("name"),
                        rs.getString("status")
 */