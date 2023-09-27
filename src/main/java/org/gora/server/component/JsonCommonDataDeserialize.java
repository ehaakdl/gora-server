package org.gora.server.component;

import java.io.IOException;

import org.gora.server.model.CommonData;
import org.gora.server.model.eServiceRouteType;
import org.gora.server.model.network.PlayerCoordinate;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonCommonDataDeserialize extends JsonDeserializer<CommonData> {

    @Override
    public CommonData deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode root = p.getCodec().readTree(p);
            eServiceRouteType serviceRouteType = eServiceRouteType.convert(root.get("type").asInt());
            if (serviceRouteType == null) {
                throw new JsonParseException("is not null service route type");
            }

            CommonData commonData = new CommonData();
            commonData.setType(serviceRouteType);
            commonData.setKey(root.get("key").asText());

            switch (serviceRouteType) {
                case player_coordinate:
                    PlayerCoordinate playerCoordinate = objectMapper.treeToValue(root.get("data"),
                            PlayerCoordinate.class);
                    if (playerCoordinate == null) {
                        throw new RuntimeException();
                    }
                    commonData.setData(playerCoordinate);
                    break;

                default:
                    throw new RuntimeException();
            }

            return commonData;
        } catch (Exception e) {
            throw new JsonParseException(p, "json parser fail", e);
        }
    }

}