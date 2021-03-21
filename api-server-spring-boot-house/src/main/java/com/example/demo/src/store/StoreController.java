package com.example.demo.src.store;

import com.example.demo.src.user.UserProvider;
import com.example.demo.src.user.UserService;
import com.example.demo.src.user.model.GetCheckEmailRes;
import com.example.demo.src.user.model.PatchUserLogoutRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.store.model.*;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.isRegexEmail;

@RestController
@RequestMapping("/app/stores")
public class StoreController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final StoreProvider storeProvider;
    @Autowired
    private final StoreService storeService;
    @Autowired
    private final JwtService jwtService;


    public StoreController(StoreProvider storeProvider, StoreService storeService, JwtService jwtService){
        this.storeProvider = storeProvider;
        this.storeService = storeService;
        this.jwtService = jwtService;
    }

    @ResponseBody
    @GetMapping("")
    public BaseResponse<GetStoreHomeRes> getStoreHome() throws BaseException {
        int pageIdx=1;
        try{
            if(jwtService.getJwt()==null){
                return new BaseResponse<>(EMPTY_JWT);
            }

            else{
                int userIdx=jwtService.getUserIdx();
                GetStoreHomeRes getStoreHomeRes = storeProvider.getStoreHome(pageIdx,userIdx);
                return new BaseResponse<>(getStoreHomeRes);
            }

        }catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }

    }

    @ResponseBody
    @GetMapping("/product/{productIdx}")
    public BaseResponse<GetStoreProductRes> getStoreProduct(@PathVariable("productIdx")int productIdx) throws BaseException {
        try{
            if(jwtService.getJwt()==null){
                return new BaseResponse<>(EMPTY_JWT);
            }

            else{
                int userIdx=jwtService.getUserIdx();
                GetStoreProductRes getStoreProductRes = storeProvider.getProduct(userIdx,productIdx);
                return new BaseResponse<>(getStoreProductRes);
            }

        }catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }



    @ResponseBody
    @GetMapping("/product/{productIdx}/option")
    public BaseResponse<GetProductOptionFinalRes> getProductOption(@PathVariable("productIdx")int productIdx) throws BaseException {
        try{
            if(jwtService.getJwt()==null){
                return new BaseResponse<>(EMPTY_JWT);
            }

            else{
                int userIdx=jwtService.getUserIdx();
                GetProductOptionFinalRes getProductOptionFinalRes = storeProvider.getProductOption(productIdx);
                return new BaseResponse<>(getProductOptionFinalRes);
            }

        }catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }



    @ResponseBody
    @GetMapping("/product/{productIdx}/review")
    public BaseResponse<List<GetMoreReviewRes>> getMoreReview(@PathVariable("productIdx")int productIdx) throws BaseException {
        try{
            if(jwtService.getJwt()==null){
                return new BaseResponse<>(EMPTY_JWT);
            }

            else{
                int userIdx=jwtService.getUserIdx();
                List<GetMoreReviewRes> getMoreReviewRes = storeProvider.getMoreReview(userIdx,productIdx);
                return new BaseResponse<>(getMoreReviewRes);
            }

        }catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }



}
