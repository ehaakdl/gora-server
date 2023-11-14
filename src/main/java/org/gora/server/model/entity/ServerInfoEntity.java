package org.gora.server.model.entity;

import java.util.Date;

import org.gora.server.common.NetworkUtils;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "server_info")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
public class ServerInfoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;
    @Column
    private Float minX;
    @Column
    private Float maxX;
    @Column
    private Float minY;
    @Column
    private Float maxY;
    @Column
    private String tcpPorts;
    @Column
    private String udpPorts;
    @Column
    private String ip;
    @Column
    private Boolean isConnect;
    @Column
    @Enumerated(EnumType.STRING)
    private eServerType type;
    @CreationTimestamp
    @Column
    private Date createdAt;
    @UpdateTimestamp
    @Column
    private Date updatedAt;
    @Column
    @ColumnDefault("-1")
    private Long createdBy;
    @Column
    // todo mysql 8 버전 on update 에 -1 추가못함
    @ColumnDefault("-1")
    private Long updatedBy;

    public static ServerInfoEntity create(eServerType type, Float maxX, Float minX, Float minY, Float maxY,
            String tcpPorts,
            String udpPorts) {

        return ServerInfoEntity.builder()
                .ip(NetworkUtils.getLocalIpAddress())
                .isConnect(true)
                .maxX(maxX)
                .minX(minX)
                .maxY(maxY)
                .minY(minY)
                .tcpPorts(tcpPorts)
                .udpPorts(udpPorts)
                .build();
    }

    public static UserEntity createBasicUser(String password, String email) {
        return UserEntity.builder()
                .type(eUserType.basic)
                .password(password)
                .email(email)
                .build();

    }
}
