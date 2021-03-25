package com.example.demo.src.store.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.Data;

import java.util.Date;

@Getter
@Setter

@Data
public class GetKakaoPayReadyRes {
    private String tid;
    //private String tms_result;
    //private String next_redirect_app_url;
    //private String next_redirect_mobile_url;
    private String next_redirect_pc_url;
    //private String android_app_scheme;
    //private String ios_app_scheme;
    private Date created_at;
    private int orderIdx;
}
