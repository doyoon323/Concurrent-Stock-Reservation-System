package com.example.reservation_system.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.reservation_system.entity.Product;
import com.example.reservation_system.entity.Reservation;
import com.example.reservation_system.entity.Stock;
import com.example.reservation_system.entity.User;
import com.example.reservation_system.repository.ProductRepository;
import com.example.reservation_system.repository.ReservationRepository;
import com.example.reservation_system.repository.StockRepository;
import com.example.reservation_system.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationService {
    
    private final ReservationRepository reservationRepository;
    private final StockRepository stockRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    



    /**
     * 예약 생성
     * @param userId,productId
     * @return long
     */
    @Transactional // 재고차감은 됐는데 예약이 안되거나하는 경우는 없음. 전분 실패하거나, 전부 성공하거나.  (중간에 하나라도 실패하면 DB는 깨끗하게 원상복구)
    public Long createReservation(Long userId, Long productId, int amount, String type){
        Stock stock;
        if ("pessimistic".equals(type)) stock = stockRepository.findByProductIdWithPessimisticLock(productId).orElseThrow();
        else if ("optimistic".equals(type)) stock = stockRepository.findByProductIdWithOptimisticLock(productId).orElseThrow();
        else stock = stockRepository.findByProductId(productId).orElseThrow(); 
        
        stock.decreaseQuantity(amount);

        Product product = productRepository.findById(productId)
                    .orElseThrow(()->new IllegalArgumentException("존재하지 않는 상품입니다."));
    
        User user = userRepository.findById(userId)
                .orElseThrow(()->new IllegalArgumentException("존재하지 않는 유저입니다."));
                
        Reservation reservation = Reservation.builder()
                    .user(user)
                    .product(product)
                    .amount(amount)
                    .status("PENDING")
                    .expiryDate(LocalDateTime.now().plusMinutes(5))
                    .build();

        return reservationRepository.save(reservation).getId();
    } // Transactional은 일단 걸고 보는게 제일 좋은것같다. 재검토할 때 뺄 수 있음 뺴는게 좋을듯(txn 비용이 크다면) 
    // Transactional -> 최초의 데이터 상태를 기억해두기 때문에, 끝날 때 quantity가 변하면 save()를 하지 않아도 JPA가 알아서 update 쿼리를 날림
    

    /// 사용자 A의 요청을 선점하기 (실제 재고 칸에서 max만큼 빼버리기)
    /// 예약표 작성 -> A가 찜했고, 5분 뒤면 무효라는 쪽지를 만들기
    /// 자물쇠 풀기
    /// 3개 예약됨 


    /// A는 5분이라는 골든타임이 생긴다. 결제하거나 살까말까 고민할 수 있다. 
    /// 최대재고 3개 중 2개를 살거라고 수정을 한다 -> 예약표를 변경하고 상태를 확정
    /// 남은 1개는 실제 재고 칸으로 돌려보내기 

// 예약 작성까진 완성했으니, 이제 결제할건지 취소할건지 고민해야한다.
    
    /**
     *  결제 확정 
     */
    @Transactional
    public void confirmReservation(Long reservationId, int finalAmount){
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(()-> new IllegalArgumentException("예약 정보를 찾을 수 없습니다."));
        
                if (!"PENDING".equals(reservation.getStatus())){
                    throw new IllegalArgumentException("이미 처리되었거나 취소된 예약입니다.");
                }

                if (finalAmount > reservation.getAmount()){
                    throw new IllegalArgumentException("선점한 수량보다 많이 구매할 수 없습니다.");
                }

                // 찜한 수량과 결제 수량이 똑같다면?
                if (reservation.getAmount() == finalAmount) {
                    reservation.confirm(finalAmount); // Stock 락 없이 즉시 확정!
                    return; 
                }
                int leftover = reservation.getAmount() - finalAmount;

                if (leftover > 0){
                    Stock stock = stockRepository.findByProductIdWithPessimisticLock(reservation.getProduct().getId())
                        .orElseThrow(()-> new IllegalArgumentException("재고 정보가 없습니다."));
                    
                    stock.increaseQuantity(leftover);
                }
                reservation.confirm(finalAmount);
    }

    /**
     * 결제 전 사용자 요청에 의한 취소 
     */
    @Transactional
    public void cancelHold(Long reservationId){
        Reservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow(() -> new IllegalArgumentException("예약 정보를 찾을 수 없습니다."));

        if (!"PENDING".equals(reservation.getStatus())) {
            throw new IllegalStateException("취소 가능한 상태가 아닙니다.");
        }

        Stock stock = stockRepository.findByProductIdWithPessimisticLock(reservation.getProduct().getId())
                .orElseThrow(() -> new IllegalArgumentException("재고 정보가 없습니다."));
        stock.increaseQuantity(reservation.getAmount());

        reservation.cancel();
    }



    /**
     * 결제 후 주문 취소   (현재 결제 후 주문 취소가 불가능 합니다 ㅋ 이건 나중으로 미룰게요)
     * @param reservationId
     */
    public void cancelReservation(Long reservationId){
    }
}