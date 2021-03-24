package com.example.demo.src.scrap;


import com.example.demo.src.scrap.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class ScrapDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {

        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
    public int checkEvalableExist(int evalableIdx){
        return this.jdbcTemplate.queryForObject("select Exists(select * from Evalable where idx=?)",int.class,evalableIdx);
    }

    public int checkScrapExist(int userIdx,int evalableIdx,int contentIdx){
        return this.jdbcTemplate.queryForObject("select Exists(select status from Scrap\n" +
                "where evalableIdx=? and contentIdx=? and userIdx=? ) as ScrapExist", int.class,evalableIdx,contentIdx,userIdx);
    }
    public char checkScrap(int userIdx,int evalableIdx,int contentIdx){
        return this.jdbcTemplate.queryForObject("select status from Scrap \n" +
                "where evalableIdx =? and contentIdx=? and userIdx=?", char.class,evalableIdx,contentIdx,userIdx);
    }
    public PatchScrapRes patchScrap(String status,int userIdx,int evalableIdx,int contentIdx){
        this.jdbcTemplate.update("UPDATE Scrap set status=? \n" +
                        "where userIdx=? and evalableIdx=? and contentIdx=?",
                status,userIdx,evalableIdx,contentIdx);
        return this.jdbcTemplate.queryForObject("select idx as scrapIdx,status,userIdx,evalableIdx,contentIdx from Scrap\n" +
                        "where userIdx=? and evalableIdx=? and contentIdx=?",  // queryForObject는 하나만 반환할 때 사용
                (rs, rowNum) -> new PatchScrapRes(
                        rs.getInt("scrapIdx"),
                        rs.getInt("userIdx"),
                        rs.getInt("evalableIdx"),
                        rs.getInt("contentIdx"),
                        rs.getString("status")),
                userIdx,evalableIdx,contentIdx);
    }


    public PatchScrapRes createScrap(String status, int userIdx,int evalableIdx,int contentIdx){
        this.jdbcTemplate.update("insert into Scrap (userIdx,evalableIdx,contentIdx,createdAt,status) VALUES (?,?,?,now(),?)",
                userIdx,evalableIdx,contentIdx,status);
        return this.jdbcTemplate.queryForObject("select idx as scrapIdx,userIdx,evalableIdx,contentIdx,status from Scrap\n" +
                        "where userIdx=? and evalableIdx=? and contentIdx=? ",
                (rs, rowNum) -> new PatchScrapRes(
                        rs.getInt("scrapIdx"),
                        rs.getInt("userIdx"),
                        rs.getInt("evalableIdx"),
                        rs.getInt("contentIdx"),
                        rs.getString("status")),
                userIdx,evalableIdx,contentIdx);
    }
}