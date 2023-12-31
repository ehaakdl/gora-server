package org.gora.server.model.entity;

public enum eServerType {
    master, slave;

    public static eServerType convert(String serverType) {
        for (eServerType value : eServerType.values()) {
            if (value.name().equals(serverType)) {
                return value;
            }
        }
        return null;
    }
}
