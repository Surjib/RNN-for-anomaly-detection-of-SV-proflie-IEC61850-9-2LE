package org.example.filters;


/** Фильтр Фурье**/
public class Fourier {

    public static int size = 80;  // Размер буфера/выборки

    private final double[] bufferX = new double[size]; // Цикличный буфер проекции Х

    private final double[] bufferY = new double[size]; // Цикличный буфер проекции Y

    private int count = 0; // Счетчик для обхода по буферу

    private double sumX = 0;

    private double sumY = 0;

    double x;
    double y;

    double Fx = 0.0;
    double Fy = 0.0;

    double mag = 0.0;

    double angle = 0.0;



    public double[] process(double instVal) {

        x =  instVal*Math.sin(2*Math.PI*count/size);
        y = instVal*Math.cos(2*Math.PI*count/size);

        sumX += x + bufferX[count];
        sumY += y + bufferY[count];

        bufferX[count] = -x;
        bufferY[count] = -y;

        Fx = 2.0/size * sumX;
        Fy = 2.0/size * sumY;

        mag = Math.round(Math.sqrt(Math.pow(Fx, 2) + Math.pow(Fy, 2)) * 1000d) / 1000d;
        angle = Math.round(Math.toDegrees(Math.atan(Fy/Fx)) * 1000d) / 1000d;


        count++;
        if(count >= size) count = 0;
        double [] result = {mag, angle};
        return result;
    }



}
