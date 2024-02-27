package org.example.logiclanodes.measurements;



import lombok.Getter;
import lombok.Setter;
import org.example.filters.Fourier;
import org.example.packetStructureCapture.PhsMeas;
import org.example.packetStructureCapture.SvPacket;

import java.util.ArrayList;

/** Узел расчета токов, напряжений, мощностей**/
public class MMXU {

    /* Пофазно мгновенные значения на вход
    * Ток*/
    public ArrayList<double[]> IphsA = new ArrayList<>();
    public ArrayList<double[]> IphsB = new ArrayList<>();
    public ArrayList<double[]> IphsC = new ArrayList<>();

    /* Напряжение */
    public ArrayList<double[]> UphsA = new ArrayList<>();
    public ArrayList<double[]> UphsB = new ArrayList<>();
    public ArrayList<double[]> UphsC = new ArrayList<>();


    @Setter@Getter
    private ArrayList<SvPacket> SvPackets = new ArrayList<>();


    /* Экземпляры фильтров для каждой фазы *
    * Ток*/
    private final Fourier phsACurrent = new Fourier();
    private final Fourier phsBCurrent = new Fourier();
    private final Fourier phsCCurrent = new Fourier();

    /* Напряжение*/
    private final Fourier phsAVoltage = new Fourier();
    private final Fourier phsBVoltage = new Fourier();
    private final Fourier phsCVoltage = new Fourier();



    public void process(PhsMeas value) {
        /** Фильтрация анлогового сигнала*/
        IphsA.add(phsACurrent.process(value.getInstIa() / 1000d));
        IphsB.add(phsBCurrent.process(value.getInstIb() / 1000d));
        IphsC.add(phsCCurrent.process(value.getInstIc() / 1000d));

        UphsA.add(phsAVoltage.process(value.getInstUa() / 1000d));
        UphsB.add(phsBVoltage.process(value.getInstUb() / 1000d));
        UphsC.add(phsCVoltage.process(value.getInstUc() / 1000d));


    }
}
