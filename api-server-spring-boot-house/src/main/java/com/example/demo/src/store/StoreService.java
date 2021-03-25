package com.example.demo.src.store;

import com.example.demo.config.BaseException;
import com.example.demo.config.secret.Secret;
import com.example.demo.src.store.model.PatchHelpfulRes;
import com.example.demo.src.store.model.PostReviewRes;
import com.example.demo.src.store.StoreDao;
import com.example.demo.src.store.StoreProvider;
import com.example.demo.src.store.model.*;
import com.example.demo.utils.AES128;
import com.example.demo.utils.JwtService;
import com.fasterxml.jackson.databind.ser.Serializers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

import java.util.ArrayList;
import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

// Service Create, Update, Delete 의 로직 처리
@Service
public class StoreService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final StoreDao storeDao;
    private final StoreProvider storeProvider;
    private final JwtService jwtService;
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Autowired
    public StoreService(StoreDao storeDao, StoreProvider storeProvider, JwtService jwtService) {
        this.storeDao = storeDao;
        this.storeProvider = storeProvider;
        this.jwtService = jwtService;

    }


    public PatchHelpfulRes patchHelpful(int userIdx, int reviewIdx) throws BaseException {
        if(storeDao.checkReview(reviewIdx)==1){
            int exist=storeDao.checkHelpfulExist(userIdx,reviewIdx);
            if(exist==1) {
                char status = storeProvider.checkHelpful(userIdx, reviewIdx);
                if (status == 'T') {
                    PatchHelpfulRes patchHelpfulRes = storeDao.patchHelpful("F", userIdx, reviewIdx);
                    return patchHelpfulRes;
                }
                else{
                    PatchHelpfulRes patchHelpfulRes = storeDao.patchHelpful("T", userIdx, reviewIdx);
                    return patchHelpfulRes;
                }
            } else{
                PatchHelpfulRes patchHelpfulRes =storeDao.createHelpful("T",userIdx,reviewIdx);
                return patchHelpfulRes;
            }
        }else{
            throw new BaseException(NON_EXISTENT_REVIEW);
        }

    }




    public PostReviewRes createReview(PostReviewReq postReviewReq,int productIdx,int userIdx) throws BaseException {
        if(storeDao.checkProduct(productIdx)!=1){
            throw new BaseException(NON_EXISTENT_PRODUCT);
        }
        else if(storeDao.checkSetProduct(productIdx)==1){
            throw new BaseException(INVALID_PRODUCT);
        }
        else if(storeDao.checkReviewByUser(userIdx,productIdx)==1){
            throw new BaseException(ALREADY_WRITTEN_REVIEW);
        }
        else if(postReviewReq.getAgreement().equals("T")&& postReviewReq.getContent().length()>=20 && postReviewReq.getStarPoint()>=1&&postReviewReq.getStarPoint()<=5){
            int reviewIdx = storeDao.createReview(postReviewReq,productIdx,userIdx);

            return new PostReviewRes(reviewIdx);
        }
        else{
            throw new BaseException(NOT_COMPLETED_REVIEW);
        }
    }





    public PatchReviewRes patchReview(PatchReviewReq patchReviewReq,int reviewIdx,int userIdx) throws BaseException {
        if(storeDao.checkReview(reviewIdx)!=1){
            throw new BaseException(NON_EXISTENT_REVIEW);
        }
        else if(storeDao.checkReviewUser(userIdx,reviewIdx)!=1){
            throw new BaseException(INVALID_USER_ACCESS);
        }
        else if(patchReviewReq.getContent().length()>=20 && patchReviewReq.getStarPoint()>=1&&patchReviewReq.getStarPoint()<=5){
            int rIdx = storeDao.patchReview(patchReviewReq,reviewIdx);
            PatchReviewRes patchReviewRes = new PatchReviewRes(rIdx);

            return patchReviewRes;
        }
        else{
            throw new BaseException(NOT_COMPLETED_REVIEW);
        }
    }


    public PostCouponRes postCoupon(int userIdx,int couponIdx) throws BaseException{
        if(storeDao.checkCoupon(couponIdx)!=1){
            throw new BaseException(NON_EXISTENT_COUPON);
        }
        else if(storeDao.checkHasCoupon(userIdx,couponIdx)==1){
            throw new BaseException(ALREADY_ISSUED_COUPON);
        }
        else{
            int idx = storeDao.postCoupon(userIdx,couponIdx);
            PostCouponRes postCouponRes = new PostCouponRes(idx);
            return postCouponRes;
        }

    }


    public List<PostImmediatePRes> getPurchaseInfo(PostImmediatePReq postImmediatePReq,int productIdx,int userIdx) throws BaseException{
        if(storeDao.checkProduct(productIdx)!=1){
            throw new BaseException(NON_EXISTENT_PRODUCT);
        }else {
            GetProductOptionFinalRes forcheck = storeProvider.getProductOption(productIdx);
            int numOfRequired = 0;
            int numOfSelective = 0;
            List<Integer> requiredOptionIdx = new ArrayList<>();

            for (int i = 0; i < forcheck.getOption().size(); i++) {
                if (forcheck.getOption().get(i).getRequired().equals("T")) {
                    numOfRequired++;
                    requiredOptionIdx.add(forcheck.getOption().get(i).getOptionIdx());
                } else {
                    numOfSelective++;
                }
            }

            int a = 0;
            List<Integer> nowOption = new ArrayList<>();
            for (int i = 0; i < postImmediatePReq.getOptionIdx().size(); i++) {

                for (int k = 0; k < forcheck.getOption().size(); k++) {  // option 주제 들
                    for (int j = 1; j < forcheck.getOption().get(k).getOptionDetail().size(); j++) { // 옵션주제 내부의 옵션들
                        System.out.println("가져온값"+postImmediatePReq.getOptionIdx().get(i));
                        System.out.println("비교값"+forcheck.getOption().get(k).getOptionDetail().get(j).getDetailidx());
                        if (postImmediatePReq.getOptionIdx().get(i) == forcheck.getOption().get(k).getOptionDetail().get(j).getDetailidx() && forcheck.getOption().get(k).getRequired().equals("T")) {

                            nowOption.add(forcheck.getOption().get(k).getOptionIdx());

                        } else if (postImmediatePReq.getOptionIdx().get(i) == forcheck.getOption().get(k).getOptionDetail().get(j).getDetailidx() && forcheck.getOption().get(k).getRequired().equals("F")) {
                            a++;
                        }
                    }
                }
            }
            System.out.println("nowoption길이"+nowOption.size());
            System.out.println("a값:"+a);
            int b = 0;
            for (int i = 0; i < nowOption.size() - 1; i++) {
                if (nowOption.get(i) != nowOption.get(i + 1)) {
                    b++;
                }
            }

            List<PostImmediatePRes> finalResult = new ArrayList<>();
            String options="·";

            if(nowOption.size()!=numOfRequired){
                throw new BaseException(NOT_REQUIRED_OPTION);
            }
            else if(postImmediatePReq.getOptionIdx().size()>numOfRequired+numOfSelective || postImmediatePReq.getOptionIdx().size()<=0){
                throw new BaseException(INVALID_OPTION_REQUEST);
            }
            else if(postImmediatePReq.getOptionIdx().size()!=nowOption.size()+a){
                throw new BaseException(INVALID_OPTION_REQUEST);
            }

            else if(a<=numOfSelective && b==numOfRequired-1){
                for(int f=0;f<postImmediatePReq.getOptionIdx().size();f++){
                    PostImmediatePRes postImmediatePRes = storeDao.getPaymentProductInfo(postImmediatePReq.getOptionIdx().get(f));

                    finalResult.add(postImmediatePRes);
                    if(finalResult.get(f).getPriceForCalculate()==0){
                        options=options+finalResult.get(f).getOptionName()+" / ";
                    }else{
                        options=options+finalResult.get(f).getOptionName();
                    }

                }
                System.out.println(options);
                if(a==0){
                    finalResult.get(finalResult.size()-1).setOptionName(options);
                }
                else{
                    finalResult.get(finalResult.size()-1-a).setOptionName(options);
                }
                for(int l=0;l<finalResult.size();l++){
                    if(finalResult.get(l).getPriceForCalculate()==0){
                        finalResult.remove(l);
                    }
                }



                return finalResult;

            }
            else{
                throw new BaseException(INVALID_OPTION_REQUEST);
            }

        }



        }






}
