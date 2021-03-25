package com.example.demo.src.store.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.text.DecimalFormat;

@Getter
@Setter

public class PostImmediatePRes {
    private int productIdx;
    private String productName;
    private String optionName;
    private String required;
    private String price;
    private int priceForCalculate;
    public PostImmediatePRes(int productIdx,String productName,String optionName,String required,String price, int priceForCalculate,String name){
        this.productIdx=productIdx;
        this.productName=productName;
        this.optionName = name+": "+optionName;
        this.required=required;
        this.price = price+"원";
        this.priceForCalculate=priceForCalculate;
    }
}
/*
rs.getInt("productIdx"),
                        rs.getString("productName"),
                        rs.getString("optionName"),
                        rs.getString("required"),
                        rs.getString("price"),
                        rs.getString("name")
 */
