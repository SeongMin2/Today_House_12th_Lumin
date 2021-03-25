package com.example.demo.src.store.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PostKakaoPayReadyReq {
    private String orderer;
    private String ordererEmail;
    private String ordererPhoneNum;
    private String receiver;
    private String receiverPhoneNum;
    private String address;
    private String payment;
    private String totalPrice;
    private List<Integer> detailOption;
    private String number;
}
