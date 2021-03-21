package com.example.demo.src.store;

import com.example.demo.config.BaseException;
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
import java.util.List;

import static com.example.demo.config.BaseResponseStatus.NON_EXISTENT_PRODUCT;

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

            GetStoreProductStarRes getStoreProductStarRes = storeDao.getProductStar(productIdx);
            List<GetStoreProductReviewRes> getStoreProductReviewRes = storeDao.getProductReview(productIdx);
            GetStoreProductRes getStoreProductRes = new GetStoreProductRes(getStoreProductInfoRes, getStoreProductStarRes, getStoreProductReviewRes);

            for (int i = 0; i < getStoreProductImgRes.size(); i++) {
                getStoreProductRes.getProductImg().add(getStoreProductImgRes.get(i).getImgUrl());
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

                for (int i = 0; i < getProductOptionRes.size(); i++) {
                    int h = 0;
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


    public List<GetMoreReviewRes> getMoreReview (int userIdx, int productIdx){
        List<GetMoreReviewRes> getMoreReviewRes = storeDao.getMoreReview(userIdx,productIdx);
        return getMoreReviewRes;
    }


    public GetMoreReviewRes getOneReview(int userIdx, int reviewIdx){
        GetMoreReviewRes getOneReviewRes = storeDao.getOneReview(userIdx,reviewIdx);
        return getOneReviewRes;
    }


    public char checkHelpful(int userIdx,int reviewIdx){
        return storeDao.checkHelpful(userIdx,reviewIdx);
    }




}
