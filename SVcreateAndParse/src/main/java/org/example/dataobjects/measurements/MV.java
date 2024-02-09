package org.example.dataobjects.measurements;


import org.example.common.AnalogueValue;
import org.example.common.GenDataObject;
import org.example.common.Quality;
import org.example.common.Timestamp;

/** Измеряемое значение **/
public class MV extends GenDataObject {


    private AnalogueValue instMag = new AnalogueValue();


    private Quality q = new Quality();


    private Timestamp t = new Timestamp();






    public AnalogueValue getInstMag() {
        return instMag;
    }

    public void setInstMag(AnalogueValue instMag) {
        this.instMag = instMag;
    }

    public Quality getQ() {
        return q;
    }

    public void setQ(Quality q) {
        this.q = q;
    }

    public Timestamp getT() {
        return t;
    }

    public void setT(Timestamp t) {
        this.t = t;
    }
}
