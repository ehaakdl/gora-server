package org.gora.server.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommonData {
    private Object data;
    private eCodeType type;
    private String key;
    @JsonIgnore
    private String senderIp;
}
