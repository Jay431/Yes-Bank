package com.in10s.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import com.in10s.applog.AppLogger;
import com.in10s.appprop.CErrorCodes;
import com.in10s.appprop.CRSLoadmqProperties;
import net.sf.json.JSONObject;

public class DataBaseProcess {
	
	public String internalHtmlSeqId(Connection dbConn){
		
		   AppLogger.info("Start of fetching the internal Html SeqId is");
	       String strSEQID = "";
	       PreparedStatement psSequencestmId = null;
	       ResultSet rsSequenceId = null;
	       String strQuery = CRSLoadmqProperties.QUERY_INTERNAL_SEQUENCE;
	       
	       try {
	    	   
	    	   strQuery = strQuery.replace("<<SCHEMA_NAME>>", BoneCPDBPool.strSchemaName);
	    	   AppLogger.debug("Query to fetching the internal Html SeqId is : "+strQuery);
	           psSequencestmId = dbConn.prepareStatement(strQuery);
	           rsSequenceId = psSequencestmId.executeQuery();
	           
	           if (rsSequenceId.next()) {
	        	   
	               strSEQID = Integer.toString(rsSequenceId.getInt(1));
	               
	           }

	       } catch (SQLException sqlEx) {
	    	   
	           strSEQID="1234";
	           AppLogger.error(CErrorCodes.INTERNAL_SQLEXCEPTION+" : Default Value 1234 is set as sequence Id as SQLException occurred while fetching the internal Html SeqId : ",sqlEx );
	           
	       } catch (Exception sqlEx) {
	    	   
	           strSEQID="1234";
	           AppLogger.error(CErrorCodes.INTERNAL_EXCEPTION+" : Default Value 1234 is set as sequence Id as Exception occurred while fetching the internal Html SeqId : ",sqlEx );
	           
	       } finally {
	           
	    	   closeResultset(rsSequenceId);
	    	   closePreparedStatement(psSequencestmId);
	    	   
	       }
	       AppLogger.info("request ID : "+strSEQID);
	       AppLogger.info("End of internalHtmlSeqId");
	       return strSEQID;
				
	}
	
	public String validateSessionId(Connection dbConn, String strSessionId,String strSEQID) {

		String strProcessflag = "";
		PreparedStatement psValidateSession = null;
		ResultSet rsValidateSession = null;
		String strQuery = CRSLoadmqProperties.QUERY_VALIDATE_SESSIONID;
		 
		AppLogger.info(strSEQID+" : Start of Validating the Session Id");
		
		try {
			strQuery = strQuery.replace("<<SCHEMA_NAME>>", BoneCPDBPool.strSchemaName);
			AppLogger.info(strSEQID+" : Query for Validating the Session Id is : "+strQuery);
			psValidateSession = dbConn.prepareStatement(strQuery);
			psValidateSession.setString(1, strSessionId);

			rsValidateSession = psValidateSession.executeQuery();

			if (rsValidateSession.next()) {

				strProcessflag = rsValidateSession.getString(2);

			}

		} catch (SQLException sqlEx) {

			AppLogger.error(strSEQID+" : "+CErrorCodes.INTERNAL_SQLEXCEPTION+" : SQLException occurred while validating the sessionId : ", sqlEx);

		} catch (Exception ex) {

			AppLogger.error(strSEQID+" : "+CErrorCodes.INTERNAL_EXCEPTION+" : Exception occurred while validating the sessionId : ", ex);

		} finally {

			closePreparedStatement(psValidateSession);
			closeResultset(rsValidateSession);

		}

		return strProcessflag;

	}

	public boolean upDateProcessFlag(Connection dbConn, String strSessionId,String strSEQID,String strStatus,String strExistingStatus) {
		
		boolean bProcessFlagStatus = false;
		PreparedStatement psUpdateProcessFalg = null;
		int nCount = 0;
		String strQuery = CRSLoadmqProperties.QUERY_UPDATE_PROCESSFLAG;
		 
		AppLogger.info(strSEQID+" : Start of updating the Process flag");
		try {
			strQuery = strQuery.replace("<<SCHEMA_NAME>>", BoneCPDBPool.strSchemaName);
			AppLogger.debug(strSEQID+" : Query to update the Process flag is : "+strQuery);
			psUpdateProcessFalg = dbConn.prepareStatement(strQuery);
			psUpdateProcessFalg.setString(1, strStatus);
			psUpdateProcessFalg.setTimestamp(2, new java.sql.Timestamp(new java.util.Date().getTime()));				
			psUpdateProcessFalg.setString(3, strSessionId);
			psUpdateProcessFalg.setString(4, strExistingStatus);
			
			nCount = psUpdateProcessFalg.executeUpdate();
			
			if(nCount > 0 ){
				
				bProcessFlagStatus = true;
				AppLogger.info(strSEQID+" : Process flag is updated Successfully");
			}
			

			
		} catch (SQLException sqlEx) {
			
			AppLogger.error(strSEQID+" : "+CErrorCodes.INTERNAL_SQLEXCEPTION+" : SQLException occurred while updating the Process flag : ", sqlEx);
			 
		} catch (Exception ex){
			
			AppLogger.error(strSEQID+" : "+CErrorCodes.INTERNAL_EXCEPTION+" : Exception occurred while updating the Process flag : ", ex);
			
		} finally {
			
			closePreparedStatement(psUpdateProcessFalg);
			
		}
		AppLogger.info(strSEQID+" : End of updating the Process flag");
		return bProcessFlagStatus;
	}
	
	
	public static String getSequenceVal(Connection con) {
		
		AppLogger.info("Start of getSequenceVal");
       String strSEQID = "";
       PreparedStatement getSequencestmt = null;
       ResultSet sequenceRS = null;
       String strQuery = CRSLoadmqProperties.YESBANKDEEPLINKSEQ;
       
       try {
    	   
    	   strQuery = strQuery.replace("<<SCHEMA_NAME>>", BoneCPDBPool.strSchemaName);
           getSequencestmt = con.prepareStatement(strQuery);
           sequenceRS = getSequencestmt.executeQuery();
           
           if (sequenceRS.next()) {
        	   
               strSEQID = Integer.toString(sequenceRS.getInt(1));
               
           }

       } catch (SQLException sqlEx) {
    	   
           strSEQID="1234";
           AppLogger.error(CErrorCodes.INTERNAL_SQLEXCEPTION+" : Default Value 1234 is set as sequence Id as SQLException occurred while fetching the get Sequence Val : ",sqlEx );
           
       } catch (Exception ex) {
    	   
           strSEQID="1234";
           AppLogger.error(CErrorCodes.INTERNAL_EXCEPTION+" : Default Value 1234 is set as sequence Id as Exception occurred while fetching the get Sequence Val : ",ex );
           
       } finally {
           
    	   closeResultset(sequenceRS);
    	   closePreparedStatement(getSequencestmt);
    	   
       }
       
       AppLogger.info("request ID : "+strSEQID);
        AppLogger.info("End of getSequenceVal");
       return strSEQID;
   }
	
	public static String auditJspStatus(JSONObject auditInfo) {
		
       AppLogger.info("Start of auditJspStatus");
       String strfilePatgh = "";
       String sequenceId="";
       Connection con = null;
       
       try {
    	   
           con = BoneCPDBPool.getConnection();
           sequenceId = getSequenceVal(con);
           auditInfo.put("REQUESTID", sequenceId);
           deeplinkAuditMaster(con, auditInfo, strfilePatgh, sequenceId, "");
          
       } catch (Exception e) {
    	   
    	   AppLogger.error(sequenceId+":Error occured in auditJspStatus : ",e);
       
       } finally {
    	   
           try {
        	   
               if (con != null) {
            	   
                   con.close();
                   con = null;
                   
               }

           } catch (SQLException e) {
        	   
        	   AppLogger.error(sequenceId+" : "+CErrorCodes.INTERNAL_SQLEXCEPTION+" : SQLException occured in closing resources auditJspStatus ",e );
        	   
           } catch (Exception e) {
        	   
        	   AppLogger.error(sequenceId+" : "+CErrorCodes.INTERNAL_SQLEXCEPTION+" : Exception occured in closing resources auditJspStatus ",e );
           
           }
       }
       AppLogger.info(sequenceId+" : End of auditJspStatus");
       return sequenceId;
   }
	
	
	public String fetchPassword(Connection dbConn,String strSquenceId,String strJobKey,String passWordInRequest){
		
		String actualpass = "";
		PreparedStatement psFetchPassword = null;
		ResultSet rsFectchPassword = null;
//		String strComments = "";
		String strPassword = "";
//		String strFetchPassword = CRSLoadmqProperties.QUERY_FETCH_PASSWORD;
		String strFetchPassword = "";
		
		AppLogger.debug(strSquenceId+" : fetching password from data based for authentication");
		try {
			
			if(strJobKey.startsWith("M")) {
				
				strFetchPassword = CRSLoadmqProperties.QUERY_FETCH_PASSWORD;
				
			} else if (strJobKey.startsWith("S")) {
				
				strFetchPassword = CRSLoadmqProperties.QUERY_FETCH_PASSWORD_PRINT_JOB;
				
			} else {
				
				strFetchPassword = CRSLoadmqProperties.QUERY_FETCH_PASSWORD;
				
			}
			
			strFetchPassword = strFetchPassword.replace("<<SCHEMA_NAME>>", BoneCPDBPool.strSchemaName);
	        AppLogger.debug(strSquenceId + " : Query to Fetch password from DB : " +strFetchPassword );	        
	        
	        psFetchPassword = dbConn.prepareStatement(strFetchPassword);
	        AppLogger.debug(strSquenceId + " : JOBKEY IS "+strJobKey);
	        psFetchPassword.setString(1, strJobKey);
	        rsFectchPassword = psFetchPassword.executeQuery();
	        
	        if (rsFectchPassword.next()) {
	            
	        	strPassword = rsFectchPassword.getString(1) == null ? "" : rsFectchPassword.getString(1);
	        	
	            if (!strPassword.isEmpty()) {
	            	//actualpass = CRSLoadmqProperties.ispassEncrypted.equalsIgnoreCase("true") ? new CAuthenticate().Decrypt(strPassword) : strPassword;
	            	actualpass = strPassword;
	            }
	            
	            if(!actualpass.isEmpty()){
	            	
	            	if (!passWordInRequest.trim().equals(actualpass)) {
		            	
		            	actualpass = "";
		            	
		            } 
	            	
	            }       
	            
	        }else{
	        	
	        	AppLogger.info(strSquenceId + " password not exists" ); 
	        	
	        }
	        
		} catch (SQLException sqlEx) {
			
			AppLogger.error(strSquenceId+" : " +CErrorCodes.INTERNAL_SQLEXCEPTION+" : SQLException occurred while fetching the password : ",sqlEx);
			
		} catch (Exception sqlEx) {
			
			AppLogger.error(strSquenceId+" : " +CErrorCodes.INTERNAL_EXCEPTION+" : Exception occurred while fetching the password : ",sqlEx);
			
		}
		
		return actualpass;
		
	}
	
	public static void deeplinkAuditMaster(Connection con, JSONObject auditInfo, String strfilePatgh, String seqid, String strredId) {
        
        PreparedStatement psAudit = null;
        String strAuditQuery = CRSLoadmqProperties.QUERY_DEEPLINK_AUDIT;
        strAuditQuery = strAuditQuery.replace("<<SCHEMA_NAME>>", BoneCPDBPool.strSchemaName);
        
        try {
            
        	AppLogger.info(seqid + " : Audit info :--->" + auditInfo);
            psAudit = con.prepareStatement(strAuditQuery);
            psAudit.setString(1, auditInfo.optString("REQUESTID"));
            psAudit.setString(2, auditInfo.optString("JOBKEY"));
            psAudit.setString(3, auditInfo.optString("FORMAT"));
            psAudit.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            psAudit.setString(5, auditInfo.optString("STATUS"));
            psAudit.setString(6, auditInfo.optString("COMMENTS"));
            psAudit.setString(7, strfilePatgh);
            psAudit.setString(8, auditInfo.optString("browserName"));
            psAudit.setString(9, auditInfo.optString("browserVersion"));
            psAudit.setString(10, auditInfo.optString("ipAddr"));
            int naudit = psAudit.executeUpdate();
            
            if (naudit == 1) {
               
            	AppLogger.info(seqid + " : Successfully audit the Request ");

            } else {
                
            	AppLogger.info(seqid + " : error occured while auditing the status");

            }

        } catch (SQLException sqlex) {

        	AppLogger.error(seqid + " : "+CErrorCodes.INTERNAL_SQLEXCEPTION+" : SQLException occurred while auditing the request data : ",sqlex);

        } catch (Exception ex) {
        	
        	AppLogger.error(seqid + " : "+CErrorCodes.INTERNAL_EXCEPTION+" : Exception occurred while auditing the request data : ",ex);

        } finally {
           
        	closePreparedStatement(psAudit);
        	
        }

    }
	
	
	/**
	 * @param dbConn
	 * @param strSessionId
	 * @param strSEQID
	 * @param requestTimeStamp
	 * @return
	 */
	public boolean validateSessionTime(Connection dbConn, String strSessionId,String strSEQID,java.sql.Timestamp requestTimeStamp){
		
		boolean bSessionStatus = false;
		PreparedStatement ps_ExpiryIime = null;
		ResultSet rs_ExpiryIime = null;
		String strQuery = CRSLoadmqProperties.QUERY_SESSION_EXPIRY;
		java.sql.Timestamp expiryTimeStamp = null;
		
		AppLogger.info(strSEQID+" : Fetching the Session Expiry time");
		
		try {
			
			strQuery = strQuery.replace("<<SCHEMA_NAME>>", BoneCPDBPool.strSchemaName);
			AppLogger.debug(strSEQID+" : Query to fetch the session expiry time : "+strQuery);
			ps_ExpiryIime = dbConn.prepareStatement(strQuery);
			ps_ExpiryIime.setString(1, strSessionId);		
			rs_ExpiryIime = ps_ExpiryIime.executeQuery();
			
			while (rs_ExpiryIime.next()) {
				
				expiryTimeStamp = rs_ExpiryIime.getTimestamp(1);
				
			}
			AppLogger.info(strSEQID+ " : Session expiry time is : "+expiryTimeStamp+ " and request initiated at : "+requestTimeStamp);
			if (requestTimeStamp.before(expiryTimeStamp)) {

				bSessionStatus = true;

			} 
			
		} catch (SQLException sqlEx) {
			
			AppLogger.error(strSEQID+" : "+CErrorCodes.INTERNAL_SQLEXCEPTION+" : SQLException occurred while validating session expiry time", sqlEx);
			
		} catch (Exception ex) {
			
			AppLogger.error(strSEQID+" : "+CErrorCodes.INTERNAL_EXCEPTION+" : Exception occurred while validating session expiry time", ex);
		
		} finally {
			
			closeResultset(rs_ExpiryIime);
			closePreparedStatement(ps_ExpiryIime);
			
		}
		
		return bSessionStatus;
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

	public static void closeStatement(Statement statement) {
		
		try {

			if (statement != null) {

				statement.close();
				statement = null;

			}

		} catch (SQLException sqlEx) {

			AppLogger.error(CErrorCodes.INTERNAL_SQLEXCEPTION+" : SQLException occurred while closing statement : ", sqlEx);

		} catch (Exception ex) {

			AppLogger.error(CErrorCodes.INTERNAL_EXCEPTION+" : Exception occurred while closing statement : ", ex);

		}

	}

}
