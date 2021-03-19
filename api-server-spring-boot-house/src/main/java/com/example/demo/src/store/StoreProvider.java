package com.example.demo.src.store;

import com.example.demo.config.BaseException;
import com.example.demo.config.secret.Secret;
import com.example.demo.src.store.model.*;
import com.example.demo.src.user.UserDao;
import com.example.demo.utils.AES128;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
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
        List<GetStoreCategoryRes> getStoreCategoryRes = storeDao.getStoreCategory(pageIdx);
        List<GetStoreTDPDRes> getStoreTDPDRes = storeDao.getStoreTDPD(userIdx);
        List<GetStorePopPDRes>  getStorePopPDRes = storeDao.getStorePopPD(userIdx);
        GetStoreHomeRes getStoreHomeRes = new GetStoreHomeRes(getStoreCategoryRes,getStoreTDPDRes,getStorePopPDRes);
        return getStoreHomeRes;
    }
}
