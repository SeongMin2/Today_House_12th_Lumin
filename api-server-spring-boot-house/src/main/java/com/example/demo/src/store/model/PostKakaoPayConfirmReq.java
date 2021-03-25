package com.example.demo.src.store.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PostKakaoPayConfirmReq {
    private String tid;
    private String pg_token;
    private int orderIdx;
}
