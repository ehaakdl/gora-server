##### 설명
```mmorpg 게임, 클라이언트에 실시간 데이터를 처리하는 곳 입니다.```
  
##### 스펙
- java(17)
- Spring boot(3.1.4)

##### port list
- udp_server_port: 11111(서버가 listen 하는 포트)
- udp_client_port: 11112(클라이언트가 listen 하는 포트)
- tcp_server_port: 11200

##### env
```
DB_USERNAME=gora
DB_PASSWORD=1234
DB_HOST=localhost
DB_PORT=3306
DB_NAME=gora
MAX_DEFAULT_QUE_SZ=1000
JWT_SECRET_KEY=a89e2da3-704d-4ff0-a803-c8d8dc57cbf1
TCP_ACCEPT_THREAD_COUNT=1
TCP_ACCEPT_EVENT_COUNT=1
```

##### dockerize
```
gradlew build --exclude-task test
gradlew jibDockerBuild
```
##### protobuf
```
protoc -I=./protobuf --java_out=./protobuf/out ./protobuf/*.proto
```

##### 실행방법
```.docker/env.example 파일에 담긴 환경변수를 실행할때 추가해준다.(vscode 사용시 기본으로 셋팅되어있다.)```
<img width="728" alt="image" src="https://github.com/ehaakdl/gora-server/assets/6407466/45153458-3a8d-482d-b0ab-0e75c62a1c7c">


##### 구조
![Server Architecture](https://github.com/ehaakdl/gora-server/assets/6407466/51e55d46-7e3a-43a2-b165-320af1c7971e)
