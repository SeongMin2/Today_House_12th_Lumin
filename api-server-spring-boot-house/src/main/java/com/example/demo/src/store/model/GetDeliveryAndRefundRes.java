package com.example.demo.src.store.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.text.DecimalFormat;
import java.util.List;

@Getter
@Setter

public class GetDeliveryAndRefundRes {
    private int idx;
    private String delivery;
    private String deliveryCost;
    private String payment;
    private String farRegionCost;
    private String limitedArea;
    private String refundCost;
    private String exchangeCost;
    private String address;
    private String businessName;
    private String representative;
    private String businessAddress;
    private String phoneNum;
    private String email;
    private String businessNum;
    public GetDeliveryAndRefundRes(int idx,String delivery,String deliveryCost,String freeConditionPrice,String payment,String additionalCost,String limitedArea,int refundCost,String exchangeCost,
                                   String address,String businessName,String representative,String businessAddress,String phoneNum,String email,String businessNum){
        DecimalFormat formatter = new DecimalFormat("###,###");
        this.idx=idx;
        this.delivery=delivery;
        if(deliveryCost.equals("0")){
            this.deliveryCost="무료배송";
        }else{
            this.deliveryCost=deliveryCost+"원 ("+freeConditionPrice+"원 이상 무료)";
        }
        this.payment =payment;
        if(additionalCost.equals("0")){
            this.farRegionCost="F";
        }else{
            this.farRegionCost=additionalCost+"원";
        }
        this.limitedArea=limitedArea;
        int a=refundCost*2;
        this.refundCost="편도 "+formatter.format(refundCost)+"원 (최초 배송비가 무료인 경우 "+formatter.format(a)+"원 부과)";
        this.exchangeCost=exchangeCost;
        this.address=address;
        this.businessName=businessName;
        this.representative=representative;
        this.businessAddress=businessAddress;
        this.phoneNum=phoneNum;
        this.email=email;
        this.businessNum=businessNum;
    }


}
/*
rs.getInt("idx"),
                        rs.getString("delivery"),
                        rs.getString("deliveryCost"),
                        rs.getString("freeConditionPrice"),
                        rs.getString("payment"),
                         rs.getString("additionalCost"),
                        rs.getString("limitedArea"),
                        rs.getString("refundCost"),
                        rs.getString("exchangeCost"),

                        rs.getString("address"),
                        rs.getString("businessName"),
                        rs.getString("representative"),
                        rs.getString("businessAddress"),
                        rs.getString("phoneNum"),
                        rs.getString("email"),
                        rs.getString("businessNum")
 */