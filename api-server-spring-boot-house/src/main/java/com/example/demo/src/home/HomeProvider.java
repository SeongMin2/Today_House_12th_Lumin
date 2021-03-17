package com.example.demo.src.home;

import com.example.demo.config.BaseException;
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


//Provider : Read의 비즈니스 로직 처리
@Service
public class HomeProvider {

    private final HomeDao homeDao;
    private final JwtService jwtService;

    private JdbcTemplate jdbcTemplate;
    final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    public HomeProvider(HomeDao homeDao,JwtService jwtService) {

        this.homeDao = homeDao;
        this.jwtService = jwtService;
    }


}