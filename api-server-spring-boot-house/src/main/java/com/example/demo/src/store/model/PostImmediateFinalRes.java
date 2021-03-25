package com.example.demo.src.store.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PostImmediateFinalRes {
    private List<PostImmediatePRes> product;
    private String point;
    private int pointForCalculate;
}
