package com.example.demo.src.store.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class PatchHelpfulRes {
    private int userIdx;
    private int reviewIdx;
    private String status;
    private String result;
    public PatchHelpfulRes(int userIdx,int reviewIdx,String status){
        this.userIdx=userIdx;
        this.reviewIdx=reviewIdx;
        this.status=status;
        this.result="상태가 "+status+"로 변경되었습니다.";
    }
}

/*
rs.getInt("userIdx"),
                        rs.getInt("reviewIdx"),
                        rs.getString("status")),
 */