package com.example.demo.src.store;

import com.example.demo.src.store.model.*;
import com.example.demo.src.user.model.GetUserRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.text.DecimalFormat;
import java.util.List;

@Repository
public class StoreDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<GetStoreAdRes> getStoreAd (int pageIdx){
        return this.jdbcTemplate.query("select idx,imgUrl from StoreAdvertisment\n" +
                        "where storePageIdx=?",
                (rs, rowNum) -> new GetStoreAdRes(
                        rs.getInt("idx"),
                        rs.getString("imgUrl")),
                pageIdx);
    }


    public List<GetStoreCategoryRes> getStoreCategory (int pageIdx){
        return this.jdbcTemplate.query("select idx,name,imageUrl,categoryOut from StorePage\n" +
                        "where parentIdx=?",
                (rs, rowNum) -> new GetStoreCategoryRes(
                        rs.getInt("idx"),
                        rs.getString("name"),
                        rs.getString("imageUrl"),
                        rs.getString("categoryOut")),
                pageIdx);
    }


    public List<GetStoreTDPDRes> getStoreTDPD (int userIdx){
        DecimalFormat formatter = new DecimalFormat("###,###");
        return this.jdbcTemplate.query("select Product.idx, ProductImg.imgUrl,\n" +
                        "       Case When ProductScrap.status is Null THEN 'F'\n" +
                        "        ELSE ProductScrap.status END as scrapStatus,\n" +
                        "       DATEDIFF(TodayDeal.deadline,now())+1 as leftTime # 23시간남아도 하루 남았다고 하므로 1을 더해줌\n" +
                        "     , Brand.name as brandName, Product.name as productName\n" +
                        "     , CASE WHEN TodayDeal.status='T' then TRUNCATE((Product.fixedPrice-TodayDeal.salePrice)/Product.fixedPrice*100,0)\n" +
                        "         ELSE TRUNCATE((Product.fixedPrice-Product.salePrice)/Product.fixedPrice*100,0) END as percent\n" +
                        "     , CASE WHEN TodayDeal.status='T' then TodayDeal.salePrice\n" +
                        "         ELSE Product.salePrice END as salePrice, setProductStatus\n" +
                        "     , RelatedP.relatedProduct as relatedProduct\n" +
                        "     , CASE WHEN ROUND(AVG(ProductReview.starpoint),1) is null THEN 0\n" +
                        "         ELSE ROUND(AVG(ProductReview.starpoint),1) END as starpoint,\n" +
                        "       CASE WHEN SUM(ProductReview.reviewNum) is null THEN 0\n" +
                        "         ELSE ROUND(SUM(ProductReview.reviewNum),0) END as reviewNum\n" +
                        "     , specialStatus from Product\n" +
                        "left outer join TodayDeal\n" +
                        "on TodayDeal.productIdx=Product.idx\n" +
                        "\n" +
                        "inner join Brand\n" +
                        "on Brand.idx=Product.brandIdx\n" +
                        "\n" +
                        "inner join (select imgUrl,productIdx from ProductImg\n" +
                        "group by productIdx) ProductImg\n" +
                        "on ProductImg.productIdx=Product.idx\n" +
                        "\n" +
                        "inner join (select Product.idx as productIdx,CASE WHEN SetRelation.productIdx is NULL THEN Product.idx\n" +
                        "         ELSE SetRelation.productIdx END as relatedProduct from Product\n" +
                        "left outer join SetRelation\n" +
                        "on SetRelation.setProductIdx=Product.idx) RelatedP\n" +
                        "on RelatedP.productIdx=Product.idx\n" +
                        "\n" +
                        "left outer join (select AVG(starpoint) as starpoint,COUNT(CASE WHEN Review.status='T' THEN 1 END) as reviewNum, Review.productIdx from Review\n" +
                        "group by Review.productIdx) ProductReview\n" +
                        "on ProductReview.productIdx=RelatedP.relatedProduct\n" +
                        "\n" +
                        "# 특정 사용자에 대한 scrap 여부 확인하기 위해서 left outer join함\n" +
                        "left outer join (select User.idx, User.name,contentIdx,evalableIdx,Scrap.status from User\n" +
                        "inner join Scrap\n" +
                        "on Scrap.userIdx = User.idx\n" +
                        "where User.idx=?) ProductScrap\n" +
                        "on ProductScrap.contentIdx=Product.idx and ProductScrap.evalableIdx=Product.evalableIdx\n" +
                        "where TodayDeal.status='T'\n" +
                        "#where TodayDeal.status is null # 오늘의 딜 제외하고 출력\n" +
                        "group by Product.idx\n" +
                        "order by scrapStatus DESC\n" +
                        "Limit 4",
                (rs, rowNum) -> new GetStoreTDPDRes(
                        rs.getInt("idx"),
                        rs.getString("imgUrl"),
                        rs.getString("scrapStatus"),
                        rs.getString("leftTime")+"일",
                        rs.getString("brandName"),
                        "[오늘의딜]"+rs.getString("productName"),
                        rs.getString("percent")+"%",
                        rs.getString("setProductStatus"),
                        formatter.format(rs.getInt("salePrice")),
                        rs.getString("starpoint"),
                        "리뷰 "+rs.getString("reviewNum")),
                userIdx);
    }



    public List<GetStorePopPDRes> getStorePopPD (int userIdx){
        DecimalFormat formatter = new DecimalFormat("###,###");
        return this.jdbcTemplate.query("select Product.idx, ProductImg.imgUrl,\n" +
                        "       Case When ProductScrap.status is Null THEN 'F'\n" +
                        "        ELSE ProductScrap.status END as scrapStatus\n" +
                        "     , Brand.name as brandName, Product.name as productName\n" +
                        "     , CASE WHEN TodayDeal.status='T' then TRUNCATE((Product.fixedPrice-TodayDeal.salePrice)/Product.fixedPrice*100,0)\n" +
                        "         ELSE TRUNCATE((Product.fixedPrice-Product.salePrice)/Product.fixedPrice*100,0) END as percent\n" +
                        "     , CASE WHEN TodayDeal.status='T' then TodayDeal.salePrice\n" +
                        "         ELSE Product.salePrice END as salePrice, setProductStatus\n" +
                        "     , RelatedP.relatedProduct as relatedProduct\n" +
                        "     , CASE WHEN ROUND(AVG(ProductReview.starpoint),1) is null THEN 0\n" +
                        "         ELSE ROUND(AVG(ProductReview.starpoint),1) END as starpoint,\n" +
                        "       CASE WHEN SUM(ProductReview.reviewNum) is null THEN 0\n" +
                        "         ELSE ROUND(SUM(ProductReview.reviewNum),0) END as reviewNum\n" +
                        "     , specialStatus from Product\n" +
                        "left outer join TodayDeal\n" +
                        "on TodayDeal.productIdx=Product.idx\n" +
                        "\n" +
                        "inner join Brand\n" +
                        "on Brand.idx=Product.brandIdx\n" +
                        "\n" +
                        "inner join (select imgUrl,productIdx from ProductImg\n" +
                        "group by productIdx) ProductImg\n" +
                        "on ProductImg.productIdx=Product.idx\n" +
                        "\n" +
                        "inner join (select Product.idx as productIdx,CASE WHEN SetRelation.productIdx is NULL THEN Product.idx\n" +
                        "         ELSE SetRelation.productIdx END as relatedProduct from Product\n" +
                        "left outer join SetRelation\n" +
                        "on SetRelation.setProductIdx=Product.idx) RelatedP\n" +
                        "on RelatedP.productIdx=Product.idx\n" +
                        "\n" +
                        "left outer join (select AVG(starpoint) as starpoint,COUNT(CASE WHEN Review.status='T' THEN 1 END) as reviewNum, Review.productIdx from Review\n" +
                        "group by Review.productIdx) ProductReview\n" +
                        "on ProductReview.productIdx=RelatedP.relatedProduct\n" +
                        "\n" +
                        "# 특정 사용자에 대한 scrap 여부 확인하기 위해서 left outer join함\n" +
                        "left outer join (select User.idx, User.name,contentIdx,evalableIdx,Scrap.status from User\n" +
                        "inner join Scrap\n" +
                        "on Scrap.userIdx = User.idx\n" +
                        "where User.idx=?) ProductScrap\n" +
                        "on ProductScrap.contentIdx=Product.idx and ProductScrap.evalableIdx=Product.evalableIdx\n" +
                        "#where TodayDeal.status='T'\n" +
                        "where TodayDeal.status is null # 오늘의 딜 제외하고 출력\n" +
                        "group by Product.idx",
                (rs, rowNum) -> new GetStorePopPDRes(
                        rs.getInt("idx"),
                        rs.getString("imgUrl"),
                        rs.getString("scrapStatus"),
                        rs.getString("brandName"),
                        "[오늘의딜]"+rs.getString("productName"),
                        rs.getString("percent")+"%",
                        rs.getString("setProductStatus"),
                        formatter.format(rs.getInt("salePrice")),
                        rs.getString("starpoint"),
                        "리뷰 "+rs.getString("reviewNum"),
                        rs.getString("specialStatus")),
                userIdx);
    }


}
