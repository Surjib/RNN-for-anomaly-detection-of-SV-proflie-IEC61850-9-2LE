package org.example;

import lombok.SneakyThrows;
import org.example.logiclanodes.common.LN;
import org.example.logiclanodes.input.LCOM;
import org.example.pcapFiles.EthernetListener;
import org.example.pcapFiles.SvPacketSender;

import java.util.*;

public class ComtradeParser {
    private static List<LN> inList = new ArrayList<>();


    @SneakyThrows
    public void CreateCSV(){


        Thread.sleep(20000);

        EthernetListener ethernetListener = new EthernetListener();
        ethernetListener.process();


        LCOM lcom = new LCOM();
        inList.add(lcom);


        SvPacketSender svPacketSender = new SvPacketSender();
        svPacketSender.checkNic();
//        System.out.println(EtListen.getNicArray());


        Scanner keyboard = new Scanner(System.in);


        System.out.println("Pick a Src");
        System.out.println(svPacketSender.getNicArray());

        int nicSRC = 5;
        String srcMAC = svPacketSender.getNicMAC().get(nicSRC);
        System.out.println(
                "Source" +
                        "\nName: " + svPacketSender.getNicArray().get(nicSRC) +
                        "\nIP: " + svPacketSender.getNicIP().get(nicSRC) +
                        "\nMAC: " + srcMAC
        );


        System.out.println("Pick a Dst");
        System.out.println(svPacketSender.getNicArray());

        int nicDST = 6;
        String dstMAC = svPacketSender.getNicMAC().get(nicDST);
        System.out.println(
                "Destination" +
                "\nName: " + svPacketSender.getNicArray().get(nicDST) +
                "\nIP: " + svPacketSender.getNicIP().get(nicDST) +
                "\nMAC: " + dstMAC
                );


        String broadMAC = "ff:ff:ff:ff:ff:ff";


        svPacketSender.setNickName(svPacketSender.getNicArray().get(nicSRC));
        svPacketSender.initializeNetworkInterface();

        svPacketSender.setSrcMAC(srcMAC);
        svPacketSender.setDstMAC(broadMAC);

//        for (int i = 0; i < 400; i++) {

//        svPacketSender.process();
//        }


        inList.add(svPacketSender);

        svPacketSender.phsAInst = lcom.OUT.get(0);
        svPacketSender.phsBInst = lcom.OUT.get(1);
        svPacketSender.phsCInst = lcom.OUT.get(2);

        svPacketSender.phsAUnst = lcom.OUT.get(3);
        svPacketSender.phsBUnst = lcom.OUT.get(4);
        svPacketSender.phsCUnst = lcom.OUT.get(5);





        lcom.setFilePath(
                "E:\\DZ\\11sem\\AI_Enregy\\KP\\pythonProject\\PSCAD_files\\testGrid.gf42\\Rank_00001\\Run_00001\\",
                "ABC_W1");


        while (lcom.hasNextData()) {
            inList.forEach(LN::process);
        }
        System.out.println("end");
    }
}
