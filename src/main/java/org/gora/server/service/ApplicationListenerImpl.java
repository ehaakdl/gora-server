package org.gora.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gora.server.common.CommonUtils;
import org.gora.server.common.eEnv;
import org.gora.server.component.network.PacketRouter;
import org.gora.server.component.network.PacketSender;
import org.gora.server.component.network.TcpServer;
import org.gora.server.component.network.UdpServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ApplicationListenerImpl implements ApplicationListener<ContextRefreshedEvent> {
    private final PacketRouter packetRouter;
    private final PacketSender packetSender;
    private final UdpServer udpServer;
    private final TcpServer tcpServer;
    @Value("${app.udp_server_port:11111}")
    private int udpServerPort;
    @Value("${app.tcp_server_port:11200}")
    private int tcpServerPort;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.info("start server");

        try {
            udpServer.startup(this.udpServerPort);
        } catch (Exception e) {
            log.error("udp server fail start");
            log.error(CommonUtils.getStackTraceElements(e));
            udpServer.shutdown();
            return;
        }
        log.info("UDP server startUp");

        try {
            tcpServer.startup(tcpServerPort);
        } catch (InterruptedException e) {
            log.error("tcp server fail start");
            log.error(CommonUtils.getStackTraceElements(e));
            tcpServer.shutdown();
            return;
        }
        log.info("TCP server startUp");

        packetRouter.run();
        log.info("receiver thread startUp");

        packetSender.run();
        log.info("sender thread startUp");
    }
}
