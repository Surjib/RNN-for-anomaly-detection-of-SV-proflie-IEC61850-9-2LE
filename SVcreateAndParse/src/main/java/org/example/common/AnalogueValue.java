package org.example.common;


/** Аналоговый сигнал **/
public class AnalogueValue extends GenDataObject {


    private GenDataAttribute<Double> f = new GenDataAttribute<>(0d); // Значение аналогового сигнала






    public GenDataAttribute<Double> getF() {
        return f;
    }

    public void setF(GenDataAttribute<Double> f) {
        this.f = f;
    }
}
