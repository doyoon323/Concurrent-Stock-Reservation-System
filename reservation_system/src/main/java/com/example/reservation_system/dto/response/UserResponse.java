package com.example.reservation_system.dto.response;

import com.example.reservation_system.entity.User;
import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Getter
@AllArgsConstructor //클래스의 모든 필드를 파라미터로 받는 생성자를 자동으로 만들어준다. public ProductResponse(Long id, String name, Integer price)이거 자동으로 만들어줌 
@Builder
public class UserResponse {
    private Long id;
    private String email;
    private String loginId;
    private String nickname;

    //enttity를 dto와 매핑
    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .loginId(user.getLoginId())
                .nickname(user.getNickname())
                .build();
    }
}