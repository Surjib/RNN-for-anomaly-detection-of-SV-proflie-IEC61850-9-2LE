package org.example.packetStructure;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter @Setter
public class APDU {
    private int noASDU;

    private ArrayList<ASDU> seqASDU = new ArrayList<ASDU>();
}
