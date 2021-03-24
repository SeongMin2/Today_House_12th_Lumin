package com.example.demo.src.scrap;

import com.fasterxml.jackson.databind.ser.Serializers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.scrap.model.*;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.isRegexEmail;

@RestController
@RequestMapping("/app/scrap")
public class ScrapController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final ScrapService scrapService;
    @Autowired
    private final JwtService jwtService;


    public ScrapController(ScrapService scrapService, JwtService jwtService) {
        this.scrapService = scrapService;
        this.jwtService = jwtService;
    }


    @ResponseBody
    @PostMapping ("/{evalableIdx}/{contentIdx}")
    public BaseResponse<PatchScrapRes> patchScrapRes(@PathVariable("evalableIdx") int evalableIdx,@PathVariable("contentIdx") int contentIdx) throws BaseException {
        try{
            if(jwtService.getJwt()==null){
                return new BaseResponse<>(EMPTY_JWT);
            }

            else{
                int userIdx=jwtService.getUserIdx();
                PatchScrapRes patchScrapRes = scrapService.patchScrap(userIdx,evalableIdx,contentIdx);
                return new BaseResponse<>(patchScrapRes);
            }

        }catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }

    }
}