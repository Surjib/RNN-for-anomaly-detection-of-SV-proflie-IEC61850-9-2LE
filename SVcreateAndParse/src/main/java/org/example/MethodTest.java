package org.example;

import com.opencsv.CSVWriter;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileWriter;

public class MethodTest {
    @SneakyThrows
    public static void main(String[] args) {
//        String test = "88ba";
//        String oneByte = test.substring(0, 2);
//        System.out.println(oneByte);

//        String test = "ad";
//        System.out.println(test.length() / 2);

//        byte [] test = SvSend.convertToByte("500958");
//        for (byte el:test) {
//            System.out.print(el + " ");
//        }

//        Integer test = 0xd8;
//        System.out.println(test.byteValue());
//
//        Integer test = (int) 0000000;
//        Integer test1 = test;
//        System.out.println(test1);

        String[][] employees = {
                {"Man", "Sparkes", "msparkes0@springhow.com", "Engineering"},
                {"Dulcinea", "Terzi", "dterzi1@springhow.com", "Engineering"},
                {"Tamar", "Bedder", "tbedder2@springhow.com", "Legal"},
                {"Vance", "Scouller", "vscouller3@springhow.com", "Sales"},
                {"Gran", "Jagoe", "gjagoe4@springhow.com", "Business Development"}
        };

        File csvFile = new File("src/main/resources/employees.csv");

        FileWriter fileWriter = new FileWriter(csvFile);
        CSVWriter writer = new CSVWriter(fileWriter);

        writer.writeNext(new String[]{"Ia", "Ib", "Ic", "Ua", "Ub", "Uc"});

        //write header line here if you need.

        for (String[] data : employees) {
           writer.writeNext(data);
        }
        writer.close();
    }
}
