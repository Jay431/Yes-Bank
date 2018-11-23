/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.in10s.utils;

import java.util.Vector;

/**
 *
 * @author rajkumar
 */
public class Base64 {

    public static String base64_chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";

    public Base64()
    {
    }

    public static boolean is_base64(char c) 
    {
        return (Character.isLetter(c) || Character.isDigit(c) || (c == '+') || (c == '/'));
    }
    public static String base64_encode_str(Vector<Character> bytes_to_encode, int in_len)
    {
        long lStartTime = System.currentTimeMillis();        
        //String ret = "";
        String ret1 ="";
        int i = 0;
        int j = 0;
        int k = 0;
        char[] char_array_3 = new char[3];
        //char[] char_array_4 = new char[4];
        int [] int_array_4 = new int[4];
        while (in_len-- > 0)
        {
            char_array_3[i++] = bytes_to_encode.get(k++);//[k++];
            if (i == 3) 
            {
                //char_array_4[0] = (char) ((char_array_3[0] & 0xfc) >>> 2);
                int_array_4[0]  = (char_array_3[0] & 0xfc)/ 4;
               // char_array_4[1] = (char) (((char_array_3[0] & 0x03) << 4) + ((char_array_3[1] & 0xf0) >>> 4));
                int_array_4[1]  = ((char_array_3[0] & 0x03)*16) + ((char_array_3[1] & 0xf0)/16);
               // char_array_4[2] = (char) (((char_array_3[1] & 0x0f) << 2) + ((char_array_3[2] & 0xc0) >>> 6));
                int_array_4[2]  = (((char_array_3[1] & 0x0f)*4) + ((char_array_3[2] & 0xc0)/64));
               // char_array_4[3] = (char) (char_array_3[2] & 0x3f);
                int_array_4[3]  = (char_array_3[2] & 0x3f);

                for (i = 0; (i < 4); i++)
                {
                    //ret += base64_chars[char_array_4[i]];
                  //  ret += base64_chars.charAt(char_array_4[i]);//[char_array_4[i]];
                    ret1+= base64_chars.charAt(int_array_4[i]);
                }
                i = 0;
            }
        }
        if (i > 0)
        {
            for (j = i; j < 3; j++)
            {
                char_array_3[j] = '\0';
            }            
           // char_array_4[0] = (char) ((char_array_3[0] & 0xfc) >> 2);
            int_array_4[0]  = (char_array_3[0] & 0xfc)/ 4;
          //  char_array_4[1] = (char) (((char_array_3[0] & 0x03) << 4) + ((char_array_3[1] & 0xf0) >> 4));
            int_array_4[1]  = ((char_array_3[0] & 0x03)*16) + ((char_array_3[1] & 0xf0)/16);
         //   char_array_4[2] = (char) (((char_array_3[1] & 0x0f) << 2) + ((char_array_3[2] & 0xc0) >> 6));
            int_array_4[2]  = (((char_array_3[1] & 0x0f)*4) + ((char_array_3[2] & 0xc0)/64));

            for (j = 0; (j < i + 1); j++)
            {
                //ret += base64_chars[char_array_4[j]];
             //   ret += base64_chars.charAt(char_array_4[j]);
                ret1+= base64_chars.charAt(int_array_4[j]);
            }
            while ((i++ < 3)) {
                //ret += '=';
                ret1+='=';
            }
        }
        System.out.println("Decode method end time : "+(System.currentTimeMillis()-lStartTime));
        return ret1;
    }
    public static String base64_encode(char bytes_to_encode[], int in_len)
    {
        long lStartTime = System.currentTimeMillis();        
        String ret = "";
        int i = 0;
        int j = 0;
        int k = 0;
        char[] char_array_3 = new char[3];
        char[] char_array_4 = new char[4];
        while (in_len-- > 0)
        {
            char_array_3[i++] = bytes_to_encode[k++];
            if (i == 3) 
            {
                char_array_4[0] = (char) ((char_array_3[0] & 0xfc) >> 2);
                char_array_4[1] = (char) (((char_array_3[0] & 0x03) << 4) + ((char_array_3[1] & 0xf0) >> 4));
                char_array_4[2] = (char) (((char_array_3[1] & 0x0f) << 2) + ((char_array_3[2] & 0xc0) >> 6));
                char_array_4[3] = (char) (char_array_3[2] & 0x3f);

                for (i = 0; (i < 4); i++)
                {
                    //ret += base64_chars[char_array_4[i]];
                    ret += base64_chars.charAt(char_array_4[i]);//[char_array_4[i]];
                }
                i = 0;
            }
        }
        if (i > 0)
        {
            for (j = i; j < 3; j++)
            {
                char_array_3[j] = '\0';
            }            
            char_array_4[0] = (char) ((char_array_3[0] & 0xfc) >> 2);
            char_array_4[1] = (char) (((char_array_3[0] & 0x03) << 4) + ((char_array_3[1] & 0xf0) >> 4));
            char_array_4[2] = (char) (((char_array_3[1] & 0x0f) << 2) + ((char_array_3[2] & 0xc0) >> 6));

            for (j = 0; (j < i + 1); j++)
            {
                //ret += base64_chars[char_array_4[j]];
                ret += base64_chars.charAt(char_array_4[j]);
            }
            while ((i++ < 3)) {
                ret += '=';
            }
        }
        System.out.println("Decode method end time : "+(System.currentTimeMillis()-lStartTime));
        return ret;
    }
    public static String base64_decode(String encoded_string)
    {
        long lstarttime = System.currentTimeMillis();
        int in_len = encoded_string.length();
        int i = 0;
        int j = 0;
        int in_ = 0;
        char[] char_array_4 = new char[4];
        char[] char_array_3 = new char[3];
        String ret = "";
        while ((in_len-- > 0) && (encoded_string.charAt(in_) != '=') && is_base64(encoded_string.charAt(in_)))
        {
            char_array_4[i++] = encoded_string.charAt(in_);
            in_++;
            if (i == 4)
            {
                for (i = 0; i < 4; i++)
                {
                    char_array_4[i] = (char) base64_chars.indexOf(char_array_4[i]);
                }
                char_array_3[0] = (char) ((char_array_4[0] << 2) + ((char_array_4[1] & 0x30) >> 4));
                char_array_3[1] = (char) (((char_array_4[1] & 0xf) << 4) + ((char_array_4[2] & 0x3c) >> 2));
                char_array_3[2] = (char) (((char_array_4[2] & 0x3) << 6) + char_array_4[3]);

                for (i = 0; (i < 3); i++)
                {
                    ret += char_array_3[i];
                }
                i = 0;
            }
        }

        if (i > 0) 
        {
            for (j = 0; j < i; j++) 
            {
                char_array_4[j] = (char) base64_chars.indexOf(char_array_4[j]);
            }

            char_array_3[0] = (char) ((char_array_4[0] << 2) + ((char_array_4[1] & 0x30) >> 4));
            char_array_3[1] = (char) (((char_array_4[1] & 0xf) << 4) + ((char_array_4[2] & 0x3c) >> 2));

            for (j = 0; (j < i - 1); j++) 
            {
                ret += char_array_3[j];
            }
        }
        System.out.println("Decode functionality completed , elapsed time in ms : "+(System.currentTimeMillis()-lstarttime));
        return ret;
    }
}
