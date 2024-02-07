package org.example.packetStructure;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SvPacket {
    private String macDst;

    private String macSrs;

    private String type;

    private String timestamp;

    private String appId;

    private int length;

    private String reserved1;

    private String reserved2;

    private APDU apdu = new APDU();


}

