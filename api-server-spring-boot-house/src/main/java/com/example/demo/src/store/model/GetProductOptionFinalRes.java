package com.example.demo.src.store.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter

public class GetProductOptionFinalRes {
    private List<GetOptionSetProductRes> setProduct = new ArrayList<>();
    private List<GetProductOptionRes> option = new ArrayList<>();

}
