package com.example.demo.src.store;

import com.example.demo.config.BaseException;
import com.example.demo.config.secret.Secret;
import com.example.demo.src.store.model.PatchHelpfulRes;
import com.example.demo.src.user.UserDao;
import com.example.demo.src.user.UserProvider;
import com.example.demo.src.user.model.*;
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

}
