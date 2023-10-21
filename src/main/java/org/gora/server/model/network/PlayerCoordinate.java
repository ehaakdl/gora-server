package org.gora.server.model.network;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerCoordinate implements Serializable{
    private float x;
    private float y;
}
