# 1. 노드 설치된 환경 가져오기
FROM node:20-alpine

# 2. 내 코드가 들어갈 폴더 만들기
WORKDIR /app

# 3. 내 컴퓨터에 있는 소스코드 몽땅 복사하기
COPY . .

# 4. 필요한 라이브러리 설치
RUN npm install

# 5. (Next.js 빌드 - 생략하고 바로 dev로 돌려도 됩니다만, 배포용이니 빌드는 해줍시다)
RUN npm run build

# 6. 서버 실행 (3000번 포트에서 Next.js 실행)
EXPOSE 3000
CMD ["npm", "run", "start"]
