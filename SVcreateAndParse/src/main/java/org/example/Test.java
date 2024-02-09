package org.example;

import org.example.logiclanodes.common.LN;
import org.example.logiclanodes.input.LCOM;
import org.example.pcapFiles.EthernetListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {
    private static List<LN> inList = new ArrayList<>();

    public static void main(String[] args) {
        EthernetListener EtListen = new EthernetListener();
        EtListen.checkNic();
//        System.out.println(EtListen.getNicArray());

        Scanner keyboard = new Scanner(System.in);
        System.out.println("Pick a NIC");
        System.out.println(EtListen.getNicArray());
        int nicPos = keyboard.nextInt();


        System.out.println(
                "Name: " + EtListen.getNicArray().get(nicPos) +
                "\nIP: " + EtListen.getNicIP().get(nicPos) +
                "\nMAC: " + EtListen.getNicMAC().get(nicPos)
                );

        LCOM lcom = new LCOM();
        inList.add(lcom);

        lcom.setFilePath(
                "E:\\DZ\\11sem\\AI_Enregy\\KP\\pythonProject\\PSCAD_files\\testGrid.gf42\\Rank_00001\\Run_00001\\",
                "ABC_W1");

        /** Запуск алгоритма, пока в .dat файле есть строки*/

        while (lcom.hasNextData()) {
            inList.forEach(LN::process);
        }
        System.out.println("end");
    }
}
