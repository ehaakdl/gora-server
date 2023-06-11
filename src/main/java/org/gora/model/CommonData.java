package org.gora.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.net.InetSocketAddress;

/**
 * {@link CommonData} is the Entity for parsed message.
 *
 * @author Sameer Narkhede See <a href="https://narkhedesam.com">https://narkhedesam.com</a>
 * @since Sept 2020
 */
@Data
@AllArgsConstructor
public class CommonData {
    private Object data;
}
