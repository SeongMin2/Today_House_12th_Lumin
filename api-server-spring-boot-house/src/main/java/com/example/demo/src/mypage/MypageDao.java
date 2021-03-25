package com.example.demo.src.mypage;


import com.example.demo.src.mypage.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class MypageDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {

        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public GetMypageUserRes getMypageUser(int userIdx){
        return this.jdbcTemplate.queryForObject("select User.Idx                   as userIdx,\n" +
                        "       name                       as userName,\n" +
                        "       userimageUrl,\n" +
                        "       (select count(Scrap.idx)\n" +
                        "\n" +
                        "        FROM Scrap\n" +
                        "\n" +
                        "        WHERE User.idx=Scrap.userIdx\n" +
                        "          and Scrap.status = 'T') as scrapCount,\n" +
                        "       (select count(Heart.idx)\n" +
                        "\n" +
                        "        FROM Heart\n" +
                        "\n" +
                        "        WHERE User.idx=Heart.userIdx\n" +
                        "          and Heart.status = 'T') as heartCount\n" +
                        "from User where User.idx=?",
                (rs, rowNum) -> new GetMypageUserRes(
                        rs.getInt("userIdx"),
                        rs.getString("userName"),
                        rs.getString("userimageUrl"),
                        rs.getInt("scrapCount"),
                        rs.getInt("heartCount")),
                userIdx);
    }

    public List<GetPictureDetailRes> getPictureDetail(int userIdx){
        return this.jdbcTemplate.query("select Pictures.idx,pictureUrl,place FROM Pictures INNER JOIN PicturePost PP on Pictures.picturepostIdx = PP.idx\n" +
                        "inner join User U on PP.userIdx = U.idx where U.idx=?",
                (rs, rowNum) -> new GetPictureDetailRes(
                        rs.getString("pictureUrl"),
                        rs.getString("place")),
                userIdx);
    }
    public int countPicture(int userIdx){
        return this.jdbcTemplate.queryForObject("select (select count(pictureUrl)) as countPicture\n" +
                "from Pictures\n" +
                "         inner join PicturePost PP on Pictures.picturepostIdx = PP.idx\n" +
                "         inner join User U on PP.useridx = U.idx\n" +
                "where userIdx = ?",int.class,userIdx);
    }

}