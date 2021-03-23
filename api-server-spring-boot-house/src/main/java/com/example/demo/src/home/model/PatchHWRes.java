package com.example.demo.src.home.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class PatchHWRes {
    private int userIdx;
    private int hwIdx;
    private String status;
    private String result;
    public PatchHWRes(int userIdx,int hwIdx,String status){
        this.userIdx=userIdx;
        this.hwIdx=hwIdx;
        this.status=status;
        this.result="게시글이 삭제 되었습니다.";
    }
}