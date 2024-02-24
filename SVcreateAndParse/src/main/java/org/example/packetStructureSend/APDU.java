package org.example.packetStructureSend;

public class APDU {

    private static byte[] appID = {0x40, 0x00};
    private static byte[] lgth = {0x00, 0x6c};
    private static byte[] reserved1 = {0x00, 0x00};
    private static byte[] reserved2 = {0x00, 0x00};
    private static byte savPDU_tag = 0x60;
    private static byte savPDU_lgt = 0x62;
    private static byte noASDU_tag = (byte) 0x80;
    private static byte noASDU_lgt = 0x01;
    private static byte noASDU = 0x01;
    private static byte SequenceofASDU_tag = (byte) 0xa2;
    private static byte SequenceofASDU_lgt = 0x5d;


    public byte[] convertToBytes(){
        byte[] result = new byte[15];
        int offset = 0;

        System.arraycopy(appID, 0,result,offset,2);
        offset+=2;

        System.arraycopy(lgth, 0,result,offset,2);
        offset+=2;

        System.arraycopy(reserved1, 0,result,offset,2);
        offset+=2;

        System.arraycopy(reserved2, 0,result,offset,2);
        offset+=2;

        result[offset] = savPDU_tag;
        offset+=1;

        result[offset] = savPDU_lgt;
        offset+=1;

        result[offset] = noASDU_tag;
        offset+=1;

        result[offset] = noASDU_lgt;
        offset+=1;

        result[offset] = noASDU;
        offset+=1;

        result[offset] = SequenceofASDU_tag;
        offset+=1;

        result[offset] = SequenceofASDU_lgt;
        offset+=1;

        return result;
    }
}
