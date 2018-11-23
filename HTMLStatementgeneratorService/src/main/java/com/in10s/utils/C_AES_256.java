package com.in10s.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Random;
import java.util.Vector;

public class C_AES_256
{
    private String m_strkey = "AESEncryption256AESEncryption256";
    public final int BLOCK_SIZE = 16;
    public final int KEY_SIZE = 32;
    public final int NUM_ROUNDS = 14;
    public char[] m_key;
    public Vector<Character> m_key1 = new Vector<Character>();
    public Vector<Character> m_salt = new Vector<Character>();
    public char[] m_rkey;
    public char m_buffer[] = new char[3 * BLOCK_SIZE];
    public int m_buffer_pos = 0;
    public int m_remainingLength = 0;
    public boolean m_decryptInitialized = false;
    public Vector<Character> encrypted = new Vector<Character>();
    public Vector<Character> Decrypted = new Vector<Character>();
    public String strDecrypt = "";
    public Random generator = new Random();
    
    public static char sbox[] = {
        0x63, 0x7c, 0x77, 0x7b, 0xf2, 0x6b, 0x6f, 0xc5,
        0x30, 0x01, 0x67, 0x2b, 0xfe, 0xd7, 0xab, 0x76,
        0xca, 0x82, 0xc9, 0x7d, 0xfa, 0x59, 0x47, 0xf0,
        0xad, 0xd4, 0xa2, 0xaf, 0x9c, 0xa4, 0x72, 0xc0,
        0xb7, 0xfd, 0x93, 0x26, 0x36, 0x3f, 0xf7, 0xcc,
        0x34, 0xa5, 0xe5, 0xf1, 0x71, 0xd8, 0x31, 0x15,
        0x04, 0xc7, 0x23, 0xc3, 0x18, 0x96, 0x05, 0x9a,
        0x07, 0x12, 0x80, 0xe2, 0xeb, 0x27, 0xb2, 0x75,
        0x09, 0x83, 0x2c, 0x1a, 0x1b, 0x6e, 0x5a, 0xa0,
        0x52, 0x3b, 0xd6, 0xb3, 0x29, 0xe3, 0x2f, 0x84,
        0x53, 0xd1, 0x00, 0xed, 0x20, 0xfc, 0xb1, 0x5b,
        0x6a, 0xcb, 0xbe, 0x39, 0x4a, 0x4c, 0x58, 0xcf,
        0xd0, 0xef, 0xaa, 0xfb, 0x43, 0x4d, 0x33, 0x85,
        0x45, 0xf9, 0x02, 0x7f, 0x50, 0x3c, 0x9f, 0xa8,
        0x51, 0xa3, 0x40, 0x8f, 0x92, 0x9d, 0x38, 0xf5,
        0xbc, 0xb6, 0xda, 0x21, 0x10, 0xff, 0xf3, 0xd2,
        0xcd, 0x0c, 0x13, 0xec, 0x5f, 0x97, 0x44, 0x17,
        0xc4, 0xa7, 0x7e, 0x3d, 0x64, 0x5d, 0x19, 0x73,
        0x60, 0x81, 0x4f, 0xdc, 0x22, 0x2a, 0x90, 0x88,
        0x46, 0xee, 0xb8, 0x14, 0xde, 0x5e, 0x0b, 0xdb,
        0xe0, 0x32, 0x3a, 0x0a, 0x49, 0x06, 0x24, 0x5c,
        0xc2, 0xd3, 0xac, 0x62, 0x91, 0x95, 0xe4, 0x79,
        0xe7, 0xc8, 0x37, 0x6d, 0x8d, 0xd5, 0x4e, 0xa9,
        0x6c, 0x56, 0xf4, 0xea, 0x65, 0x7a, 0xae, 0x08,
        0xba, 0x78, 0x25, 0x2e, 0x1c, 0xa6, 0xb4, 0xc6,
        0xe8, 0xdd, 0x74, 0x1f, 0x4b, 0xbd, 0x8b, 0x8a,
        0x70, 0x3e, 0xb5, 0x66, 0x48, 0x03, 0xf6, 0x0e,
        0x61, 0x35, 0x57, 0xb9, 0x86, 0xc1, 0x1d, 0x9e,
        0xe1, 0xf8, 0x98, 0x11, 0x69, 0xd9, 0x8e, 0x94,
        0x9b, 0x1e, 0x87, 0xe9, 0xce, 0x55, 0x28, 0xdf,
        0x8c, 0xa1, 0x89, 0x0d, 0xbf, 0xe6, 0x42, 0x68,
        0x41, 0x99, 0x2d, 0x0f, 0xb0, 0x54, 0xbb, 0x16
    };
    
    public static char sboxinv[] = {
        0x52, 0x09, 0x6a, 0xd5, 0x30, 0x36, 0xa5, 0x38,
        0xbf, 0x40, 0xa3, 0x9e, 0x81, 0xf3, 0xd7, 0xfb,
        0x7c, 0xe3, 0x39, 0x82, 0x9b, 0x2f, 0xff, 0x87,
        0x34, 0x8e, 0x43, 0x44, 0xc4, 0xde, 0xe9, 0xcb,
        0x54, 0x7b, 0x94, 0x32, 0xa6, 0xc2, 0x23, 0x3d,
        0xee, 0x4c, 0x95, 0x0b, 0x42, 0xfa, 0xc3, 0x4e,
        0x08, 0x2e, 0xa1, 0x66, 0x28, 0xd9, 0x24, 0xb2,
        0x76, 0x5b, 0xa2, 0x49, 0x6d, 0x8b, 0xd1, 0x25,
        0x72, 0xf8, 0xf6, 0x64, 0x86, 0x68, 0x98, 0x16,
        0xd4, 0xa4, 0x5c, 0xcc, 0x5d, 0x65, 0xb6, 0x92,
        0x6c, 0x70, 0x48, 0x50, 0xfd, 0xed, 0xb9, 0xda,
        0x5e, 0x15, 0x46, 0x57, 0xa7, 0x8d, 0x9d, 0x84,
        0x90, 0xd8, 0xab, 0x00, 0x8c, 0xbc, 0xd3, 0x0a,
        0xf7, 0xe4, 0x58, 0x05, 0xb8, 0xb3, 0x45, 0x06,
        0xd0, 0x2c, 0x1e, 0x8f, 0xca, 0x3f, 0x0f, 0x02,
        0xc1, 0xaf, 0xbd, 0x03, 0x01, 0x13, 0x8a, 0x6b,
        0x3a, 0x91, 0x11, 0x41, 0x4f, 0x67, 0xdc, 0xea,
        0x97, 0xf2, 0xcf, 0xce, 0xf0, 0xb4, 0xe6, 0x73,
        0x96, 0xac, 0x74, 0x22, 0xe7, 0xad, 0x35, 0x85,
        0xe2, 0xf9, 0x37, 0xe8, 0x1c, 0x75, 0xdf, 0x6e,
        0x47, 0xf1, 0x1a, 0x71, 0x1d, 0x29, 0xc5, 0x89,
        0x6f, 0xb7, 0x62, 0x0e, 0xaa, 0x18, 0xbe, 0x1b,
        0xfc, 0x56, 0x3e, 0x4b, 0xc6, 0xd2, 0x79, 0x20,
        0x9a, 0xdb, 0xc0, 0xfe, 0x78, 0xcd, 0x5a, 0xf4,
        0x1f, 0xdd, 0xa8, 0x33, 0x88, 0x07, 0xc7, 0x31,
        0xb1, 0x12, 0x10, 0x59, 0x27, 0x80, 0xec, 0x5f,
        0x60, 0x51, 0x7f, 0xa9, 0x19, 0xb5, 0x4a, 0x0d,
        0x2d, 0xe5, 0x7a, 0x9f, 0x93, 0xc9, 0x9c, 0xef,
        0xa0, 0xe0, 0x3b, 0x4d, 0xae, 0x2a, 0xf5, 0xb0,
        0xc8, 0xeb, 0xbb, 0x3c, 0x83, 0x53, 0x99, 0x61,
        0x17, 0x2b, 0x04, 0x7e, 0xba, 0x77, 0xd6, 0x26,
        0xe1, 0x69, 0x14, 0x63, 0x55, 0x21, 0x0c, 0x7d
    };
    
    public C_AES_256(String strkey)
    {
        if (strkey.isEmpty())
        {
            strkey = m_strkey;
        }

        int size = strkey.length() > KEY_SIZE ? KEY_SIZE : strkey.length();
        m_key = new char[size];
        for (int i = 0; i < size; i++)
        {
            m_key[i] = strkey.charAt(i);
        }

        size = KEY_SIZE - m_key.length;
        m_salt.setSize(size);
        for (int i = 0; i < size; i++) 
        {
            m_salt.set(i, '0');
        }
        m_rkey = new char[KEY_SIZE];
        for (int i = 0; i < KEY_SIZE; i++)
        {
            m_rkey[i] = 0;
        }
    }
    
    public void setkey(char[] key)
    {
        for (int i = 0; i < m_key.length; i++)
        {
            m_key[i] = key[i];
        }
    }
    
    public void copy_key() 
    {
        for (int i = 0; i < m_key.length; ++i)
        {
            m_rkey[i] = m_key[i];
        }
        for (int i = 0; i < m_salt.size(); ++i)
        {
            m_rkey[i + m_key.length] = m_salt.get(i);
        }
    }
    
    public char[] MakeCharArray(String strInput)
    {
        char[] input = new char[strInput.length()];
        for (int i = 0; i < strInput.length(); i++)
        {
            input[i] = strInput.charAt(i);
        }
        return input;
    }
    
    public void decrypt_start(int encrypted_length)
    {
        m_remainingLength = encrypted_length;
        for (int j = 0; j < m_salt.size(); ++j)
        {
            m_salt.set(j, '0');
        }
        m_remainingLength -= m_salt.size();
        m_buffer_pos = 0;
        m_decryptInitialized = false;
    }
    
    public void decrypt_continue(byte[] strInput)
    {
        //for (int i = 0; i < strInput.length; i++)
        for(byte b:strInput)
        {
            m_buffer[m_buffer_pos++] = (char)b;
            check_and_decrypt_buffer();
        }
    }
    
    public void decrypt_continue(String strInput)
    {
    	System.out.println("length : "+strInput.length());
        for (int i = 0; i < strInput.length(); i++)
        {
            m_buffer[m_buffer_pos++] = strInput.charAt(i);
            check_and_decrypt_buffer();
        }
    }
    
    public void check_and_decrypt_buffer()
    {
        if (!m_decryptInitialized && m_buffer_pos == m_salt.size() + 1)
        {
            char j;
            int padding;
            // Get salt
            for (j = 0; j < m_salt.size(); ++j)
            {
                m_salt.add(j, m_buffer[j]);
            }
            // Get padding
            padding = (m_buffer[j] & 0xFF);
            m_remainingLength -= padding + 1;
            // Start decrypting
            m_buffer_pos = 0;

            m_decryptInitialized = true;
        } 
        else if (m_decryptInitialized && m_buffer_pos == BLOCK_SIZE)
        {
            decrypt();
            for (m_buffer_pos = 0; m_buffer_pos < BLOCK_SIZE; ++m_buffer_pos)
            {
                if (m_remainingLength > 0)
                {
                    Decrypted.add(m_buffer[m_buffer_pos]);
                    //strDecrypt += m_buffer[m_buffer_pos];
                    --m_remainingLength;
                }
            }
            m_buffer_pos = 0;
        }
    }
    
    public int decrypt_end()
    {
        return encrypted.size();
    }
    
    public void AESDescrypt(byte[] strInput)
    {
        decrypt_start(strInput.length);
        decrypt_continue(strInput);
        decrypt_end();       
    }
    
    public void AESDescrypt(String strInput)
    {
        decrypt_start(strInput.length());
        decrypt_continue(strInput);
        decrypt_end();       
    }
    
    public void AESEncrypt(String strInput)
    {
        Aes256Encrypt_start(strInput.length());
        encrypt_continue(strInput);
        encrypt_end();        
    }
    
    public int encrypt_end()
    {
        if (m_buffer_pos > 0)
        {
            while (m_buffer_pos < BLOCK_SIZE)
            {
                m_buffer[m_buffer_pos++] = (char) 0;// & 0xff;
            }
            encrypt();
            for (m_buffer_pos = 0; m_buffer_pos < BLOCK_SIZE; ++m_buffer_pos)
            {
                encrypted.add(m_buffer[m_buffer_pos]);
                --m_remainingLength;
            }
            m_buffer_pos = 0;
        }
        return encrypted.size();
    }
    
    public int encrypt_continue(String strInput) 
    {
        for (int i = 0; i < strInput.length(); i++) 
        {
            m_buffer[m_buffer_pos++] = strInput.charAt(i);
            check_and_encrypt_buffer();
        }
        return encrypted.size();
    }
    
    void add_round_key(int round) 
    {
        int i = KEY_SIZE / 2;
        int index = -1;       
        while (i-- > 0)
        {
            index = round & 1;
            if (index == 1)
            {
                index = i + 16;
            }
            else
            {
                index = i;
            }
            m_buffer[i] ^= m_rkey[index];
        }
    }
    
    public void sub_bytes()
    {
        int i = KEY_SIZE / 2;       
        while (i-- > 0)
        {                   
            m_buffer[i] = sbox[m_buffer[i] & 0xff];
        }
    }
    
    public int rj_xtime(int x)
    {
        int val = x & 0x80;
        if (val > 0) 
        {
            return (((x & 0xff) << 1) ^ 0x1b);
        } 
        else
        {
            return ((x & 0xff) << 1);
        }
    }
    
    public void shift_rows() 
    {
        char i, j, k, l; /* to make it potentially parallelable :) */

        i = m_buffer[1];
        m_buffer[1] = m_buffer[5];
        m_buffer[5] = m_buffer[9];
        m_buffer[9] = m_buffer[13];
        m_buffer[13] = i;

        j = m_buffer[10];
        m_buffer[10] = m_buffer[2];
        m_buffer[2] = j;

        k = m_buffer[3];
        m_buffer[3] = m_buffer[15];
        m_buffer[15] = m_buffer[11];
        m_buffer[11] = m_buffer[7];
        m_buffer[7] = k;

        l = m_buffer[14];
        m_buffer[14] = m_buffer[6];
        m_buffer[6] = l;
    }

    public void mix_columns()
    {
        char a, b, c, d, e;
        for (int i = 0; i < 16; i += 4)
        {
            a = m_buffer[i];
            b = m_buffer[i + 1];
            c = m_buffer[i + 2];
            d = m_buffer[i + 3];

            e = (char) ((a ^ b ^ c ^ d) & 0xff);
           
            m_buffer[i] ^= (e ^ rj_xtime(a ^ b)) & (0xff);
            m_buffer[i + 1] ^= (e ^ rj_xtime(b ^ c)) & (0xff);
            m_buffer[i + 2] ^= (e ^ rj_xtime(c ^ d)) & (0xff);
            m_buffer[i + 3] ^= (e ^ rj_xtime(d ^ a)) & (0xff);
        }
    }
    
    public char expand_enc_key(char rc)
    {
        m_rkey[0] = (char) (m_rkey[0] ^ sbox[m_rkey[29]] ^ (rc));
        m_rkey[1] = (char) (m_rkey[1] ^ sbox[m_rkey[30]]);
        m_rkey[2] = (char) (m_rkey[2] ^ sbox[m_rkey[31]]);
        m_rkey[3] = (char) (m_rkey[3] ^ sbox[m_rkey[28]]);
        // rc = FE(rc);
        rc = (char) (((rc & 0xff) << 1) ^ ((((rc & 0xff) >>> 7) & 1) * 0x1b));
        for (int i = 4; i < 16; i += 4)
        {
            m_rkey[i] = (char) (m_rkey[i] ^ m_rkey[i - 4]);
            m_rkey[i + 1] = (char) (m_rkey[i + 1] ^ m_rkey[i - 3]);
            m_rkey[i + 2] = (char) (m_rkey[i + 2] ^ m_rkey[i - 2]);
            m_rkey[i + 3] = (char) (m_rkey[i + 3] ^ m_rkey[i - 1]);
        }
        m_rkey[16] = (char) (m_rkey[16] ^ sbox[m_rkey[12]]);
        m_rkey[17] = (char) (m_rkey[17] ^ sbox[m_rkey[13]]);
        m_rkey[18] = (char) (m_rkey[18] ^ sbox[m_rkey[14]]);
        m_rkey[19] = (char) (m_rkey[19] ^ sbox[m_rkey[15]]);

        for (int i = 20; i < 32; i += 4)
        {
            m_rkey[i] = (char) (m_rkey[i] ^ m_rkey[i - 4]);
            m_rkey[i + 1] = (char) (m_rkey[i + 1] ^ m_rkey[i - 3]);
            m_rkey[i + 2] = (char) (m_rkey[i + 2] ^ m_rkey[i - 2]);
            m_rkey[i + 3] = (char) (m_rkey[i + 3] ^ m_rkey[i - 1]);
        }
        return rc;
    }

    public void shift_rows_inv()
    {
        char i, j, k, l; /* same as above :) */
        i = m_buffer[1];
        m_buffer[1] = m_buffer[13];
        m_buffer[13] = m_buffer[9];
        m_buffer[9] = m_buffer[5];
        m_buffer[5] = i;

        j = m_buffer[2];
        m_buffer[2] = m_buffer[10];
        m_buffer[10] = j;

        k = m_buffer[3];
        m_buffer[3] = m_buffer[7];
        m_buffer[7] = m_buffer[11];
        m_buffer[11] = m_buffer[15];
        m_buffer[15] = k;

        l = m_buffer[6];
        m_buffer[6] = m_buffer[14];
        m_buffer[14] = l;
    }

    
    public void sub_bytes_inv()
    {
        int i = KEY_SIZE / 2;
        while(i-- > 0)
        {
            m_buffer[i] = sboxinv[m_buffer[i] & 0xff];
        }
    }
    
    public char expand_dec_key(char rc)
    {
        for (int i = 28; i > 16; i -= 4)
        {
            m_rkey[i + 0] = (char) (m_rkey[i + 0] ^ m_rkey[i - 4]);
            m_rkey[i + 1] = (char) (m_rkey[i + 1] ^ m_rkey[i - 3]);
            m_rkey[i + 2] = (char) (m_rkey[i + 2] ^ m_rkey[i - 2]);
            m_rkey[i + 3] = (char) (m_rkey[i + 3] ^ m_rkey[i - 1]);
        }

        m_rkey[16] = (char) (m_rkey[16] ^ sbox[m_rkey[12]]);
        m_rkey[17] = (char) (m_rkey[17] ^ sbox[m_rkey[13]]);
        m_rkey[18] = (char) (m_rkey[18] ^ sbox[m_rkey[14]]);
        m_rkey[19] = (char) (m_rkey[19] ^ sbox[m_rkey[15]]);

        for (int i = 12; i > 0; i -= 4)
        {
            m_rkey[i + 0] = (char) (m_rkey[i + 0] ^ m_rkey[i - 4]);
            m_rkey[i + 1] = (char) (m_rkey[i + 1] ^ m_rkey[i - 3]);
            m_rkey[i + 2] = (char) (m_rkey[i + 2] ^ m_rkey[i - 2]);
            m_rkey[i + 3] = (char) (m_rkey[i + 3] ^ m_rkey[i - 1]);
        }
        if ((rc & 1) > 0)
        {
            rc = (char) ((rc >> 1) ^ 0x8d);
        } 
        else
        {
            rc = (char) ((rc >> 1) ^ 0);
        }
        m_rkey[0] = (char) (m_rkey[0] ^ sbox[m_rkey[29]] ^ (rc));
        m_rkey[1] = (char) (m_rkey[1] ^ sbox[m_rkey[30]]);
        m_rkey[2] = (char) (m_rkey[2] ^ sbox[m_rkey[31]]);
        m_rkey[3] = (char) (m_rkey[3] ^ sbox[m_rkey[28]]);
        return rc;
    }
    
    public void mix_columns_inv()
    {
        char a, b, c, d, e, x, y, z;
        for (int i = 0; i < 16; i += 4)
        {
            a = m_buffer[i];
            b = m_buffer[i + 1];
            c = m_buffer[i + 2];
            d = m_buffer[i + 3];

            e = (char) (a ^ b ^ c ^ d);
            z = (char) rj_xtime(e);
            x = (char) (e ^ rj_xtime(rj_xtime(z ^ a ^ c)));
            y = (char) (e ^ rj_xtime(rj_xtime(z ^ b ^ d)));

            m_buffer[i] ^= x ^ rj_xtime(a ^ b);
            m_buffer[i + 1] ^= y ^ rj_xtime(b ^ c);
            m_buffer[i + 2] ^= x ^ rj_xtime(c ^ d);
            m_buffer[i + 3] ^= y ^ rj_xtime(d ^ a);
        }
    }
    
    public void decrypt()
    {
        char rcon = 1;
        int i;
        copy_key();
        for (i = NUM_ROUNDS / 2; i > 0; --i) 
        {
            rcon = expand_enc_key(rcon);
        }
        add_round_key(NUM_ROUNDS);
        shift_rows_inv();
        sub_bytes_inv();    
        for (i = NUM_ROUNDS, rcon = (0x80 & 0xff); --i > 0;)
        {
            if ((i & 1) > 0) {
                rcon = expand_dec_key(rcon);
            }
            add_round_key(i);
            mix_columns_inv();
            shift_rows_inv();
            sub_bytes_inv();
        }
        add_round_key(i);
    }
    
    public void encrypt()
    {
        char rcon;
        int i;
        int val = 0;
        copy_key();
        add_round_key(0);
        for (i = 1, rcon = 1; i < NUM_ROUNDS; ++i)
        {
            sub_bytes();
            shift_rows();
            mix_columns();

            val = i & 1;
            if (val == 0)
            {
                rcon = expand_enc_key(rcon);
            }
            add_round_key(i);
        }
        sub_bytes();
        shift_rows();
        rcon = expand_enc_key(rcon);
        add_round_key(i);
    }
    
    public void check_and_encrypt_buffer() 
    {
        if (m_buffer_pos == BLOCK_SIZE)
        {
            encrypt();
            for (m_buffer_pos = 0; m_buffer_pos < BLOCK_SIZE; ++m_buffer_pos)
            {
                encrypted.add(m_buffer[m_buffer_pos]);
                --m_remainingLength;
            }
            m_buffer_pos = 0;
        }
    }
    
    public int Aes256Encrypt_start(int plain_length)
    {
        m_remainingLength = plain_length;
         char ch;
         for (int i = 0; i < m_salt.size(); i++)
         {
           //  ch = (char)(((int)(Math.random())) & 0xff);
             ch = (char)((generator.nextInt()) & 0xff);            
             m_salt.add(i,ch);
         }
        // Calculate padding
        int padding = 0;
        if (m_remainingLength % BLOCK_SIZE != 0)
        {
            padding = (BLOCK_SIZE - (m_remainingLength % BLOCK_SIZE));
        }
        m_remainingLength += padding;
        // Add salt
        encrypted.addAll(m_salt);        
        m_remainingLength += m_salt.size();

        // Add 1 bytes for padding size      
        encrypted.addElement((char) (padding & 0xFF));
        ++m_remainingLength;
      
        m_buffer_pos = 0;
        return encrypted.size();
    }
    
    public void DecryptFile(String strSrcFilePath, String strDestFilePath)
    {
        long lstarttime = System.currentTimeMillis();
        File file = null;
        FileInputStream istream = null;
        String strdata = "";
        OutputStream os = null;
         try 
        {
            file = new File(strSrcFilePath);
            if (file.exists())
            {
                istream = new FileInputStream(file);
                byte[] chunk = new byte[(int)file.length()];
                
                
//                fr = new FileReader(file);
//                BufferedReader br = new BufferedReader(fr);
//                char[] cbuf = new char[(int) file.length()];
//                br.read(cbuf);              
//                strInput= String.valueOf(cbuf);
               
                while ((istream.read(chunk)) != -1)
                {                    
                   // strInput += new String(chunk);
//                    for(byte b : chunk)
//                    {
//                     strInput += (char)b;
//                    }
                    long lstarttime1 = System.currentTimeMillis();
                    AESDescrypt(chunk);
                    System.out.println("Decrypt for loop method end : "+(System.currentTimeMillis()-lstarttime1));
                } 
                 
                // AESDescrypt(strInput);
//                long lstarttime1 = System.currentTimeMillis();
//                ArrayList<Character> list = new ArrayList<Character>(Decrypted);
                 // char []ch = new char[Decrypted.size()];
                 // Decrypted.toArray(ch);
                //  strdata = String.valueOf(Decrypted);
                for(int i = 0 ; i < Decrypted.size() ; i++)
                {                  
                    strdata += Decrypted.get(i);
                }   
                
                os = new FileOutputStream(new File(strDestFilePath));
                os.write(strdata.getBytes(), 0, strdata.length());
                os.close();
            }
        } 
        catch (Exception exp)
        {
            System.out.println("Exception : " + exp);
        }
        System.out.println("Decrypt file End , Elapsed time in ms : "+(System.currentTimeMillis()-lstarttime));
    }
    
    public void DecryptFilewithDecoding(String strSrcFilePath, String strDestFilePath)
    {
        long lstarttime = System.currentTimeMillis();
        File file = null;
        FileInputStream istream = null;
        String strInput = "", strBase64Encode = "",strdata = "";
        OutputStream os = null;       
        try 
        {
            file = new File(strSrcFilePath);
            if (file.exists())
            {
                istream = new FileInputStream(file);
                byte[] chunk = new byte[(int)file.length()];
                while ((istream.read(chunk)) != -1)
                {                    
                    strInput += new String(chunk);
                }
                strBase64Encode = Base64.base64_decode(strInput);
                AESDescrypt(strBase64Encode);               
                long lstarttime1 = System.currentTimeMillis();
                for(int i = 0 ; i < Decrypted.size() ; i++)
                {
                   // ch[i++] = s;
                    strdata += Decrypted.get(i);
                }   
                System.out.println("Decrypt for loop method end : "+(System.currentTimeMillis()-lstarttime1));
                os = new FileOutputStream(new File(strDestFilePath));
                os.write(strdata.getBytes(), 0, strdata.length());
                os.close();
            }
        } 
        catch (Exception exp)
        {
            System.out.println("Exception : " + exp);
        }
        System.out.println("Decrypt file End , Elapsed time in ms : "+(System.currentTimeMillis()-lstarttime));
    }
    
    public void EncryptFilewithEncoding(String strSrcFilePath, String strDestFilePath)
    {
        File file = null;
        FileInputStream istream = null;
        String strInput = "",strBase64Encode="";
        OutputStream os = null;
        try 
        {
            file = new File(strSrcFilePath);
            if(file.exists())
            {
                istream = new FileInputStream(file);
                byte[] chunk = new byte[(int) file.length()];              
                while ((istream.read(chunk)) != -1) 
                {
                    strInput += new String(chunk);
                }
                AESEncrypt(strInput);
                strBase64Encode = Base64.base64_encode_str(encrypted,encrypted.size());              
                os = new FileOutputStream(new File(strDestFilePath));               
                os.write(strBase64Encode.getBytes(), 0, strBase64Encode.length());
                os.close();
            }
            else
            {
                System.out.println("Request file is not found : "+ strSrcFilePath);
            }
        } 
        catch (Exception exp)
        {
            System.out.println("Exception : "+exp); 
        }
    }
    
    public void EncryptFile(String strSrcFilePath, String strDestFilePath)
    {
        File file = null;
        FileInputStream istream = null;
        String strInput = "";
        OutputStream os = null;
        try 
        {
            file = new File(strSrcFilePath);
            if(file.exists())
            {
                istream = new FileInputStream(file);
                byte[] chunk = new byte[(int) file.length()];              
                while ((istream.read(chunk)) != -1) 
                {
                    strInput += new String(chunk);
                }
                istream.close();
                AESEncrypt(strInput);            
                os = new FileOutputStream(new File(strDestFilePath));
                for (char s : encrypted)               
                {                  
                    os.write(s);
                }
                os.close();
            }
            else
            {
                System.out.println("Request file is not found : "+strSrcFilePath);
            }
        } 
        catch (Exception exp)
        {
            System.out.println("Exception : "+exp); 
        }
    }
    
    public static void main(String[] args)
    {
        String strInput = "Another cause of this problem is that incorrect dependency (JAR files) in ClassPath. Some developer includes Jersey 2 JAR files and puzzled why Java is still complaining about com.sun.jersey.spi.container.servlet.ServletContainer, well there is a significant difference between Jersey 1x and Jersey 2.x in terms of this class. In Jersey 2 this class is moved from com.sun.jersey package to org.glassfish.jersey package. So even if you have downloaded Jersey 2 JAR file you will still get this error Read more: https://javarevisited.blogspot.com/2016/02/javalangclassnotfoundexception-com.sun.jersey.spi.container.servlet.ServletContainer-solution.html#ixzz5TRiyDr3Q";       
        String strkey = "AESEncryption256AESEncryption256";
//        C_AES_256 obj = new C_AES_256(strkey);
//        String strInputFile = "E:\\MyJava\\Encryption\\Document1.txt";
//        String strDecodeFile = "E:\\MyJava\\Encryption\\Document2.txt";  
        
      //  obj.EncryptFilewithEncoding(strInputFile);
      //  obj.DecryptFilewithDecoding(strDecodeFile);
        
       // obj.EncryptFile(strInputFile);
        //obj.DecryptFile(strDecodeFile);
        
       // obj.AESEncrypt(strInput);
//        char[] ch = new char[obj.encrypted.size()];
//        int i = 0;
//        String str = "";
//        for (char s : obj.encrypted)
//        {
//            str += s;
//            ch[i++] = s;
//        }
//        obj.Decrypted.clear();
//        obj.AESDescrypt(str);
//        String str1 = "";       
//        for (char s : obj.Decrypted)
//        {
//            str1 += s;
//            System.out.println("Char:" + s);
//        }
//        System.out.println("Decrypted value - "+str1);
//        String strdata = "";
//        String str = "";
//        String base64encode = "";
//        try 
//        {
//            if (file.exists())
//            {
//                FileInputStream is = new FileInputStream(file);
//                byte[] chunk = new byte[(int)file.length()];
//                int chunkLen = 0;
//                while ((chunkLen = is.read(chunk)) != -1)
//                {                   
//                    strInput += new String(chunk, StandardCharsets.UTF_8);
//                }
//            }            
//            obj.AESEncrypt(strInput);
//            char[] ch = new char[obj.encrypted.size()];
//            int i = 0;
//            for (char s : obj.encrypted) 
//            {
//                str += s;
//                ch[i++] = s;
//            }
//            base64encode = Base64.base64_encode(ch, obj.encrypted.size());
//            OutputStream os = new FileOutputStream(new File("E:\\MyJava\\Encryption\\Document2.txt"));
//            os.write(base64encode.getBytes(), 0, base64encode.length());
//            // obj.AESEncrypt(strInput);
//
////        char []ch = new char[obj.encrypted.size()];
////        int  i = 0;
////        for (char s : obj.encrypted) 
////        {
////            str += s;
////            ch[i++]=s;
////        }
////        System.out.println("Encryption value : "+str);
//            //  String base64encode = Base64.base64_encode(ch, obj.encrypted.size());
//            String base64Decode = Base64.base64_decode(base64encode);
//            obj.AESDescrypt(str);
//            String str1 = "";
//            for (char s : obj.Decrypted)
//            {
//                str1 += s;
//                System.out.println("Char:" + s);
//            }
//            System.out.println("Decrypted value : " + str1);
//            OutputStream os1 = new FileOutputStream(new File("E:\\MyJava\\Encryption\\Document3.txt"));
//            os1.write(str1.getBytes(), 0, str1.length());
//        } 
//        catch (Exception exp) 
//        {
//            
//        }
    }
}
