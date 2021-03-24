package com.example.demo.src.store.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter

public class GetDandRFinalRes {
    private List<GetDandRSetProductRes> setProduct = new ArrayList<>();
    private List<GetDeliveryAndRefundRes> deliveryAndRefund = new ArrayList<>();
}
