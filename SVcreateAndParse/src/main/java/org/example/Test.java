package org.example;

import lombok.SneakyThrows;
import org.example.logiclanodes.common.LN;
import org.example.logiclanodes.input.LCOM;
import org.example.pcapFiles.SvPacketSender;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Test {
    private static List<LN> inList = new ArrayList<>();

    @SneakyThrows
    public static void main(String[] args) {

        LCOM lcom = new LCOM();
        inList.add(lcom);


        SvPacketSender EtListen = new SvPacketSender();
        EtListen.checkNic();
//        System.out.println(EtListen.getNicArray());

        Scanner keyboard = new Scanner(System.in);


        System.out.println("Pick a Src");
        System.out.println(EtListen.getNicArray());

        int nicSRC = 4;
        String srcMAC = EtListen.getNicMAC().get(nicSRC);
        System.out.println(
                "Source" +
                        "\nName: " + EtListen.getNicArray().get(nicSRC) +
                        "\nIP: " + EtListen.getNicIP().get(nicSRC) +
                        "\nMAC: " + srcMAC
        );


        System.out.println("Pick a Dst");
        System.out.println(EtListen.getNicArray());

        int nicDST = 1;
        String dstMAC = EtListen.getNicMAC().get(nicDST);
        System.out.println(
                "Destination" +
                "\nName: " + EtListen.getNicArray().get(nicDST) +
                "\nIP: " + EtListen.getNicIP().get(nicDST) +
                "\nMAC: " + dstMAC
                );


        String broadMAC = "ff:ff:ff:ff:ff:ff";


        EtListen.setNickName(EtListen.getNicArray().get(nicSRC));
        EtListen.initializeNetworkInterface();

        EtListen.setSrcMAC(srcMAC);
        EtListen.setDstMAC(dstMAC);

//        for (int i = 0; i < 400; i++) {

        EtListen.process();
//        }


        inList.add(EtListen);

        EtListen.phsAInst = lcom.OUT.get(0);
        EtListen.phsBInst = lcom.OUT.get(1);
        EtListen.phsCInst = lcom.OUT.get(2);

        EtListen.phsAUnst = lcom.OUT.get(3);
        EtListen.phsBUnst = lcom.OUT.get(4);
        EtListen.phsCUnst = lcom.OUT.get(5);















        lcom.setFilePath(
                "E:\\DZ\\11sem\\AI_Enregy\\KP\\pythonProject\\PSCAD_files\\testGrid.gf42\\Rank_00001\\Run_00001\\",
                "ABC_W1");

        /** Вставить запуск EthernetListener'a */

        while (lcom.hasNextData()) {
            inList.forEach(LN::process);
        }
        System.out.println("end");
    }
}
