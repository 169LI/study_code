package com.jiafei.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 十六进制转成图片
 */
public class sa
{
    public static void main(String[] args) throws Exception
    {
        sa to = new sa();
        InputStream is = new FileInputStream("D:/aaa.txt");
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String str = null;
        StringBuilder sb = new StringBuilder();
        while ((str = br.readLine()) != null)
        {
            System.out.println(str);
            sb.append(str);
        }
        to.saveToImgFile(sb.toString().toUpperCase(), "D:/bbb.jpg");
    }

    public void saveToImgFile(String src, String output)
    {
        if (src == null || src.length() == 0)
        {
            return;
        }
        try
        {
            FileOutputStream out = new FileOutputStream(new File(output));
            byte[] bytes = src.getBytes();
            for (int i = 0; i < bytes.length; i += 2)
            {
                out.write(charToInt(bytes[i]) * 16 + charToInt(bytes[i + 1]));
            }
            out.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private int charToInt(byte ch)
    {
        int val = 0;
        if (ch >= 0x30 && ch <= 0x39)
        {
            val = ch - 0x30;
        }
        else if (ch >= 0x41 && ch <= 0x46)
        {
            val = ch - 0x41 + 10;
        }
        return val;
    }
}