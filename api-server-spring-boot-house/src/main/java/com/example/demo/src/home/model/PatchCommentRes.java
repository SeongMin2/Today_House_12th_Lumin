package com.example.demo.src.home.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class PatchCommentRes {
    private int userIdx;
    private int commentIdx;
    private String status;
    private String result;
    public PatchCommentRes(int userIdx,int commentIdx,String status){
        this.userIdx=userIdx;
        this.commentIdx=commentIdx;
        this.status=status;
        this.result="댓글이 삭제 되었습니다.";
    }
}