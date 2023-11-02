package org.gora.server.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.gora.server.model.network.NetworkPakcetProtoBuf;
import org.gora.server.model.network.eServiceType;
import org.springframework.stereotype.Component;

import com.google.protobuf.ByteString;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NetworkUtils {
    public static final int DATA_MAX_SIZE = 841;
    public static final int TOTAL_MAX_SIZE = 1500;
    public static final int HEADER_SIZE = 659;
    public static final int PAD = 0;
    
    public static byte[] removePadding(byte[] target, int paddingSize) {
        if(target.length < paddingSize){
            throw new RuntimeException();
        } else if (target.length == paddingSize) {
            return null;
        }
        int lastLength = target.length - paddingSize;
        return Arrays.copyOf(target, lastLength);
    }

    public static byte[] addPadding(byte[] original, int blockSize) {
        byte[] padded;
        if (original == null) {
            padded = new byte[blockSize];
            for (int index = 0; index < padded.length; index++) {
                padded[index] = PAD;
            }

            return padded;
        }

        int padLength = blockSize;
        padded = new byte[original.length + padLength];
        System.arraycopy(original, 0, padded, 0, original.length);
        for (int i = original.length; i < padded.length; i++) {
            padded[i] = PAD;
        }
        return padded;
    }

    public static String getIdentify() {
        return CommonUtils.replaceUUID() + System.currentTimeMillis();
    }

    public static NetworkPakcetProtoBuf.NetworkPacket getEmptyData(eServiceType type, String identify) {
        byte[] newBytes = addPadding(null, NetworkUtils.DATA_MAX_SIZE);
        return NetworkPakcetProtoBuf.NetworkPacket.newBuilder()
                .setData(ByteString.copyFrom(newBytes))
                .setDataSize(0)
                .setType(type.getType())
                .setIdentify(identify)
                .build();
    }

    public static List<NetworkPakcetProtoBuf.NetworkPacket> getSegment(byte[] target, eServiceType type,
            String identify, int startIndex, String key) {
        List<NetworkPakcetProtoBuf.NetworkPacket> result = new ArrayList<>();
        byte[] newBytes;
        if (target == null) {
            return null;
        }

        int totalSize = target.length;
        int paddingSize;
        if (totalSize < NetworkUtils.DATA_MAX_SIZE) {
            paddingSize = NetworkUtils.DATA_MAX_SIZE - totalSize;
        } else if (totalSize > NetworkUtils.DATA_MAX_SIZE) {
            if (totalSize % NetworkUtils.DATA_MAX_SIZE > 0) {
                paddingSize = NetworkUtils.DATA_MAX_SIZE - totalSize % NetworkUtils.DATA_MAX_SIZE;
            } else {
                paddingSize = 0;
            }
        } else {
            paddingSize = 0;
        }

        if (paddingSize > 0) {
            target = addPadding(target, paddingSize);
        }

        int segmentCount = target.length / NetworkUtils.DATA_MAX_SIZE;
        int srcPos = 0;

        int dataSize = NetworkUtils.DATA_MAX_SIZE;
        for (int index = 0; index < segmentCount; index++) {
            // 마지막 데이터는 패딩이 붙기 때문에 실제 데이터 사이즈를 구해준다.
            if (index == segmentCount - 1) {
                dataSize = NetworkUtils.DATA_MAX_SIZE - paddingSize;
            }
            newBytes = new byte[NetworkUtils.DATA_MAX_SIZE];
            System.arraycopy(target, srcPos, newBytes, 0, NetworkUtils.DATA_MAX_SIZE);
            NetworkPakcetProtoBuf.NetworkPacket packet = NetworkPakcetProtoBuf.NetworkPacket.newBuilder()
                    .setData(ByteString.copyFrom(newBytes))
                    .setDataSize(dataSize)
                    .setTotalSize(totalSize)
                    .setType(type.getType())
                    .setIdentify(identify)
                    .build();
            
            
            result.add(packet);
            srcPos = srcPos + NetworkUtils.DATA_MAX_SIZE;
        }

        if (result.isEmpty()) {
            return null;
        } else {
            return result;
        }
    }
}
