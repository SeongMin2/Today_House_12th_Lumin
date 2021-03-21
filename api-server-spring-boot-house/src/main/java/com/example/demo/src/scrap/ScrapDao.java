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

    public int checkScrapExist(int userIdx,int contentIdx,int evalableIdx){
        return this.jdbcTemplate.queryForObject("select Exists(select status from Scrap\n" +
                "where contentIdx=? and evalableIdx=? and userIdx=? ) as ScrapExist", int.class,contentIdx,evalableIdx,userIdx);
    }
    public char checkScrap(int userIdx,int contentIdx,int evalableIdx){
        return this.jdbcTemplate.queryForObject("select status from Scrap \n" +
                "where contentIdx=? and evalableIdx =? and userIdx=?", char.class,contentIdx,evalableIdx,userIdx);
    }
    public PatchScrapRes patchScrap(String status,int userIdx,int contentIdx,int evalableIdx){
        this.jdbcTemplate.update("UPDATE Scrap set status=? \n" +
                        "where userIdx=? and contentIdx=? and evalableIdx=?",
                status,userIdx,contentIdx,evalableIdx);
        return this.jdbcTemplate.queryForObject("select status,userIdx,contentIdx,evalableIdx from Scrap\n" +
                        "where userIdx=? and contentIdx=? and evalableIdx=?",  // queryForObject는 하나만 반환할 때 사용
                (rs, rowNum) -> new PatchScrapRes(
                        rs.getInt("userIdx"),
                        rs.getInt("contentIdx"),
                        rs.getInt("evalableIdx"),
                        rs.getString("status")),
                userIdx,contentIdx,evalableIdx);
    }


    public PatchScrapRes createScrap(String status, int userIdx, int contentIdx,int evalableIdx){
        this.jdbcTemplate.update("insert into Scrap (userIdx,contentIdx,evalableIdx,createdAt,status) VALUES (?,?,?,now(),?)",
                userIdx,contentIdx,evalableIdx,status);
        return this.jdbcTemplate.queryForObject("select userIdx,contentIdx,evalableIdx,status from Scrap\n" +
                        "where userIdx=? and contentIdx=? and evalableIdx=?",
                (rs, rowNum) -> new PatchScrapRes(
                        rs.getInt("userIdx"),
                        rs.getInt("contentIdx"),
                        rs.getInt("evalableIdx"),
                        rs.getString("status")),
                userIdx,contentIdx,evalableIdx);
    }
}