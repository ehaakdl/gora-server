package org.gora.server.common;

import java.net.InetAddress;
import java.util.Arrays;

import org.gora.server.model.network.NetworkPackcetProtoBuf.NetworkPacket;
import org.gora.server.model.network.eServiceType;
import org.springframework.stereotype.Component;

import com.google.protobuf.ByteString;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NetworkUtils {
    public static final int DATA_MAX_SIZE = 1453;
    public static final int TOTAL_MAX_SIZE = 1500;
    public static final int PAD = 0;
    public static final String UDP_EMPTY_CHANNEL_ID = "00000000000000000000000000000000";

    public static String getLocalIpAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            return null;
        }
    }

    public static byte[] removePadding(byte[] target, int paddingSize) {
        if (target.length < paddingSize) {
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
        return CommonUtils.replaceUUID();
    }

    public static NetworkPacket getEmptyData(eServiceType type, String udpChannelId) {
        byte[] newBytes = addPadding(null, NetworkUtils.DATA_MAX_SIZE);
        return NetworkPacket.newBuilder()
                .setData(ByteString.copyFrom(newBytes))
                .setDataSize(0)
                .setChannelId(udpChannelId)
                .setType(type.getType())
                .build();
    }

    public static NetworkPacket getPacket(byte[] target, eServiceType type, String udpChannelId) {
        if (target == null) {
            return null;
        }

        int dataSize = target.length;
        if (dataSize >= NetworkUtils.DATA_MAX_SIZE) {
            throw new RuntimeException();
        }

        int paddingSize;
        if (dataSize < NetworkUtils.DATA_MAX_SIZE) {
            paddingSize = NetworkUtils.DATA_MAX_SIZE - dataSize;
        } else {
            paddingSize = 0;
        }

        if (paddingSize > 0) {
            target = addPadding(target, paddingSize);
        }

        if (target.length != NetworkUtils.DATA_MAX_SIZE) {
            throw new RuntimeException();
        }

        return NetworkPacket.newBuilder()
                .setData(ByteString.copyFrom(target))
                .setChannelId(udpChannelId)
                .setDataSize(dataSize)
                .setType(type.getType())
                .build();

    }
}
