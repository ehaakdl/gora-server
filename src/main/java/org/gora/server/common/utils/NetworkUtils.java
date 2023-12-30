package org.gora.server.common.utils;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.gora.server.model.network.eServiceType;
import org.gora.server.model.network.protobuf.NetworkPacketProtoBuf.NetworkPacket;
import org.springframework.stereotype.Component;

import com.google.protobuf.ByteString;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NetworkUtils {
    public static final int DATA_MAX_SIZE = 1377;
    public static final int TOTAL_MAX_SIZE = 1500;
    public static final int PAD = 0;
    public static final String UDP_EMPTY_CHANNEL_ID = "0000000000000000000000000000000000000000000000000000000000000000";

    public static String getLocalIpAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            return null;
        }
    }

    public static byte[] removePadding(byte[] target, int paddingSize) {
        if (paddingSize <= 0) {
            return target;
        } else if (target.length < paddingSize) {
            throw new RuntimeException();
        } else if (target.length == paddingSize) {
            return target;
        }
        int lastLength = target.length - paddingSize;
        return Arrays.copyOf(target, lastLength);
    }

    public static byte[] addPadding(int blockSize) {
        byte[] result = new byte[blockSize];
        for (int index = 0; index < result.length; index++) {
            result[index] = PAD;
        }

        return result;
    }

    public static byte[] addPadding(byte[] original, int blockSize) {
        if (original == null) {
            throw new RuntimeException();
        }

        byte[] reuslt = new byte[blockSize + original.length];
        System.arraycopy(original, 0, reuslt, 0, original.length);
        for (int i = original.length; i < reuslt.length; i++) {
            reuslt[i] = PAD;
        }

        return reuslt;
    }

    public static String generateIdentify() {
        return CommonUtils.replaceUUID();
    }

    public static NetworkPacket getEmptyData(eServiceType type) {
        byte[] newBytes = addPadding(NetworkUtils.DATA_MAX_SIZE);
        return NetworkPacket.newBuilder()
                .setData(ByteString.copyFrom(newBytes))
                .setSequence(0)
                .setIdentify(NetworkUtils.generateIdentify())
                .setDataSize(0)
                .setTotalSize(0)
                .setChannelId(NetworkUtils.UDP_EMPTY_CHANNEL_ID)
                .setType(type.getType())
                .build();
    }

    public static NetworkPacket getEmptyData(eServiceType type, String udpChannelId) {
        byte[] newBytes = addPadding(NetworkUtils.DATA_MAX_SIZE);
        return NetworkPacket.newBuilder()
                .setData(ByteString.copyFrom(newBytes))
                .setSequence(0)
                .setIdentify(NetworkUtils.generateIdentify())
                .setDataSize(0)
                .setTotalSize(0)
                .setChannelId(udpChannelId)
                .setType(type.getType())
                .build();
    }

    public static List<NetworkPacket> generateSegmentPacket(byte[] target, eServiceType type,
            String identify, int totalSize, String udpChannelId) {
        if (target == null) {
            return null;
        }

        int paddingSize = calcPaddingSize(totalSize);
        if (paddingSize > 0) {
            target = addPadding(target, paddingSize);
        }

        return makePackets(target, paddingSize, type, totalSize, identify, udpChannelId);
    }

    private static int calcPaddingSize(int totalSize) {
        int paddingSize;
        if (totalSize < NetworkUtils.DATA_MAX_SIZE) {
            paddingSize = NetworkUtils.DATA_MAX_SIZE - totalSize;
        } else {
            if (totalSize % NetworkUtils.DATA_MAX_SIZE > 0) {
                paddingSize = NetworkUtils.DATA_MAX_SIZE - totalSize % NetworkUtils.DATA_MAX_SIZE;
            } else {
                paddingSize = totalSize % NetworkUtils.DATA_MAX_SIZE;
            }
        }

        return paddingSize;
    }

    private static NetworkPacket createNetworkPacket(byte[] data, int dataSize, int totalSize, String identify,
            String udpChannelId, int sequence, eServiceType serviceType) {
        return NetworkPacket.newBuilder()
                .setData(ByteString.copyFrom(data))
                .setDataSize(dataSize)
                .setTotalSize(totalSize)
                .setType(serviceType.getType())
                .setIdentify(identify)
                .setChannelId(udpChannelId)
                .setSequence(sequence)
                .build();
    }

    private static List<NetworkPacket> makePackets(byte[] target, int paddingSize, eServiceType type, int totalSize,
            String identify, String udpChannelId) {
        int segmentTotalCount = target.length / NetworkUtils.DATA_MAX_SIZE;
        int srcPos = 0;

        int dataSize = NetworkUtils.DATA_MAX_SIZE;
        int sequence = 0;
        byte[] copyBytes;
        List<NetworkPacket> result = new ArrayList<>();
        for (int index = 0; index < segmentTotalCount; index++) {
            // 마지막 데이터는 패딩이 붙기 때문에 실제 데이터 사이즈를 구해준다.
            if (index == segmentTotalCount - 1) {
                dataSize = NetworkUtils.DATA_MAX_SIZE - paddingSize;
            }
            copyBytes = new byte[NetworkUtils.DATA_MAX_SIZE];
            System.arraycopy(target, srcPos, copyBytes, 0, NetworkUtils.DATA_MAX_SIZE);
            NetworkPacket packet = createNetworkPacket(copyBytes, dataSize, totalSize, identify, udpChannelId,
                    sequence++, type);

            result.add(packet);
            srcPos = srcPos + NetworkUtils.DATA_MAX_SIZE;
        }

        return result;
    }

    public static String generateChannelId() {
        return CommonUtils.replaceUUID();
    }
}
