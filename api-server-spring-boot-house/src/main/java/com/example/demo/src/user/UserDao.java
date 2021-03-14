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
        this.jdbcTemplate.update("insert into User (createdAt, name, emailId, pw, mandatoryConsent,optionalConsent ) VALUES (NOW(),?,?,?,?,?)",
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



    public PostUserLoginPWRes checkAccount(String email){
        return this.jdbcTemplate.queryForObject("select emailId, pw, idx from User where emailId=?",
                (rs, rowNum) -> new PostUserLoginPWRes(
                        rs.getString("emailId"),
                        rs.getString("pw"),
                        rs.getInt("idx")),
                email);
    }


    public int recordLog(int userIdx,String status){
        this.jdbcTemplate.update("insert into LogHistory (createdAt, userIdx,status ) VALUES (NOW(),?,?)",
                userIdx,status
        );
        return 1;
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



}