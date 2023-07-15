package org.gora.server.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gora.server.common.CommonUtils;

import java.io.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class CommonData implements Serializable{
    private Object data;
    private eCodeType type;
    private String key;
}
