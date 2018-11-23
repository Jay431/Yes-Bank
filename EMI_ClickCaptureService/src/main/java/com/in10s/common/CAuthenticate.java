package com.in10s.common;

public class CAuthenticate {

    public String Encrypt(String Data) {

        String key = "toUpperCase";
        String dest = "";

        int len = key.length();
        int SrcAsc;
        int SrcPos;
        int KeyPos = -1;
        int offset = ((int) (Math.random() * 10000) % 255) + 1;

        dest = Integer.toHexString(offset);

        if (dest.length() == 1) {

            dest = "0" + dest;

        }

        for (SrcPos = 0; SrcPos < Data.length(); SrcPos++) {

            int ascii = (Data.substring(SrcPos, SrcPos + 1)).charAt(0);

            SrcAsc = (ascii + offset) % 255;

            if (KeyPos < (len - 1)) {

                KeyPos++;

            } else {

                KeyPos = 0;

            }

            ascii = (key.substring(KeyPos, KeyPos + 1)).charAt(0);

            SrcAsc = SrcAsc ^ ascii;

            if (SrcAsc <= 15) {

                dest = dest + " " + Integer.toHexString(SrcAsc);

            } else {

                dest = dest + Integer.toHexString(SrcAsc);

            }

            offset = SrcAsc;

        }

        dest = dest.toUpperCase();

        return dest;

    }

    public String Decrypt(String Data) {

        String key = "toUpperCase";
        String dest = "";

        int len = key.length();
        int SrcAsc;
        int SrcPos;
        int KeyPos = -1;
        int offset = (Integer.decode("#" + Data.substring(0, 2))).intValue();

        for (SrcPos = 2; SrcPos < (Data.length() - 1); SrcPos += 2) {

            SrcAsc = (Integer.decode("#" + (Data.substring(SrcPos, SrcPos + 2)).trim())).intValue();

            if (KeyPos < (len - 1)) {

                KeyPos++;

            } else {

                KeyPos = 0;

            }

            int ascii = (key.substring(KeyPos, KeyPos + 1)).charAt(0);
            int TmpSrcAsc = SrcAsc ^ ascii;

            if (TmpSrcAsc <= offset) {

                TmpSrcAsc = (255 + TmpSrcAsc) - offset;

            } else {

                TmpSrcAsc = TmpSrcAsc - offset;

            }

            char c = (char) TmpSrcAsc;

            dest = dest + c;
            offset = SrcAsc;

        }

        return dest;

    }

    public static void main(String[] args) {

        System.out.println(":: " + new CAuthenticate().Decrypt("57FB27DE1E A74D0"));
        System.out.println(":: " + new CAuthenticate().Encrypt("UNICAP_UAE_1"));

    }

}
