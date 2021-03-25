package com.example.demo.src.store;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;

import java.net.URI;
import java.net.URISyntaxException;
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


    public GetKakaoPayReadyRes kakaoPay(PostKakaoPayReadyReq postKakaoPayReadyReq,int userIdx, int productIdx) throws BaseException {
        if (storeDao.checkProduct(productIdx) != 1) {
            throw new BaseException(NON_EXISTENT_PRODUCT);
        } else {
            int orderIdx = storeDao.postOrders(postKakaoPayReadyReq, userIdx);
            for (int i = 0; i < postKakaoPayReadyReq.getDetailOption().size(); i++) {
                int a = storeDao.postOrderProduct(postKakaoPayReadyReq.getDetailOption().get(i), orderIdx);
            }
            String productName = storeDao.getProductName(productIdx);
            String userName = storeDao.getUserName(userIdx);


            String HOST = "https://kapi.kakao.com";

            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "KakaoAK " + "f57245abbbc2a593e17e09074aa5f3dd");
            headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

            // 서버로 요청할 Body
            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("cid", "TC0ONETIME");     // 받아야함
            body.add("partner_order_id", orderIdx+"");
            body.add("partner_user_id", userName);
            body.add("item_name", productName);
            body.add("quantity", postKakaoPayReadyReq.getNumber());
            body.add("total_amount", postKakaoPayReadyReq.getTotalPrice());
            body.add("tax_free_amount", "0");
            body.add("approval_url", "https://www.naver.com");
            body.add("cancel_url", "https://luminitworld.tistory.com/97");
            body.add("fail_url", "https://www.daum.net");

            HttpEntity<MultiValueMap<String, String>> requestParam = new HttpEntity<>(body, headers);

            try {
                GetKakaoPayReadyRes getKakaoPayReadyRes = restTemplate.postForObject(new URI(HOST + "/v1/payment/ready"), requestParam, GetKakaoPayReadyRes.class);
                getKakaoPayReadyRes.setOrderIdx(orderIdx);
                return getKakaoPayReadyRes;

            } catch (RestClientException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (URISyntaxException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return null;
        }
    }



    public PostKakaoPayConfirmRes kakaoPayConfirm(PostKakaoPayConfirmReq postKakaoPayConfirmReq,int userIdx,int productIdx) throws BaseException {
        if (storeDao.checkProduct(productIdx) != 1) {
            throw new BaseException(NON_EXISTENT_PRODUCT);
        } else{
            String userName = storeDao.getUserName(userIdx);

            String HOST = "https://kapi.kakao.com";

            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "KakaoAK " + "f57245abbbc2a593e17e09074aa5f3dd");
            headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

            // 서버로 요청할 Body
            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("cid", "TC0ONETIME");
            body.add("tid",postKakaoPayConfirmReq.getTid());
            body.add("partner_order_id", postKakaoPayConfirmReq.getOrderIdx()+"");
            body.add("partner_user_id", userName);
            body.add("pg_token", postKakaoPayConfirmReq.getPg_token());


            HttpEntity<MultiValueMap<String, String>> requestParam = new HttpEntity<>(body, headers);

            try {
                PostKakaoPayConfirmRes postKakaoPayConfirmRes = restTemplate.postForObject(new URI(HOST + "/v1/payment/approve"), requestParam, PostKakaoPayConfirmRes.class);
                int orderIdx = storeDao.patchOrderStatus(postKakaoPayConfirmReq.getOrderIdx(),postKakaoPayConfirmReq);
                return postKakaoPayConfirmRes;

            } catch (RestClientException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (URISyntaxException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }



            return null;
        }
    }






}
