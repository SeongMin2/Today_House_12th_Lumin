package com.example.demo.src.scrap;

import com.example.demo.config.BaseException;
import com.example.demo.src.scrap.model.*;
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
public class ScrapProvider {
    private final ScrapDao scrapDao;
    private final JwtService jwtService;

    private JdbcTemplate jdbcTemplate;

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Autowired
    public ScrapProvider(ScrapDao scrapDao, JwtService jwtService) {
        this.scrapDao = scrapDao;
        this.jwtService = jwtService;
    }

    public char checkScrap(int userIdx, int evalableIdx,int contentIdx){
        return scrapDao.checkScrap(userIdx,evalableIdx,contentIdx);
    }
}