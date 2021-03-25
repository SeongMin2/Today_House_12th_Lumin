package com.example.demo.src.user;

import com.fasterxml.jackson.databind.ser.Serializers;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;


import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.config.BaseResponseStatus.INVALID_ACCESS_TOKEN;
import static com.example.demo.utils.ValidationRegex.isRegexEmail;

@RestController
@RequestMapping("/app/users")
public class UserController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final UserProvider userProvider;
    @Autowired
    private final UserService userService;
    @Autowired
    private final JwtService jwtService;




    public UserController(UserProvider userProvider, UserService userService, JwtService jwtService){
        this.userProvider = userProvider;
        this.userService = userService;
        this.jwtService = jwtService;
    }



    /**
     * 회원 조회 API
     * [GET] /users
     * 회원 번호 및 이메일 검색 조회 API
     * [GET] /users?userIdx= && Email=
     * @return BaseResponse<List<GetUserRes>>
     *
     */
  /*  //Query String
    @ResponseBody
    @GetMapping("") // (GET) 127.0.0.1:9000/app/users
    public BaseResponse<List<GetUserRes>> getUsers(@RequestParam(required = false) String Email) {
        // Get Users
        List<GetUserRes> getUsersRes = userProvider.getUsers(Email);
        return new BaseResponse<>(getUsersRes);
    } */

    /**
     * 회원 1명 조회 API
     * [GET] /users/:userIdx
     * @return BaseResponse<GetUserRes>
     */
    // Path-variable
    /*
    @ResponseBody
    @GetMapping("/{userIdx}") // (GET) 127.0.0.1:9000/app/users/:userIdx
    public BaseResponse<GetUserRes> getUser(@PathVariable("userIdx") int userIdx) {
        // Get Users
        GetUserRes getUserRes = userProvider.getUser(userIdx);
        return new BaseResponse<>(getUserRes);
    } */


    /**
     * 회원가입 API
     * [POST] /users
     * @return BaseResponse<PostUserRes>
     */
    // Body
    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostUserRes> createUser(@RequestBody PostUserReq postUserReq) throws BaseException {
        //req에 입력하지 않은 경우
        if(postUserReq.getEmailId() == null || postUserReq.getPassword()==null || postUserReq.getPasswordCheck()==null || postUserReq.getNickName()==null || postUserReq.getMandatoryConsent()==null){
            return new BaseResponse<>(POST_USERS_EMPTY);
        }

        //이메일 정규표현
        if(!isRegexEmail(postUserReq.getEmailId())){
            return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
        }

        if(postUserReq.getPassword().length()<8){
            return new BaseResponse<>(INSUFFICIENT_PW_RANGE);
        }
        if(postUserReq.getPassword().length()>16){
            return new BaseResponse<>(EXCEED_PW_RANGE);
        }

        if(postUserReq.getNickName().length()<2 || postUserReq.getNickName().length()>15){
            return new BaseResponse<>(INSUFFICIENT_NAME_RANGE);
        }

        try{
            String forLoginPw = postUserReq.getPassword();

            PostUserRes postUserRes = userService.createUser(postUserReq);

            PostUserLoginReq postUserLoginReq = new PostUserLoginReq(postUserReq.getEmailId(),forLoginPw);
            PostUserLoginRes postUserLoginRes = userService.loginUser(postUserLoginReq);

            return new BaseResponse<>(postUserRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    @ResponseBody
    @GetMapping("/email-check")
    public BaseResponse<GetCheckEmailRes> checkEmail(@RequestParam(required = false) String emailId) throws BaseException {
        // Get Users
        GetCheckEmailRes getCheckEmailRes = new GetCheckEmailRes();
        int result = userProvider.checkEmailId(emailId);

        if(!isRegexEmail(emailId)){
            return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
        }

        if(result==1){
            return new BaseResponse<>(POST_USERS_EXISTS_EMAIL);
        }

        else{
            return new BaseResponse<>(getCheckEmailRes);
        }
    }




    @ResponseBody
    @GetMapping("/nickname-check")
    public BaseResponse<GetCheckNameRes> checkName(@RequestParam(required = false) String nickname) throws BaseException {
        // Get Users
        GetCheckNameRes getCheckNameRes = new GetCheckNameRes();

        int result = userProvider.checkName(nickname);
        if(nickname.length()<2){
            return new BaseResponse<>(INSUFFICIENT_NAME_RANGE);
        }

        if(result==1){
            List<GetNameListRes> getNameListRes = userProvider.getNameList(nickname);
            int num = getNameListRes.size();
            num=num+1;
            String recommandName = nickname+num;
            return new BaseResponse<>(POST_USER_EXISTS_NAME,recommandName);
        }

        if(nickname.length()>15){
            return new BaseResponse<>(INSUFFICIENT_NAME_RANGE);
        }

        else{
            return new BaseResponse<>(getCheckNameRes);
        }
    }






    @ResponseBody
    @PostMapping("/login") //마찬가지로 아무것도 없는 것은 post방식으로 /app/users 를 사용하겠다는 의미
    public BaseResponse<PostUserLoginRes> loginUser(@RequestBody PostUserLoginReq postUserLoginReq) throws BaseException {  // json으로 받아오는데 알아서 객체가 되어 받아짐 -> PostUserReq를 보면 받아올 것에 대한 객체가 구성되어 있고
        if(postUserLoginReq.getEmailId() == null){   //validation처리
            return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
        }
        //이메일 정규표현     // 이건 5주차 skip
        if(!isRegexEmail(postUserLoginReq.getEmailId())){   // 형식적 validation
            return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
        }
        try{
            PostUserLoginRes postUserLoginRes = userService.loginUser(postUserLoginReq);  // 조회가 아닌 행위는 service에서 진행하므로 UserService의 객체 userService에서 가져옴, 그래서 위에서 받아서 createUser로 넘김
            return new BaseResponse<>(postUserLoginRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }



    @ResponseBody
    @PatchMapping ("/logout")
    public BaseResponse<PatchUserLogoutRes> logoutUser() throws BaseException{
    /*
        int userIdxx=0;
        try{
            userIdxx = Integer.parseInt(userIdx);
        } catch(NumberFormatException e){ } */

        try{
            if(jwtService.getJwt()==null){
                return new BaseResponse<>(EMPTY_JWT);
            }

            else{
                PatchUserLogoutRes patchUserLogoutRes = userService.patchLogout(jwtService.getUserIdx());
                return new BaseResponse<>(patchUserLogoutRes);
            }
            /*
            else{
                return new BaseResponse<>(USERS_INVALID_ACCESS);
            } */
        }catch(BaseException exception){
                return new BaseResponse<>((exception.getStatus()));
            }
    }





    @ResponseBody
    @GetMapping("/kakao/login") //이렇게 따로 path variable이 들어가 있음// (GET) 127.0.0.1:9000/app/users/:userIdx //이렇게 /app/users뒤에 이어서 /:userIdx설정
    // node.js 같은경우는 path-variable할 때 :userIdx 이런식으로 해주는게 맞으나 Spring-boot같은 경우는 인식을 못하므로 {userIdx}로 해줍니다.
    public BaseResponse<PostUserLoginRes> kakaotest() throws Exception{

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String header=request.getHeader("X-ACCESS-TOKEN");
        if(header==null){
            return new BaseResponse<>(EMPTY_ACCESS_TOKEN);
        }
        else {
            String reqURL = "https://kapi.kakao.com/v2/user/me";
            String access_Token = header;
            try {
                URL url = new URL(reqURL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                //    요청에 필요한 Header에 포함될 내용
                conn.setRequestProperty("Authorization", "Bearer " + access_Token);

                int responseCode = conn.getResponseCode();
                System.out.println("responseCode : " + responseCode);
                if (responseCode != 200) { // 만약 잘못된 access token을 전달하게 되면 보통 400번 코드가 나오고 성공하면 200번 코드가 나옴
                    System.out.println("인증실패");
                    return new BaseResponse<>(INVALID_ACCESS_TOKEN);
                }
                else {
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    String line = "";
                    String result = "";

                    while ((line = br.readLine()) != null) {
                        result += line;
                    }
                    System.out.println("response body(카카오 res) : " + result);

                    JSONParser parser = new JSONParser();
                    JSONObject jsonObject = (JSONObject) parser.parse(result);
                    JSONObject jsonObject2 = (JSONObject) parser.parse(jsonObject.get("kakao_account")+"");

                    String id;
                    if (jsonObject.get("id") == null) {
                        System.out.println("인증실패");
                        return new BaseResponse<>(INVALID_ACCESS_TOKEN);
                    }
                    else { // 이곳이 찐 성공
                        String emailId=jsonObject2.get("email")+"";
                        System.out.println("카카오 계정 id존재:" + jsonObject.get("id"));
                        if(emailId==null){
                            return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);  // post는 아니지만 해당 예외가 문구가 맞으니 그냥 재활용
                        }
                        if(!isRegexEmail(emailId)){   // 형식적 validation
                            return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
                        }

                        else if(userProvider.checkEmailId(emailId)==1){   // userProvider.checkEmail도 여기서 재활용 의미적 validation이긴 하지만 재활용이므로 여기서 점검
                            if(userProvider.checkKakaoSocial(emailId)=='T'){  // kakaosocial 로그인 'T'로 되어 있으면 정상로그인
                                PostUserLoginRes postUserLoginRes = userProvider.socialLogin(emailId);
                                postUserLoginRes.setStatus("카카오 소셜로그인 성공!");
                                return new BaseResponse<>(postUserLoginRes);
                            }
                            else if(userProvider.checkKakaoSocial(emailId)=='F'){
                                PostUserLoginRes postUserLoginRes = userProvider.socialLogin(emailId);
                                postUserLoginRes.setStatus("이미 가입된 로그인입니다. 해당 소셜로그인으로 연동하겠습니까?");
                                postUserLoginRes.setJwt("0");
                                return new BaseResponse<>(postUserLoginRes);
                            }
                        }
                        else{
                            PostUserLoginRes postUserLoginRes = new PostUserLoginRes("0",0,"noOne");
                            postUserLoginRes.setStatus("존재하지 않는 계정입니다. 새로 가입하시겠습니까?");
                            return new BaseResponse<>(postUserLoginRes);
                        }

                    }
                }

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return null;
    }


/*

    @ResponseBody
    @PostMapping("/kakao") //마찬가지로 아무것도 없는 것은 post방식으로 /app/users 를 사용하겠다는 의미
    public BaseResponse<PostKakaoUserRes> createKakaoUser(@RequestBody PostKakaoUserReq postUserLoginReq) throws BaseException {  // json으로 받아오는데 알아서 객체가 되어 받아짐 -> PostUserReq를 보면 받아올 것에 대한 객체가 구성되어 있고
        if(postUserLoginReq.getEmailId() == null){   //validation처리
            return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
        }
        //이메일 정규표현     // 이건 5주차 skip
        if(!isRegexEmail(postUserLoginReq.getEmailId())){   // 형식적 validation
            return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
        }
        try{
            PostUserLoginRes postUserLoginRes = userService.loginUser(postUserLoginReq);  // 조회가 아닌 행위는 service에서 진행하므로 UserService의 객체 userService에서 가져옴, 그래서 위에서 받아서 createUser로 넘김
            return new BaseResponse<>(postUserLoginRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
*/









}
