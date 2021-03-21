package com.example.demo.src.scrap.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class PatchScrapRes {
    private int userIdx;
    private int evalableIdx;
    private int contentIdx;
    private String status;
    private String result;
    public PatchScrapRes(int userIdx,int evalableIdx,int contentIdx,String status){
        this.userIdx=userIdx;
        this.evalableIdx=evalableIdx;
        this.contentIdx=contentIdx;
        this.status=status;
        this.result="상태가 "+status+"로 변경되었습니다.";
    }
}