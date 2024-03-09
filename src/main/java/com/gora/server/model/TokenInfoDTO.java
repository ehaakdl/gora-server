package com.gora.server.model;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TokenInfoDTO {
    private String token;
    private Date expiredAt;

}
