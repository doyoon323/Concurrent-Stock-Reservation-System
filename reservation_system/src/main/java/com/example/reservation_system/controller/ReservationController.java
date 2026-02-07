package com.example.reservation_system.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.reservation_system.dto.request.ReservationRequest;
import com.example.reservation_system.facade.ReservationFacade;
import com.example.reservation_system.service.ReservationService;

import lombok.RequiredArgsConstructor;

@RestController //웹 요청을 받아서 데이터를 반환하는 곳 
@RequestMapping("/api/reservations")
@RequiredArgsConstructor // private final을 자동으로 초기화해줌. 생성자로 
public class ReservationController {
    private final ReservationService reservationService;
    private final ReservationFacade reservationFacade; // Service 대신 Facade 주입!

    @PostMapping(value = {"", "/{lockType}"}) //두가지 경로 처리 
    public ResponseEntity<Long> create(
        @PathVariable(required = false) String lockType, // 경로 변수가 없어도 에러 안 남
        @RequestBody ReservationRequest request
    ){
      
        String type = (lockType == null) ? "pessimistic" : lockType;
        
        Long id = reservationFacade.createReservation(
            request.getUserId(),
            request.getProductId(),
            request.getAmount(),
            type
        );

        return ResponseEntity.ok(id);
    }

    




    // POST /api/reservations/{id}/confirm?finalAmount=2
    @PostMapping("/{id}/confirm")
    public ResponseEntity<Void> confirm(//void 데이터,내용물은 따로 없어! 상태만 알려줄게(404,200..)
        @PathVariable Long id,  // Path의 일부를 변수로 쓰겠다. 
        @RequestParam int finalAmount) { // ? 를 붙여서 데이터를 전달할건데, 추가적인 옵션이나 수치야. 
        reservationService.confirmReservation(id, finalAmount);
        
        return ResponseEntity.ok().build();  
    }
    

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancel(@PathVariable Long id) { 
        reservationService.cancelHold(id);
        return ResponseEntity.ok().build();
    }
   
}
