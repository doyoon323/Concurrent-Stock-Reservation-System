import http from 'k6/http';
import { check, sleep } from 'k6';
import { Counter, Trend } from 'k6/metrics';

// 커스텀 지표 추가 (성공/실패 횟수를 더 명확히 보기 위함)
const successCounter = new Counter('successful_orders');
const failureCounter = new Counter('failed_orders');

export const options = {
  vus: __ENV.VUS || 10,           // 환경변수로 가변 처리
  iterations: __ENV.ITERS || 100,
};

const TEST_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNzY4NDg5OTc5LCJleHAiOjE3Njg1NzYzNzl9.cVg4gE2bZ4j9boE3E7MwNUf7iPj8Zjnat7IONbvrq2M"
const LOCK_TYPE = __ENV.TYPE || 'pessimistic'; // pessimistic, optimistic, redis, atomic

export function setup() {
  const productId = 12;
  const targetQuantity = 500;
  
  // 1. URL 수정 (// 제거 및 @RequestParam 반영)
  // 2. http.post -> http.patch로 변경
  //const url = `http://localhost:8080/api/products/${productId}/stock?quantity=${targetQuantity}`;
  const url = `http://localhost/api/products/${productId}/stock?quantity=${targetQuantity}`;
  
  const params = {
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${TEST_TOKEN}`,
    },
  };

  const res = http.patch(url, null, params); // Body는 비우고 URL 파라미터로 전달
  
  console.log(`[Setup] Inventory Reset for Product ${productId} to ${targetQuantity}: ${res.status === 200 ? 'Success' : 'Failed'}`);
  
  return { status: res.status };
}


export default function () {
  ///const url = `http://localhost:8080/api/reservations?type=${LOCK_TYPE}`;
  const url = `http://localhost/api/reservations/${LOCK_TYPE}`;
  
  const payload = JSON.stringify({
    userId: 1,
    productId: 12,
    amount: 1
  });

  const params = {
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${TEST_TOKEN}`,
    },
  };

  const res = http.post(url, payload, params);
  
  const isSuccess = res.status === 200 || res.status === 201;
  
  if (isSuccess) {
    successCounter.add(1);
  } else {
    failureCounter.add(1);
  }

  check(res, {
    'is status 2xx': (r) => isSuccess,
  });
}

//k6 run -e TYPE=pessimistic -e VUS=10 -e ITERS=100 test_template.js
//k6 run -e TYPE=pessimistic -e VUS=1000 -e ITERS=2000 test_template.js
//k6 run -e TYPE=optimistic -e VUS=1000 -e ITERS=2000 test_template.js