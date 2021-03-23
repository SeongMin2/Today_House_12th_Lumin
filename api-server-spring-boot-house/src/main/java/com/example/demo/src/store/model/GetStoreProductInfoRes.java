package com.example.demo.src.store.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter

public class GetStoreProductInfoRes {
    private int productIdx;
    private String scrapStatus;
    private String scrapCount;
    private String leftTime;  // F이면 그냥 오늘의 딜이 아닌 것임
    private String brandName;
    private String productName;
    private String percent;
    private String originalPrice;
    private String salePrice;
    private String specialStatus;
    private String save;
    private String deliveryCost;
    private String outDescription;
    private List<String> setOutDescription=new ArrayList<>();
    private String limitedArea;
    private String setProductStatus;
    private String starPoint;
    private String reviewNum;
    private String couponStatus;

    public GetStoreProductInfoRes(int productIdx,String scrapStatus,String scrapCount,String leftTime,String brandName,String productName,String percent,String originalPrice
            ,String salePrice,String specialStatus,String savePoint,String saveRate,String deliveryCost,String freeConditionPrice,String outDescription
            ,String limitedArea,String additionalCost,String setProductStatus,String starPoint,String reviewNum,String couponStatus){
        DecimalFormat formatter = new DecimalFormat("###,###");
        this.productIdx=productIdx;
        this.scrapStatus=scrapStatus;
        this.scrapCount=scrapCount;
        if(leftTime.equals("0")){ //오늘의 딜이 아닌경우
            this.leftTime="F";
            this.productName=productName;
        }
        else{ // 오늘의 딜인경우
            if(leftTime.length()<=2){
                this.leftTime=leftTime+"일 남음";
            }
            else{
                this.leftTime=leftTime+" 남음";
            }
            this.productName="[오늘의딜]"+productName;
        }
        this.brandName=brandName;
        this.percent=percent;
        this.originalPrice=originalPrice;
        if(setProductStatus.equals("T")){ // 세트 묶음 상품일 경우
            this.salePrice=salePrice+"원 외";
            this.save= "F";
            this.outDescription="F";
            this.setOutDescription.add("예외 상품이 있을 수 있습니다 (개별 상품정보 참고)");
        }
        else{ // 세트 상품 아닐 경우
            this.salePrice=salePrice+"원";
            this.save=savePoint+" 적립 (WELCOME "+saveRate+" 적립)";
            this.outDescription=outDescription;
        }
        this.specialStatus=specialStatus;

        if(deliveryCost.equals("0")){
            this.deliveryCost="무료배송";
        }
        else{
            this.deliveryCost=deliveryCost+"원 ("+freeConditionPrice+"원 이상 무료배송)";
        }

        if(limitedArea.equals("도서산간 지역 / 제주도")){
            if(setProductStatus.equals("T")){
                this.limitedArea="F";
                this.setOutDescription.add("제주도/도서산간 지역 배송 불가");
            }
            else{
                this.limitedArea="제주도/도서산간 "+additionalCost+"원";
            }

        }
        else{
            this.limitedArea="F";
        }
        this.setProductStatus=setProductStatus;
        this.starPoint=starPoint;
        this.reviewNum=reviewNum;
        if(couponStatus.equals("0")){
            this.couponStatus="F";
        }else{
            this.couponStatus="최대 "+couponStatus+"원 할인쿠폰";
        }

    }

}

/*
 rs.getInt("idx"),
                        rs.getString("scrapStatus"),
                        rs.getString("scrapCount"),
                        rs.getString("leftTime"),  // 이거 오늘의 딜인 경우 아닌경우해서 provider 에서 붙여줘야함
                        rs.getString("brandName"),
                        rs.getString("productName"),  // 이것도 오늘의 딜인 경우 아닌경우 해줘야함
                        rs.getString("percent")+"%",
                        formatter.format(rs.getInt("originalPrice")),
                        formatter.format(rs.getInt("salePrice")),   // 이것도 세트인 경우 아닌경우
                        rs.getString("specialStatus"),
                        rs.getString("savePoint"),     // 이것은 세트인지 아닌지에 따라 다르게 해줘야 함
                        rs.getString("saveRate"),      // 이것도 세트인지 아닌지에 따라
                        rs.getString("deliveryCost"),   // 이것은 0 인지 아닌지에 따라 무료배송 해줘야 함
                        rs.getString("freeConditionPrice"),   // 이것도 일부 처리해줘야함
                        rs.getString("outDescription"),
                        rs.getString("limitedArea"),
                        rs.getString("additionalCost"),
                        rs.getString("setProductStatus"),
                        rs.getString("starpoint"),
                        "("+rs.getString("revieweNum")+")",
                        rs.getString("productInfoImgUrl")
 */