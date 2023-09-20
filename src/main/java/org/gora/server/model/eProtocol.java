package org.gora.server.model;

import lombok.Getter;

//전송할 데이터가 어떤 프로토콜 인지 나타냄
@Getter
public enum eProtocol {
    tcp, udp
}
