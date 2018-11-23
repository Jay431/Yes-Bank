package com.in10s.yes;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import com.in10s.appprop.CRSLoadmqProperties;
import com.in10s.dao.BoneCPDBPool;
import com.in10s.applog.AppLogger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import net.sf.json.JSONObject;


public class CRSSequenceGenerator {
	
//    public static String getSequenceVal(Connection con) {
//    	AppLogger.info("Start of getSequenceVal");
//        String strSEQID = "";
//        PreparedStatement getSequencestmt = null;
//        ResultSet sequenceRS = null;
//        try {
//            String fetchSeq = CRSLoadmqProperties.HBANKDEEPLINKSEQ;
//            getSequencestmt = con.prepareStatement(fetchSeq);
//            sequenceRS = getSequencestmt.executeQuery();
//            if (sequenceRS.next()) {
//                strSEQID = Integer.toString(sequenceRS.getInt("NEXTVAL"));
//            }
//
//        } catch (Exception e) {
//            strSEQID="1234";
//            AppLogger.error("Error occured in getSequenceVal",e);
//        } finally {
//            try {
//                if (sequenceRS != null) {
//                    sequenceRS.close();
//                    sequenceRS = null;
//                }
//                if (getSequencestmt != null) {
//                    getSequencestmt.close();
//                    getSequencestmt = null;
//                }
//            } catch (Exception e) {
//            	AppLogger.error("Error occured in closing resources  getSequenceVal",e );
//            }
//        }
//        AppLogger.info("request ID : "+strSEQID);
//        AppLogger.info("End of getSequenceVal");
//        return strSEQID;
//    }

//    public static String auditJspStatus(JSONObject auditInfo) {
//    	AppLogger.info("Start of auditJspStatus");
//        String strfilePatgh = "";
//        String sequenceId="";
//        Connection con = null;
//        try {
//            con = BoneCPDBPool.getConnection();
//            sequenceId = getSequenceVal(con);
//            auditInfo.put("REQUESTID", sequenceId);
//            //CRSOnTheFly.deeplinkAuditMaster(con, auditInfo, strfilePatgh, sequenceId, "");
//           
//        } catch (Exception e) {
//        	AppLogger.error(sequenceId+":Error occured in auditJspStatus",e);
//        } finally {
//            try {
//                if (con != null) {
//                    con.close();
//                    con = null;
//                }
//
//            } catch (Exception e) {
//            	AppLogger.error(sequenceId+"Error occured in closing resources  auditJspStatus",e );
//            	
//            }
//        }
//        AppLogger.info(sequenceId+":End of auditJspStatus");
//        return sequenceId;
//    }
    
    public static void updateJspStatus(String strReqId,String strcommenmts) {
        System.out.println("IN");
        AppLogger.info(strReqId+":Starting of updateJspStatus :");
        Connection con=null;
        PreparedStatement ps = null;
        try {
            con = BoneCPDBPool.getConnection();
            ps=con.prepareStatement("UPDATE DEEPLINK_AUDITMASTER SET COMMENTS=? WHERE REQUEST_ID=?");
           
            ps.setString(1, strcommenmts);
            ps.setString(2, strReqId);
            int res=ps.executeUpdate();
            AppLogger.debug(strReqId+":updateJspStatus upted status:"+res);
            
        } catch (Exception e) {
        	AppLogger.error(strReqId+":Error occured in updateJspStatus",e );
            
        } finally {
            try {
                if(ps!=null){
                    ps.close();
                    ps=null;
                }
                if (con != null) {
                    con.close();
                    con = null;
                }

            } catch (Exception e) {
            	AppLogger.error(strReqId+":Error occured in closing resources of updateJspStatus",e );
            }
        }
        AppLogger.info(strReqId+":End of updateJspStatus :");
    }

}

