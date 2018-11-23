package com.in10s.yes;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.in10s.applog.AppLogger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author raghuram
 */
public class CRSGetTime {

    /**
     *
     * @param d
     * @return
     */
    public  String getformattedTime(Date d) {
        String sss = "";
        try {
          //  System.out.println(">>>>>>>>>>>>>>  "+d);
            SimpleDateFormat ss = new SimpleDateFormat("yyyyMMddHHmmss");
            sss = ss.format(d);
           // System.out.println("time in0-0-0-0-0 "+sss);
            // System.out.println(sss);
            // return sss;
        } catch (Exception e) {
        	AppLogger.error("Exception : ",e);
        }
        return sss;
    }

    public  Date getTime1(String strtime) {
        Date d = null;
        try {
            SimpleDateFormat ss1 = new SimpleDateFormat("yyyyMMddHHmmss");
            d = ss1.parse(strtime);

        } catch (Exception e) {
        	AppLogger.error("Exception : ",e);
        }
        return d;
    }
}

