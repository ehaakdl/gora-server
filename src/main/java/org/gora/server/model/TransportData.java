package org.gora.server.model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import org.gora.server.common.CommonUtils;
import org.gora.server.model.exception.ExpiredPacketException;
import org.gora.server.model.network.ClientNetworkBuffer;
import org.gora.server.model.network.ClientNetworkDataWrapper;
import org.gora.server.model.network.eNetworkType;
import org.gora.server.model.network.eServiceType;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TransportData {
    private String chanelId;
    private byte[] data;
    private eServiceType type;

    public static TransportData create(eServiceType type, byte[] data, String chanelId) {
        return TransportData.builder().chanelId(chanelId).data(data).type(type).build();
    }

    public static TransportData convert(ByteArrayOutputStream dataBuffer, byte[] data, int dataNonPaddingSize,
            ClientNetworkDataWrapper dataWrapper, int totalSize, eServiceType serviceType, String identify,
            eNetworkType networkType, String resourceKey, ClientNetworkBuffer clientNetworkBuffer) throws IOException {
        dataBuffer.write(Arrays.copyOf(data, dataNonPaddingSize));
        dataWrapper.setAppendAt(System.currentTimeMillis());
        if (dataBuffer.size() == totalSize) {
            try {
                return TransportData.create(serviceType, dataBuffer.toByteArray(),
                        resourceKey);
            } catch (Exception e) {
                throw new ExpiredPacketException(resourceKey, e);
            }
        } else if (dataBuffer.size() > totalSize) {
            throw new RuntimeException();
        } else {
            return null;
        }
    }
}
