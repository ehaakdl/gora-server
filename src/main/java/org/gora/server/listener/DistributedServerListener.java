package org.gora.server.listener;

import org.gora.server.model.entity.ServerInfoEntity;
import org.gora.server.model.entity.eServerType;
import org.gora.server.repository.ServerInfoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class DistributedServerListener implements ApplicationListener<ContextRefreshedEvent> {
    private final ServerInfoRepository serverInfoRepository;
    @Value("${app.udp_server_port}")
    private Integer udpServerPortEnv;
    @Value("${app.tcp_server_port}")
    private Integer tcpServerPortEnv;
    @Value("${app.server_type}")
    private String serverTypeEnv;
    @Value("${app.min_x}")
    private Float minXEnv;
    @Value("${app.max_x}")
    private Float maxXEnv;
    @Value("${app.minY}")
    private Float minYEnv;
    @Value("${app.maxY}")
    private Float maxYEnv;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.info("server info write");

        String tcpServerPort = String.valueOf(tcpServerPortEnv);
        String udpServerPort = String.valueOf(udpServerPortEnv);
        eServerType serverType = eServerType.convert(serverTypeEnv);
        ServerInfoEntity serverInfoEntity = ServerInfoEntity.create(serverType, maxXEnv, minXEnv, minYEnv, maxYEnv,
                tcpServerPort, udpServerPort);

        serverInfoRepository.save(serverInfoEntity);
    }
}
