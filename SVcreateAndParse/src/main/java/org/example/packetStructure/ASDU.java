package org.example.packetStructure;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ASDU {

    private String svID;

    private int smpCnt;

    private int confRev;

    private int smpSynch;

    private PhsMeas Dataset;


}
