package com.example.demo.src.store.model;

import lombok.Getter;
import lombok.Setter;
import lombok.Data;

import java.util.Date;

@Getter
@Setter

@Data
public class PostKakaoPayConfirmRes {
    private String aid;
    private String tid;
    private String cid;
    private String partner_order_id;
    private String partner_user_id;
    private String payment_method_type;
    private String item_name;
    private String quantity;
    private Amount amount;
    private String created_at;
    private String approved_at;
}
