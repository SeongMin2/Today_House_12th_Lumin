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
                        "           ELSE SEC_TO_TIME(TIMESTAMPDIFF(SECOND,now(),TodayDeal.deadline)) END as leftTime # 23??????????????? ?????? ???????????? ????????? 1??? ?????????\n" +
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
                        "# ?????? ???????????? ?????? scrap ?????? ???????????? ????????? left outer join???\n" +
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
                        "#where TodayDeal.status is null # ????????? ??? ???????????? ??????\n" +
                        "group by Product.idx\n" +
                        "order by scrapStatus DESC\n" +
                        "Limit 4",
                (rs, rowNum) -> new GetStoreTDPDRes(
                        rs.getInt("idx"),
                        rs.getInt("evalableIdx"),
                        rs.getString("imgUrl"),
                        rs.getString("scrapStatus"),
                        rs.getString("leftTime"),
                        rs.getString("brandName"),
                        "[????????????]"+rs.getString("productName"),
                        rs.getString("percent")+"%",
                        rs.getString("setProductStatus"),
                        formatter.format(rs.getInt("salePrice")),
                        rs.getString("starpoint"),
                        "?????? "+rs.getString("reviewNum"),
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
                        "# ?????? ???????????? ?????? scrap ?????? ???????????? ????????? left outer join???\n" +
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
                        "where TodayDeal.status is null # ????????? ??? ???????????? ??????\n" +
                        "group by Product.idx",
                (rs, rowNum) -> new GetStorePopPDRes(
                        rs.getInt("idx"),
                        rs.getInt("evalableIdx"),
                        rs.getString("imgUrl"),
                        rs.getString("scrapStatus"),
                        rs.getString("brandName"),
                        "[????????????]"+rs.getString("productName"),
                        rs.getString("percent")+"%",
                        rs.getString("setProductStatus"),
                        formatter.format(rs.getInt("salePrice")),
                        rs.getString("starpoint"),
                        "?????? "+rs.getString("reviewNum"),
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
                        "         ELSE Product.productInfo END as productInfoImgUrl,\n" +
                        "       Case when couponStatus.maxSalePrice is null then 0\n" +
                        "            ElsE couponStatus.maxSalePrice End as couponStatus from Product\n" +
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
                        "# ?????? ???????????? ?????? scrap ?????? ???????????? ????????? left outer join???\n" +
                        "left outer join (select User.idx, User.name,contentIdx,evalableIdx,Scrap.status from User\n" +
                        "inner join Scrap\n" +
                        "on Scrap.userIdx = User.idx\n" +
                        "where User.idx=1) ProductScrap\n" +
                        "on ProductScrap.contentIdx=Product.idx and ProductScrap.evalableIdx=Product.evalableIdx\n" +
                        "\n" +
                        "inner join DeliveryandRefund\n" +
                        "on DeliveryandRefund.productIdx = Product.idx\n" +
                        "\n" +
                        "left outer join (select Scrap.contentIdx,COUNT(CASE WHEN Scrap.status='T' THEN 1 END) as scrapCount from Scrap\n" +
                        "where evalableIdx=?\n" +
                        "group by contentIdx) scrapNum\n" +
                        "on scrapNum.contentIdx=Product.idx\n" +
                        "\n" +
                        "left outer join (select productIdx,MAX(saleRange) as maxSalePrice from Coupon\n" +
                        "inner join CouponProduct\n" +
                        "on CouponProduct.couponIdx=Coupon.idx\n" +
                        "group by CouponProduct.productIdx) couponStatus\n" +
                        "on couponStatus.productIdx=Product.idx\n" +
                        "\n" +
                        "\n" +
                        "where Product.idx=?  # ????????????\n" +
                        "group by Product.idx",
                (rs, rowNum) -> new GetStoreProductInfoRes(
                        rs.getInt("idx"),
                        rs.getString("scrapStatus"),
                        rs.getString("scrapCount"),
                        rs.getString("leftTime"),  // ?????? ????????? ?????? ?????? ?????????????????? provider ?????? ???????????????
                        rs.getString("brandName"),
                        rs.getString("productName"),  // ????????? ????????? ?????? ?????? ???????????? ????????????
                        rs.getString("percent")+"%",
                        formatter.format(rs.getInt("originalPrice")),
                        formatter.format(rs.getInt("salePrice")),   // ????????? ????????? ?????? ?????? ????????????
                        rs.getString("specialStatus"),
                        formatter.format(rs.getInt("savePoint")),     // ????????? ???????????? ???????????? ?????? ????????? ????????? ???
                        rs.getString("saveRate"),      // ????????? ???????????? ???????????? ??????
                        rs.getString("deliveryCost"),   // ????????? 0 ?????? ???????????? ?????? ???????????? ????????? ???
                        rs.getString("freeConditionPrice"),   // ????????? ?????? ??????????????????
                        rs.getString("outDescription"),
                        rs.getString("limitedArea"),
                        formatter.format(rs.getInt("additionalCost")),
                        rs.getString("setProductStatus"),
                        rs.getString("starpoint"),
                        "("+rs.getString("reviewNum")+")",
                        rs.getInt("couponStatus")
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
                        "       DATE_FORMAT(Review.createdAt,'%Y.%m.%d') as createdAt,fromLocal,\n" +
                        "       Case when imgUrl is null then 'F'\n" +
                        "           ELSE imgUrl END as imgUrl,content,\n" +
                        "       CASE WHEN helpfful.helpfulCount is Null THEN 0 #DATE_FORMAT(createdAt, '%Y???%m???%d??? %h???%i???%s???')\n" +
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




    public List<GetStoreSetProductReviewRes> getSetProductReview(int productIdx){
        return this.jdbcTemplate.query("select Review.idx,Review.userIdx,productIdx,setProducts.name,User.userimageUrl,User.name as userName,starPoint,\n" +
                        "       DATE_FORMAT(Review.createdAt,'%Y.%m.%d') as createdAt,fromLocal,\n" +
                        "       Case when imgUrl is null then 'F'\n" +
                        "           ELSE imgUrl END as imgUrl,content,\n" +
                        "       CASE WHEN helpfful.helpfulCount is Null THEN 0 #DATE_FORMAT(createdAt, '%Y???%m???%d??? %h???%i???%s???')\n" +
                        "         ELSE helpfful.helpfulCount END as helpful,setProducts.selectOrder\n" +
                        "        from Review\n" +
                        "inner join User\n" +
                        "on User.idx=Review.userIdx\n" +
                        "\n" +
                        "left outer join (select reviewIdx,  COUNT(CASE WHEN Helpful.status='T' THEN 1 END) as helpfulCount from Helpful\n" +
                        "group by reviewIdx) helpfful\n" +
                        "on helpfful.reviewIdx=Review.idx\n" +
                        "\n" +
                        "inner join (select ho.idx,ho.relatedProduct,ho.selectOrder,Product.name from Product\n" +
                        "inner join (select Product.idx, Product.name\n" +
                        "     , setProductStatus\n" +
                        "     ,RelatedP.relatedProduct as relatedProduct,RelatedP.selectOrder from Product  # SetRelation.productIdx????????? null??? ????????? ?????? Product.idx??? ??????\n" +
                        "\n" +
                        "inner join (select Product.idx as productIdx,CASE WHEN SetRelation.productIdx is NULL THEN Product.idx\n" +
                        "         ELSE SetRelation.productIdx END as relatedProduct, selectOrder from Product\n" +
                        "left outer join SetRelation\n" +
                        "on SetRelation.setProductIdx=Product.idx) RelatedP\n" +
                        "on RelatedP.productIdx=Product.idx\n" +
                        "\n" +
                        "where Product.idx=?) ho\n" +
                        "on ho.relatedProduct=Product.idx) setProducts\n" +
                        "on setProducts.relatedProduct=Review.productIdx\n" +
                        "\n" +
                        "order by helpful DESC\n" +
                        "LIMIT 3",
                (rs, rowNum) -> new GetStoreSetProductReviewRes(
                        rs.getInt("idx"),
                        rs.getInt("userIdx"),
                        rs.getInt("productIdx"),
                        "?????? "+rs.getString("selectOrder")+". ",
                        rs.getString("name"),
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
                        "# ?????? ???????????? ?????? scrap ?????? ???????????? ????????? left outer join???\n" +
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
                        "?????? "+rs.getString("selectOrder"),
                        rs.getString("imgUrl"),
                        rs.getString("scrapStatus"),
                        rs.getString("brandName"),
                        rs.getString("productName"),
                        rs.getString("starpoint"),
                        "?????? "+rs.getString("reviewNum"),
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



    public List<GetMoreReviewRes> getMoreReview(int userIdx,int productIdx,String order){
        return this.jdbcTemplate.query("select Review.idx,Review.userIdx,productIdx,User.userimageUrl,name as userName,starPoint,\n" +
                        "       DATE_FORMAT(Review.createdAt,'%Y.%m.%d') as createdAt,fromLocal,\n" +
                        "       Case when imgUrl is null then 'F'\n" +
                        "           ELSE imgUrl END as imgUrl,content,\n" +
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
                        "on Helpful.reviewIdx=Review.idx and Helpful.userIdx=? # ?????? ????????????\n" +
                        "\n" +
                        "where productIdx=?\n" +
                        "order by "+order+" DESC",
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




    public List<GetMoreSetReviewRes> getMoreSetReview(int userIdx,int productIdx,String order){
        return this.jdbcTemplate.query("select Review.idx,Review.userIdx,productIdx,setProducts.name,User.userimageUrl,User.name as userName,starPoint,\n" +
                        "       DATE_FORMAT(Review.createdAt,'%Y.%m.%d') as createdAt,fromLocal,\n" +
                        "       Case when imgUrl is null then 'F'\n" +
                        "           ELSE imgUrl END as imgUrl,content,\n" +
                        "       CASE WHEN helpfful.helpfulCount is Null THEN 0\n" +
                        "         ELSE helpfful.helpfulCount END as helpful,\n" +
                        "       CASE WHEN Helpful.status is Null THEN 'F'\n" +
                        "         ELSE Helpful.status END as helpfulStatus, setProducts.selectOrder from Review\n" +
                        "inner join User\n" +
                        "on User.idx=Review.userIdx\n" +
                        "\n" +
                        "left outer join (select reviewIdx,  COUNT(CASE WHEN Helpful.status='T' THEN 1 END) as helpfulCount from Helpful\n" +
                        "group by reviewIdx) helpfful\n" +
                        "on helpfful.reviewIdx=Review.idx\n" +
                        "\n" +
                        "left outer join Helpful\n" +
                        "on Helpful.reviewIdx=Review.idx and Helpful.userIdx=? # ?????? ????????????\n" +
                        "\n" +
                        "inner join (select ho.idx,ho.relatedProduct,ho.selectOrder,Product.name from Product\n" +
                        "inner join (select Product.idx, Product.name\n" +
                        "     , setProductStatus\n" +
                        "     ,RelatedP.relatedProduct as relatedProduct,RelatedP.selectOrder from Product  # SetRelation.productIdx????????? null??? ????????? ?????? Product.idx??? ??????\n" +
                        "\n" +
                        "inner join (select Product.idx as productIdx,CASE WHEN SetRelation.productIdx is NULL THEN Product.idx\n" +
                        "         ELSE SetRelation.productIdx END as relatedProduct, selectOrder from Product\n" +
                        "left outer join SetRelation\n" +
                        "on SetRelation.setProductIdx=Product.idx) RelatedP\n" +
                        "on RelatedP.productIdx=Product.idx\n" +
                        "\n" +
                        "where Product.idx=?) ho\n" +
                        "on ho.relatedProduct=Product.idx) setProducts\n" +
                        "on setProducts.relatedProduct=Review.productIdx\n" +
                        "order by "+order+" DESC",
                (rs, rowNum) -> new GetMoreSetReviewRes(
                        rs.getInt("idx"),
                        rs.getInt("userIdx"),
                        rs.getInt("productIdx"),
                        "?????? "+rs.getString("selectOrder")+". ",
                        rs.getString("name"),
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




    public GetStoreMoreReviewFinal getMoreReviewFinal(int productIdx){
        return this.jdbcTemplate.queryForObject("select Product.idx\n" +
                        "     , CASE WHEN ROUND(AVG(ProductReview.starpoint),1) is null THEN 0\n" +
                        "         ELSE ROUND(AVG(ProductReview.starpoint),1) END as starpoint,\n" +
                        "       CASE WHEN SUM(ProductReview.reviewNum) is null THEN 0\n" +
                        "         ELSE ROUND(SUM(ProductReview.reviewNum),0) END as reviewNum from Product\n" +
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
                        "where Product.idx=?  # ????????????\n" +
                        "group by Product.idx",
                (rs, rowNum) -> new GetStoreMoreReviewFinal(
                        rs.getString("reviewNum"),
                        rs.getString("starpoint")
                ),
                productIdx);
    }



    public GetMoreReviewRes getOneReview(int userIdx,int reviewIdx){
        return this.jdbcTemplate.queryForObject("select Review.idx,Review.userIdx,productIdx,User.userimageUrl,name as userName,starPoint,\n" +
                        "       DATE_FORMAT(Review.createdAt,'%Y.%m.%d') as createdAt,fromLocal,\n" +
                        "       Case when imgUrl is null then 'F'\n" +
                        "           ELSE imgUrl END as imgUrl,content,\n" +
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
                        "on Helpful.reviewIdx=Review.idx and Helpful.userIdx=? # ?????? ????????????\n" +
                        "\n" +
                        "where Review.idx=?",
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
                userIdx,reviewIdx);
    }





    public int checkHelpfulExist(int userIdx,int reviewIdx){
        return this.jdbcTemplate.queryForObject("select Exists(select status from Helpful\n" +
                "where reviewIdx=? and userIdx=?) as helpfulExist", int.class,reviewIdx,userIdx);
    }


    public char checkHelpful(int userIdx,int reviewIdx){
        return this.jdbcTemplate.queryForObject("select status from Helpful \n" +
                "where reviewIdx=? and userIdx=?", char.class,reviewIdx,userIdx);
    }



    public PatchHelpfulRes patchHelpful(String status,int userIdx,int reviewIdx){
        this.jdbcTemplate.update("UPDATE Helpful set status=? \n" +
                        "where userIdx=? and reviewIdx=?",
                status,userIdx,reviewIdx);
        return this.jdbcTemplate.queryForObject("select status,userIdx,reviewIdx from Helpful\n" +
                        "where userIdx=? and reviewIdx=?",  // queryForObject??? ????????? ????????? ??? ??????
                (rs, rowNum) -> new PatchHelpfulRes(
                        rs.getInt("userIdx"),
                        rs.getInt("reviewIdx"),
                        rs.getString("status")),
                userIdx,reviewIdx);
    }


    public PatchHelpfulRes createHelpful(String status, int userIdx, int reviewIdx){
        this.jdbcTemplate.update("insert into Helpful (reviewIdx,userIdx,createdAt,status) VALUES (?,?,now(),?)",  // insert,update,delete ????????? ??? update??? ???????????? ???
                reviewIdx,userIdx,status);
        return this.jdbcTemplate.queryForObject("select status,userIdx,reviewIdx from Helpful\n" +
                        "where userIdx=? and reviewIdx=?",  // queryForObject??? ????????? ????????? ??? ??????
                (rs, rowNum) -> new PatchHelpfulRes(
                        rs.getInt("userIdx"),
                        rs.getInt("reviewIdx"),
                        rs.getString("status")),
                userIdx,reviewIdx);
    }



    public int checkReview(int reviewIdx){
        return this.jdbcTemplate.queryForObject("select EXISTS(select status from Review\n" +
                "where idx=?) as exist", int.class,reviewIdx);
    }



    public String changeTDStatus(){
        this.jdbcTemplate.update("UPDATE TodayDeal set status= Case when TIMESTAMPDIFF(SECOND,now(),TodayDeal.deadline)<=0 then 'F'\n" +
                        "ELSE 'T' END\n" +
                        "where status='T'");
        return "????????????";
    }




    public int createReview(PostReviewReq postReviewReq,int productIdx,int userIdx){
        this.jdbcTemplate.update("insert into Review (userIdx,productIdx,createdAt,status,starPoint,imgUrl,content,fromLocal,agreement) Values (?,?,now(),'T',?,?,?,'F',?)",  // insert,update,delete ????????? ??? update??? ???????????? ???
                userIdx,productIdx,postReviewReq.getStarPoint(),postReviewReq.getImgUrl(),postReviewReq.getContent(),postReviewReq.getAgreement()
                );
        return this.jdbcTemplate.queryForObject("select last_insert_id()",int.class);
    }


    public int checkReviewByUser(int userIdx, int productIdx){
        return this.jdbcTemplate.queryForObject("select Exists(select * from Review\n" +
                "where userIdx=? and productIdx=?) as exist", int.class,userIdx,productIdx);
    }



    public GetReviewPageRes getReviewPage(int productIdx){
        return this.jdbcTemplate.queryForObject("select Product.idx,Brand.name as brandName,\n" +
                        "       CASE WHEN TodayDeal.status is null then 'F'\n" +
                        "           Else TodayDeal.status End as todayDeal\n" +
                        "       ,Product.name,imgUrl from Product\n" +
                        "inner join ProductImg\n" +
                        "on ProductImg.productIdx=Product.idx\n" +
                        "\n" +
                        "inner join Brand\n" +
                        "on Brand.idx=Product.brandIdx\n" +
                        "\n" +
                        "left outer join TodayDeal\n" +
                        "on TodayDeal.productIdx=Product.idx\n" +
                        "\n" +
                        "where Product.idx=?\n" +
                        "group by Product.idx",  // queryForObject??? ????????? ????????? ??? ??????
                (rs, rowNum) -> new GetReviewPageRes(
                        rs.getInt("idx"),
                        "["+rs.getString("brandName")+"]",
                        rs.getString("todayDeal"),
                        rs.getString("name"),
                        rs.getString("imgUrl")),
                productIdx);
    }


    public int patchReview(PatchReviewReq patchReviewReq,int reviewIdx){
        this.jdbcTemplate.update("update Review set updatedAt=now(),starPoint=?,imgUrl=?,content=?\n" +
                        "where idx=?",  // insert,update,delete ????????? ??? update??? ???????????? ???
                patchReviewReq.getStarPoint(),patchReviewReq.getImgUrl(),patchReviewReq.getContent(),reviewIdx
        );
        return reviewIdx;
    }


    public int checkReviewUser(int userIdx,int reviewIdx){
        return this.jdbcTemplate.queryForObject("select Exists(select userIdx from Review\n" +
                "where userIdx=? and idx=?) as exist",int.class ,userIdx,reviewIdx);
    }



    public List<GetStoreProductImgRes> getProductDscImg(int productIdx){
        return this.jdbcTemplate.query("select idx,detailImgUrl from ProductDetailImg\n" +
                        "where productIdx=?",  // queryForObject??? ????????? ????????? ??? ??????
                (rs, rowNum) -> new GetStoreProductImgRes(
                        rs.getInt("idx"),
                        rs.getString("detailImgUrl")),
                productIdx);
    }


    public int checkProductCoupon(int productIdx){
        return this.jdbcTemplate.queryForObject("select EXISTS(select * from CouponProduct\n" +
                "where productIdx=?) as exist", int.class,productIdx);
    }



    public List<GetProductCouponRes> getProductCoupon(int productIdx,int userIdx){
        DecimalFormat formatter = new DecimalFormat("###,###");
        return this.jdbcTemplate.query("select Coupon.idx, name, maxPrice,\n" +
                        "       DATEDIFF(expiration,now()) as leftDay,expiration, Coupon.status as couponStatus,saleRange,\n" +
                        "       CASE when CouponStatus.status is null then 'T'\n" +
                        "            ELSE 'F' END as receivable from Coupon\n" +
                        "inner join CouponProduct\n" +
                        "on CouponProduct.couponIdx=Coupon.idx\n" +
                        "\n" +
                        "left outer join CouponStatus\n" +
                        "on CouponStatus.couponIdx=Coupon.idx and CouponStatus.userIdx=?\n" +
                        "\n" +
                        "where productIdx=? and Coupon.status='T'",  // queryForObject??? ????????? ????????? ??? ??????
                (rs, rowNum) -> new GetProductCouponRes(
                        rs.getInt("idx"),
                        rs.getInt("saleRange"),
                        rs.getString("name"),
                        formatter.format(rs.getInt("maxPrice"))+"??? ?????? ?????????",
                        rs.getString("leftDay")+"??? ??????",
                        rs.getString("receivable")),
                userIdx,productIdx);
    }


    public int checkCoupon(int couponIdx) {
        return this.jdbcTemplate.queryForObject("select Exists(select idx from Coupon\n" +
                "where idx=? and status='T') as exist", int.class, couponIdx);
    }

    public int checkHasCoupon(int userIdx,int couponIdx){
        return this.jdbcTemplate.queryForObject("select Exists(select status from CouponStatus\n" +
                "where userIdx=? and couponIdx=?) as exist", int.class, userIdx,couponIdx);
    }



    public int postCoupon(int userIdx,int couponIdx){
        this.jdbcTemplate.update("insert into CouponStatus (userIdx,couponIdx,createdAt,status) VALUES (?,?,now(),'T')",  // insert,update,delete ????????? ??? update??? ???????????? ???
                userIdx,couponIdx);
        return this.jdbcTemplate.queryForObject("select last_insert_id()",int.class);
    }

    public String changeCouponStatus(){
        this.jdbcTemplate.update("UPDATE Coupon set status= Case when DATEDIFF(expiration,now())<=0 then 'F'\n" +
                "ELSE 'T' END\n" +
                "where status='T'");
        return "?????? ???????????? ??????";
    }



    public List<GetDandRSetProductRes> getDSetProduct(int productIdx){
        return this.jdbcTemplate.query("select Product.idx,RelatedP.selectOrder,ProductImg.imgUrl,\n" +
                        "      Product.name as productName\n" +
                        "     , CASE WHEN TodayDeal.status='T' then TRUNCATE((Product.fixedPrice-TodayDeal.salePrice)/Product.fixedPrice*100,0)\n" +
                        "         ELSE TRUNCATE((Product.fixedPrice-Product.salePrice)/Product.fixedPrice*100,0) END as percent\n" +
                        "     ,setProductStatus\n" +
                        "     , RelatedP.relatedProduct as relatedProduct\n" +
                        "      from Product\n" +
                        "left outer join TodayDeal\n" +
                        "on TodayDeal.productIdx=Product.idx\n" +
                        "\n" +
                        "inner join (select Product.idx as productIdx,CASE WHEN SetRelation.productIdx is NULL THEN Product.idx\n" +
                        "         ELSE SetRelation.productIdx END as relatedProduct,selectOrder from Product\n" +
                        "left outer join SetRelation\n" +
                        "on SetRelation.setProductIdx=Product.idx\n" +
                        "where Product.idx=?) RelatedP\n" +
                        "on RelatedP.relatedProduct=Product.idx\n" +
                        "\n" +
                        "inner join (select imgUrl,productIdx from ProductImg\n" +
                        "group by productIdx) ProductImg\n" +
                        "on ProductImg.productIdx=Product.idx\n" +
                        "\n" +
                        "group by Product.idx",  // queryForObject??? ????????? ????????? ??? ??????
                (rs, rowNum) -> new GetDandRSetProductRes(
                        rs.getInt("idx"),
                        rs.getString("selectOrder"),
                        rs.getString("imgUrl"),
                        rs.getString("productName")
                ),
                productIdx);
    }



    public List<GetDeliveryAndRefundRes> getDandR (int productIdx){
        DecimalFormat formatter = new DecimalFormat("###,###");
        return this.jdbcTemplate.query("select DeliveryandRefund.idx, delivery,\n" +
                        "       deliveryCost,\n" +
                        "       freeConditionPrice,payment,additionalCost,\n" +
                        "      limitedArea,\n" +
                        "       refundCost, exchangeCost, DeliveryandRefund.address,\n" +
                        "       businessName,representative,DeliveryInfo.address as businessAddress,phoneNum,email,\n" +
                        "       businessNum\n" +
                        "       from DeliveryandRefund\n" +
                        "inner join DeliveryInfo\n" +
                        "on DeliveryInfo.productIdx=DeliveryandRefund.productIdx\n" +
                        "where DeliveryandRefund.productIdx=?",  // queryForObject??? ????????? ????????? ??? ??????
                (rs, rowNum) -> new GetDeliveryAndRefundRes(
                        rs.getInt("idx"),
                        rs.getString("delivery"),
                        rs.getString("deliveryCost"),
                        formatter.format(rs.getInt("freeConditionPrice")),
                        rs.getString("payment"),
                        formatter.format(rs.getInt("additionalCost")),
                        rs.getString("limitedArea"),
                        rs.getInt("refundCost"),
                        formatter.format(rs.getInt("exchangeCost")),
                        rs.getString("address"),
                        rs.getString("businessName"),
                        rs.getString("representative"),
                        rs.getString("businessAddress"),
                        rs.getString("phoneNum"),
                        rs.getString("email"),
                        rs.getString("businessNum")
                ),
                productIdx);
    }


    public int getRequiredNum(int productIdx){
        return this.jdbcTemplate.queryForObject("select COUNT(Case when ProductOption.required='T' then 1 end) as numOfRequired from ProductOption\n" +
                "where productIdx=?", int.class, productIdx);
    }

    public int getSelectiveNum(int productIdx){
        return this.jdbcTemplate.queryForObject("select COUNT(Case when ProductOption.required='F' then 1 end) as numOfSelective from ProductOption\n" +
                "where productIdx=?", int.class, productIdx);
    }


    public PostImmediatePRes getPaymentProductInfo(int detailIdx){
        DecimalFormat formatter = new DecimalFormat("###,###");
        return this.jdbcTemplate.queryForObject("select ProductOption.productIdx,imgUrl ,Product.name as productName,ProductOption.name as optionName,required,price, OptionContent.name from ProductOption\n" +
                        "inner join OptionContent\n" +
                        "on ProductOption.idx=OptionContent.optionIdx\n" +
                        "inner join Product\n" +
                        "on Product.idx=ProductOption.productIdx\n" +
                        "inner join ProductImg\n" +
                        "on ProductImg.productIdx=Product.idx\n" +
                        "where OptionContent.idx=?\n" +
                        "group by productIdx",  // queryForObject??? ????????? ????????? ??? ??????
                (rs, rowNum) -> new PostImmediatePRes(
                        rs.getInt("productIdx"),
                        rs.getString("imgUrl"),
                        rs.getString("productName"),
                        rs.getString("optionName"),
                        rs.getString("required"),
                        formatter.format(rs.getInt("price")),
                        rs.getInt("price"),
                        rs.getString("name")
                ),
                detailIdx);
    }


    public int getPoint(int userIdx){
        return this.jdbcTemplate.queryForObject("select point from User\n" +
                "where idx=?", int.class, userIdx);
    }



    public int postOrders(PostKakaoPayReadyReq postKakaoPayReadyReq,int userIdx){
        this.jdbcTemplate.update("insert into Orders (userIdx,createdAt,status,orderer,ordererEmail,phoneNum,receiver,\n" +
                        "                    receiverPhoneNum,address,payment,totalPrice,number) VALUES (?,now(),'F',?,?,?,?,?,?,?,?,?)",  // insert,update,delete ????????? ??? update??? ???????????? ???
                userIdx,postKakaoPayReadyReq.getOrderer(),postKakaoPayReadyReq.getOrdererEmail(),postKakaoPayReadyReq.getOrdererPhoneNum(),
                postKakaoPayReadyReq.getReceiver(),postKakaoPayReadyReq.getReceiverPhoneNum(),postKakaoPayReadyReq.getAddress(),
                postKakaoPayReadyReq.getPayment(),postKakaoPayReadyReq.getTotalPrice(),postKakaoPayReadyReq.getNumber());

        return this.jdbcTemplate.queryForObject("select last_insert_id()",int.class);
    }

    public int postOrderProduct(int detailOption,int orderIdx){
        this.jdbcTemplate.update("insert into OrderProduct (orderIdx,detailIdx,createdAt,status) VALUES (?,?,now(),'T')",  // insert,update,delete ????????? ??? update??? ???????????? ???
                orderIdx,detailOption);

        return this.jdbcTemplate.queryForObject("select last_insert_id()",int.class);
    }

    public String getProductName(int productIdx){
        return this.jdbcTemplate.queryForObject("select name from Product\n" +
                "where idx=?", String.class, productIdx);
    }

    public String getUserName(int userIdx){
        return this.jdbcTemplate.queryForObject("select name from User\n" +
                "where idx=?", String.class, userIdx);
    }


    public int patchOrderStatus(int orderIdx,PostKakaoPayConfirmReq postKakaoPayConfirmReq){
        this.jdbcTemplate.update("update Orders set status='T',tid=? where idx=?",
                postKakaoPayConfirmReq.getTid(),orderIdx
        );
        return orderIdx;
    }


    public int checkJwt(String jwt){
        return this.jdbcTemplate.queryForObject("select EXISTS(select status from JwtManagement\n" +
                        "where jwt=? and status='F') as exist",
                int.class,
                jwt);
    }




}
