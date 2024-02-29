package org.example.packetStructureCapture;

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

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(validity);
        sb.append(overflow);
        sb.append(outOfRange);
        sb.append(badReference);
        sb.append(oscillatory);
        sb.append(failure);
        sb.append(oldData);
        sb.append(inconsistent);
        sb.append(inaccurate);
        sb.append(source);
        sb.append(test);
        sb.append(operatorBlocked);
        sb.append(derived);


        return sb.toString();
    }
}
