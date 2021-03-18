package com.example.demo.src.home;

import com.fasterxml.jackson.databind.ser.Serializers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.home.model.*;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.isRegexEmail;

@RestController
@RequestMapping("/app/home")
public class HomeController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final HomeProvider homeProvider;
    @Autowired
    private final HomeService homeService;
    @Autowired
    private final JwtService jwtService;


    public HomeController(HomeProvider homeProvider, HomeService homeService, JwtService jwtService) {
        this.homeProvider = homeProvider;
        this.homeService = homeService;
        this.jwtService = jwtService;
    }

    //Query String
    @ResponseBody
    @GetMapping("/house-warm") // (GET) 127.0.0.1:9000/app/users
    public BaseResponse<List<GetHousewarmingRes>> getHw() {
        List<GetHousewarmingRes> getHousewarmingRes = homeProvider.getHw();
        return new BaseResponse<>(getHousewarmingRes);
    }

    //Query String
    @ResponseBody
    @GetMapping("/picture") // (GET) 127.0.0.1:9000/app/users
    public BaseResponse<List<GetPictureRes>> getPicture() {
        List<GetPictureRes> getPictureRes = homeProvider.getPicture();
        return new BaseResponse<>(getPictureRes);
    }


}