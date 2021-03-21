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
        return this.jdbcTemplate.query("select Product.idx, Product.evalableIdx, ProductImg.imgUrl,\n" +
                        "       Case When ProductScrap.status is Null THEN 'F'\n" +
                        "        ELSE ProductScrap.status END as scrapStatus,\n" +
                        "       CASE WHEN TIMESTAMPDIFF(SECOND,now(),TodayDeal.deadline) <=0 then 0\n" +
                        "           WHEN TIMESTAMPDIFF(SECOND,now(),TodayDeal.deadline)>86400 then DATEDIFF(TodayDeal.deadline,now())\n" +
                        "           ELSE SEC_TO_TIME(TIMESTAMPDIFF(SECOND,now(),TodayDeal.deadline)) END as leftTime # 23시간남아도 하루 남았다고 하므로 1을 더해줌\n" +
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
                        "     , specialStatus\n" +
                        "     , CASE WHEN DeliveryandRefund.deliveryCost=0 THEN 'T'\n" +
                        "         ELSE 'F' END as freeDeliveryStatus from Product\n" +
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
                        "\n" +
                        "inner join DeliveryandRefund\n" +
                        "on DeliveryandRefund.productIdx=Product.idx\n" +
                        "\n" +
                        "where TodayDeal.status='T'\n" +
                        "#where TodayDeal.status is null # 오늘의 딜 제외하고 출력\n" +
                        "group by Product.idx\n" +
                        "order by scrapStatus DESC\n" +
                        "Limit 4",
                (rs, rowNum) -> new GetStoreTDPDRes(
                        rs.getInt("idx"),
                        rs.getInt("evalableIdx"),
                        rs.getString("imgUrl"),
                        rs.getString("scrapStatus"),
                        rs.getString("leftTime")+"일",
                        rs.getString("brandName"),
                        "[오늘의딜]"+rs.getString("productName"),
                        rs.getString("percent")+"%",
                        rs.getString("setProductStatus"),
                        formatter.format(rs.getInt("salePrice")),
                        rs.getString("starpoint"),
                        "리뷰 "+rs.getString("reviewNum"),
                        rs.getString("freeDeliveryStatus")),
                userIdx);
    }



    public List<GetStorePopPDRes> getStorePopPD (int userIdx){
        DecimalFormat formatter = new DecimalFormat("###,###");
        return this.jdbcTemplate.query("select Product.idx, Product.evalableIdx, ProductImg.imgUrl,\n" +
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
                        "     , specialStatus\n" +
                        "     , CASE WHEN DeliveryandRefund.deliveryCost=0 THEN 'T'\n" +
                        "         ELSE 'F' END as freeDeliveryStatus from Product\n" +
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
                        "\n" +
                        "inner join DeliveryandRefund\n" +
                        "on DeliveryandRefund.productIdx=Product.idx\n" +
                        "\n" +
                        "#where TodayDeal.status='T'\n" +
                        "where TodayDeal.status is null # 오늘의 딜 제외하고 출력\n" +
                        "group by Product.idx",
                (rs, rowNum) -> new GetStorePopPDRes(
                        rs.getInt("idx"),
                        rs.getInt("evalableIdx"),
                        rs.getString("imgUrl"),
                        rs.getString("scrapStatus"),
                        rs.getString("brandName"),
                        "[오늘의딜]"+rs.getString("productName"),
                        rs.getString("percent")+"%",
                        rs.getString("setProductStatus"),
                        formatter.format(rs.getInt("salePrice")),
                        rs.getString("starpoint"),
                        "리뷰 "+rs.getString("reviewNum"),
                        rs.getString("specialStatus"),
                        rs.getString("freeDeliveryStatus")),
                userIdx);
    }


    public List<GetStoreProductImgRes> getProductImg(int productIdx){
        return this.jdbcTemplate.query("select idx,productIdx,imgUrl from ProductImg\n" +
                        "where productIdx=?",
                (rs, rowNum) -> new GetStoreProductImgRes(
                        rs.getInt("idx"),
                        rs.getString("imgUrl")
                ),
                productIdx);
    }






    public GetStoreProductInfoRes getProductInfo(int userIdx,int productIdx){
        DecimalFormat formatter = new DecimalFormat("###,###");
        return this.jdbcTemplate.queryForObject("select Product.idx,\n" +
                        "       Case When ProductScrap.status is Null THEN 'F'\n" +
                        "        ELSE ProductScrap.status END as scrapStatus,\n" +
                        "       Case when scrapNum.scrapCount is null then 0\n" +
                        "        ELSE scrapNum.scrapCount END as scrapCount,\n" +
                        "       CASE WHEN TodayDeal.status='T' THEN\n" +
                        "           CASE WHEN TIMESTAMPDIFF(SECOND,now(),TodayDeal.deadline) <=0 then 0\n" +
                        "           WHEN TIMESTAMPDIFF(SECOND,now(),TodayDeal.deadline)>86400 then DATEDIFF(TodayDeal.deadline,now())\n" +
                        "           ELSE SEC_TO_TIME(TIMESTAMPDIFF(SECOND,now(),TodayDeal.deadline)) END\n" +
                        "         ELSE 0 END as leftTime\n" +
                        "     , Brand.name as brandName, Product.name as productName\n" +
                        "     , CASE WHEN TodayDeal.status='T' then TRUNCATE((Product.fixedPrice-TodayDeal.salePrice)/Product.fixedPrice*100,0)\n" +
                        "         ELSE TRUNCATE((Product.fixedPrice-Product.salePrice)/Product.fixedPrice*100,0) END as percent,\n" +
                        "       Product.fixedPrice as originalPrice\n" +
                        "     , CASE WHEN TodayDeal.status='T' then TodayDeal.salePrice\n" +
                        "         ELSE Product.salePrice END as salePrice\n" +
                        "     , CASE WHEN TodayDeal.status='T' then ROUND(TodayDeal.salePrice*Product.saveRate/100,0)\n" +
                        "         ELSE ROUND(Product.salePrice*Product.saveRate/100,0) END as savePoint ,saveRate\n" +
                        "     ,deliveryCost, freeConditionPrice,outDescription,limitedArea,additionalCost,setProductStatus\n" +
                        "     , RelatedP.relatedProduct as relatedProduct\n" +
                        "     , CASE WHEN ROUND(AVG(ProductReview.starpoint),1) is null THEN 0\n" +
                        "         ELSE ROUND(AVG(ProductReview.starpoint),1) END as starpoint,\n" +
                        "       CASE WHEN SUM(ProductReview.reviewNum) is null THEN 0\n" +
                        "         ELSE ROUND(SUM(ProductReview.reviewNum),0) END as reviewNum\n" +
                        "     , specialStatus\n" +
                        "     , CASE WHEN Product.productInfo is Null THEN '0'\n" +
                        "         ELSE Product.productInfo END as productInfoImgUrl from Product\n" +
                        "left outer join TodayDeal\n" +
                        "on TodayDeal.productIdx=Product.idx\n" +
                        "\n" +
                        "inner join Brand\n" +
                        "on Brand.idx=Product.brandIdx\n" +
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
                        "\n" +
                        "inner join DeliveryandRefund\n" +
                        "on DeliveryandRefund.productIdx = Product.idx\n" +
                        "\n" +
                        "left outer join (select Scrap.contentIdx,COUNT(CASE WHEN Scrap.status='T' THEN 1 END) as scrapCount from Scrap\n" +
                        "where evalableIdx=2\n" +
                        "group by contentIdx) scrapNum\n" +
                        "on scrapNum.contentIdx=Product.idx\n" +
                        "\n" +
                        "where Product.idx=?  # 제품결정\n" +
                        "group by Product.idx",
                (rs, rowNum) -> new GetStoreProductInfoRes(
                        rs.getInt("idx"),
                        rs.getString("scrapStatus"),
                        rs.getString("scrapCount"),
                        rs.getString("leftTime"),  // 이거 오늘의 딜인 경우 아닌경우해서 provider 에서 붙여줘야함
                        rs.getString("brandName"),
                        rs.getString("productName"),  // 이것도 오늘의 딜인 경우 아닌경우 해줘야함
                        rs.getString("percent")+"%",
                        formatter.format(rs.getInt("originalPrice")),
                        formatter.format(rs.getInt("salePrice")),   // 이것도 오늘의 딜인 경우 아닌경우
                        rs.getString("specialStatus"),
                        formatter.format(rs.getInt("savePoint")),     // 이것은 세트인지 아닌지에 따라 다르게 해줘야 함
                        rs.getString("saveRate"),      // 이것도 세트인지 아닌지에 따라
                        rs.getString("deliveryCost"),   // 이것은 0 인지 아닌지에 따라 무료배송 해줘야 함
                        rs.getString("freeConditionPrice"),   // 이것도 일부 처리해줘야함
                        rs.getString("outDescription"),
                        rs.getString("limitedArea"),
                        formatter.format(rs.getInt("additionalCost")),
                        rs.getString("setProductStatus"),
                        rs.getString("starpoint"),
                        "("+rs.getString("reviewNum")+")",
                        rs.getString("productInfoImgUrl")
                        ),
                userIdx,productIdx);
    }


    public GetStoreProductStarRes getProductStar(int productIdx){
        return this.jdbcTemplate.queryForObject("select Product.idx,  Product.name\n" +
                        "     , RelatedP.relatedProduct as relatedProduct,\n" +
                        "       SUM(CASE WHEN fiveNum is Null THEN 0 ELSE fiveNum END) as fiveNum,\n" +
                        "       SUM(CASE WHEN fourNum is Null THEN 0 ELSE fourNum END) as fourNum,\n" +
                        "       SUM(CASE WHEN threeNum is Null THEN 0 ELSE threeNum END) as threeNum,\n" +
                        "       SUM(CASE WHEN twoNum is Null THEN 0 ELSE twoNum END) as twoNum,\n" +
                        "       SUM(CASE WHEN oneNum is Null THEN 0 ELSE oneNum END) as oneNum from Product\n" +
                        "inner join (select Product.idx as productIdx,CASE WHEN SetRelation.productIdx is NULL THEN Product.idx\n" +
                        "         ELSE SetRelation.productIdx END as relatedProduct from Product\n" +
                        "left outer join SetRelation\n" +
                        "on SetRelation.setProductIdx=Product.idx) RelatedP\n" +
                        "on RelatedP.productIdx=Product.idx\n" +
                        "\n" +
                        "left outer join (select productIdx,\n" +
                        "       COUNT(CASE WHEN starPoint=5 THEN 1 END) as fiveNum,\n" +
                        "       COUNT(CASE WHEN starPoint=4 THEN 1 END) as fourNum,\n" +
                        "       COUNT(CASE WHEN starPoint=3 THEN 1 END) as threeNum,\n" +
                        "       COUNT(CASE WHEN starPoint=2 THEN 1 END) as twoNum,\n" +
                        "       COUNT(CASE WHEN starPoint=1 THEN 1 END) as oneNum from Review\n" +
                        "group by productIdx) starNum\n" +
                        "on starNum.productIdx= RelatedP.relatedProduct\n" +
                        "where Product.idx=?\n" +
                        "group by Product.idx",
                (rs, rowNum) -> new GetStoreProductStarRes(
                        rs.getInt("idx"),
                        rs.getString("fiveNum"),
                        rs.getString("fourNum"),
                        rs.getString("threeNum"),
                        rs.getString("twoNum"),
                        rs.getString("oneNum")),
                productIdx);
    }


    public List<GetStoreProductReviewRes> getProductReview(int productIdx){
        return this.jdbcTemplate.query("select Review.idx,Review.userIdx,productIdx,User.userimageUrl,name as userName,starPoint,\n" +
                        "       DATE_FORMAT(Review.createdAt,'%Y.%m.%d') as createdAt,fromLocal,imgUrl,content,\n" +
                        "       CASE WHEN helpfful.helpfulCount is Null THEN 0 #DATE_FORMAT(createdAt, '%Y년%m월%d일 %h시%i분%s초')\n" +
                        "         ELSE helpfful.helpfulCount END as helpful\n" +
                        "        from Review\n" +
                        "inner join User\n" +
                        "on User.idx=Review.userIdx\n" +
                        "\n" +
                        "left outer join (select reviewIdx,  COUNT(CASE WHEN Helpful.status='T' THEN 1 END) as helpfulCount from Helpful\n" +
                        "group by reviewIdx) helpfful\n" +
                        "on helpfful.reviewIdx=Review.idx\n" +
                        "\n" +
                        "where productIdx=?\n" +
                        "order by helpful DESC\n" +
                        "LIMIT 3",
                (rs, rowNum) -> new GetStoreProductReviewRes(
                        rs.getInt("idx"),
                        rs.getInt("userIdx"),
                        rs.getString("userimageUrl"),
                        rs.getString("userName"),
                        rs.getString("starPoint"),
                        rs.getString("createdAt"),
                        rs.getString("fromLocal"),
                        rs.getString("imgUrl"),
                        rs.getString("content")
                ),
                productIdx);
    }


    public List<GetStoreSetProductRes> getSetProduct(int productIdx,int userIdx){
        DecimalFormat formatter = new DecimalFormat("###,###");
        return this.jdbcTemplate.query("select Product.idx,RelatedP.selectOrder,ProductImg.imgUrl,\n" +
                        "       Case When ProductScrap.status is Null THEN 'F'\n" +
                        "        ELSE ProductScrap.status END as scrapStatus,\n" +
                        "       CASE WHEN TodayDeal.status='T' THEN\n" +
                        "           CASE WHEN TIMESTAMPDIFF(SECOND,now(),TodayDeal.deadline) <=0 then 0\n" +
                        "           WHEN TIMESTAMPDIFF(SECOND,now(),TodayDeal.deadline)>86400 then DATEDIFF(TodayDeal.deadline,now())\n" +
                        "           ELSE SEC_TO_TIME(TIMESTAMPDIFF(SECOND,now(),TodayDeal.deadline)) END\n" +
                        "         ELSE 0 END as leftTime\n" +
                        "     , Brand.name as brandName, Product.name as productName\n" +
                        "     , CASE WHEN TodayDeal.status='T' then TRUNCATE((Product.fixedPrice-TodayDeal.salePrice)/Product.fixedPrice*100,0)\n" +
                        "         ELSE TRUNCATE((Product.fixedPrice-Product.salePrice)/Product.fixedPrice*100,0) END as percent,\n" +
                        "       Product.fixedPrice as originalPrice\n" +
                        "     , CASE WHEN TodayDeal.status='T' then TodayDeal.salePrice\n" +
                        "         ELSE Product.salePrice END as salePrice\n" +
                        "     ,setProductStatus\n" +
                        "     , RelatedP.relatedProduct as relatedProduct\n" +
                        "     , CASE WHEN ROUND(AVG(ProductReview.starpoint),1) is null THEN 0\n" +
                        "         ELSE ROUND(AVG(ProductReview.starpoint),1) END as starpoint,\n" +
                        "       CASE WHEN SUM(ProductReview.reviewNum) is null THEN 0\n" +
                        "         ELSE ROUND(SUM(ProductReview.reviewNum),0) END as reviewNum\n" +
                        "     , specialStatus\n" +
                        "     , CASE WHEN DeliveryandRefund.deliveryCost=0 THEN 'T'\n" +
                        "         ELSE 'F' END as freeDeliveryStatus\n" +
                        "      from Product\n" +
                        "left outer join TodayDeal\n" +
                        "on TodayDeal.productIdx=Product.idx\n" +
                        "\n" +
                        "inner join Brand\n" +
                        "on Brand.idx=Product.brandIdx\n" +
                        "\n" +
                        "inner join (select Product.idx as productIdx,CASE WHEN SetRelation.productIdx is NULL THEN Product.idx\n" +
                        "         ELSE SetRelation.productIdx END as relatedProduct,selectOrder from Product\n" +
                        "left outer join SetRelation\n" +
                        "on SetRelation.setProductIdx=Product.idx\n" +
                        "where Product.idx=?) RelatedP\n" +
                        "on RelatedP.relatedProduct=Product.idx\n" +
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
                        "\n" +
                        "inner join DeliveryandRefund\n" +
                        "on DeliveryandRefund.productIdx = Product.idx\n" +
                        "\n" +
                        "inner join (select imgUrl,productIdx from ProductImg\n" +
                        "group by productIdx) ProductImg\n" +
                        "on ProductImg.productIdx=Product.idx\n" +
                        "\n" +
                        "group by Product.idx",
                (rs, rowNum) -> new GetStoreSetProductRes(
                        rs.getInt("idx"),
                        "선택 "+rs.getString("selectOrder"),
                        rs.getString("imgUrl"),
                        rs.getString("scrapStatus"),
                        rs.getString("brandName"),
                        rs.getString("productName"),
                        rs.getString("starpoint"),
                        "리뷰 "+rs.getString("reviewNum"),
                        rs.getString("percent")+"%",
                        formatter.format(rs.getInt("originalPrice")),
                        formatter.format(rs.getInt("salePrice")),
                        rs.getString("specialStatus"),
                        rs.getString("freeDeliveryStatus")
                ),
                productIdx,userIdx);
    }



    public List<GetProductOptionRes> getProductOption(int productIdx){
        return this.jdbcTemplate.query("select idx,productIdx,name,required from ProductOption\n" +
                        "where productIdx=?",
                (rs, rowNum) -> new GetProductOptionRes(
                        rs.getInt("idx"),
                        rs.getInt("productIdx"),
                        rs.getString("name"),
                        rs.getString("required")
                ),
                productIdx);
    }



    public List<GetProductOptionDetailRes> getProductOptionDetail(int productIdx){
        DecimalFormat formatter = new DecimalFormat("###,###");
        return this.jdbcTemplate.query("select OptionContent.idx, optionIdx,productIdx,OptionContent.name,OptionContent.status,price from ProductOption\n" +
                        "inner join OptionContent\n" +
                        "on OptionContent.optionIdx=ProductOption.idx\n" +
                        "\n" +
                        "where productIdx=?\n" +
                        "order by price",
                (rs, rowNum) -> new GetProductOptionDetailRes(
                        rs.getInt("idx"),
                        rs.getInt("optionIdx"),
                        rs.getInt("productIdx"),
                        rs.getString("name"),
                        rs.getInt("price"),
                        formatter.format(rs.getInt("price")),
                        rs.getString("status")
                ),
                productIdx);
    }



    public int checkSetProduct(int productIdx){
        return this.jdbcTemplate.queryForObject("select EXISTS(select setProductStatus from Product\n" +
                "where idx=? and setProductStatus='T') as exist", int.class,productIdx);
    }


    public List<GetOptionSetProductRes> getOptionSetProduct(int productIdx){
        DecimalFormat formatter = new DecimalFormat("###,###");
        return this.jdbcTemplate.query("select Product.idx,RelatedP.selectOrder,ProductImg.imgUrl,\n" +
                        "     Brand.name as brandName, Product.name as productName\n" +
                        "     , CASE WHEN TodayDeal.status='T' then TRUNCATE((Product.fixedPrice-TodayDeal.salePrice)/Product.fixedPrice*100,0)\n" +
                        "         ELSE TRUNCATE((Product.fixedPrice-Product.salePrice)/Product.fixedPrice*100,0) END as percent,\n" +
                        "       Product.fixedPrice as originalPrice\n" +
                        "     , CASE WHEN TodayDeal.status='T' then TodayDeal.salePrice\n" +
                        "         ELSE Product.salePrice END as salePrice\n" +
                        "     ,setProductStatus\n" +
                        "     , RelatedP.relatedProduct as relatedProduct\n" +
                        "     , specialStatus\n" +
                        "     , CASE WHEN DeliveryandRefund.deliveryCost=0 THEN 'T'\n" +
                        "         ELSE 'F' END as freeDeliveryStatus\n" +
                        "      from Product\n" +
                        "left outer join TodayDeal\n" +
                        "on TodayDeal.productIdx=Product.idx\n" +
                        "\n" +
                        "inner join Brand\n" +
                        "on Brand.idx=Product.brandIdx\n" +
                        "\n" +
                        "inner join (select Product.idx as productIdx,CASE WHEN SetRelation.productIdx is NULL THEN Product.idx\n" +
                        "         ELSE SetRelation.productIdx END as relatedProduct,selectOrder from Product\n" +
                        "left outer join SetRelation\n" +
                        "on SetRelation.setProductIdx=Product.idx\n" +
                        "where Product.idx=?) RelatedP\n" +
                        "on RelatedP.relatedProduct=Product.idx\n" +
                        "\n" +
                        "left outer join (select AVG(starpoint) as starpoint,COUNT(CASE WHEN Review.status='T' THEN 1 END) as reviewNum, Review.productIdx from Review\n" +
                        "group by Review.productIdx) ProductReview\n" +
                        "on ProductReview.productIdx=RelatedP.relatedProduct\n" +
                        "\n" +
                        "inner join DeliveryandRefund\n" +
                        "on DeliveryandRefund.productIdx = Product.idx\n" +
                        "\n" +
                        "inner join (select imgUrl,productIdx from ProductImg\n" +
                        "group by productIdx) ProductImg\n" +
                        "on ProductImg.productIdx=Product.idx\n" +
                        "\n" +
                        "group by Product.idx",
                (rs, rowNum) -> new GetOptionSetProductRes(
                        rs.getInt("idx"),
                        rs.getString("selectOrder"),
                        rs.getString("imgUrl"),
                        rs.getString("brandName"),
                        rs.getString("productName"),
                        rs.getString("percent")+"%",
                        formatter.format(rs.getInt("salePrice")),
                        rs.getString("specialStatus"),
                        rs.getString("freeDeliveryStatus")
                ),
                productIdx);
    }



    public int getMaxPrice(int productIdx){
        return this.jdbcTemplate.queryForObject("select Max(price) as maxPrice from ProductOption\n" +
                "inner join OptionContent\n" +
                "on OptionContent.optionIdx=ProductOption.idx\n" +
                "\n" +
                "where productIdx=? and ProductOption.required='T'\n" +
                "order by price",int.class,productIdx);
    }



    public int getMinPrice(int productIdx){
        return this.jdbcTemplate.queryForObject("select Min(price) as maxPrice from ProductOption\n" +
                "inner join OptionContent\n" +
                "on OptionContent.optionIdx=ProductOption.idx\n" +
                "\n" +
                "where productIdx=? and ProductOption.required='T' and price>0\n" +
                "order by price",int.class,productIdx);
    }



    public int checkProduct(int productIdx){
        return this.jdbcTemplate.queryForObject("select EXISTS(select * from Product\n" +
                "where idx=?) as exist",int.class,productIdx);
    }



    public List<GetMoreReviewRes> getMoreReview(int userIdx,int productIdx){
        return this.jdbcTemplate.query("select Review.idx,Review.userIdx,productIdx,User.userimageUrl,name as userName,starPoint,\n" +
                        "       DATE_FORMAT(Review.createdAt,'%Y.%m.%d') as createdAt,fromLocal,imgUrl,content,\n" +
                        "       CASE WHEN helpfful.helpfulCount is Null THEN 0\n" +
                        "         ELSE helpfful.helpfulCount END as helpful,\n" +
                        "       CASE WHEN Helpful.status is Null THEN 'F'\n" +
                        "         ELSE Helpful.status END as helpfulStatus from Review\n" +
                        "inner join User\n" +
                        "on User.idx=Review.userIdx\n" +
                        "\n" +
                        "left outer join (select reviewIdx,  COUNT(CASE WHEN Helpful.status='T' THEN 1 END) as helpfulCount from Helpful\n" +
                        "group by reviewIdx) helpfful\n" +
                        "on helpfful.reviewIdx=Review.idx\n" +
                        "\n" +
                        "left outer join Helpful\n" +
                        "on Helpful.reviewIdx=Review.idx and Helpful.userIdx=? # 이게 꿀팁이네\n" +
                        "\n" +
                        "where productIdx=?",
                (rs, rowNum) -> new GetMoreReviewRes(
                        rs.getInt("idx"),
                        rs.getInt("userIdx"),
                        rs.getString("userimageUrl"),
                        rs.getString("userName"),
                        rs.getString("starPoint"),
                        rs.getString("createdAt"),
                        rs.getString("fromLocal"),
                        rs.getString("imgUrl"),
                        rs.getString("content"),
                        rs.getString("helpful"),
                        rs.getString("helpfulStatus")
                ),
                userIdx,productIdx);
    }

}
