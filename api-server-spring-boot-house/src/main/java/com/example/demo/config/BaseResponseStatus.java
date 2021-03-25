package com.example.demo.config;

import lombok.Getter;

/**
 * 에러 코드 관리
 */
@Getter
public enum BaseResponseStatus {
    /**
     * 1000 : 요청 성공
     */
    SUCCESS(true, 1000, "요청에 성공하였습니다."),


    /**
     * 2000 : Request 오류
     */
    // Common
    REQUEST_ERROR(false, 2000, "입력값을 확인해주세요."),
    EMPTY_JWT(false, 2001, "JWT를 입력해주세요."),
    INVALID_JWT(false, 2002, "유효하지 않은 JWT입니다."),

    // users
    USERS_EMPTY_USER_ID(false, 2010, "유저 아이디 값을 확인해주세요."),

    // [POST] /users
    POST_USERS_EMPTY(false,2014,"아직 입력하지 않은 필수항목이 있습니다."),
    POST_USERS_EMPTY_EMAIL(false, 2015, "이메일을 입력해주세요."),
    POST_USERS_INVALID_EMAIL(false, 2016, "이메일 형식을 확인해주세요."),
    POST_USERS_EXISTS_EMAIL(false,2017,"중복된 이메일입니다."),
    PASSWORD_CONFIRM_ERROR(false,2018,"두 비밀번호가 일치하지 않습니다."),
    POST_USER_EXISTS_NAME(false,2019,"사용 중인 별명입니다."),
    INSUFFICIENT_PW_RANGE(false,2020,"비밀번호를 8자 이상 입력해주세요."),
    EXCEED_PW_RANGE(false,2021,"비밀번호를 16자 이하로 입력해주세요."),
    INSUFFICIENT_NAME_RANGE(false,2022,"별명을 2~15자 내로 입력해주세요."),
    USERS_INVALID_ACCESS(false,2023,"본인만 접근 가능합니다."),
    NON_EXISTENT_PRODUCT(false,2024,"존재하지 않는 상품입니다."),
    NON_EXISTENT_REVIEW(false,2025,"존재하지 않는 리뷰입니다."),
    NON_EXISTENT_POST(false,2026,"존재하지 않는 게시글입니다."),
    NOT_COMPLETED_REVIEW(false,2027,"필수 입력 사항을 확인해주세요"),
    INVALID_PRODUCT(false,2028,"세트형 상품에는 리뷰를 작성할 수 없습니다."),
    INVALID_USER_ACCESS(false,2029,"잘못된 사용자 접근입니다."),
    NON_EXISTENT_COMMENT(false,2030,"댓글을 입력해주세요."),
    INVALID_PRODUCT_COUPON_ACCESS(false,2031,"해당 상품에는 쿠폰이 존재하지 않습니다."),
    ALREADY_DELETE_POST(false,2032,"이미 삭제된 게시글 입니다."),
    NON_EVALABLE_EXIST(false,2033,"evalableIdx를 확인해주세요."),
    NON_EXISTENT_COUPON(false,2034,"유효하지 않는 쿠폰입니다."),
    ALREADY_ISSUED_COUPON(false,2035,"이미 발급된 쿠폰입니다."),
    FAIL_DELETE_COMMENT(false,2036,"존재하지 않는 댓글입니다."),
    NOT_REQUIRED_OPTION(false,2037,"필수 옵션을 모두 선택하지 않았습니다."),
    INVALID_OPTION_REQUEST(false, 2038,"잘못된 옵션 요청입니다."),
    EMPTY_ACCESS_TOKEN(false,2039,"ACCESS TOKEN을 입력해주세요"),
    INVALID_ACCESS_TOKEN(false,2040,"잘못된 Access Token입니다."),
    POST_USERS_EMPTY_NICKNAME(false,2041,"닉네임을 입력해주세요"),




    /**
     * 3000 : Response 오류
     */
    // Common
    RESPONSE_ERROR(false, 3000, "값을 불러오는데 실패하였습니다."),

    // [POST] /users
    DUPLICATED_EMAIL(false, 3013, "중복된 이메일입니다."),
    POST_USERS_NONEXIST_ACCOUNT(false,3014,"아이디 또는 비밀번호가 잘못되었습니다."),
    ALREADY_LOGGED(false,3015,"이미 로그인되어 있습니다"),
    ALREADY_LOGOUT(false,3016,"이미 로그아웃되어 있습니다."),
    ALREADY_WRITTEN_REVIEW(false,3017,"이미 리뷰를 작성한 제품입니다."),


    /**
     * 4000 : Database, Server 오류
     */
    DATABASE_ERROR(false, 4000, "데이터베이스 연결에 실패하였습니다."),
    SERVER_ERROR(false, 4001, "서버와의 연결에 실패하였습니다."),

    PASSWORD_ENCRYPTION_ERROR(false, 4011, "비밀번호 암호화에 실패하였습니다."),
    PASSWORD_DECRYPTION_ERROR(false, 4012, "비밀번호 복호화에 실패하였습니다.");


    // 5000 : 필요시 만들어서 쓰세요
    // 6000 : 필요시 만들어서 쓰세요


    private final boolean isSuccess;
    private final int code;
    private final String message;

    private BaseResponseStatus(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}
