package org.example;

import lombok.SneakyThrows;
import org.example.logiclanodes.common.LN;
import org.example.logiclanodes.input.LCOM;
import org.example.packetStructureCapture.SvPacket;
import org.example.pcapFiles.EthernetListener;
import org.example.pcapFiles.SvPacketSender;
import org.example.pcapFiles.SvParser;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Test {
    private static List<LN> inList = new ArrayList<>();

    @SneakyThrows
    public static void main(String[] args) {
        EthernetListener ethernetListener = new EthernetListener();

        ethernetListener.checkNic();
        ethernetListener.setNickName("VMware Virtual Ethernet Adapter for VMnet8");
        ethernetListener.getNicArray();


        SvParser svParser = new SvParser();

        AtomicInteger curCnt = new AtomicInteger();

//        HashMap<String, ArrayList<SvPacket>> sourceMap = new HashMap<>();

//        ArrayList<Optional> array = new ArrayList<>();

        Set<Optional> setSvPckt = new HashSet<>();

        ethernetListener.addListener(packet -> {
            Optional<SvPacket> svPacket = svParser.decode(packet);
            int noASDU = svPacket.get().getApdu().getNoASDU();
            for (int i = 0; i < noASDU; i++) {


                if (svPacket.isPresent() && curCnt.get() != svPacket.get().getApdu().getSeqASDU().get(i).getSmpCnt()) {


                    setSvPckt.add(svPacket);
                    System.out.println(setSvPckt.size());

//                    if (setSvPckt.size() >= 37000) {
//                        System.out.println(setSvPckt);
//                    }

                    curCnt.set(svPacket.get().getApdu().getSeqASDU().get(i).getSmpCnt());  //update counter else writes packet twice
                }
            }

        });

        ethernetListener.start();



        LCOM lcom = new LCOM();
        inList.add(lcom);


        SvPacketSender svPacketSender = new SvPacketSender();
        svPacketSender.checkNic();
//        System.out.println(EtListen.getNicArray());


        Scanner keyboard = new Scanner(System.in);


        System.out.println("Pick a Src");
        System.out.println(svPacketSender.getNicArray());

        int nicSRC = 4;
        String srcMAC = svPacketSender.getNicMAC().get(nicSRC);
        System.out.println(
                "Source" +
                        "\nName: " + svPacketSender.getNicArray().get(nicSRC) +
                        "\nIP: " + svPacketSender.getNicIP().get(nicSRC) +
                        "\nMAC: " + srcMAC
        );


        System.out.println("Pick a Dst");
        System.out.println(svPacketSender.getNicArray());

        int nicDST = 1;
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

        svPacketSender.process();
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

        /** Вставить запуск EthernetListener'a */

        while (lcom.hasNextData()) {
            inList.forEach(LN::process);
        }
        System.out.println("end");
    }
}
