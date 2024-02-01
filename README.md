##### 설명
```mmorpg 게임, 클라이언트에서 보낸 패킷을 처리하는 곳 입니다.```
  
##### 스펙
- java(17)
- Spring boot(3.1.4)

##### Port list
- udp_server_port: 11111(서버가 listen 하는 포트)
- udp_client_port: 11112(클라이언트가 listen 하는 포트)
- tcp_server_port: 11200

##### dockerize
```
gradlew build --exclude-task test
gradlew jibDockerBuild
```

##### 실행방법
```.docker/app-example.env 파일에 담긴 환경변수를 실행할때 추가해준다.(vscode 사용시 기본으로 셋팅되어있다.)```
<img width="728" alt="image" src="https://github.com/ehaakdl/gora-server/assets/6407466/45153458-3a8d-482d-b0ab-0e75c62a1c7c">


##### 전체 구조
![Server Architecture drawio](https://github.com/ehaakdl/gora-server/assets/6407466/ef301484-3ab3-4d2f-be50-2ba6635ba595)

