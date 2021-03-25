package com.example.demo.src.store.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter

public class PostImmediatePReq {
    private List<Integer> optionIdx = new ArrayList<>();
    private int nothing = 0;
}
