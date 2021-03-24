package com.example.demo.src.store;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.config.Constant;
import com.example.demo.src.store.model.*;
import com.example.demo.utils.JwtService;
import com.fasterxml.jackson.databind.ser.Serializers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class StoreProvider {
    private final StoreDao storeDao;
    private final JwtService jwtService;

    private JdbcTemplate jdbcTemplate;

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Autowired
    public StoreProvider(StoreDao storeDao, JwtService jwtService) {
        this.storeDao = storeDao;
        this.jwtService = jwtService;
    }

    public GetStoreHomeRes getStoreHome (int pageIdx, int userIdx) throws BaseException{
        String status= storeDao.changeTDStatus();
        System.out.println(status);

        List<GetStoreAdRes> getStoreAdRes = storeDao.getStoreAd(pageIdx);
        List<GetStoreCategoryRes> getStoreCategoryRes = storeDao.getStoreCategory(pageIdx);
        List<GetStoreTDPDRes> getStoreTDPDRes = storeDao.getStoreTDPD(userIdx);
        List<GetStorePopPDRes>  getStorePopPDRes = storeDao.getStorePopPD(userIdx);

        GetStoreHomeRes getStoreHomeRes = new GetStoreHomeRes(getStoreCategoryRes,getStoreTDPDRes,getStorePopPDRes);
        for(int i=0;i<getStoreAdRes.size();i++){
            getStoreHomeRes.getStoreAdvertisement().add(getStoreAdRes.get(i).getAdImgUrl());
        }

        return getStoreHomeRes;
    }


    public GetStoreProductRes getProduct(int userIdx, int productIdx) throws BaseException {
        int exist = storeDao.checkProduct(productIdx);
        if(exist!=1){
            throw new BaseException(NON_EXISTENT_PRODUCT);
        }
        else {
            List<GetStoreProductImgRes> getStoreProductImgRes = storeDao.getProductImg(productIdx);

            GetStoreProductInfoRes getStoreProductInfoRes = storeDao.getProductInfo(userIdx, productIdx);

            List<GetStoreProductImgRes> getStoreProductDscImgRes = storeDao.getProductDscImg(productIdx);

            GetStoreProductStarRes getStoreProductStarRes = storeDao.getProductStar(productIdx);
            GetStoreProductRes getStoreProductRes = new GetStoreProductRes(getStoreProductInfoRes, getStoreProductStarRes);
            if(storeDao.checkSetProduct(productIdx)==1){
                List<GetStoreSetProductReviewRes> getStoreSetProductReviewRes = storeDao.getSetProductReview(productIdx);
                getStoreProductRes.setSetProductReview(getStoreSetProductReviewRes);

            }else{
                List<GetStoreProductReviewRes> getStoreProductReviewRes = storeDao.getProductReview(productIdx);
                getStoreProductRes.setReview(getStoreProductReviewRes);
            }
            //List<GetStoreProductReviewRes> getStoreProductReviewRes = storeDao.getProductReview(productIdx);



            for (int i = 0; i < getStoreProductImgRes.size(); i++) {
                getStoreProductRes.getProductImg().add(getStoreProductImgRes.get(i).getImgUrl());
            }

            for (int i = 0; i < getStoreProductDscImgRes.size(); i++) {
                getStoreProductRes.getProductDescriptionImg().add(getStoreProductDscImgRes.get(i).getImgUrl());
            }

            if (getStoreProductInfoRes.getSetProductStatus().equals("T")) {
                List<GetStoreSetProductRes> getStoreSetProductRes = storeDao.getSetProduct(getStoreProductInfoRes.getProductIdx(), userIdx);
                getStoreProductRes.setSetProduct(getStoreSetProductRes);
            }


            return getStoreProductRes;
        }
    }



    public GetProductOptionFinalRes getProductOption (int productIdx) throws BaseException {
        int exist = storeDao.checkProduct(productIdx);
        if(exist!=1){
            throw new BaseException(NON_EXISTENT_PRODUCT);
        }
        else {
            GetProductOptionFinalRes getProductOptionFinalRes = new GetProductOptionFinalRes();

            DecimalFormat formatter = new DecimalFormat("###,###");

            int checkSetProduct = storeDao.checkSetProduct(productIdx);

            if (checkSetProduct == 1) {
                List<GetOptionSetProductRes> getOptionSetProductRes = storeDao.getOptionSetProduct(productIdx);
                getProductOptionFinalRes.setSetProduct(getOptionSetProductRes);

            } else {
                int maxPrice = storeDao.getMaxPrice(productIdx);
                int minPrice = storeDao.getMinPrice(productIdx);
                String maxxPrice = formatter.format(maxPrice);
                String minnPrice = formatter.format(minPrice);
                System.out.println("최고가:" + maxxPrice);
                System.out.println("최소가:" + minnPrice);
                List<GetProductOptionRes> getProductOptionRes = storeDao.getProductOption(productIdx);

                List<GetProductOptionDetailRes> getProductOptionDetailRes = storeDao.getProductOptionDetail(productIdx);
                GetProductOptionDetailRes zero = new GetProductOptionDetailRes(0,0,0,"선택안함",0,"0","F");

                for (int i = 0; i < getProductOptionRes.size(); i++) {
                    int h = 0;
                    getProductOptionRes.get(i).getOptionDetail().add(zero);
                    for (int k = 0; k < getProductOptionDetailRes.size(); k++) {
                        if (getProductOptionRes.get(i).getOptionIdx() == getProductOptionDetailRes.get(k).getOptionIdx()) {

                            getProductOptionRes.get(i).getOptionDetail().add(getProductOptionDetailRes.get(k));

                            if (getProductOptionRes.get(i).getOptionDetail().get(h).getPrice().equals("0")) {

                                getProductOptionRes.get(i).getOptionDetail().get(h).setPrice(minnPrice + "원~" + maxxPrice + "원");

                            } else {
                                getProductOptionRes.get(i).getOptionDetail().get(h).setPrice(getProductOptionDetailRes.get(k).getPrice() + "원");
                            }

                            h++;
                        }
                    }

                }

                getProductOptionFinalRes.setOption(getProductOptionRes);
            }

            return getProductOptionFinalRes;
        }

    }


    public List<GetMoreReviewRes> getMoreReview (int userIdx, int productIdx,String order){
        List<GetMoreReviewRes> getMoreReviewRes = storeDao.getMoreReview(userIdx,productIdx,order);
        return getMoreReviewRes;
    }


    public GetStoreMoreReviewFinal getMoreReviewFinal(int userIdx, int productIdx,String order) throws BaseException{
        int exist = storeDao.checkProduct(productIdx);
        if(exist!=1){
            throw new BaseException(NON_EXISTENT_PRODUCT);
        }
        else {
            GetStoreMoreReviewFinal getStoreMoreReviewFinal = storeDao.getMoreReviewFinal(productIdx);
            GetStoreProductStarRes getStoreProductStarRes = storeDao.getProductStar(productIdx);
            if(storeDao.checkSetProduct(productIdx)==1){
                List<GetMoreSetReviewRes> getMoreSetReviewRes = storeDao.getMoreSetReview(userIdx,productIdx,order);
                getStoreMoreReviewFinal.setSetProductReview(getMoreSetReviewRes);
            }else{
                List<GetMoreReviewRes> getMoreReviewRes = storeDao.getMoreReview(userIdx, productIdx, order);
                getStoreMoreReviewFinal.setReview(getMoreReviewRes);
            }

            getStoreMoreReviewFinal.setStarDistribution(getStoreProductStarRes);


            return getStoreMoreReviewFinal;
        }
    }




    public GetMoreReviewRes getOneReview(int userIdx, int reviewIdx) throws BaseException{
        int exist = storeDao.checkReview(reviewIdx);
        if(exist==1){
            GetMoreReviewRes getOneReviewRes = storeDao.getOneReview(userIdx,reviewIdx);
            return getOneReviewRes;
        }
        else{
            throw new BaseException(NON_EXISTENT_REVIEW);
        }

    }


    public GetReviewPageRes getReviewPage(int productIdx, int userIdx) throws BaseException{
        if(storeDao.checkProduct(productIdx)!=1){
            throw new BaseException(NON_EXISTENT_PRODUCT);
        }
        else if(storeDao.checkSetProduct(productIdx)==1){
            throw new BaseException(INVALID_PRODUCT);
        }
        else if(storeDao.checkReviewByUser(userIdx,productIdx)==1){
            throw new BaseException(ALREADY_WRITTEN_REVIEW);
        }
        else{
            GetReviewPageRes getReviewPageRes =storeDao.getReviewPage(productIdx);
            getReviewPageRes.setUserIdx(userIdx);

            return getReviewPageRes;
        }


    }


    public char checkHelpful(int userIdx,int reviewIdx){
        return storeDao.checkHelpful(userIdx,reviewIdx);
    }



    public List<GetProductCouponRes> getProductCoupon(int productIdx,int userIdx) throws BaseException{
        int exist = storeDao.checkProduct(productIdx);
        if(exist!=1){
            throw new BaseException(NON_EXISTENT_PRODUCT);
        }else if(storeDao.checkProductCoupon(productIdx)!=1){
            throw new BaseException(INVALID_PRODUCT_COUPON_ACCESS);

        }else{
            String changeCouponStatus=storeDao.changeCouponStatus(); // 쿠폰 만료인것 자동으로 비활성화시키기
            System.out.println(changeCouponStatus);
            List<GetProductCouponRes> getProductCouponRes =storeDao.getProductCoupon(productIdx,userIdx);
            return getProductCouponRes;
        }
    }


    public GetDandRFinalRes getDandR(int productIdx) throws BaseException{
        if(storeDao.checkProduct(productIdx)!=1){
            throw new BaseException(NON_EXISTENT_PRODUCT);
        }
        else if(storeDao.checkSetProduct(productIdx)==1){
            List<GetDandRSetProductRes> getDandRSetProductRes = storeDao.getDSetProduct(productIdx);
            GetDandRFinalRes getDandRFinalRes = new GetDandRFinalRes();
            for(int i=0;i<getDandRSetProductRes.size();i++){
                getDandRFinalRes.getSetProduct().add(getDandRSetProductRes.get(i));
            }
            return getDandRFinalRes;

        }
        else{
            List<GetDeliveryAndRefundRes> getDeliveryAndRefundRes =storeDao.getDandR(productIdx);
            GetDandRFinalRes getDandRFinalRes = new GetDandRFinalRes();
            for(int i=0;i<getDeliveryAndRefundRes.size();i++){
                getDandRFinalRes.getDeliveryAndRefund().add(getDeliveryAndRefundRes.get(i));
            }
            return getDandRFinalRes;
        }
    }




}
