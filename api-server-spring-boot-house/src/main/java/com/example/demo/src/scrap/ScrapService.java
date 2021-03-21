package com.example.demo.src.scrap;



import com.example.demo.config.BaseException;
import com.example.demo.config.secret.Secret;
import com.example.demo.src.scrap.model.*;
import com.example.demo.utils.AES128;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

import static com.example.demo.config.BaseResponseStatus.*;

// Service Create, Update, Delete 의 로직 처리
@Service
public class ScrapService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ScrapDao scrapDao;
    private final ScrapProvider scrapProvider;
    private final JwtService jwtService;
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Autowired
    public ScrapService(ScrapDao scrapDao,ScrapProvider scrapProvider,JwtService jwtService) {
        this.scrapDao = scrapDao;
        this.scrapProvider=scrapProvider;
        this.jwtService = jwtService;

    }

    public PatchScrapRes patchScrap(int userIdx, int evalableIdx,int contentIdx){
        int exist=scrapDao.checkScrapExist(userIdx,contentIdx,evalableIdx);
        if(exist==1) {
            char status = scrapProvider.checkScrap(userIdx, evalableIdx,contentIdx);
            if (status == 'T') {
                PatchScrapRes patchScrapRes = scrapDao.patchScrap("F", userIdx, evalableIdx,contentIdx);
                return patchScrapRes;
            }
            else{
                PatchScrapRes patchScrapRes = scrapDao.patchScrap("T", userIdx,evalableIdx, contentIdx);
                return patchScrapRes;
            }
        } else{
            PatchScrapRes patchScrapRes =scrapDao.createScrap("T",userIdx,evalableIdx,contentIdx);
            return patchScrapRes;
        }

    }
}