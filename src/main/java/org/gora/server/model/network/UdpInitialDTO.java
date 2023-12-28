package org.gora.server.model.network;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@RequiredArgsConstructor
public class UdpInitialDTO {
    private final String clientIp;
}