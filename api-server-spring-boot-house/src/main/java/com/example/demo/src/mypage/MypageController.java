package com.example.demo.src.mypage;

import com.fasterxml.jackson.databind.ser.Serializers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.mypage.model.*;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.isRegexEmail;

@RestController
@RequestMapping("/app/mypage")
public class MypageController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final MypageProvider mypageProvider;
    @Autowired
    private final MypageService mypageService;
    @Autowired
    private final JwtService jwtService;


    public MypageController(MypageProvider mypageProvider, MypageService mypageService, JwtService jwtService) {
        this.mypageProvider = mypageProvider;
        this.mypageService = mypageService;
        this.jwtService = jwtService;
    }

    @ResponseBody
    @GetMapping("")
    public BaseResponse<GetMypageProfileRes> getMypageProfile() throws BaseException {
        try {
            if (jwtService.getJwt() == null) {
                return new BaseResponse<>(EMPTY_JWT);
            } else {
                int userIdx = jwtService.getUserIdx();
                GetMypageProfileRes getMypageProfileRes = mypageProvider.getMypageProfile(userIdx);
                return new BaseResponse<>(getMypageProfileRes);
            }

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

    }

}