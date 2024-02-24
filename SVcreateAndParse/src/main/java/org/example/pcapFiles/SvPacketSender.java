package org.example.pcapFiles;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.example.dataobjects.measurements.MV;
import org.example.packetStructureSend.APDU;
import org.example.packetStructureSend.ASDU;
import org.pcap4j.packet.EthernetPacket;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.UnknownPacket;
import org.pcap4j.packet.namednumber.EtherType;
import org.pcap4j.util.MacAddress;

import java.io.UnsupportedEncodingException;
@Slf4j
public class SvPacketSender extends EthernetListener{

    public MV phsAInst = new MV();
    public MV phsBInst = new MV();
    public MV phsCInst = new MV();
    public MV phsNuetInst = new MV();

    public MV phsAUnst = new MV();
    public MV phsBUnst = new MV();
    public MV phsCUnst = new MV();
    public MV phsNuetUnst = new MV();

    @Getter @Setter
    private String srcMAC;
    @Getter @Setter
    private String dstMAC;
    private short count;

    int test = 0;


    public SvPacketSender() {
        this.phsAInst.getInstMag().getF().setValue(0d);
        this.phsBInst.getInstMag().getF().setValue(0d);
        this.phsCInst.getInstMag().getF().setValue(0d);
        this.phsNuetInst.getInstMag().getF().setValue(0d);
        this.phsAUnst.getInstMag().getF().setValue(0d);
        this.phsBUnst.getInstMag().getF().setValue(0d);
        this.phsCUnst.getInstMag().getF().setValue(0d);
        this.phsNuetUnst.getInstMag().getF().setValue(0d);
    }

    @SneakyThrows @Override
    public void process() {

        APDU apdu = new APDU();
//        PcapHandle sendHandle = .openLive(SNAPLEN, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, READ_TIMEOUT);
        ASDU asdu = new ASDU("0000MU0101", count, compileValues());
        byte[] payload = new byte[apdu.convertToBytes().length + asdu.convertToBytes().length];

        System.arraycopy(apdu.convertToBytes(), 0, payload, 0, apdu.convertToBytes().length);
        System.arraycopy(asdu.convertToBytes(), 0, payload, apdu.convertToBytes().length, asdu.convertToBytes().length);

        UnknownPacket.Builder svBuilder = new UnknownPacket.Builder();
        svBuilder.rawData(payload);


        EthernetPacket.Builder etherBuilder = new EthernetPacket.Builder();
        etherBuilder
                .srcAddr(MacAddress.getByName(srcMAC))
                .dstAddr(MacAddress.getByName(dstMAC))
                .type(new EtherType((short) 0x88ba, "SV_IEC61850"))
                .payloadBuilder(svBuilder)
                .paddingAtBuild(true);
        Packet p = etherBuilder.build();

        this.getHandle().sendPacket(p);
        log.info("Packet sent " + test);
        test+=1;

        if (count <3999){
            count += 1;  
        }else {
            count = 0;
        }
        Thread.sleep((long) 0.5);
            
        
    }
    /*Метод фундаментально неверно работает - переводит из hex в decimal*/

//    public static byte[] convertToByte(String stringLine) throws UnsupportedEncodingException {
//        int offset = 0;
//
//        byte[] result = new byte[stringLine.length() / 2];
//        for (int i = 0; i < result.length; i++) {
//            String oneByte = stringLine.substring(0+offset, 2+offset);
//            Integer hex = Integer.parseInt(oneByte, 16);
//            result[i] = hex.byteValue();
//            offset+=2;
//        }
//
////        StringBuilder sb = new StringBuilder();
////        for (byte b : bytes) {
////            sb.append(String.format("%02X ", b));
////        }
////        result = sb.toString());
////        }
////        return result;
//        return result;
//    }

    public static byte[] convertMACtoByte(String macString) throws UnsupportedEncodingException {
        String[] macArray = macString.split(":");
        byte[] result = new byte[6];

        for (int i = 0; i < 6; i++) {
            Integer hex = Integer.parseInt(macArray[i], 16);
            result[i] = hex.byteValue();
        }
        return result;
    }

    public int[] compileValues(){
        int[] result = new int[8];
        result[0] = phsAInst.getInstMag().getF().getValue().intValue();
        result[1] = phsBInst.getInstMag().getF().getValue().intValue();
        result[2] = phsCInst.getInstMag().getF().getValue().intValue();
        result[3] = phsNuetInst.getInstMag().getF().getValue().intValue();

        result[4] = phsAUnst.getInstMag().getF().getValue().intValue();
        result[5] = phsBUnst.getInstMag().getF().getValue().intValue();
        result[6] = phsCUnst.getInstMag().getF().getValue().intValue();
        result[7] = phsNuetUnst.getInstMag().getF().getValue().intValue();
        return result;
    }
}
