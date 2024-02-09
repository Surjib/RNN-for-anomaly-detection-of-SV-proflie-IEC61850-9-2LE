package org.example.common;

/** Метка времени */
public class Timestamp extends GenDataObject {

    private long value = 0L;




    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }
}
