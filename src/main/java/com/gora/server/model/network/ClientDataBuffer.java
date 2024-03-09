package com.gora.server.model.network;

import java.io.ByteArrayOutputStream;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@RequiredArgsConstructor
public class ClientDataBuffer {
    private final ByteArrayOutputStream buffer;
    private int recentSequence;
}