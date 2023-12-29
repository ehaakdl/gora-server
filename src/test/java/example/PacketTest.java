package example;

import java.util.List;

import org.gora.server.common.utils.NetworkUtils;
import org.gora.server.model.network.eServiceType;
import org.gora.server.model.network.protobuf.NetworkPacketProtoBuf.NetworkPacket;
import org.gora.server.model.network.protobuf.TestProtoBuf;

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
