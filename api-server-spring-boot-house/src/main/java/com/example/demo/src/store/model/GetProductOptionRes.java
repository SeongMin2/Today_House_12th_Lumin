package com.example.demo.src.store.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter

public class GetProductOptionRes {
    private int optionIdx;
    private int productIdx;
    private String name;
    private String required;
    private List<GetProductOptionDetailRes> optionDetail = new ArrayList<>();
    public GetProductOptionRes(int optionIdx,int productIdx,String name,String required){
        this.optionIdx=optionIdx;
        this.productIdx=productIdx;
        this.name=name;
        this.required=required;
    }
}

/*
rs.getInt("idx"),
                        rs.getInt("productIdx"),
                        rs.getString("name"),
                        rs.getString("required")
 */