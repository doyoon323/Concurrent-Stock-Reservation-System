import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  vus: 100,
  iterations: 1000,
};

// 1. 실제 테스트용 토큰과 유저 ID를 여기에 넣으세요
const TEST_TOKEN = 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIzMiIsImlhdCI6MTc2NzY4MDk4MiwiZXhwIjoxNzY3NzY3MzgyfQ.2bF1lnIqONRVJRGAmd3dQoxf97hy7W7M72VfYJD73t4';
const TEST_USER_ID = 32; 

export default function () {
  const url = 'http://localhost:8080/api/reservations?type=atomic'; // 주소 수정
  
  const payload = JSON.stringify({
    userId: TEST_USER_ID,    // 필드명 수정
    productId: 12,
    amount: 1                // 필드명 수정
  });

  const params = {
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${TEST_TOKEN}`, // 인증 헤더 추가
    },
  };

  const res = http.post(url, payload, params);
  
  check(res, {
    'is status 200': (r) => r.status === 200,
    'is status 201': (r) => r.status === 201, // 서버 설정에 따라 201일 수도 있음
  });
}