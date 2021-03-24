package com.example.demo.src.home;


import com.example.demo.config.BaseException;
import com.example.demo.config.secret.Secret;
import com.example.demo.src.home.model.*;
import com.example.demo.src.home.HomeDao;
import com.example.demo.src.home.HomeProvider;
import com.example.demo.utils.AES128;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.fasterxml.jackson.databind.ser.Serializers;
import org.springframework.stereotype.Service;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;


import javax.sql.DataSource;

import static com.example.demo.config.BaseResponseStatus.*;

// Service Create, Update, Delete 의 로직 처리
@Service
public class HomeService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final HomeDao homeDao;
    private final HomeProvider homeProvider;
    private final JwtService jwtService;
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Autowired
    public HomeService(HomeDao homeDao, HomeProvider homeProvider, JwtService jwtService) {
        this.homeDao = homeDao;
        this.homeProvider = homeProvider;
        this.jwtService = jwtService;

    }

    public PostPictureReviewRes createPictureReview(PostPictureReviewReq postPictureReviewReq, int picturepostIdx, int userIdx) throws BaseException {
        if (homeDao.checkPicturePost(picturepostIdx) != 1) {
            throw new BaseException(NON_EXISTENT_POST);
        } else if (postPictureReviewReq.getComment().length() > 0) {
            int reviewIdx = homeDao.createPictureReview(postPictureReviewReq, picturepostIdx, userIdx);

            return new PostPictureReviewRes(reviewIdx);
        } else {
            throw new BaseException(NON_EXISTENT_COMMENT);
        }
    }

    public PatchPictureRes patchPicturePostStatus(int picturepostIdx, int userIdx) throws BaseException {

        if (homeDao.checkPicturePost(picturepostIdx) != 1) {

            throw new BaseException(NON_EXISTENT_POST);
        } else if (homeDao.checkPictureUser(userIdx, picturepostIdx) != 1) {

            throw new BaseException(INVALID_USER_ACCESS);
        } else if (homeProvider.checkPicturePostStatus(userIdx, picturepostIdx) == 'T') {
            PatchPictureRes patchPictureRes = homeDao.patchPicturePostStatus("F", userIdx, picturepostIdx);
            return patchPictureRes;
        } else {
            throw new BaseException(ALREADY_DELETE_POST);
        }
    }

    public PatchHWRes patchHWStatus(int hwIdx, int userIdx) throws BaseException {
        if (homeDao.checkHW(hwIdx) != 1) {

            throw new BaseException(NON_EXISTENT_POST);
        } else if (homeDao.checkHWUser(userIdx, hwIdx) != 1) {

            throw new BaseException(INVALID_USER_ACCESS);
        } else if (homeProvider.checkHWStatus(userIdx, hwIdx) == 'T') {

            PatchHWRes patchHWRes = homeDao.patchHWStatus("F", userIdx, hwIdx);
            return patchHWRes;
        } else {
            throw new BaseException(ALREADY_DELETE_POST);
        }
    }

    @Transactional
    public PatchHeartRes patchHeart(int userIdx, int evalableIdx,int contentIdx) throws BaseException{
        int exist=homeDao.checkHeartExist(userIdx,evalableIdx,contentIdx);
        if(homeDao.checkEvalableExist(evalableIdx)==0 || evalableIdx==2){
            throw new BaseException(NON_EVALABLE_EXIST);
        }
        else if(exist==1) {
            char status = homeProvider.checkHeart(userIdx, evalableIdx,contentIdx);
            if (status == 'T') {
                PatchHeartRes patchHeartRes = homeDao.patchHeart("F", userIdx, evalableIdx,contentIdx);
                return patchHeartRes;
            }
            else{
                PatchHeartRes patchHeartRes = homeDao.patchHeart("T", userIdx,evalableIdx, contentIdx);
                return patchHeartRes;
            }
        } else{
            PatchHeartRes patchHeartRes =homeDao.createHeart("T",userIdx,evalableIdx,contentIdx);
            return patchHeartRes;
        }

    }

}