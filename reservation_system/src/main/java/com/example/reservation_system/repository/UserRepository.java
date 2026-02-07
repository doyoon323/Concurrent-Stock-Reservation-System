package com.example.reservation_system.repository;

import com.example.reservation_system.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;


public interface UserRepository extends JpaRepository<User,Long>{ // 이 창고는 User 전용이며, 이 물건을 찾을 때 쓰는 열쇠타입은 long이다. 
// 왜 interface 일까? -> 우리는 이런 기능을 원해라고만 선언만하고, 실제 
// 구현은 spring이 런타임에 대행해주기 때문.List<User> findByUserId(long userId);
// 작동원리 -> spring data jpa가 다이나믹 프록시 기술을 사용한다. 
    // JpaRepository에 이미 아래와 같은 기능들이 존재
    /*
    save()
    findByID( id로 데이터 한 건 찾기 )
    findAll (모든 데이터 목록 가져오기)
    delete() (데이터 삭제)
    */
    //이메일로 유저를 찾는 기능     

    Optional<User> findByLoginId(String loginId); // 유저 객체 전체를 반환하는 것

    boolean existsByLoginId(String loginId);
    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);
}