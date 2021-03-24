package com.example.demo.src.home;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.config.secret.Secret;
import com.example.demo.src.home.model.*;
import com.example.demo.utils.AES128;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.List;

import static com.example.demo.config.BaseResponseStatus.NON_EXISTENT_POST;

//Provider : Read의 비즈니스 로직 처리
@Service
public class HomeProvider {

    private final HomeDao homeDao;
    private final JwtService jwtService;

    private JdbcTemplate jdbcTemplate;
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    public GetHomeMainRes getHomeMain (int userIdx) throws BaseException{

        List<GetHousewarmingRes> getHousewarmingRes = homeDao.getHw(userIdx);
        List<GetPictureRes>  getPictureRes = homeDao.getPicture(userIdx);

        GetHomeMainRes getHomeMainRes = new GetHomeMainRes(getHousewarmingRes,getPictureRes);

        return getHomeMainRes;
    }
    @Autowired
    public HomeProvider(HomeDao homeDao,JwtService jwtService) {

        this.homeDao = homeDao;
        this.jwtService = jwtService;
    }

    public List<GetHousewarmingRes> getHw(int userIdx) {
        List<GetHousewarmingRes> getHousewarmingRes = homeDao.getHw(userIdx);
        return getHousewarmingRes;
    }

    public List<GetPictureRes> getPicture(int userIdx) {
        List<GetPictureRes> getPictureRes = homeDao.getPicture(userIdx);
        return getPictureRes;
    }

    public List<GetPictureReviewRes> getReviews(int userIdx, int picturepostIdx) {
        List<GetPictureReviewRes> getPictureReviewRes = homeDao.getReviews(userIdx,picturepostIdx);
        return getPictureReviewRes;
    }

    public GetPicturePostRes getPicturePost(int userIdx, int picturepostIdx) throws BaseException {
        int exist = homeDao.checkPicturePost(picturepostIdx);
        if(exist!=1){
            throw new BaseException(NON_EXISTENT_POST);
        }
        else {
            List<GetPictureListRes> getPictureListRes = homeDao.getPictureImg(picturepostIdx);
            String comment=homeDao.getComment(picturepostIdx);
            GetPicturePostRes getPicturePostRes = new GetPicturePostRes();
            List<GetPictureReviewRes> getPictureReviewRes = homeDao.getReviews(userIdx,picturepostIdx);
            getPicturePostRes.setReview(getPictureReviewRes);

            //List<GetStoreProductReviewRes> getStoreProductReviewRes = storeDao.getProductReview(productIdx);
            getPicturePostRes.setComment(comment);
            for (int i = 0; i < getPictureListRes.size(); i++) {
                getPicturePostRes.getPictureUrl().add(getPictureListRes.get(i).getPictureUrl());
            }


            return getPicturePostRes;
        }
    }

    public char checkPicturePostStatus(int userIdx, int picturepostIdx){
        return homeDao.checkPicturePostStatus(userIdx,picturepostIdx);
    }
    public char checkHWStatus(int userIdx, int hwIdx){
        return homeDao.checkHWstatus(userIdx,hwIdx);
    }
    public char checkCommentStatus(int userIdx, int commentIdx){
        return homeDao.checkCommentstatus(userIdx,commentIdx);
    }

    public char checkHeart(int userIdx, int evalableIdx,int contentIdx){
        return homeDao.checkHeart(userIdx,evalableIdx,contentIdx);
    }

}