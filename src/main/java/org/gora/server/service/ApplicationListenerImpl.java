package org.gora.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gora.server.common.CommonUtils;
import org.gora.server.common.eEnv;
import org.gora.server.component.network.PacketRouter;
import org.gora.server.component.network.PacketSender;
import org.gora.server.component.network.UdpServer;
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

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.info("start server");

        int port = Integer.parseInt(CommonUtils.getEnv(eEnv.SERVER_PORT, eEnv.getDefaultStringTypeValue(eEnv.SERVER_PORT)));
        try {
            udpServer.startup(port);
        } catch (Exception e) {
            if (udpServer != null) {
                udpServer.shutdown();
            }
            log.error(CommonUtils.getStackTraceElements(e));
        }
        log.info("UDP server startUp");

        packetRouter.run();
        log.info("receiver thread startUp");

        packetSender.run();
        log.info("sender thread startUp");
    }
}
