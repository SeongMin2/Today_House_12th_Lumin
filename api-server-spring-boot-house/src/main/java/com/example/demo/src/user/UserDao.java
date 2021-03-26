package com.example.demo.src.user;


import com.example.demo.src.user.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class UserDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<GetUserRes> getUsers(String email){
        return this.jdbcTemplate.query("select * from UserInfo where email =?",
                (rs, rowNum) -> new GetUserRes(
                        rs.getInt("userIdx"),
                        rs.getString("userName"),
                        rs.getString("ID"),
                        rs.getString("Email"),
                        rs.getString("password")),
                email);
    }

    public GetUserRes getUser(int userIdx){
        return this.jdbcTemplate.queryForObject("select * from UserInfo where userIdx = ?",
                (rs, rowNum) -> new GetUserRes(
                        rs.getInt("userIdx"),
                        rs.getString("userName"),
                        rs.getString("ID"),
                        rs.getString("Email"),
                        rs.getString("password")),
                userIdx);
    }


    public int createUser(PostUserReq postUserReq){
        this.jdbcTemplate.update("insert into User (createdAt, name, emailId, pw, mandatoryConsent,optionalConsent,userimageUrl ) VALUES (NOW(),?,?,?,?,?,'F')",
                new Object[]{postUserReq.getNickName(), postUserReq.getEmailId(), postUserReq.getPassword(), postUserReq.getMandatoryConsent(),postUserReq.getOptionalConsent(),}
        );
        return this.jdbcTemplate.queryForObject("select last_insert_id()",int.class);
    }



    public int checkName(String name){
        return this.jdbcTemplate.queryForObject("select exists(select name from User where name = ?)",
                int.class,
                name);
    }



    public int checkEmail(String email){
        return this.jdbcTemplate.queryForObject("select exists(select emailId from User where emailId = ?)",
                int.class,
                email);

    }

    public int checkKakaoEmail(String email){
        return this.jdbcTemplate.queryForObject("select exists(select emailId from User where emailId = ? and kakaoSocial='T')",
                int.class,
                email);

    }



    public PostUserLoginPWRes checkAccount(String email){
        return this.jdbcTemplate.queryForObject("select emailId, pw, idx from User where emailId=?",
                (rs, rowNum) -> new PostUserLoginPWRes(
                        rs.getString("emailId"),
                        rs.getString("pw"),
                        rs.getInt("idx")),
                email);
    }


    public String recordLog(int userIdx,String status){
        this.jdbcTemplate.update("insert into LogHistory (createdAt, userIdx,status ) VALUES (NOW(),?,?)",
                userIdx,status
        );
        return this.jdbcTemplate.queryForObject("select name from User\n" +
                        "where Idx=?",
                String.class,
                userIdx);
    }



    public String checkLog(int userIdx){
        return this.jdbcTemplate.queryForObject("select status from LogHistory\n" +
                        "where idx=(select max(idx) from (select idx from LogHistory where userIdx=?) a)",
                String.class,
                userIdx);
    }



    public int checkLogExist(int userIdx){
        return this.jdbcTemplate.queryForObject("select EXISTS(select idx from LogHistory where userIdx=?) as exist",
                int.class,
                userIdx);
    }



    public List<GetNameListRes> getNameList (String name){
        return this.jdbcTemplate.query("select name from User where name like '"+name+"%'",
                (rs, rowNum) -> new GetNameListRes(
                        rs.getString("name")));
    }



    public PatchUserLogoutRes patchLogout(int userIdx){
        this.jdbcTemplate.update("update LogHistory set status='O', updatedAt =NOW()\n" +
                        "where idx=(select max(idx) from (select idx from LogHistory where userIdx=?) a);",
                userIdx
        );
        return this.jdbcTemplate.queryForObject("select LogHistory.idx,userIdx,LogHistory.status,name from LogHistory\n" +
                        "inner join User\n" +
                        "on User.idx=LogHistory.userIdx\n" +
                        "where LogHistory.idx=(select max(idx) from (select idx from LogHistory where userIdx=?) a)",
                (rs, rowNum) -> new PatchUserLogoutRes(
                        rs.getInt("idx"),
                        rs.getInt("userIdx"),
                        rs.getString("status"),
                        rs.getString("name")),
                userIdx);
    }



    public char checkKakaoSocial(String emailId){
        return this.jdbcTemplate.queryForObject("select Case when kakaoSocial is null then 'F'\n" +
                        "        else kakaoSocial end as kakaoSocial from User\n" +
                        "where emailId=?",
                char.class,
                emailId);
    }



    public int getUserIdxByEmail(String emailId){
        return this.jdbcTemplate.queryForObject("select idx from User\n" +
                        "where emailId=?",
                int.class,
                emailId);
    }


    public String getUserNameByEmail(String emailId){
        return this.jdbcTemplate.queryForObject("select name from User\n" +
                        "where emailId=?",
                String.class,
                emailId);
    }


    public int createKakaoUser(PostKakaoUserReq postKakaoUserReq){
        this.jdbcTemplate.update("insert into User (createdAt, name, emailId, pw, mandatoryConsent,optionalConsent,kakaoSocial) VALUES (NOW(),?,?,'kakaoUser','F','F','T')",
                new Object[]{postKakaoUserReq.getNickName(),postKakaoUserReq.getEmailId(),}
        );
        return this.jdbcTemplate.queryForObject("select last_insert_id()",int.class);
    }



    public int postJwt(String jwt){
        this.jdbcTemplate.update("insert into JwtManagement (createdAt,status,jwt) VALUES (NOW(),'T',?)",
                jwt
        );
        return this.jdbcTemplate.queryForObject("select last_insert_id()",int.class);
    }


    public int patchJwtStatus(String jwt){
        this.jdbcTemplate.update("update JwtManagement set status='F',updatedAt=now() where jwt=?",
                jwt
        );
        return 1;
    }


    public int checkJwt(String jwt){
        return this.jdbcTemplate.queryForObject("select EXISTS(select status from JwtManagement\n" +
                        "where jwt=? and status='F') as exist",
                int.class,
                jwt);
    }



}
