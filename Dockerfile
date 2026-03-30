# 공식 PostgreSQL 16 버전 이미지 사용 (자바 21 환경에 적합한 최신 계열)
FROM postgres:16-alpine

# 환경 변수 설정 (컨테이너 실행 시 기본값)
ENV POSTGRES_USER=apanguser
ENV POSTGRES_PASSWORD=apangpass
ENV POSTGRES_DB=apang_db

# 포트 개방
EXPOSE 5432

