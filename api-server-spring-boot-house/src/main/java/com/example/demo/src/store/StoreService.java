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



}
