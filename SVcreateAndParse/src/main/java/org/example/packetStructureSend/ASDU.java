package org.example.packetStructureSend;

import java.nio.charset.StandardCharsets;

public class ASDU {
    private static byte SequenceASDU_tag = 0x30;
    private static byte SequenceASDU_lgt = 0x5b;
    private static byte svID_tag = (byte) 0x80;
    private static byte svID_lgt = 0x0A;
    private static byte smpCnt_tag = (byte) 0x82;
    private static byte smpCnt_lgt = 0x02;
    private static byte confRev_tag = (byte) 0x83;
    private static byte confRev_lgt = 0x04;
    private static int confRev = 1;
    private static byte smpSynch_tag = (byte) 0x85;
    private static byte smpSynch_lgt = 0x01;
    private static byte smpSynch = 0x01;
    private static byte SequenceofData_tag = (byte) 0x87;
    private static byte SequnceofData_lgt = 0x40;

    private String svID;
    private short smpCnt;

    private int Ia_Amp_instMag;
    private int Ia_Amp_q;
    private int Ib_Amp_instMag;
    private int Ib_Amp_q;
    private int Ic_Amp_instMag;
    private int Ic_Amp_q;
    private int Ineut_Amp_instMag;
    private int Ineut_Amp_q;
    private int Ua_Vol_instMag;
    private int Ua_Vol_q;
    private int Ub_Vol_instMag;
    private int Ub_Vol_q;
    private int Uc_Vol_instMag;
    private int Uc_Vol_q;
    private int Uneut_Vol_instMag;
    private int Uneut_Vol_q;


    public ASDU(String svID, short smpCnt, int[] mes) throws Exception {
        this.svID = svID;
        if (this.svID.length() < 10) {
            while (this.svID.length() < 10) {
                this.svID = this.svID + "0";
            }
        } else if (this.svID.length() > 10) {
            this.svID = this.svID.substring(0, 10);
        }
        this.smpCnt = smpCnt;

        if (mes.length != 8) {
            throw new Exception("Длинна массива =/= 8");
        }
        this.Ia_Amp_instMag = mes[0];
        this.Ia_Amp_q = 00000000;
        this.Ib_Amp_instMag = mes[1];
        this.Ib_Amp_q = 00000000;
        this.Ic_Amp_instMag = mes[2];
        this.Ic_Amp_q = 00000000;
        this.Ineut_Amp_instMag = mes[3];
        this.Ineut_Amp_q = 00000000;
        this.Ua_Vol_instMag = mes[4];
        this.Ua_Vol_q = 00000000;
        this.Ub_Vol_instMag = mes[5];
        this.Ub_Vol_q = 00000000;
        this.Uc_Vol_instMag = mes[6];
        this.Uc_Vol_q = 00000000;
        this.Uneut_Vol_instMag = mes[7];
        this.Uneut_Vol_q = 00000000;
    }

    public byte[] convertToBytes() {
        byte[] result = new byte[93];
        int offset = 0;

        result[offset] = SequenceASDU_tag;
        offset += 1;

        result[offset] = SequenceASDU_lgt;
        offset += 1;

        result[offset] = svID_tag;
        offset += 1;

        result[offset] = svID_lgt;
        offset += 1;

        System.arraycopy(convertingToByte(svID), 0, result, offset, 10);
        offset += 10;

        result[offset] = smpCnt_tag;
        offset += 1;

        result[offset] = smpCnt_lgt;
        offset += 1;

        System.arraycopy(convertingToByte(smpCnt), 0, result, offset, 2);
        offset += 2;

        result[offset] = confRev_tag;
        offset += 1;

        result[offset] = confRev_lgt;
        offset += 1;

        System.arraycopy(convertingToByte(confRev), 0, result, offset, 4);
        offset += 4;

        result[offset] = smpSynch_tag;
        offset += 1;

        result[offset] = smpSynch_lgt;
        offset += 1;

        result[offset] = smpSynch;
        offset += 1;

        result[offset] = SequenceofData_tag;
        offset += 1;

        result[offset] = SequnceofData_lgt;
        offset += 1;

        System.arraycopy(convertingToByte(Ia_Amp_instMag), 0, result, offset, 4);
        offset += 4;

        System.arraycopy(convertingToByte(Ia_Amp_q), 0, result, offset, 4);
        offset += 4;

        System.arraycopy(convertingToByte(Ib_Amp_instMag), 0, result, offset, 4);
        offset += 4;

        System.arraycopy(convertingToByte(Ib_Amp_q), 0, result, offset, 4);
        offset += 4;

        System.arraycopy(convertingToByte(Ic_Amp_instMag), 0, result, offset, 4);
        offset += 4;

        System.arraycopy(convertingToByte(Ic_Amp_q), 0, result, offset, 4);
        offset += 4;

        System.arraycopy(convertingToByte(Ineut_Amp_instMag), 0, result, offset, 4);
        offset += 4;

        System.arraycopy(convertingToByte(Ineut_Amp_q), 0, result, offset, 4);
        offset += 4;

        System.arraycopy(convertingToByte(Ua_Vol_instMag), 0, result, offset, 4);
        offset += 4;

        System.arraycopy(convertingToByte(Ua_Vol_q), 0, result, offset, 4);
        offset += 4;

        System.arraycopy(convertingToByte(Ub_Vol_instMag), 0, result, offset, 4);
        offset += 4;

        System.arraycopy(convertingToByte(Ub_Vol_q), 0, result, offset, 4);
        offset += 4;

        System.arraycopy(convertingToByte(Uc_Vol_instMag), 0, result, offset, 4);
        offset += 4;

        System.arraycopy(convertingToByte(Uc_Vol_q), 0, result, offset, 4);
        offset += 4;

        System.arraycopy(convertingToByte(Uneut_Vol_instMag), 0, result, offset, 4);
        offset += 4;

        System.arraycopy(convertingToByte(Uneut_Vol_q), 0, result, offset, 4);
        offset += 4;

        return result;
    }

    public static byte[] convertingToByte(String str) {
        return str.getBytes(StandardCharsets.UTF_8);
    }

    public static byte[] convertingToByte(short val) {
        byte[] res = new byte[2];
        res[1] = (byte) (val & 0xff);
        res[0] = (byte) ((val >> 8) & 0xff);
        return res;
    }

    public static byte[] convertingToByte(int val) {
        byte[] res = new byte[4];
        res[3] = (byte) (val & 0xff);
        res[2] = (byte) ((val >> 8) & 0xff);
        res[1] = (byte) ((val >> 16) & 0xff);
        res[0] = (byte) ((val >> 24) & 0xff);
        return res;
    }
}