package com.example.demo.src.user;



import com.example.demo.config.BaseException;
import com.example.demo.config.secret.Secret;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.AES128;
import com.example.demo.utils.JwtService;
import com.fasterxml.jackson.databind.ser.Serializers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import javax.transaction.Transactional;

import static com.example.demo.config.BaseResponseStatus.*;

// Service Create, Update, Delete 의 로직 처리
@Service
public class UserService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final UserDao userDao;
    private final UserProvider userProvider;
    private final JwtService jwtService;
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Autowired
    public UserService(UserDao userDao, UserProvider userProvider, JwtService jwtService) {
        this.userDao = userDao;
        this.userProvider = userProvider;
        this.jwtService = jwtService;

    }

    //POST
    public PostUserRes createUser(PostUserReq postUserReq) throws BaseException {
        //중복
        if(postUserReq.getPassword().equals(postUserReq.getPasswordCheck())){
            if(userProvider.checkName(postUserReq.getNickName())==1){
                throw new BaseException(POST_USER_EXISTS_NAME);
            }

            if(userProvider.checkEmailId(postUserReq.getEmailId()) ==1){
                throw new BaseException(POST_USERS_EXISTS_EMAIL);
            }

            String pwd;
            try{
                //암호화
                pwd = new AES128(Secret.USER_INFO_PASSWORD_KEY).encrypt(postUserReq.getPassword());
                postUserReq.setPassword(pwd);
            } catch (Exception ignored) {
                throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
            }

            int userIdx = userDao.createUser(postUserReq);

            //jwt발급
            String jwt = jwtService.createJwt(userIdx);
            return new PostUserRes(jwt,userIdx);
        }
        else{
            throw new BaseException(PASSWORD_CONFIRM_ERROR);
        }

    }


    @Transactional
    public PostUserLoginRes loginUser(PostUserLoginReq postUserLoginReq) throws BaseException {
        //System.out.println("테스트1:"+postUserLoginReq.getEmailId());

        int userIdx = userProvider.checkAccount(postUserLoginReq.getEmailId(),postUserLoginReq.getPassword());
        String name;
        if(userProvider.checkLogExist(userIdx)!=1){  // 신규로 처음 로그인하는 사람을 위한
            name = userDao.recordLog(userIdx,"I");

        }
        else{
            if(userProvider.checkLog(userIdx).equals("I")){
                throw new BaseException(ALREADY_LOGGED);
            }
            name = userDao.recordLog(userIdx,"I");

        }


        //jwt발급
        String jwt = jwtService.createJwt(userIdx);

        return new PostUserLoginRes(jwt,userIdx,name);
    }



    public PatchUserLogoutRes patchLogout(int userIdx) throws BaseException {
        if(userProvider.checkLog(userIdx).equals("I")){
            PatchUserLogoutRes patchUserLogoutRes = userDao.patchLogout(userIdx);
            return patchUserLogoutRes;
        }
        else{
            throw new BaseException(ALREADY_LOGOUT);
        }
    }


    public PostUserLoginRes socialLogin(String emailId) throws BaseException {
        int userIdx = userDao.getUserIdxByEmail(emailId);
        //String name = userDao.getUserNameByEmail(emailId);
        String name;
        if(userProvider.checkLogExist(userIdx)!=1){  // 신규로 처음 로그인하는 사람을 위한
            name = userDao.recordLog(userIdx,"I");

        }
        else{
            if(userProvider.checkLog(userIdx).equals("I")){
                throw new BaseException(ALREADY_LOGGED);
            }
            name = userDao.recordLog(userIdx,"I");

        }

        String jwt = jwtService.createJwt(userIdx);
        PostUserLoginRes postUserLoginRes =new PostUserLoginRes(jwt,userIdx,name);


        return postUserLoginRes;

    }



    @Transactional
    public PostKakaoUserRes createKakaoUser (PostKakaoUserReq postKakaoUserReq) throws BaseException{
        if(userProvider.checkKakaoEmailId(postKakaoUserReq.getEmailId()) ==1){
            throw new BaseException(POST_USERS_EXISTS_EMAIL);

        }else{
            int userIdx = userDao.createKakaoUser(postKakaoUserReq);
            String name;
            if(userProvider.checkLogExist(userIdx)!=1){  // 신규로 처음 로그인하는 사람을 위한
                name = userDao.recordLog(userIdx,"I");

            }
            else{
                if(userProvider.checkLog(userIdx).equals("I")){
                    throw new BaseException(ALREADY_LOGGED);
                }
                name = userDao.recordLog(userIdx,"I");

            }

            //jwt발급
            String jwt = jwtService.createJwt(userIdx);
            return new PostKakaoUserRes(userIdx,jwt);

        }
    }



}
