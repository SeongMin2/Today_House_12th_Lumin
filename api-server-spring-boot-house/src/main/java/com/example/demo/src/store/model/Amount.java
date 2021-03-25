package com.example.demo.src.store.model;

import java.util.Date;

import lombok.Data;

@Data
public class Amount {
    private String total;
    private String tax_free;
    private String vat;
    private String point;
    //private String discount;
}
