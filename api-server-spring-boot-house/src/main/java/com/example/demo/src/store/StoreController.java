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




/*
    @ResponseBody
    @GetMapping("/product/{productIdx}/review")
    public BaseResponse<List<GetMoreReviewRes>> getMoreReview(@PathVariable("productIdx")int productIdx,@RequestParam(required = false) String order) throws BaseException {
        try{
            if(jwtService.getJwt()==null){
                return new BaseResponse<>(EMPTY_JWT);
            }

            else{
                if(order==null){
                    order="helpful";
                }
                int userIdx=jwtService.getUserIdx();
                List<GetMoreReviewRes> getMoreReviewRes = storeProvider.getMoreReview(userIdx,productIdx,order);
                return new BaseResponse<>(getMoreReviewRes);
            }

        }catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    } */

    @ResponseBody
    @GetMapping("/product/{productIdx}/review")
    public BaseResponse<GetStoreMoreReviewFinal> getMoreReviewFinal(@PathVariable("productIdx")int productIdx,@RequestParam(required = false) String order) throws BaseException {
        try{
            if(jwtService.getJwt()==null){
                return new BaseResponse<>(EMPTY_JWT);
            }

            else{
                if(order==null){
                    order="helpful";
                }
                int userIdx=jwtService.getUserIdx();
                GetStoreMoreReviewFinal getStoreMoreReviewFinalRes = storeProvider.getMoreReviewFinal(userIdx,productIdx,order);
                return new BaseResponse<>(getStoreMoreReviewFinalRes);
            }

        }catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }




    @ResponseBody
    @GetMapping("/product/review/{reviewIdx}")
    public BaseResponse<GetMoreReviewRes> getOneReview(@PathVariable("reviewIdx")int reviewIdx) throws BaseException {
        try{
            if(jwtService.getJwt()==null){
                return new BaseResponse<>(EMPTY_JWT);
            }

            else{
                int userIdx=jwtService.getUserIdx();
                GetMoreReviewRes getOneReviewRes = storeProvider.getOneReview(userIdx,reviewIdx);
                return new BaseResponse<>(getOneReviewRes);
            }

        }catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }




    @ResponseBody
    @PatchMapping ("/product/review/{reviewIdx}/helpful")
    public BaseResponse<PatchHelpfulRes> patchHelpful(@PathVariable("reviewIdx") int reviewIdx) throws BaseException {
        try{
            if(jwtService.getJwt()==null){
                return new BaseResponse<>(EMPTY_JWT);
            }

            else{
                int userIdx=jwtService.getUserIdx();
                PatchHelpfulRes patchHelpfulRes = storeService.patchHelpful(userIdx,reviewIdx);
                return new BaseResponse<>(patchHelpfulRes);
            }

        }catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }

    }


// 그 리뷰쓰기 클릭시 제품나오도록

    @ResponseBody
    @GetMapping("/product/{productIdx}/review-click")
    public BaseResponse<GetReviewPageRes> getReviewPage(@PathVariable("productIdx")int productIdx) throws BaseException {
        try{
            if(jwtService.getJwt()==null){
                return new BaseResponse<>(EMPTY_JWT);
            }

            else{
                int userIdx=jwtService.getUserIdx();
                GetReviewPageRes getReviewPageRes = storeProvider.getReviewPage(productIdx,userIdx);
                return new BaseResponse<>(getReviewPageRes);
            }

        }catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }




    @ResponseBody
    @PostMapping("/product/{productIdx}/review")
    public BaseResponse<PostReviewRes> createReview(@RequestBody PostReviewReq postReviewReq,@PathVariable("productIdx")int productIdx ) throws BaseException {  // json으로 받아오는데 알아서 객체가 되어 받아짐 -> PostUserReq를 보면 받아올 것에 대한 객체가 구성되어 있고
        try{
            if(jwtService.getJwt()==null){
                return new BaseResponse<>(EMPTY_JWT);
            }

            else{
                int userIdx=jwtService.getUserIdx();
                PostReviewRes postReviewRes = storeService.createReview(postReviewReq,productIdx,userIdx);  // 조회가 아닌 행위는 service에서 진행하므로 UserService의 객체 userService에서 가져옴, 그래서 위에서 받아서 createUser로 넘김
                return new BaseResponse<>(postReviewRes);
            }

        }catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }

    }


    @ResponseBody
    @PatchMapping("/product/review/{reviewIdx}")
    public BaseResponse<PatchReviewRes> patchReview(@RequestBody PatchReviewReq patchReviewReq,@PathVariable("reviewIdx")int reviewIdx ) throws BaseException {  // json으로 받아오는데 알아서 객체가 되어 받아짐 -> PostUserReq를 보면 받아올 것에 대한 객체가 구성되어 있고
        try{
            if(jwtService.getJwt()==null){
                return new BaseResponse<>(EMPTY_JWT);
            }

            else{
                int userIdx=jwtService.getUserIdx();
                PatchReviewRes patchReviewRes = storeService.patchReview(patchReviewReq,reviewIdx,userIdx);  // 조회가 아닌 행위는 service에서 진행하므로 UserService의 객체 userService에서 가져옴, 그래서 위에서 받아서 createUser로 넘김
                return new BaseResponse<>(patchReviewRes);
            }

        }catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }

    }


    @ResponseBody
    @PostMapping("/product/{productIdx}/immediate-purchase")
    public BaseResponse<PostReviewRes> getPayment(@RequestBody PostReviewReq postReviewReq,@PathVariable("productIdx")int productIdx ) throws BaseException {  // json으로 받아오는데 알아서 객체가 되어 받아짐 -> PostUserReq를 보면 받아올 것에 대한 객체가 구성되어 있고
        try{
            if(jwtService.getJwt()==null){
                return new BaseResponse<>(EMPTY_JWT);
            }

            else{
                int userIdx=jwtService.getUserIdx();
                PostReviewRes postReviewRes = storeService.createReview(postReviewReq,productIdx,userIdx);  // 조회가 아닌 행위는 service에서 진행하므로 UserService의 객체 userService에서 가져옴, 그래서 위에서 받아서 createUser로 넘김
                return new BaseResponse<>(postReviewRes);
            }

        }catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }

    }












}
