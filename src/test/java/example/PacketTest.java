package example;

import java.util.List;

import com.gora.server.common.utils.NetworkUtils;
import com.gora.server.model.network.eServiceType;
import com.gora.server.model.network.protobuf.NetworkPacketProtoBuf.NetworkPacket;
import com.gora.server.model.network.protobuf.TestProtoBuf;

import com.google.protobuf.ByteString;

public class PacketTest {
    public static void main(String[] args) {
        NetworkPacket packet = NetworkUtils.getEmptyData(eServiceType.health_check);
        TestProtoBuf.Test testData = TestProtoBuf.Test.newBuilder().setMsg(ByteString.copyFromUtf8("")).build();

        List<NetworkPacket> packets = NetworkUtils.generateSegmentPacket(testData.toByteArray(), eServiceType.test,
                NetworkUtils.generateIdentify(),
                testData.toByteArray().length, NetworkUtils.UDP_EMPTY_CHANNEL_ID);
    }
}
