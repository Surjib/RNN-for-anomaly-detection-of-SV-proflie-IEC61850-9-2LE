package org.example.packetStructure;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Quality {
    private int  validity;

    private int overflow;

    private int outOfRange;

    private int badReference;

    private int oscillatory;

    private int failure;

    private int oldData;

    private int inconsistent;

    private int inaccurate;

    private int source;

    private int test;

    private int operatorBlocked;

    private int derived;
}
