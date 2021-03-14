package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostUserReq {
    private String emailId;
    private String password;
    private String passwordCheck;
    private String nickName;
    private String mandatoryConsent;
    private String optionalConsent;

}
