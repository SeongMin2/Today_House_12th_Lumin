package com.example.demo.src.home.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class PatchHeartRes {
    private int heartIdx;
    private int userIdx;
    private int evalableIdx;
    private int contentIdx;
    private String status;
    private String result;
    public PatchHeartRes(int heartIdx,int userIdx,int evalableIdx,int contentIdx,String status){
        this.heartIdx=heartIdx;
        this.userIdx=userIdx;
        this.evalableIdx=evalableIdx;
        this.contentIdx=contentIdx;
        this.status=status;
        this.result="상태가 "+status+"로 변경되었습니다.";
    }
}