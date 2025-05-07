package org.example.logiclanodes.input;




import org.example.dataobjects.measurements.MV;
import org.example.logiclanodes.common.LN;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/** Узел для считывания COMTRADE файла **/
public class LCOM extends LN {

    private File cfgFile; //Считываемый .cfg файл
    private File datFile; //Считываемый .dat файл

    /* Списки содержимого .dat и .cfg файлов (Построчно)*/
    private List<String> cfgDataFile = new ArrayList<>();
    private List<String> datDataFile = new ArrayList<>();

    /* Списки масштабирующих коэф. a, b */
    private List<Double> aCoefList = new ArrayList<>();
    private List<Double> bCoefList = new ArrayList<>();

    private int analogNumber = 0; // Число аналоговых сигналов
    private int discreteNumber = 0; // Число дискретных сигналов
    private Iterator<String> dataIterator; // Итератор для прохода по списку

    public final List<MV> OUT = new ArrayList<>();  // Список измеренных значений

    /* Конструктор узла создающий буфер измеренных значений  */
    public LCOM() {
        for (int i = 0; i < 20; i++) {
            OUT.add(new MV());
        }
    }

    /**Запись скорректированного измеренного значения каждого из каналов **/
    @Override
    public void process() {
        if(dataIterator.hasNext()) {
            String[] line = dataIterator.next().split(",");
            long t = Long.parseLong(line[1].trim());

            for (int i = 0; i < analogNumber; i++) {
                double value = Double.parseDouble(line[i + 2]);
                value *= aCoefList.get(i);
                value += bCoefList.get(i);

                OUT.get(i).getT().setValue(t);
                OUT.get(i).getInstMag().getF().setValue(Double.valueOf((int) (value * 1000000))); //возможно 1000 чтобы результат был в А и В
            }

//            System.out.println(OUT.get(3).getInstMag().getF().getValue());
        }
    }

    public boolean hasNextData() {
        return dataIterator.hasNext();
    }

    /** Считывание содержимого .cfg и .dat файлов **/
    public void setFilePath(String path, String name) {

        cfgFile = new File(path + name + ".cfg");
        datFile = new File(path + name + ".dat");


        cfgDataFile = readFileByLine(cfgFile);
        datDataFile = readFileByLine(datFile);

        dataIterator = datDataFile.listIterator();

        extractCfgFileData();
    }

    /** Запись содержимого .dat файла (число сигналов)**/
    private void extractCfgFileData() {
        analogNumber = Integer.parseInt(cfgDataFile.get(1).split(", ")[1].replace("A", ""));
        discreteNumber = Integer.parseInt(cfgDataFile.get(1).split(", ")[2].replace("D", ""));

        for (int i = 2; i < 2 + analogNumber; i++) {
            aCoefList.add(Double.parseDouble(cfgDataFile.get(i).split(",")[5]));
            bCoefList.add(Double.parseDouble(cfgDataFile.get(i).split(",")[6]));
        }
    }

    /** Построчное чтение файла и запись в список**/
    private static List<String> readFileByLine(File file){
        List<String> list = new ArrayList<>();

        try {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);

            String line = br.readLine();
            while(line != null){
                list.add(line);
                line = br.readLine();
            }
            br.close();

        } catch (FileNotFoundException e) {
            System.err.println("Неверно указан путь");;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return list;
    }
}
