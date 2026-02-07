package com.example.reservation_system.dto.request;

import com.example.reservation_system.entity.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserSignUpRequest{
    @NotBlank(message = "아이디를 입력해주세요")
    private String loginId;

    @Email (message = "이메일 형식이 아닙니다.")
    @NotBlank(message = "이메일을 입력해주세요")
    private String email;

    @NotBlank(message = "비밀번호를 입력해주세요")
    private String password;

    private String nickname;

    @NotBlank (message = "휴대폰 번호를 입력해주세요")
    @Pattern(
        regexp = "^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}$",
        message = "휴대폰 번호 형식이 올바르지 않습니다. (예: 010-1234-5678)"
    )
    private String phoneNumber;
}


/**유저는 회원가입 창에서 loginId, password만 보냅니다.

그런데 해커가 HTTP 요청에 몰래 "role": "ADMIN", "point": 999999 같은 값을 섞어 보냅니다.

서버가 엔티티를 그대로 받아 DB에 저장해버리면, 듣도 보도 못한 유저가 관리자 권한을 가진 채 가입되는 보안 사고가 터집니다.

DTO는 딱 가입에 필요한 필드만 열어두는 '보안 가드' 역할을 합니다.
 */

    
    