package com.in10s.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.log4j.Logger;

import com.in10s.applog.AppLogger;
import com.in10s.appprop.CErrorCodes;
import com.in10s.appprop.CRSHttpErrorMsgs;
import com.in10s.appprop.CRSLoadmqProperties;
import com.in10s.dao.BoneCPDBPool;
import com.in10s.dao.CAuthenticate;
import com.in10s.dao.DataBaseProcess;
import com.in10s.utils.Base64;
import com.in10s.utils.C_AES_256;
import com.in10s.yes.CRSGetTime;

import eu.bitwalker.useragentutils.UserAgent;
import eu.bitwalker.useragentutils.Version;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

@Path("/statement")
public class GenerateHtmlStatement {

	 static Logger log = Logger.getLogger(GenerateHtmlStatement.class);
	
	
	@Context
    private UriInfo context;

    @Context
    private HttpServletRequest request;
    

	@GET
	@Path("/generatehtml")
	@Produces("text/html")
	public Response getResultByPassingValue(@QueryParam("params") String requestparams) {

		String strJobKey = "";
		String strSessionId = "";
		String reqdataafterDecoding = "";
		String strProcessFlag = "";
		String comments = "";
//		String strXmlFilePath = "";
		String strSEQID= "";
		String format = "";
		String strFilePath = "";
		String jobkeyRegEx = "";
		String splitWith = "";
        
		JSONObject auditInfo = new JSONObject();
		JSONObject httpErrorsmsgs = null;
		JSONObject jsonJobcmdReplacewith = null;
               
		FileInputStream fileInputStream = null;

		DataBaseProcess dbOperation = new DataBaseProcess();;
		RunUniserverDaemonJob runDemon = null;

		Connection dbConn = null;
		 PreparedStatement pstmt = null;
         ResultSet rs = null;
		long currRequest = 0;
		
		java.sql.Timestamp requestTimeStamp = null;
		
		try {
			
			requestTimeStamp = new java.sql.Timestamp(new java.util.Date().getTime());
			
			comments = "Request is initiated at " + getDatetimeinMillis();
			AppLogger.info(comments);
			auditInfo = getBrowserDetails();
			auditInfo.put("STATUS", "-1");
			
			httpErrorsmsgs = CRSHttpErrorMsgs.getErrorcodes();

			if (dbConn == null) {

				dbConn = BoneCPDBPool.getConnection();

			}

			if (dbConn != null) {

//				dbOperation = new DataBaseProcess();
				strSEQID = dbOperation.internalHtmlSeqId(dbConn);
				format = CRSLoadmqProperties.statment_Format;
				
				strSEQID = "INT"+strSEQID; // INT is appended to the sequence_id to differentiate the request in audit table
				auditInfo.put("REQUESTID", strSEQID);
				
				if(!requestparams.isEmpty()) {
					
				reqdataafterDecoding = decodeAND_DecryptSring(requestparams.trim(),strSEQID);
				
				if(!reqdataafterDecoding.isEmpty()) {
				
				splitWith = CRSLoadmqProperties.special_Char_To_Split;
				AppLogger.info( strSEQID+" special character to split he request data is :"+splitWith);
				splitWith = "\\"+splitWith;
				
				String[] keyValuePairs = reqdataafterDecoding.split(splitWith);

				
				Map<String, String> map = new HashMap<>();

				for (String pair : keyValuePairs) {
					String[] entry = pair.split("=");

					map.put(entry[0].trim(), entry[1].trim());

				}
				
				strJobKey = map.get("jobkey");
				strSessionId = map.get("session_id");
				
				AppLogger.debug(strSEQID+" : In Request Data where jobkey is "+strJobKey+ " and SessionId is "+strSessionId);
				
		        if (validateParam(strJobKey)) {
		        
		        	jobkeyRegEx = CRSLoadmqProperties.jobkeyregex;
		        	
		        	if (!jobkeyRegEx.equalsIgnoreCase("NO")) {
		        		
		        		String strSplited_JobKey = strJobKey.substring(1,strJobKey.length());
		        		
			            if (!validateKey(strSplited_JobKey, jobkeyRegEx, strSEQID, "")) {

			                comments = "invalid jobkey ";
//			                String strcomments = strSEQID + " : " + comments;
			                AppLogger.info(strSEQID + " : "+CErrorCodes.INVALID_JOBKEY+" : " + comments);
			                auditInfo.put("COMMENTS", comments);
			                AppLogger.info(strSEQID + ":  Service ends at " + getDatetimeinMillis());
			                return Response.status(417).entity(httpErrorsmsgs.optString("INVALIDPARAMS")).build();
			            }
			            
			        } else {
			        	
			        	AppLogger.info(strSEQID + ": Regular expression for validating job key is not configured");
			        	
			        }
		        	
					try {
						
				        auditInfo.put("JOBKEY", strJobKey);
				        auditInfo.put("FORMAT", format); 
						
							if (validateParam(strSessionId)) {
	
								boolean bSenExpiryStatus = dbOperation.validateSessionTime(dbConn,strSessionId,strSEQID,requestTimeStamp);
								
								
								if(bSenExpiryStatus) {
									
									AppLogger.info(strSEQID+ " : Session time validation completed");
									strProcessFlag = dbOperation.validateSessionId(dbConn, strSessionId,strSEQID); // validation Session Id
	
									if (!strProcessFlag.isEmpty()) {
	
										AppLogger.info(strSEQID+ " : Session id validation completed");
									
										if (!strProcessFlag.equals("1")) {
	
										// updating the process falg
											if (dbOperation.upDateProcessFlag(dbConn, strSessionId,strSEQID,"1","0")) {
	
									            String jobCommand = "";
									            
									            if (format.equalsIgnoreCase("HTML")) { // not needed this condition remove
	
									                jobCommand = CRSLoadmqProperties.html_job_command;
	
									            } 
									            // newly added
	//								            String mof_key = "";
	//								            String MOF_KEY_FetchQuery = CRSLoadmqProperties.strFetchMOFKEY;
	//								            pstmt = dbConn.prepareStatement(MOF_KEY_FetchQuery);
	//								            pstmt.setString(1, jobKey);
	//								            rs = pstmt.executeQuery();
	//								            if (rs.next()) {
	//								                mof_key = rs.getString("MOF_KEY");
	//								                AppLogger.debug(strSEQID + ": MOF_KEY : " + mof_key + " : Job_Key" + jobKey);
	//								            } else {
	//								            	AppLogger.debug(strSEQID + ": MOF_KEY : " + mof_key + " is empty for this job key : Job_Key" + jobKey);
	//								            }
									            //
									            
									            AppLogger.debug(strSEQID + " : job command is : " + jobCommand);
									            jsonJobcmdReplacewith = new JSONObject();
									            jsonJobcmdReplacewith = (JSONObject) JSONSerializer.toJSON(CRSLoadmqProperties.JSON_REPLACEING_VALUES_INJOB_COMMOND);
									            
									            try{
									            	
										            if (!jobCommand.isEmpty()) {
										            	
										                String query = "";
										                
										                if(strJobKey.startsWith("M")) {
										                	
										                	 query = CRSLoadmqProperties.query;
										                	 
										                } else if (strJobKey.startsWith("S")){
										                	
										                	query = CRSLoadmqProperties.query_print;
										                	
										                } 
														/*else {
										                	
										                	query = CRSLoadmqProperties.query;
										                	
										                }*/
										                
										                query = query.replace("<<SCHEMA_NAME>>", BoneCPDBPool.strSchemaName);
		
										                AppLogger.debug(strSEQID+" : Query to fetch data to send in uniserve job command : "+query);
										                
										                int n = query.indexOf("from");
										                String strQuery = query.substring(6, n);
		
										                AppLogger.debug(strSEQID + " : sub query is :" + strQuery.trim());
										                String[] strcolArry = strQuery.trim().split(",");
		
										                pstmt = dbConn.prepareStatement(query);
										                pstmt.setString(1, strJobKey.trim());
		
										                rs = pstmt.executeQuery();
		
										                if (rs.next()) {
										                    int n1 = 0;
										                    String strjobparameters = "";
										                    for (int i = 0; i < strcolArry.length; i++) {
		
										                        strjobparameters = strjobparameters + strcolArry[i] + " , ";
		
										                        String jobparams = "";
										                        
										                        if (jobCommand.contains(jsonJobcmdReplacewith.optString("input_file"))) {
										                        	
										                            if (i == 0) {
										                            	
										                                n1++;
										                                String strFile = rs.getString(strcolArry[i]);
										                                if (strFile == null) {
										                                    strFile = "";
										                                }
		
		//								                                if (new File(strFile).exists()) {//not required
										                                    jobCommand = jobCommand.replace(jsonJobcmdReplacewith.optString("input_file"), strFile);
		//								                                } else {
										//
		//								                                    comments = strFile + " input  file not existed : ";
		//								                                    String strcomments = strSEQID + " : " + comments;
		//								                                    AppLogger.info(strcomments);
		//								                                    auditInfo.put("COMMENTS", comments);
		//								                                    dbOperation.deeplinkAuditMaster(dbConn, auditInfo, strFilePath, strSEQID, "");
		//								                                    AppLogger.info(strSEQID + ": Service ends at " + getDatetimeinMillis());
										//
		//								                                    return Response.status(417).entity(httpErrorsmsgs.optString("FILENOTAVAIL")).build();
		//								                                }
										                            }
										                            
										                        } else {
										                            int m = 0;
										                            if (n1 == 0) {
										                                m = i + 2;
										                            } else {
										                                m = i + 1;
										                            }
										                            jobparams = "<<col" + m + ">>";
										                            if (rs.getString(strcolArry[i]) == null) {
										                            	
										                                comments = "can't replace jobcommand with null values of " + strcolArry[i] + "column";
										                                String strcomments = strSEQID + " : " + comments;
										                                AppLogger.info(strcomments);
										                                auditInfo.put("COMMENTS", comments);
										                                AppLogger.info(strSEQID + ": Service ends at " + getDatetimeinMillis());
		
										                                return Response.status(417).entity(httpErrorsmsgs.optString("INVALIDJOBPARAMS")).build();
										                            
										                            }
										                            
										                            jobCommand = jobCommand.replace(jobparams, rs.getString(strcolArry[i]));
		
										                        }
										                    }
		
										                    AppLogger.debug(strSEQID + " : Job command parameters are : " + strjobparameters);
		
										                    jobCommand = jobCommand.replace(jsonJobcmdReplacewith.optString("job_key"), strJobKey);
		//								                    jobCommand = jobCommand.replace("<<mofkey>>", mof_key); //newly added
										                    jobCommand = jobCommand.replace(jsonJobcmdReplacewith.optString("req_id"), strSEQID);
		
										                    String ordid = CRSLoadmqProperties.orgid;
		
										                    AppLogger.info(strSEQID + " : final job command after replacing all the column values : " + jobCommand);
		
										                    String strError_Details = "CONTINUE_WORKER$^$Invalid input file mapped for processing@!@Error1$^$Error Response Message1@!@Error2$^$Error Response Message";
										                    
										                    runDemon = new RunUniserverDaemonJob();
										                    
										                    String strResponse = runDemon.RunDaemonJob(jobCommand, currRequest, true, ordid, strSEQID,log);
		
										                    AppLogger.info(strSEQID + ": Tracking Response : " + strResponse);
		
										                    if (strResponse.isEmpty()) {
		
										                        comments = "Internal Error occured";
										                        String strcomments = strSEQID + " : " + comments;
										                        AppLogger.info(strcomments);
										                        auditInfo.put("COMMENTS", comments);
										                        AppLogger.info(strSEQID + ": Service ends at " + getDatetimeinMillis());
										                        dbOperation.upDateProcessFlag(dbConn, strSessionId,strSEQID,"0","1");
										                        return Response.status(417).entity(httpErrorsmsgs.optString("INTERNAL")).build();
		//								       
										                    } else {
		
										                        String[] Error_Details = strError_Details.split("\\@\\!\\@");
										                        Map<String, String> map_Error = new HashMap<String, String>();
										                        String strErrorKey = "";
										                        String strErrorValue = "";
		
										                        for (int nIndex = 0; nIndex < Error_Details.length; ++nIndex) {
		
										                            if (!Error_Details[nIndex].isEmpty() && Error_Details[nIndex].split("\\$\\^\\$").length == 2) {
										                                strErrorKey = Error_Details[nIndex].split("\\$\\^\\$")[0];
										                                strErrorValue = Error_Details[nIndex].split("\\$\\^\\$")[1];
										                                map_Error.put(strErrorKey, strErrorValue);
										                            }
										                        }
		
										                        String[] responseArr = strResponse.split("\1");
										                        int nCount = responseArr.length;
										                        AppLogger.debug(strSEQID + " : response count : " + nCount);
										                        if (nCount > 3) {
		
										                            String str_Response_Status = responseArr[1];
										                            String str_Response = responseArr[3];
		
										                            if (str_Response_Status.equals("3") && !str_Response.isEmpty() && !map_Error.containsKey(str_Response)) {
		
										                                String strOutputPath = responseArr[3]; // JobKey
										                                AppLogger.info(strSEQID + " :Resultant OutputPath: " + strOutputPath);
		
										                                if (!strOutputPath.isEmpty()) {
										                                    if (!strOutputPath.toLowerCase().endsWith(format.toLowerCase())) {
										                                    	 
										                                        comments = "mismatch file type and format ";
		//								                                        String strcomments = strSEQID + " : "+CErrorCodes.MISMATCH_FILE_FORMAT+" : " + comments;
										                                        AppLogger.info(strSEQID + " : "+CErrorCodes.MISMATCH_FILE_FORMAT+" : " + comments);
										                                        auditInfo.put("COMMENTS", comments);
										                                        AppLogger.info(strSEQID + ": Service ends at " + getDatetimeinMillis());
										                                        dbOperation.upDateProcessFlag(dbConn, strSessionId,strSEQID,"0","1");
										                                        return Response.status(417).entity(httpErrorsmsgs.optString("MISMATCHFORMAT")).build();
										                                    }
										                                    
										                                    strFilePath = strOutputPath;
										                                    AppLogger.debug(strSEQID + " : HTML path is " + strOutputPath);
		
										                                    File file = new File(strOutputPath);
										                                    if (file.exists()) {
		
										                                        String strcomments = "success";
										                                        AppLogger.info(strSEQID + " : success");
		
										                                        auditInfo.put("COMMENTS", strcomments);
										                                        auditInfo.put("STATUS", "1");
										                                        fileInputStream = new FileInputStream(file);
										                                        AppLogger.info(strSEQID + ": Service ends at " + getDatetimeinMillis());
										                                        javax.ws.rs.core.Response.ResponseBuilder responseBuilder = javax.ws.rs.core.Response.ok((Object) fileInputStream);
										                                        responseBuilder.type(MediaType.TEXT_HTML);
										                                        responseBuilder.header("Content-Disposition", "filename=" + file.getName());
										                                        // }
										                                        return responseBuilder.build();
										                                    } else {
		
										                                        comments = " output path not existed";
										                                        AppLogger.info(strSEQID + " : "+CErrorCodes.OUTPUT_FILE_PATH_NOT_EXIST+" : " + comments);
										                                        auditInfo.put("COMMENTS", comments);
										                                        AppLogger.info(strSEQID + ": Service ends at " + getDatetimeinMillis());
										                                        dbOperation.upDateProcessFlag(dbConn, strSessionId,strSEQID,"0","1");
										                                        return Response.status(417).entity(httpErrorsmsgs.optString("INTERNAL")).build();
										                                    }
										                                } else {
		
										                                    comments = " empty output path ";
										                                    AppLogger.info(strSEQID + " : "+CErrorCodes.OUTPUT_FILE_PATH_IS_EMPTY+" : " + comments);
										                                    auditInfo.put("COMMENTS", comments);
										                                    AppLogger.info(strSEQID + ": Service ends at " + getDatetimeinMillis());
										                                    dbOperation.upDateProcessFlag(dbConn, strSessionId,strSEQID,"0","1");
										                                    return Response.status(417).entity(httpErrorsmsgs.optString("INTERNAL")).build();
										                                }
										                            } else {
		
										                                comments = " Internal Error occured";
										                                AppLogger.info(strSEQID + " : "+CErrorCodes.UNISERVE_INCORRECT_RESPONSE+" : " +  comments);
										                                auditInfo.put("COMMENTS", comments);
										                                AppLogger.info(strSEQID + ": Service ends at " + getDatetimeinMillis());
										                                dbOperation.upDateProcessFlag(dbConn, strSessionId,strSEQID,"0","1");
										                                return Response.status(417).entity(httpErrorsmsgs.optString("INTERNAL")).build();
		
										                            }
										                        } else {
		
										                            comments = "Invalid response received while processing request :";
										                            AppLogger.info(strSEQID + " : "+CErrorCodes.UNISERVE_INCORRECT_RESPONSE+" : " + comments);
										                            auditInfo.put("COMMENTS", comments);
										                            AppLogger.info(strSEQID + ": Service ends at " + getDatetimeinMillis());
										                            dbOperation.upDateProcessFlag(dbConn, strSessionId,strSEQID,"0","1");
										                            return Response.status(417).entity(httpErrorsmsgs.optString("UNISERVEFAIL")).build();
										                        
										                        }
										                    }
										                    
										                } else {
		
										                    comments = " none of record is available with this" + strJobKey + " job key";
										                    AppLogger.info(strSEQID + " : "+CErrorCodes.DATA_NOT_AVAILABLE+" : " + comments);
										                    auditInfo.put("COMMENTS", comments);
										                    AppLogger.info(strSEQID + ": Service ends at " + getDatetimeinMillis());
										                    dbOperation.upDateProcessFlag(dbConn, strSessionId,strSEQID,"0","1");
										                    return Response.status(417).entity(httpErrorsmsgs.optString("NOKEY")).build();
										                    
										                }
										            } else {
	
									                comments = " html jobcommand is empty";
									                AppLogger.info(strSEQID + " : "+CErrorCodes.EMPTY_JOB_COMMAND+" : " + comments);
									                auditInfo.put("COMMENTS", comments);
									                AppLogger.info(strSEQID + ": Service ends at " + getDatetimeinMillis());
									                dbOperation.upDateProcessFlag(dbConn, strSessionId,strSEQID,"0","1");
									                return Response.status(417).entity(httpErrorsmsgs.optString("EMPTYJOBCOMM")).build();
									            }
									            
											} catch (FileNotFoundException fileNotFndEx){
												
												comments = "Internal Error occurred";
												auditInfo.put("COMMENTS", comments);
												AppLogger.error(CErrorCodes.INTERNAL_IOEXCEPTION + "FileNotFoundException occurred while Processing the Job with Uniserve : ",fileNotFndEx);
												dbOperation.upDateProcessFlag(dbConn, strSessionId,strSEQID,"0","1");
												return Response.status(417).entity(httpErrorsmsgs.optString("INTERNAL")).build();
											
											} catch (SQLException sqlEx){
												
												comments = "Internal Error occurred";
												auditInfo.put("COMMENTS", comments);
												AppLogger.error(CErrorCodes.INTERNAL_SQLEXCEPTION + "SQLException occurred while Processing the Job with Uniserve : ",sqlEx);
												dbOperation.upDateProcessFlag(dbConn, strSessionId,strSEQID,"0","1");
												return Response.status(417).entity(httpErrorsmsgs.optString("INTERNAL")).build();
											
											}   catch (Exception ex){
												
												comments = "Internal Error occurred";
												auditInfo.put("COMMENTS", comments);
												AppLogger.error(CErrorCodes.INTERNAL_EXCEPTION + "Exception occurred while Processing the Job with Uniserve : ",ex);
												dbOperation.upDateProcessFlag(dbConn, strSessionId,strSEQID,"0","1");
												return Response.status(417).entity(httpErrorsmsgs.optString("INTERNAL")).build();
											
											} finally {
												
												dbOperation.closeResultset(rs);
												dbOperation.closePreparedStatement(pstmt);
												
											}
	
										} else {
	
											comments = "Unable to Process the Job";
											auditInfo.put("COMMENTS", comments);
											AppLogger.info(strSEQID+" : "+CErrorCodes.REQUEST_ALREADYIN_PROCESS + " : " + comments);
											return Response.status(417).entity(httpErrorsmsgs.optString("REQUESTALREDYINPROCESS")).build();
										}
	
									} else {
	
										comments = "Request already in process";
										auditInfo.put("COMMENTS", comments);
										AppLogger.info(strSEQID+" : "+CErrorCodes.REQUEST_ALREADYIN_PROCESS + " : " + comments);
										return Response.status(417).entity(httpErrorsmsgs.optString("REQUESTALREDYINPROCESS")).build(); 
										
									}
	
								} else {
	
									comments = "Invalid Session Id";
									auditInfo.put("COMMENTS", comments);
									AppLogger.info(strSEQID+" : "+CErrorCodes.REQUEST_VALIDATION_FAILURE + " : " + comments);
									return Response.status(417).entity(httpErrorsmsgs.optString("SESSIONVALIDATION")).build(); 
								}
	
							} else {
								
								comments = "Session expired ";
								auditInfo.put("COMMENTS", comments);
								AppLogger.info(strSEQID+" : "+CErrorCodes.REQUEST_VALIDATION_FAILURE + " : " + comments);
								return Response.status(417).entity(httpErrorsmsgs.optString("SESSIONEXPIRED")).build(); 
								
							}
								
							} else {
	
								comments = "Session Id is empty or null";
								auditInfo.put("COMMENTS", comments);
								AppLogger.info(strSEQID+" : "+CErrorCodes.REQUEST_VALIDATION_FAILURE + " : " + comments);
								return Response.status(417).entity(httpErrorsmsgs.optString("SESSIONVALIDATION")).build();  
	
							}
	
						} catch (Exception ex) {

							comments = "Internal Error occurred";
							auditInfo.put("COMMENTS", comments);
							AppLogger.error(CErrorCodes.INTERNAL_EXCEPTION + strSEQID+" : Exception occurred while Processing the Request Data : ",ex);
							return Response.status(417).entity(httpErrorsmsgs.optString("INTERNAL")).build(); 

						}
				
		        	} else {

						comments = "JobKey is empty or null";
						auditInfo.put("COMMENTS", comments);
						AppLogger.info(strSEQID+" : "+CErrorCodes.REQUEST_VALIDATION_FAILURE + " : " + comments);
						return Response.status(417).entity(httpErrorsmsgs.optString("NULLORWMPTY")).build();
		        	}
		        
				} else {
					
					comments = "Decrypting the request Data is failed";
					auditInfo.put("COMMENTS", comments);
					AppLogger.info(strSEQID+" : "+CErrorCodes.DECRYPTION_FAILED + " : " + comments);
					return Response.status(417).entity(httpErrorsmsgs.optString("INTERNAL")).build();
					
				}
			} else {
				
				comments = "Request Data is empty";
				auditInfo.put("COMMENTS", comments);
				AppLogger.info(strSEQID+" : "+CErrorCodes.REQUEST_DATA_PROCESSING_FAILED + " : " + comments);
				return Response.status(417).entity(httpErrorsmsgs.optString("INTERNAL")).build();
				
			}
				
			} else {

				comments = "Unable to obtain the DB connection";
				auditInfo.put("COMMENTS", comments);
				AppLogger.info(strSEQID+" : "+CErrorCodes.DATABASE_CONNECTION_FAILURE + " : " + comments);
				return Response.status(417).entity(httpErrorsmsgs.optString("INTERNAL")).build(); 
				
			}

		} catch (Exception ex) {

			comments = "Internal Error occurred";
			auditInfo.put("COMMENTS", comments);
			AppLogger.error(strSEQID+" : "+CErrorCodes.INTERNAL_EXCEPTION + "Exception occurred while Obtaining the Data Base Connection :  ",ex);
			return Response.status(417).entity(httpErrorsmsgs.optString("INTERNAL")).build(); 

		} finally {
			
			dbOperation.deeplinkAuditMaster(dbConn, auditInfo, strFilePath, strSEQID, "");
			closeDBConnection(dbConn);
		}

		// return Response.status(200).entity("output").build();
	}

	
	
	 @POST
	 @Path("/htmlformat")
	 @Produces("text/html")
	 public Response postData(@FormParam("pwd") String pass, @FormParam("ke") String jobKey, @FormParam("seqence") String sequenceId) {
	  
    String formpassword1 = "";
    String formpassword = "";
    String strtokenid = "";
    String strFilePath = "";
    String comments = "";
    String strSEQID = "";
    String strLogStatement = "";
	String format = "";
	String timeinmiilis = "";
	String actualpass = "";
		
    Date date = null;
	
	FileInputStream fileInputStream = null;
	
	JSONObject auditInfo = new JSONObject();
    JSONObject httpErrorsmsgs = null;
    JSONObject jsonJobcmdReplacewith = null;

	Connection dbConn = null;
	CRSGetTime obj;
    RunUniserverDaemonJob runDemon = null;
    
    DataBaseProcess dbOperation = new DataBaseProcess();
    
    try {
    	
    	strLogStatement = "On-the-fly request for HTML file initiated at " + getDatetimeinMillis() + " for JobKey: " + jobKey;
        //AppLogger.info(strLogStatement);
    	
    	auditInfo = getBrowserDetails();
        format = CRSLoadmqProperties.statment_Format;
        
        jobKey = decodeAND_DecryptSring(jobKey, strSEQID);

        httpErrorsmsgs = CRSHttpErrorMsgs.getErrorcodes();
        
	     if(!jobKey.isEmpty()) {
	
	        auditInfo.put("STATUS", "-1");
	        auditInfo.put("JOBKEY", jobKey);
	        auditInfo.put("FORMAT", format); 
	        
	        obj = new CRSGetTime();
	        
	        
	        if(dbConn == null){
	        	
	        	dbConn = BoneCPDBPool.getConnection();
	        }
	        
	        if(dbConn != null){
	        
	        	strSEQID= sequenceId;
	        	if(strSEQID == null || strSEQID.isEmpty()){
	    	   
	        		strSEQID ="1234";
	           
	        	}
	        	auditInfo.put("REQUESTID", strSEQID);
	       
	
	        	AppLogger.debug(strSEQID + " : " + strLogStatement);
	
	        	if(validateParam(pass)){
	        	
	        		try {
	        			
	            	AppLogger.info(strSEQID + " : encrypted password with time and sequence token : " + pass);
	
	                formpassword1 = new CAuthenticate().Decrypt(pass);
	                formpassword1 = formpassword1.trim();
	
	                timeinmiilis = formpassword1.substring(0, 14);
	                strtokenid = formpassword1.substring(14, 42);
	                formpassword = formpassword1.substring(42, formpassword1.length());
	            	
	                date = obj.getTime1(timeinmiilis.trim());
	                AppLogger.debug(strSEQID + " : time when user clicks on 'GET STATEMENT' button  : " + date.toString() + "and sequence token id : " + strtokenid + " with jobkey = " + jobKey);
	
	        		} catch (Exception ex) {
	            	
	                comments = "Exception occurred while decryptring the password";
	                AppLogger.error(strSEQID+" : "+CErrorCodes.INTERNAL_EXCEPTION+" : "+comments,ex);
	                auditInfo.put("COMMENTS", comments);
	                AppLogger.info(strSEQID + ": Service ends at " + getDatetimeinMillis());
	                return Response.status(417).entity(httpErrorsmsgs.optString("INTERNAL")).build();
	                
	        		}
	        	
	        	} else {
	        	
	        		comments = "Service Failed to Make Ajax Call";
//	          	    String strcomments = strSEQID + " : " + comments;
	        		AppLogger.info(strSEQID + " : "+CErrorCodes.REQUEST_VALIDATION_FAILURE+" : " + comments);
	        		auditInfo.put("COMMENTS", comments);
	        		AppLogger.info(strSEQID + ": Service ends at " + getDatetimeinMillis());
	        		return Response.status(401).entity(httpErrorsmsgs.optString("AJAXFAIL")).build();
	        	
	        	}
	        
	        
	        	String strFormatedDate = obj.getformattedTime(new Date());
	
	        	Date currentdate = obj.getTime1(strFormatedDate);
	        
	        	if (date == null) {
	        	
	        		date = currentdate;
	            
	        	}
	        
		        AppLogger.info(strSEQID + " : current time is  : " + currentdate.toString());
		
		        long timediffrence = currentdate.getTime() - date.getTime();
		        AppLogger.info(strSEQID + " : time diffrence between login time and current time is : " + timediffrence);
		
		        String strTokenTime = CRSLoadmqProperties.tokentime.trim().isEmpty() ? "15000" : CRSLoadmqProperties.tokentime.trim();
		        AppLogger.debug(strSEQID + " : token delay time is : " + strTokenTime);
		        long ltime = Long.parseLong(strTokenTime);
		        String strtokenenable = CRSLoadmqProperties.tokenenable.trim();
		        AppLogger.debug(strSEQID + " : Token Enable flag is : " + strtokenenable);
	
	        //o-disable,1-enable
		        if (strtokenenable.equalsIgnoreCase("1")) {
		
		            if (ltime <= timediffrence) {
		            	
		                comments = "Invalid Token , because service did't get request in configurable token time(" + strTokenTime + " milli seconds)";
		//                String strcomments = strSEQID + " : "+CErrorCodes.INVALID_TOKEN+" : " + comments;
		                AppLogger.info(strSEQID + " : "+CErrorCodes.INVALID_TOKEN+" : " + comments);
		                auditInfo.put("COMMENTS", comments);
		                AppLogger.info(strSEQID + ": Service ends at " + getDatetimeinMillis() + " ERROR Message displayed on browser: " + httpErrorsmsgs.optString("INVALIDTOKEN"));
		                return Response.status(417).entity(httpErrorsmsgs.optString("INVALIDTOKEN")).build();
		            
		            }
		        } else {
	        	
		        	AppLogger.debug(strSEQID + " : Request Token is disabled ");
	        	
		        }
	
		        long currRequest = 0;
	        
		        if (jobKey == null || format == null) {
		
		            comments = "job key and format keys are null values";
		//            String strcomments = strSEQID + " : " + comments;
		            AppLogger.info(strSEQID + " : "+CErrorCodes.INVALID_JOBKEY_OR_FORMAT+" : " + comments);
		            auditInfo.put("COMMENTS", comments);
		            AppLogger.info(strSEQID + ": Service ends at " + getDatetimeinMillis());
		            return Response.status(417).entity(httpErrorsmsgs.optString("NULLINPUT")).build();
	
		        } else if (jobKey.isEmpty() || format.isEmpty()) {
	
		            comments = "empty jobkey or empty format ";
		//            String strcomments = strSEQID + " : " + comments;
		            AppLogger.info(strSEQID + " : "+CErrorCodes.INVALID_JOBKEY_OR_FORMAT+" : " + comments);
		            auditInfo.put("COMMENTS", comments);
		            AppLogger.info(strSEQID + ": Service ends at " + getDatetimeinMillis());
		            return Response.status(417).entity(httpErrorsmsgs.optString("EMPTYINPUT")).build();
	
		        }
	        
		        String jobkeyRegEx = CRSLoadmqProperties.jobkeyregex;
	        
		        if (!jobkeyRegEx.equalsIgnoreCase("NO")) {
		        	
		        	String strSplited_JobKey = jobKey.substring(1,jobKey.length());
	        	
		        	if (!validateKey(strSplited_JobKey, jobkeyRegEx, strSEQID, "")) {
	
		                comments = "invalid jobkey ";
		//                String strcomments = strSEQID + " : " + comments;
		                AppLogger.info(strSEQID + " : "+CErrorCodes.INVALID_JOBKEY+" : " + comments);
		                auditInfo.put("COMMENTS", comments);
		                AppLogger.info(strSEQID + ":  Service ends at " + getDatetimeinMillis());
		                return Response.status(417).entity(httpErrorsmsgs.optString("INVALIDPARAMS")).build();
		        	}
		        } else {
	        	
		        	AppLogger.info(strSEQID + ": Regular expression for validating job key is not configured");
	        	
		        }
	
		        actualpass = dbOperation.fetchPassword(dbConn, strSEQID,jobKey,formpassword);
	        
	        
		        if(actualpass.isEmpty()){
	        	
		            comments = "Invalid Authentication";
		
		//            String strcomments = strSEQID + " : " + comments;
		            AppLogger.info(strSEQID + ": "+CErrorCodes.AUTHENTICATION_FAILURE+" : " + comments);
		            auditInfo.put("COMMENTS", comments);
		            AppLogger.info(strSEQID + ": Service ends at " + getDatetimeinMillis());
		            
		            return Response.status(401).entity(httpErrorsmsgs.optString("INVALIDPASS")).build();
		        }
	        
	        if (format.equalsIgnoreCase("HTML")) {
	
	            String jobCommand = "";
	            //  String strTemplate = "";
	//            if (format.equalsIgnoreCase("HTML")) {
	
	                jobCommand = CRSLoadmqProperties.html_job_command;
	
	//            }
	            
	            jsonJobcmdReplacewith = (JSONObject) JSONSerializer.toJSON(CRSLoadmqProperties.JSON_REPLACEING_VALUES_INJOB_COMMOND);
	            // newly added
	            PreparedStatement pstmt = null;
	            ResultSet rs = null;
	//            String mof_key = "";
	//            String MOF_KEY_FetchQuery = CRSLoadmqProperties.strFetchMOFKEY;
	//            pstmt = dbConn.prepareStatement(MOF_KEY_FetchQuery);
	//            pstmt.setString(1, jobKey);
	//            rs = pstmt.executeQuery();
	//            if (rs.next()) {
	//                mof_key = rs.getString("MOF_KEY");
	//                AppLogger.debug(strSEQID + ": MOF_KEY : " + mof_key + " : Job_Key" + jobKey);
	//            } else {
	//            	AppLogger.debug(strSEQID + ": MOF_KEY : " + mof_key + " is empty for this job key : Job_Key" + jobKey);
	//            }
	            //
	            AppLogger.info(strSEQID + " : job command is : " + jobCommand);
	            
	            if (!jobCommand.isEmpty()) {
	            	
	                String query = "";
	                
	                if(jobKey.startsWith("M")) {
	                	
	                	query = CRSLoadmqProperties.query;
	                	
	                } else if (jobKey.startsWith("S")){
	                	
	                	query = CRSLoadmqProperties.query_print;
	                	
	                } 
					/*else {
	                	
	                	query = CRSLoadmqProperties.query;
	                	
	                }*/
	                
	                query = query.replace("<<SCHEMA_NAME>>", BoneCPDBPool.strSchemaName);
	                int n = query.indexOf("from");
	                String strQuery = query.substring(6, n);
	
	                AppLogger.info(strSEQID + " :sub query is :" + strQuery.trim());
	                String[] strcolArry = strQuery.trim().split(",");
	
	                pstmt = dbConn.prepareStatement(query);
	                pstmt.setString(1, jobKey.trim());
	
	                rs = pstmt.executeQuery();
	
	                if (rs.next()) {
	                	
	                    int n1 = 0;
	                    String strjobparameters = "";
	                    for (int i = 0; i < strcolArry.length; i++) {
	
	                        strjobparameters = strjobparameters + strcolArry[i] + " , ";
	
	                        String jobparams = "";
	                        if (jobCommand.contains(jsonJobcmdReplacewith.optString("input_file"))) {
	                            if (i == 0) {
	                                n1++;
	                                String strFile = rs.getString(strcolArry[i]);
	                                if (strFile == null) {
	                                    strFile = "";
	                                }
	                                
	//                                if (new File(strFile).exists()) {
	                                    jobCommand = jobCommand.replace(jsonJobcmdReplacewith.optString("input_file"), strFile);
	//                                } else {
	//
	//                                    comments = strFile + " input  file not existed : ";
	//                                    String strcomments = strSEQID + " : " + comments;
	//                                    AppLogger.info(strcomments);
	//                                    auditInfo.put("COMMENTS", comments);
	//                                    dbOperation.deeplinkAuditMaster(dbConn, auditInfo, strFilePath, strSEQID, "");
	//                                    AppLogger.info(strSEQID + ": Service ends at " + getDatetimeinMillis());
	//
	//                                    return Response.status(417).entity(httpErrorsmsgs.optString("FILENOTAVAIL")).build();
	//                                }
	                            }
	                        } else {
	                            int m = 0;
	                            if (n1 == 0) {
	                                m = i + 2;
	                            } else {
	                                m = i + 1;
	                            }
	                            jobparams = "<<col" + m + ">>";
	                            if (rs.getString(strcolArry[i]) == null) {
	                                comments = "can't replace jobcommand with null values of " + strcolArry[i] + "column";
	                                String strcomments = strSEQID + " : " + comments;
	                                AppLogger.info(strcomments);
	                                auditInfo.put("COMMENTS", comments);
	                               // dbOperation.deeplinkAuditMaster(dbConn, auditInfo, strFilePath, strSEQID, "");
	                                AppLogger.info(strSEQID + ": Service ends at " + getDatetimeinMillis());
	
	                                return Response.status(417).entity(httpErrorsmsgs.optString("INVALIDJOBPARAMS")).build();
	                            }
	                            jobCommand = jobCommand.replace(jobparams, rs.getString(strcolArry[i]));
	
	                        }
	                    }
	
	                    AppLogger.debug(strSEQID + " : Job command parameters are : " + strjobparameters);
	
	                    jobCommand = jobCommand.replace(jsonJobcmdReplacewith.optString("job_key"), jobKey);
	//                    jobCommand = jobCommand.replace("<<mofkey>>", mof_key); //newly added
	                    jobCommand = jobCommand.replace(jsonJobcmdReplacewith.optString("req_id"), strSEQID);
	                    
	                    String ordid = CRSLoadmqProperties.orgid;
	
	                    AppLogger.info(strSEQID + " : final job command after replaceing all the column values : " + jobCommand);
	
	                    String strError_Details = "CONTINUE_WORKER$^$Invalid input file mapped for processing@!@Error1$^$Error Response Message1@!@Error2$^$Error Response Message";
	                    
	                    runDemon = new RunUniserverDaemonJob();
	                    
	                    String strResponse = runDemon.RunDaemonJob(jobCommand, currRequest, true, ordid, strSEQID,log);
	
	                    AppLogger.info(strSEQID + ": Tracking Response : " + strResponse);
	
	                    if (strResponse.isEmpty()) {
	
	                        comments = "Internal Error occured";
	                        String strcomments = strSEQID + " : " + comments;
	                        AppLogger.info(strcomments);
	                        auditInfo.put("COMMENTS", comments);
	                        //dbOperation.deeplinkAuditMaster(dbConn, auditInfo, strFilePath, strSEQID, "");
	                        AppLogger.info(strSEQID + ": Service ends at " + getDatetimeinMillis());
	                        return Response.status(417).entity(httpErrorsmsgs.optString("INTERNAL")).build();
	//       
	                    } else {
	
	                        String[] Error_Details = strError_Details.split("\\@\\!\\@");
	                        Map<String, String> map_Error = new HashMap<String, String>();
	                        String strErrorKey = "";
	                        String strErrorValue = "";
	
	                        for (int nIndex = 0; nIndex < Error_Details.length; ++nIndex) {
	
	                            if (!Error_Details[nIndex].isEmpty() && Error_Details[nIndex].split("\\$\\^\\$").length == 2) {
	                                strErrorKey = Error_Details[nIndex].split("\\$\\^\\$")[0];
	                                strErrorValue = Error_Details[nIndex].split("\\$\\^\\$")[1];
	                                map_Error.put(strErrorKey, strErrorValue);
	                            }
	                        }
	
	                        String[] responseArr = strResponse.split("\1");
	                        int nCount = responseArr.length;
	                        AppLogger.debug(strSEQID + " : response array : " + responseArr.toString());
	                        AppLogger.debug(strSEQID + " : response count : " + nCount);
	                        
	                        for (int i = 0; i < responseArr.length; i++) {
	                        	AppLogger.debug(i+" : "+responseArr[i]);
							}
	                        
	                        if (nCount > 3) {
	
	                            String str_Response_Status = responseArr[1];
	                            String str_Response = responseArr[3];
	
	                            if (str_Response_Status.equals("3") && !str_Response.isEmpty() && !map_Error.containsKey(str_Response)) {
	
	                                String strOutputPath = responseArr[3]; // JobKey
	                                AppLogger.info(strSEQID + " :Resultant OutputPath: " + strOutputPath);
	
	                                if (!strOutputPath.isEmpty()) {
	                                	
	                                    if (!strOutputPath.toLowerCase().endsWith(format.toLowerCase())) {
	
	                                        comments = "mismatch file type and format ";
	                                        String strcomments = strSEQID + " : " + comments;
	                                        AppLogger.info(strcomments);
	                                        auditInfo.put("COMMENTS", comments);
	                                        dbOperation.deeplinkAuditMaster(dbConn, auditInfo, strFilePath, strSEQID, "");
	                                        AppLogger.info(strSEQID + ": Service ends at " + getDatetimeinMillis());
	
	                                        return Response.status(417).entity(httpErrorsmsgs.optString("MISMATCHFORMAT")).build();
	                                    }
	                                    AppLogger.debug(strSEQID + " : HTML path is " + strOutputPath);
	
	                                    File file = new File(strOutputPath);
	                                    if (file.exists()) {
	
	                                        String strcomments = "success";
	                                        AppLogger.info(strSEQID + " : success");
	
	                                        auditInfo.put("COMMENTS", strcomments);
	                                        auditInfo.put("STATUS", "1");
	                                        //dbOperation.deeplinkAuditMaster(dbConn, auditInfo, strOutputPath, strSEQID, "");
	                                        fileInputStream = new FileInputStream(file);
	                                        AppLogger.info(strSEQID + ": Service ends at " + getDatetimeinMillis());
	                                        javax.ws.rs.core.Response.ResponseBuilder responseBuilder = javax.ws.rs.core.Response.ok((Object) fileInputStream);
	                                        responseBuilder.type(MediaType.TEXT_HTML);
	                                        responseBuilder.header("Content-Disposition", "filename=" + file.getName());
	                                        // }
	                                        return responseBuilder.build();
	                                        
	                                    } else {
	
	                                        comments = " output path not existed";
	                                        AppLogger.info(strSEQID + " : "+CErrorCodes.OUTPUT_FILE_PATH_NOT_EXIST+" : " + comments);
	                                        auditInfo.put("COMMENTS", comments);
	                                       // dbOperation.deeplinkAuditMaster(dbConn, auditInfo, strFilePath, strSEQID, "");
	                                        AppLogger.info(strSEQID + ": Service ends at " + getDatetimeinMillis());
	                                        return Response.status(417).entity(httpErrorsmsgs.optString("INTERNAL")).build();
	                                        
	                                    }
	                                    
	                                } else {
	
	                                    comments = " empty output path ";
	                                    AppLogger.info(strSEQID + " : "+CErrorCodes.OUTPUT_FILE_PATH_IS_EMPTY+" : " + comments);
	                                    auditInfo.put("COMMENTS", comments);
	                                    //dbOperation.deeplinkAuditMaster(dbConn, auditInfo, strFilePath, strSEQID, "");
	                                    AppLogger.info(strSEQID + ": Service ends at " + getDatetimeinMillis());
	                                    return Response.status(417).entity(httpErrorsmsgs.optString("INTERNAL")).build();
	                                    
	                                }
	                                
	                            } else {
	
	                                comments = " Internal Error occured";
	                                AppLogger.info(strSEQID + " : "+CErrorCodes.UNISERVE_INCORRECT_RESPONSE+" : " +  comments);
	                                auditInfo.put("COMMENTS", comments);
	                                //dbOperation.deeplinkAuditMaster(dbConn, auditInfo, strFilePath, strSEQID, "");
	                                AppLogger.info(strSEQID + ": Service ends at " + getDatetimeinMillis());
	                                return Response.status(417).entity(httpErrorsmsgs.optString("INTERNAL")).build();
	
	                            }
	                            
	                        } else {
	
	                            comments = "Invalid response received while processing request";
	                            AppLogger.info(strSEQID + " : "+CErrorCodes.UNISERVE_INCORRECT_RESPONSE+" : " + comments);
	                            auditInfo.put("COMMENTS", comments);
	                            //dbOperation.deeplinkAuditMaster(dbConn, auditInfo, strFilePath, strSEQID, "");
	                            AppLogger.info(strSEQID + ": Service ends at " + getDatetimeinMillis());
	                            return Response.status(417).entity(httpErrorsmsgs.optString("UNISERVEFAIL")).build();
	                            
	                        }
	                        
	                    }
	                    
	                } else {
	
	                    comments = " none of record is available with this" + jobKey + " job key";
	                    AppLogger.info(strSEQID + " : "+CErrorCodes.DATA_NOT_AVAILABLE+" : " + comments);
	                    auditInfo.put("COMMENTS", comments);
	                    //dbOperation.deeplinkAuditMaster(dbConn, auditInfo, strFilePath, strSEQID, "");
	                    AppLogger.info(strSEQID + ": Service ends at " + getDatetimeinMillis());
	                    return Response.status(417).entity(httpErrorsmsgs.optString("NOKEY")).build();
	                    
	                	}
	                
	            } else {
	
	            	comments = " html jobcommand is empty";
	            	AppLogger.info(strSEQID + " : "+CErrorCodes.EMPTY_JOB_COMMAND+" : " + comments);
	            	auditInfo.put("COMMENTS", comments);
	            	//dbOperation.deeplinkAuditMaster(dbConn, auditInfo, strFilePath, strSEQID, "");
	            	AppLogger.info(strSEQID + ": Service ends at " + getDatetimeinMillis());
	            	return Response.status(417).entity(httpErrorsmsgs.optString("EMPTYJOBCOMM")).build();
	            		
	            	}
	            
	        } else {
	
	            comments = format + " is invalid format";
	            //String strcomments = strSEQID + " : " + comments;
	            AppLogger.info(strSEQID + " : "+CErrorCodes.INVALID_FILE_FORMAT+" : " + comments);
	            auditInfo.put("COMMENTS", comments);
	//            dbOperation.deeplinkAuditMaster(dbConn, auditInfo, strFilePath, strSEQID, "");
	            AppLogger.info(strSEQID + ": Service ends at " + getDatetimeinMillis());
	            return Response.status(417).entity(httpErrorsmsgs.optString("INVALIDFORMAT")).build();
	            
	        	}
	        
	     } else {
	    	
	        comments = "internal error occured";
	        auditInfo.put("COMMENTS", comments );
	        // dbOperation.deeplinkAuditMaster(dbConn, auditInfo, strFilePath, strSEQID, "");
	        AppLogger.info(strSEQID + " : "+CErrorCodes.DATABASE_CONNECTION_FAILURE+" :  Unable to obtain the DB connection");
	        AppLogger.info(strSEQID + ": Service ends at " + getDatetimeinMillis());
	        return Response.status(417).entity(httpErrorsmsgs.optString("INTERNAL")).build();
	    	}
	        
	  } else {
        	
        	comments = "Decrypting the request Data(Job key) is failed";
        	auditInfo.put("COMMENTS", comments );
        	// dbOperation.deeplinkAuditMaster(dbConn, auditInfo, strFilePath, strSEQID, "");
        	AppLogger.info(strSEQID + " : "+CErrorCodes.DECRYPTION_FAILED+" : "+comments);
        	AppLogger.info(strSEQID + ": Service ends at " + getDatetimeinMillis());
        	return Response.status(417).entity(httpErrorsmsgs.optString("INTERNAL")).build();
        	
    	}
	        
    } catch (Exception ex) {

        comments = "internal error occured";
        AppLogger.error(strSEQID + " : "+CErrorCodes.REQUEST_DATA_PROCESSING_FAILED+" :  Exception occurred while  processing request : ",ex);
        auditInfo.put("COMMENTS", comments + ex.getMessage());
        //dbOperation.deeplinkAuditMaster(dbConn, auditInfo, strFilePath, strSEQID, "");
        AppLogger.info(strSEQID + ": Service ends at " + getDatetimeinMillis());
        return Response.status(417).entity(httpErrorsmsgs.optString("INTERNAL")).build();

    } finally {
       
    	dbOperation.deeplinkAuditMaster(dbConn, auditInfo, strFilePath, strSEQID, "");
            closeDBConnection(dbConn);
            
        // logger.info("all streams and connections are closed");
    }

	
	 }
	
	 private JSONObject getBrowserDetails() {
		
		 String ipAddress = "";
		 String browserName = "";
		 float majVersion;
		 
		 JSONObject objBrowserDetails = null;
		 
		 try {
			 objBrowserDetails = new JSONObject();
			 String userAgent = request.getHeader("user-agent");
			 ipAddress =request.getRemoteAddr();
		        UserAgent ua = UserAgent.parseUserAgentString(userAgent);
		        Version browserVersion = ua.getBrowserVersion();
		        browserName = ua.getBrowser().toString();
		        majVersion = Float.parseFloat(browserVersion.getMajorVersion());
		        objBrowserDetails.put("browserName", browserName);
		        objBrowserDetails.put("browserVersion", majVersion);
		        objBrowserDetails.put("ipAddr", ipAddress);
		        
		} catch (NumberFormatException numFrtEx) {
	    	
	        AppLogger.error("NumberFormatException occurred while getting the browser details : ",numFrtEx);
	        
	    } catch (Exception e) {
	    	
	        AppLogger.error("Exception occurred while getting the browser details : ",e);
	        
	    }
		 
		return objBrowserDetails;
	}



	private boolean validateParam(String strRequestParam) {

			boolean bValidateStatus = false;
			
			strRequestParam = strRequestParam.trim();
			
			if (strRequestParam != null && !strRequestParam.isEmpty()) {

				bValidateStatus = true;

			}

			return bValidateStatus;
		}
	 
	 
	public static String getDatetimeinMillis() {
        String strtimeinmillis = "";
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            strtimeinmillis = format.format(new Date());

        } catch (Exception ex) {
        	
        	AppLogger.error(CErrorCodes.INTERNAL_EXCEPTION+" : Exception occurred while DateTime in Millis :", ex);
            ex.printStackTrace();
            
        }

        return strtimeinmillis;

    }
	
	
	public static boolean validateKey(String value, String strRegexprn, String seqid, String strreqID) {
        
            AppLogger.info(seqid + " : RegExp to validate JobKey: " + strRegexprn + " against input JOBKEY: " + value);
        
        String line = value;

        // logger.info("value :=" + line);
        String pattern = strRegexprn;
        // logger.info("Regular Expression:=" + pattern);
        // Create a Pattern object
        Pattern r = Pattern.compile(pattern);
        boolean b = false;
        // Now create matcher object.
        if (line != null) {

            if (!line.isEmpty()) {
                if (line.length() == 50) {
                    Matcher m = r.matcher(line);
                    if (m.find()) {

                        return true;
                    } else {

                        return false;
                        // System.out.println("NO MATCH");
                    }

                } else {
                    return false;
                }
            } else {

                return b;
            }

        } else {

            return b;
        }

	}

	public static String decodeAND_DecryptSring(String strDecodeString,String strSEQID) {
		
		 String strkey = "AESEncryption256AESEncryption256";
		 String strDecrypt = "";
		 String base64Decode = "";
			
		 C_AES_256 c_aes_256 = new C_AES_256(strkey);
		 
		 try {
			
			 base64Decode = Base64.base64_decode(strDecodeString);
			 c_aes_256.AESDescrypt(base64Decode);
		       
		       for (char s : c_aes_256.Decrypted)  {
		    	   strDecrypt += s;
		    	  
		       }
			 
		} catch (Exception ex) {
			
			AppLogger.error(strSEQID+" : "+CErrorCodes.INTERNAL_EXCEPTION+" : Exception occurred while decoding and decrypting the request data :", ex);
		
		}		 		 
		
	    return strDecrypt;
	} 
	
	private void closeDBConnection(Connection dbConnClose) {

		try {

			if (dbConnClose != null && !dbConnClose.isClosed()) {

				dbConnClose.close();
				dbConnClose = null;

			}

		} catch (SQLException sqlEx) {

			AppLogger.error(CErrorCodes.INTERNAL_SQLEXCEPTION + " : SQLException occurred while closing DB Connection : ",sqlEx);

		} catch (Exception ex) {

			AppLogger.error(CErrorCodes.INTERNAL_EXCEPTION + " : Exception occurred while closing DB Connection : ", ex);

		}

	}
}
