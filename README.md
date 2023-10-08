# 장르: mmorpg 게임

# 스펙
- java(17)
- Spring boot(3.1.4)

# port
- udp_server_port: 11111(서버가 listen 하는 포트)
- udp_client_port: 11112(클라이언트가 listen 하는 포트)
- tcp_server_port: 11200

# dockerize
```
gradlew build --exclude-task test
gradlew jibDockerBuild
```

# runtime
```.docker/env.example 파일에 담긴 환경변수를 실행할때 추가해준다.(vscode 사용시 기본으로 셋팅되어있다.)```
<img width="631" alt="image" src="https://github.com/ehaakdl/gora-backend/assets/6407466/7c2860cb-e365-499d-b78d-d4043d91df73">


# 설명
- 빠른 완성을 위해 통신 프로토콜 최적화x, 프레임워크 사용
- 게임에 필요한 기본적인 요소들 구성
- 통신 셋팅
- 이벤트 router, 네트워크 sender

# 스레드 목록
- Listen TCP 소켓
- 이벤트 처리 Router
- 네트워크 sender


# 구성도
