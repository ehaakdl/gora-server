package org.gora.server.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum eEnv {
    MAX_DEFAULT_QUE_SZ(1000)
    ;

    private Object value;

    public static int getDefaultIntTypeValue(eEnv key){
        return (int) key.value;
    }

    public static String getDefaultStringTypeValue(eEnv key){
        return String.valueOf(key.value);
    }
}
