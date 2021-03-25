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

    public List<GetScrapPictureRes> getScrapPicture(int userIdx){
        return this.jdbcTemplate.query("select Scrap.idx as scrapIdx,PicturePost.idx as picturepostIdx,P.pictureUrl as thumbnailImageUrl ,PicturePost.comment as comment\n" +
                        "    from Scrap\n" +
                        "    inner join PicturePost on PicturePost.evalableIdx=Scrap.evalableIdx and PicturePost.Idx=Scrap.contentIdx\n" +
                        "    inner join Pictures P on PicturePost.idx = P.picturepostIdx\n" +
                        "    where Scrap.userIdx = ? and Scrap.status='T' GROUP BY scrapIdx",
                (rs, rowNum) -> new GetScrapPictureRes(
                        rs.getInt("scrapIdx"),
                        rs.getInt("picturepostIdx"),
                        rs.getString("thumbnailImageUrl"),
                        rs.getString("comment")),
                userIdx);
    }
    public List<GetScrapHWRes> getScrapHW(int userIdx){
        return this.jdbcTemplate.query("select Scrap.idx as scrapIdx,Housewarming.idx as HWIdx,Housewarming.thumbnailImageUrl,Housewarming.name as title,U.name as userName from Scrap\n" +
                        "inner join Housewarming on Housewarming.evalableIdx=Scrap.evalableIdx and Housewarming.Idx=Scrap.contentIdx\n" +
                        "inner join User U on Housewarming.useridx = U.idx\n" +
                        "where Scrap.userIdx = ? and Scrap.status='T' GROUP BY scrapIdx",
                (rs, rowNum) -> new GetScrapHWRes(
                        rs.getInt("scrapIdx"),
                        rs.getInt("HWIdx"),
                        rs.getString("thumbnailImageUrl"),
                        rs.getString("title"),
                        rs.getString("userName")),
                userIdx);
    }

    public int getHWCount(int userIdx){
        return this.jdbcTemplate.queryForObject("select (select count(idx)) as scrapHWCount from Scrap where userIdx = ?" +
                " and evalableIdx=1 and Scrap.status='T'",int.class,userIdx);
    }
    public int getPictureCount(int userIdx){
        return this.jdbcTemplate.queryForObject("select (select count(idx)) as scrapPictureCount from Scrap where userIdx = ? and evalableIdx=3 and Scrap.status='T'\n",int.class,userIdx);
    }

    public int getHWHeartCount(int userIdx){
        return this.jdbcTemplate.queryForObject("select (select count(idx)) as heartHWCount\n" +
                "from Heart\n" +
                "where userIdx = ?\n" +
                "  and evalableIdx = 1\n" +
                "  and Heart.status = 'T'",int.class,userIdx);
    }
    public int getPictureHeartCount(int userIdx){
        return this.jdbcTemplate.queryForObject("select (select count(idx)) as heartHWCount\n" +
                "from Heart\n" +
                "where userIdx = ?\n" +
                "  and evalableIdx = 3\n" +
                "  and Heart.status = 'T'",int.class,userIdx);
    }

    public List<GetHeartHWRes> getHeartHW(int userIdx){
        return this.jdbcTemplate.query("select Heart.idx as heartIdx,Housewarming.idx as HWIdx,Housewarming.thumbnailImageUrl,Housewarming.name as title,U.name as userName from Heart\n" +
                        "inner join Housewarming on Housewarming.evalableIdx=Heart.evalableIdx and Housewarming.Idx=Heart.contentIdx\n" +
                        "inner join User U on Housewarming.useridx = U.idx\n" +
                        "where Heart.userIdx = ? and Heart.status='T' GROUP BY heartIdx",
                (rs, rowNum) -> new GetHeartHWRes(
                        rs.getInt("heartIdx"),
                        rs.getInt("HWIdx"),
                        rs.getString("thumbnailImageUrl"),
                        rs.getString("title"),
                        rs.getString("userName")),
                userIdx);
    }
    public List<GetHeartPictureRes> getHeartPicture(int userIdx){
        return this.jdbcTemplate.query("select Heart.idx           as heartIdx,\n" +
                        "       PicturePost.idx     as picturepostIdx,\n" +
                        "       P.pictureUrl        as thumbnailImageUrl from Heart\n" +
                        "         inner join PicturePost on PicturePost.evalableIdx = Heart.evalableIdx and PicturePost.Idx = Heart.contentIdx\n" +
                        "         inner join Pictures P on PicturePost.idx = P.picturepostIdx\n" +
                        "where Heart.userIdx = ?\n" +
                        "  and Heart.status = 'T'\n" +
                        "GROUP BY heartIdx\n",
                (rs, rowNum) -> new GetHeartPictureRes(
                        rs.getInt("heartIdx"),
                        rs.getInt("picturepostIdx"),
                        rs.getString("thumbnailImageUrl")),
                userIdx);
    }



}