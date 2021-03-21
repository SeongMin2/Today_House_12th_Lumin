package com.example.demo.src.scrap.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class PatchScrapRes {
    private int userIdx;
    private int contentIdx;
    private int evalableIdx;
    private String status;
    private String result;
    public PatchScrapRes(int userIdx,int contentIdx,int evalableIdx,String status){
        this.userIdx=userIdx;
        this.contentIdx=contentIdx;
        this.evalableIdx=evalableIdx;
        this.status=status;
        this.result="상태가 "+status+"로 변경되었습니다.";
    }
}