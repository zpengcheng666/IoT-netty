package com.sydh.common;

import com.sydh.protocol.base.protocol.IProtocol;
import lombok.Data;

/**
 * @author bill
 */
@Data
public class ProtocolColl {

    private IProtocol protocol;

    private Long productId;

    private String transport;
}
