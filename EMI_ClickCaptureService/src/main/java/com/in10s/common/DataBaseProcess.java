package com.in10s.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.in10s.applog.AppLogger;
import com.in10s.config.CErrorCodes;
import com.in10s.config.LoadApplicationProperties;

public class DataBaseProcess {
	
	public boolean validateTransRefNum(Connection dbConn,String strTransRefNum){
		boolean bTransStatus = false;
		PreparedStatement psTranRefNumCount = null;
		ResultSet rsTransRefNumCount = null;
		int nCount = 0;
		String strTrasansRefNumQuery = LoadApplicationProperties.QUERY_TRANS_REFNUM_COUNT;
		AppLogger.info("validating the record exist or not with Transaction Reference Number : "+strTransRefNum);
		
		try{
			strTrasansRefNumQuery = strTrasansRefNumQuery.replace("<<SCHEMA_NAME>>",BoneCPDBPool.strSchemaName);
			AppLogger.debug("Query to validate the record exist or not with Transaction Reference Number : "+strTransRefNum);
			psTranRefNumCount = dbConn.prepareStatement(strTrasansRefNumQuery);
			psTranRefNumCount.setString(1, strTransRefNum);
			rsTransRefNumCount = psTranRefNumCount.executeQuery();
			
			while(rsTransRefNumCount.next()){
				
				nCount = rsTransRefNumCount.getInt(1);
				if(nCount > 0){
					bTransStatus = true;
				}
				
			}
			AppLogger.info("Number of Records exist with the Transaction Reference Number : "+strTransRefNum +" are : "+nCount);
			
		} catch (SQLException sqlEx){
			
			AppLogger.error(CErrorCodes.INTERNAL_SQLEXCEPTION+" : SQLException occurred while checking whethere the Transaction is already exist with Transaction Reference Number : "+strTransRefNum, sqlEx);
			
		} catch (Exception ex){
			
			AppLogger.error(CErrorCodes.INTERNAL_EXCEPTION+" : Exception occurred while checking whethere the Transaction is already exist with Transaction Reference Number : "+strTransRefNum, ex);
			
		} finally {
			
			closeResultset(rsTransRefNumCount);
			closePreparedStatement(psTranRefNumCount);
			
		}
		
		return bTransStatus;
	}

	public boolean captureEmiRequest(Connection dbConn, String[] strReqAftDecrypt) {
		
		int nCount;
		boolean bEmiCaptureStatus = false;
		PreparedStatement psCaptureEmiReq = null;
		String strCaptureEmiReqQuery = LoadApplicationProperties.QUERY_CAPTURE_EMIREQUEST;
		AppLogger.info("Capturing the Emi request data into a table");
		
		try {
			
			strCaptureEmiReqQuery = strCaptureEmiReqQuery.replace("<<SCHEMA_NAME>>",BoneCPDBPool.strSchemaName);
			AppLogger.debug("Query to capturing the Emi request data is : "+strCaptureEmiReqQuery);
			psCaptureEmiReq = dbConn.prepareStatement(strCaptureEmiReqQuery);
			psCaptureEmiReq.setString(1, strReqAftDecrypt[0]);
			psCaptureEmiReq.setString(2, strReqAftDecrypt[1]);
			psCaptureEmiReq.setString(3, strReqAftDecrypt[2]);
			psCaptureEmiReq.setString(4, strReqAftDecrypt[3]);
			psCaptureEmiReq.setString(5, strReqAftDecrypt[4]);
			psCaptureEmiReq.setString(6, strReqAftDecrypt[5]);
			psCaptureEmiReq.setString(7, strReqAftDecrypt[6]);
			psCaptureEmiReq.setString(8, strReqAftDecrypt[7]);
			psCaptureEmiReq.setString(9, strReqAftDecrypt[8]);
			psCaptureEmiReq.setTimestamp(10, new java.sql.Timestamp(new java.util.Date().getTime()));
			nCount = psCaptureEmiReq.executeUpdate();
			
			if(nCount > 0){
				
				bEmiCaptureStatus = true;
				AppLogger.info("Capturing the Emi request data completed Successfully");
			}
			
		} catch (SQLException sqlEx) {
			
			AppLogger.error(CErrorCodes.INTERNAL_SQLEXCEPTION+" : SQLException occurred while Capturing the Emi Request into DB table : ",sqlEx);
			
		} catch (Exception ex) {
			
			AppLogger.error(CErrorCodes.INTERNAL_EXCEPTION+" : Exception occurred while Capturing the Emi Request into DB table : ",ex);
			
		}
		return bEmiCaptureStatus;
	}
	
	public void AuditEmiRequestData(Connection dbConn, String[] strReqAftDecrypt, String strStatus, String strComments,
			String strError_des) {
		
		int nCount = 0;
		boolean bEmiCaptureStatus = false;
		PreparedStatement psCaptureEmiReq = null;
		String EmiAuditReqQuery = "";
		AppLogger.info("Auditing the Emi request data");
		
		try {
						
			if(strReqAftDecrypt.length == 9){
				
				EmiAuditReqQuery = LoadApplicationProperties.QUERY_AUDIT_EMIREQUEST;
				EmiAuditReqQuery = EmiAuditReqQuery.replace("<<SCHEMA_NAME>>",BoneCPDBPool.strSchemaName);
				psCaptureEmiReq = dbConn.prepareStatement(EmiAuditReqQuery);
				psCaptureEmiReq.setString(1, strReqAftDecrypt[0]);
				psCaptureEmiReq.setString(2, strReqAftDecrypt[1]);
				psCaptureEmiReq.setString(3, strReqAftDecrypt[2]);
				psCaptureEmiReq.setString(4, strReqAftDecrypt[3]);
				psCaptureEmiReq.setString(5, strReqAftDecrypt[4]);
				psCaptureEmiReq.setString(6, strReqAftDecrypt[5]);
				psCaptureEmiReq.setString(7, strReqAftDecrypt[6]);
				psCaptureEmiReq.setString(8, strReqAftDecrypt[7]);
				psCaptureEmiReq.setString(9, strReqAftDecrypt[8]);
				psCaptureEmiReq.setTimestamp(10, new java.sql.Timestamp(new java.util.Date().getTime()));
				psCaptureEmiReq.setString(11, strStatus);
				psCaptureEmiReq.setString(12, strComments);
				psCaptureEmiReq.setString(13, strError_des);
				
			} else {
				
				EmiAuditReqQuery = LoadApplicationProperties.QUERY_AUDIT_EMIREQUEST_FAIL;
				EmiAuditReqQuery = EmiAuditReqQuery.replace("<<SCHEMA_NAME>>",BoneCPDBPool.strSchemaName);
				psCaptureEmiReq = dbConn.prepareStatement(EmiAuditReqQuery);
				psCaptureEmiReq.setTimestamp(1, new java.sql.Timestamp(new java.util.Date().getTime()));
				psCaptureEmiReq.setString(2, strStatus);
				psCaptureEmiReq.setString(3, strComments);
				psCaptureEmiReq.setString(4, strError_des);
				
			}
			
			nCount = psCaptureEmiReq.executeUpdate();
			
			if(nCount > 0){
				
				bEmiCaptureStatus = true;
				AppLogger.info("Auditing the Emi request data is completed successfully");
			}
			
		} catch (SQLException sqlEx) {
			
			AppLogger.error(CErrorCodes.INTERNAL_SQLEXCEPTION+" : SQLException occurred while Capturing the Emi Request into DB table : ",sqlEx);
			
		} catch (Exception ex) {
			
			AppLogger.error(CErrorCodes.INTERNAL_EXCEPTION+" : Exception occurred while Capturing the Emi Request into DB table : ",ex);
			
		}
		
	}
	
	public static void closeResultset(ResultSet rsCloseResultSet) {

		try {

			if (rsCloseResultSet != null) {

				rsCloseResultSet.close();
				rsCloseResultSet = null;

			}

		} catch (SQLException sqlEx) {

			AppLogger.error(CErrorCodes.INTERNAL_SQLEXCEPTION+" : SQLException occurred while closing ResultSet : ", sqlEx);

		} catch (Exception ex) {

			AppLogger.error(CErrorCodes.INTERNAL_EXCEPTION+" : Exception occurred while closing ResultSet : ", ex);

		}

	}
	
	public static void closePreparedStatement(PreparedStatement psClosePreparedStatement) {
		
		try {

			if (psClosePreparedStatement != null) {

				psClosePreparedStatement.close();
				psClosePreparedStatement = null;

			}

		} catch (SQLException sqlEx) {

			AppLogger.error(CErrorCodes.INTERNAL_SQLEXCEPTION+" : SQLException occurred while closing PreparedStatement : ", sqlEx);

		} catch (Exception ex) {

			AppLogger.error(CErrorCodes.INTERNAL_EXCEPTION+" : Exception occurred while closing PreparedStatement : ", ex);

		}

	}
	
}
