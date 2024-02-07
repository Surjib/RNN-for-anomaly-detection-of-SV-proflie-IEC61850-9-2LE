package org.example.pcapFiles;


import org.example.packetStructure.*;
import org.pcap4j.core.PcapPacket;

import javax.xml.bind.DatatypeConverter;
import java.util.Optional;

import static org.reflections.Reflections.log;

public class SvParser {

    private static final int datasetSize = 64;

    public Optional<SvPacket> decode(PcapPacket packet){
        try {
            byte [] data = packet.getRawData();
            int length = data.length;

            SvPacket result = new SvPacket();

            result.setTimestamp(packet.getTimestamp().toString());

            result.setMacDst(byteArrayToMac(data, 0));
            result.setMacSrs(byteArrayToMac(data, 6));
            result.setType(twoByteArrayToString(data, 12));

            result.setAppId(twoByteArrayToString(data, 14));
            result.setLength(byteArrayToInt(data, 16, 2));
            result.setReserved1(twoByteArrayToString(data, 18));
            result.setReserved2(twoByteArrayToString(data, 20));

            result.setApdu(byteToAPDU(data, 24)); //24 start of apdu

            return Optional.of(result);
        } catch (Exception e) {log.error("Cannot parse SV packet");}
        return Optional.empty();
    }


    public static String byteArrayToMac (byte[] b, int offset){
        return String.format("%02x:%02x:%02x:%02x:%02x:%02x",
                b[offset],
                b[offset + 1],
                b[offset + 2],
                b[offset + 3],
                b[offset + 4],
                b[offset + 5]
        );

    }

    public APDU byteToAPDU(byte[] b, int offset){
        APDU apdu = new APDU();
        int noAsdu = byteArrayToInt(b, offset + 2, 1);
        apdu.setNoASDU(noAsdu);

        for (int i = 0; i < noAsdu; i++) {
            apdu.getSeqASDU().add(byteToASDU(b, offset + 5 + i * 93)); // 29 bit = start of asdu
        }

        return apdu;
    }

    public ASDU byteToASDU(byte[] b, int offset){
        ASDU asdu = new ASDU();

        String hexId = byteArrayToString(b, offset + 4, 10);
        byte[] decId = DatatypeConverter.parseHexBinary(hexId); //from hex to decimal array
        String id = new String(decId); // from decimal array to text (ASCII)

        asdu.setSvID(id);
        asdu.setSmpCnt(byteArrayToInt(b, offset + 14 + 2, 2));
        asdu.setConfRev(byteArrayToInt(b, offset + 18 + 2, 4));
        asdu.setSmpSynch(byteArrayToInt(b, offset + 24 + 2, 1));  // 0 - None 1 - Local 2 - Remote
        asdu.setDataset(byteToPhsMeas(b, offset + 27 + 2));
        return asdu;
    }

    public PhsMeas byteToPhsMeas(byte[] b, int offset){
        PhsMeas dataset = new PhsMeas();

        dataset.setInstIa(byteArrayToInstVal(b, offset + 8 * 0));
        dataset.setQIa(byteArrayToQuality(b, offset + 4 + 8 * 0));

        dataset.setInstIb(byteArrayToInstVal(b, offset + 8 * 1));
        dataset.setQIb(byteArrayToQuality(b, offset + 4 + 8 * 1));

        dataset.setInstIc(byteArrayToInstVal(b, offset + 8 * 2));
        dataset.setQIc(byteArrayToQuality(b, offset + 4 + 8 * 2));

        dataset.setInstIn(byteArrayToInstVal(b, offset + 8 * 3));
        dataset.setQIn(byteArrayToQuality(b, offset + 4 + 8 * 3));

        dataset.setInstUa(byteArrayToInstVal(b, offset + 8 * 4));
        dataset.setQUa(byteArrayToQuality(b, offset + 4 + 8 * 4));

        dataset.setInstUb(byteArrayToInstVal(b, offset + 8 * 5));
        dataset.setQUb(byteArrayToQuality(b, offset + 4 + 8 * 5));

        dataset.setInstUc(byteArrayToInstVal(b, offset + 8 * 6));
        dataset.setQUc(byteArrayToQuality(b, offset + 8 + 4 * 6));

        dataset.setInstUn(byteArrayToInstVal(b, offset + 8 * 7));
        dataset.setQUn(byteArrayToQuality(b, offset + 8 + 4 * 7));


        return dataset;
    }

    private Quality byteArrayToQuality(byte[] b, int offset) {
        Quality quality = new Quality();

        int qbyte = byteArrayToInt(b, offset, 4);

        quality.setValidity((qbyte & 0b11));
        qbyte = qbyte >> 2;

        quality.setOverflow((qbyte & 0b1));
        qbyte = qbyte >> 1;

        quality.setOutOfRange((qbyte & 0b1));
        qbyte = qbyte >> 1;

        quality.setBadReference((qbyte & 0b1));
        qbyte = qbyte >> 1;

        quality.setOscillatory((qbyte & 0b1));
        qbyte = qbyte >> 1;

        quality.setFailure((qbyte & 0b1));
        qbyte = qbyte >> 1;

        quality.setOldData((qbyte & 0b1));
        qbyte = qbyte >> 1;

        quality.setInconsistent((qbyte & 0b1));
        qbyte = qbyte >> 1;

        quality.setInaccurate((qbyte & 0b1));
        qbyte = qbyte >> 1;

        quality.setSource((qbyte & 0b1));
        qbyte = qbyte >> 1;

        quality.setTest((qbyte & 0b1));
        qbyte = qbyte >> 1;

        quality.setOperatorBlocked((qbyte & 0b1));
        qbyte = qbyte >> 1;

        quality.setDerived((qbyte & 0b1));




        return quality;
    }

    public static String twoByteArrayToString(byte[] b, int offset){
        return String.format("0x%02x%02x",
                b[offset],
                b[offset + 1]
        );

    }

    public static String byteArrayToString(byte[] b, int offset, int len){
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < len; i++) {
            hexString.append(String.format("%02x", b[offset + i]));
        }
        String out = new String(hexString);
        return out;

    }

    private int byteArrayToInt(byte[] b, int offset, int len) {
        int value = b[offset + len - 1] & 0xFF;
        for (int i = 1; i < len; i++) {
            value = value | (b[offset + (len - i - 1)] & 0xFF) << 8 * i;
        }
        return value;
    }


    private int byteArrayToInstVal(byte[] b, int offset) {
        return b[offset + 3] & 0xff | (b[offset + 2] & 0xff) << 8 | (b[offset + 1] & 0xff) << 16 | (b[offset] & 0xff) << 24;
    }


}

