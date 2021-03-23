package com.example.demo.src.home.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class PatchPictureRes {
    private int userIdx;
    private int picturepostIdx;
    private String status;
    private String result;
    public PatchPictureRes(int userIdx,int picturepostIdx,String status){
        this.userIdx=userIdx;
        this.picturepostIdx=picturepostIdx;
        this.status=status;
        this.result="게시글이 삭제 되었습니다.";
    }
}