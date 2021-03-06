package com.example.demo.src.mypage;

import com.example.demo.config.BaseException;
import com.example.demo.src.mypage.model.*;
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
public class MypageProvider {
    private final MypageDao mypageDao;
    private final JwtService jwtService;

    private JdbcTemplate jdbcTemplate;

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Autowired
    public MypageProvider(MypageDao mypageDao, JwtService jwtService) {
        this.mypageDao = mypageDao;
        this.jwtService = jwtService;
    }

    public GetMypageProfileRes getMypageProfile(int userIdx) throws BaseException {

        GetMypageUserRes getMypageUserRes = mypageDao.getMypageUser(userIdx);
        List<GetPictureDetailRes> getPictureDetailRes = mypageDao.getPictureDetail(userIdx);
        int countPicture = mypageDao.countPicture(userIdx);
        GetMypagePictureRes getMypagePictureRes = new GetMypagePictureRes(countPicture, getPictureDetailRes);
        GetMypageProfileRes getMypageProfileRes = new GetMypageProfileRes(getMypageUserRes,getMypagePictureRes);

        return getMypageProfileRes;
    }

    public GetScrapBookRes getScrapBook(int userIdx) throws BaseException {
        List<GetScrapHWRes> getScrapHWRes = mypageDao.getScrapHW(userIdx);
        List<GetScrapPictureRes> getScrapPictureRes = mypageDao.getScrapPicture(userIdx);
        int scrapHWCount=mypageDao.getHWCount(userIdx);
        int scrapPictureCount=mypageDao.getPictureCount(userIdx);

        GetScrapBookRes getScrapBookRes = new GetScrapBookRes(scrapPictureCount,getScrapPictureRes,scrapHWCount,getScrapHWRes);

        return getScrapBookRes;
    }

    public GetHeartBookRes getHeartBook(int userIdx) throws BaseException {
        List<GetHeartHWRes> getHeartHWRes = mypageDao.getHeartHW(userIdx);
        List<GetHeartPictureRes> getHeartPictureRes = mypageDao.getHeartPicture(userIdx);
        int heartHWCount=mypageDao.getHWHeartCount(userIdx);
        int heartPictureCount=mypageDao.getPictureHeartCount(userIdx);

        GetHeartBookRes getHeartBookRes = new GetHeartBookRes(heartPictureCount,getHeartPictureRes,heartHWCount,getHeartHWRes);

        return getHeartBookRes;
    }
}