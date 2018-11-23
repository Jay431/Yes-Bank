package com.in10s.appprop;

import java.io.File;
import java.io.FileInputStream;
//import com.in10s.restservice.listeners.logger;
import java.io.InputStream;
import java.io.StringWriter;
import net.sf.json.JSON;
import net.sf.json.JSONObject;
import net.sf.json.xml.XMLSerializer;
import org.apache.commons.io.IOUtils;

import com.in10s.listener.ApplicationListener;

/**
 *
 * @author raghuram
 */
public class CRSHttpErrorMsgs {

    public static CRSHttpErrorMsgs obj = null;
    public static JSONObject errorcode = null;

    public static JSONObject getErrorcodes() {
        InputStream is = null;
        StringWriter sw = null;
        

        try {

            if (obj == null) {
                synchronized (CRSHttpErrorMsgs.class) {
                    if (obj == null) {
                        obj = new CRSHttpErrorMsgs();
                        sw = new StringWriter();
                        
//                        is = CRSHttpErrorMsgs.class.getClassLoader().getResourceAsStream("ERRORCODES.xml");
                        File initialFile = new File(ApplicationListener.strErrorCodesFilePath);
                        is = new FileInputStream(initialFile);
                       
                        IOUtils.copy(is, sw);
                        String codes = sw.toString();
                        System.out.println("in ;;");
                        //logger.info("error code are " + codes);
                        XMLSerializer objXMLSerializer = new XMLSerializer();
                        JSON objMessageData = objXMLSerializer.read(codes.trim());
                        errorcode = (JSONObject) objMessageData;

                    }

                }

            }

        } catch (Exception ex) {
            obj = null;
            ex.printStackTrace();
           // logger.error(ex);

        }finally{
            try {
                if(is != null){
                    is.close();
                    is = null;
                }if(sw != null){
                    sw.close();
                    sw = null;
                }
            } catch (Exception e) {
            }
        }
        return errorcode;
    }

}
