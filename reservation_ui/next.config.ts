import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  /* 기존 설정이 있다면 여기에 유지하세요 */

  async rewrites() {
    return [
      {
        // 로컬에서 /api로 시작하는 모든 요청을 가로챕니다.
        source: '/api/:path*',
        // 백엔드 서버(스프링 부트 등) 주소로 넘겨줍니다.
        destination: 'http://localhost:8080/api/:path*',
      },
    ];
  },
};

export default nextConfig;