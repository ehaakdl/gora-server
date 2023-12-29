package org.gora.server.listener;

import org.gora.server.common.utils.CommonUtils;
import org.gora.server.component.Monitor;
import org.gora.server.component.network.PacketRouter;
import org.gora.server.component.network.TcpServer;
import org.gora.server.component.network.UdpServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class ThreadExecutorListener implements ApplicationListener<ContextRefreshedEvent> {
    private final PacketRouter packetRouter;
    private final UdpServer udpServer;
    private final TcpServer tcpServer;
    private final Monitor monitor;
    @Value("${app.udp_server_port}")
    private int udpServerPort;
    @Value("${app.tcp_server_port}")
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
        

        try {
            tcpServer.startup(tcpServerPort);
        } catch (InterruptedException e) {
            log.error("tcp server fail start");
            log.error(CommonUtils.getStackTraceElements(e));
            tcpServer.shutdown();
            return;
        }
        

        packetRouter.run();

        monitor.start();
    }
}
