package roomescape.member.domain;

import org.springframework.http.HttpStatus;
import roomescape.global.exception.ErrorCode;

public enum MemberErrorCode implements ErrorCode {

    INVALID_MEMBER("MEMBER_INVALID", HttpStatus.BAD_REQUEST, "회원 정보를 확인해 주세요."),
    DUPLICATE_EMAIL("MEMBER_DUPLICATE_EMAIL", HttpStatus.CONFLICT, "이미 가입된 이메일입니다."),
    MEMBER_NOT_FOUND("MEMBER_NOT_FOUND", HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다.");

    private final String code;
    private final HttpStatus status;
    private final String message;

    MemberErrorCode(String code, HttpStatus status, String message) {
        this.code = code;
        this.status = status;
        this.message = message;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public HttpStatus status() {
        return status;
    }

    @Override
    public String message() {
        return message;
    }
}
