package org.example;

import lombok.SneakyThrows;
import org.example.logiclanodes.common.LN;
import org.example.logiclanodes.input.LCOM;
import org.example.pcapFiles.SvSubscriber;
import org.example.pcapFiles.SvPublisher;

import java.util.*;

public class ComtradeParser {
    /**
     * Основной класс программы, по вызову метода CreateCSV()
     * происходит цикличное
     * 1)чтение строки данных .dat файла объектом класса LCOM
     * 2)отправка SV пакета по данным LCOM (замер 3 токов и 3 напряжений в один момент времени) объектом класса SvPublisher
     * 3)захват SV пакета объектом класса SvSubscriber и запись данных в файл группами по 7200 пакетов
     * **/
    private static List<LN> inList = new ArrayList<>();


    @SneakyThrows
    public void CreateCSV(){


        Thread.sleep(20000); // задержка для выполнения модели ПСКАД

        // создание и запуск SV подписчика
        SvSubscriber SvSubscriber = new SvSubscriber();
        SvSubscriber.process();


        // создание узла для чтения и
        LCOM lcom = new LCOM();
        lcom.setFilePath(
                "E:\\DZ\\11sem\\AI_Enregy\\KP\\pythonProject\\PSCAD_files\\testGrid.gf42\\Rank_00001\\Run_00001\\",
                "ABC_W1");
        inList.add(lcom);


        SvPublisher svPublisher = new SvPublisher();
        svPublisher.checkNic();
//        System.out.println(EtListen.getNicArray());




        System.out.println("Pick a Src");
        System.out.println(svPublisher.getNicArray());

        int nicSRC = 5;
        String srcMAC = svPublisher.getNicMAC().get(nicSRC);
        System.out.println(
                "Source" +
                        "\nName: " + svPublisher.getNicArray().get(nicSRC) +
                        "\nIP: " + svPublisher.getNicIP().get(nicSRC) +
                        "\nMAC: " + srcMAC
        );


        System.out.println("Pick a Dst");
        System.out.println(svPublisher.getNicArray());

        int nicDST = 6;
        String dstMAC = svPublisher.getNicMAC().get(nicDST);
        System.out.println(
                "Destination" +
                "\nName: " + svPublisher.getNicArray().get(nicDST) +
                "\nIP: " + svPublisher.getNicIP().get(nicDST) +
                "\nMAC: " + dstMAC
                );


        String broadMAC = "ff:ff:ff:ff:ff:ff";


        svPublisher.setNickName(svPublisher.getNicArray().get(nicSRC));
        svPublisher.initializeNetworkInterface();

        svPublisher.setSrcMAC(srcMAC);
        svPublisher.setDstMAC(broadMAC);



        inList.add(svPublisher);

        svPublisher.phsAInst = lcom.OUT.get(0);
        svPublisher.phsBInst = lcom.OUT.get(1);
        svPublisher.phsCInst = lcom.OUT.get(2);

        svPublisher.phsAUnst = lcom.OUT.get(3);
        svPublisher.phsBUnst = lcom.OUT.get(4);
        svPublisher.phsCUnst = lcom.OUT.get(5);






        while (lcom.hasNextData()) {
            inList.forEach(LN::process);
        }
        System.out.println("end");
    }
}
