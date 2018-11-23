package com.in10s.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import com.in10s.applog.AppLogger;
import com.in10s.common.BoneCPDBPool;
import com.in10s.common.DataBaseProcess;
import com.in10s.config.CErrorCodes;
import com.in10s.config.LoadApplicationProperties;
import com.in10s.utils.Base64;
import com.in10s.utils.C_AES_256;

//import net.sf.json.JSONObject;

@Path("/app")
public class EmiClickCapture {

	public EmiClickCapture() {

	}

	@GET
	@Path("/ClickCapture")
	@Produces("text/html")
	public Response clickAndCapture(@QueryParam("params") String requestparams) {

		String[] strReqAftDecrypt = null;
		String strMCC_Code = "";
		String strOriginal_transaction_date = "";
		String strAmount = "";
		String strTranRefNumber = "";
		String strCredit_Card_Num = "";
		String strAccount_number = "";
		String strEmail_Id = "";
		String strMobile_No = "";
		String strStmt_Due_Date = "";
		String strComments = "";
		String strError_des = "";

		String strStatus = "-1";

//		JSONObject auditData = null;

		Connection dbConn = null;
		DataBaseProcess dbOperation = new DataBaseProcess();

		try {

			if (dbConn == null) {

				dbConn = BoneCPDBPool.getConnection();

			}
//			requestparams = requestparams.replace("{", "");
//			requestparams = requestparams.replace("}", "");

//			requestparams = decoder.decode(requestparams);
			if (requestparams != null && !requestparams.isEmpty()) {
				
				AppLogger.info("request parameters before decoding :" + requestparams);

				requestparams = decodeAND_DecryptSring(requestparams.trim());
				AppLogger.info("request parameters after decoding :" + requestparams);
					
				if (!requestparams.isEmpty()) {

					strReqAftDecrypt = requestparams.split("\\$");

//			System.out.println("length : "+strReqAftDecrypt.length);
					if (dbConn != null) {

						if (strReqAftDecrypt.length == 9) {

							strMCC_Code = strReqAftDecrypt[0];
							strOriginal_transaction_date = strReqAftDecrypt[1];
							strAmount = strReqAftDecrypt[2];
							strTranRefNumber = strReqAftDecrypt[3];
							strCredit_Card_Num = strReqAftDecrypt[4];
							strAccount_number = strReqAftDecrypt[5];
							strEmail_Id = strReqAftDecrypt[6];
							strMobile_No = strReqAftDecrypt[7];
							strStmt_Due_Date = strReqAftDecrypt[8];

							if (validateParams(strMCC_Code)) {

								if (validateParams(strOriginal_transaction_date)) {

									if (validateDateValues(strOriginal_transaction_date, "TRANSACTION_DATE")) {

										if (validateParams(strAmount)) {

											if (validateParams(strTranRefNumber)) {

												if (validateParams(strCredit_Card_Num)) {

													if (validateParams(strAccount_number)) {

														if (validateParams(strEmail_Id)) {

															if (validateParams(strMobile_No)) {

																if (validateParams(strStmt_Due_Date)) {

																	if (validateDateValues(strStmt_Due_Date,"DUE_DATE")) {

																		if (!dbOperation.validateTransRefNum(dbConn,strTranRefNumber)) {

																			if (dbOperation.captureEmiRequest(dbConn,strReqAftDecrypt)) {

																				strComments = "Request for emi is successfully submited for Transaction reference number : "+ strTranRefNumber;
																				AppLogger.info(strComments);
																				strStatus = "1";
																				strError_des = LoadApplicationProperties.SUCCESS_MESSAGE;
																				return Response.status(200).entity(strError_des).build();

																			} else {
																				strComments = "Request for emi is not submitted for Transaction reference number : "+ strTranRefNumber;
																				AppLogger.info(CErrorCodes.REQUEST_CAPTURING_FAILED+" : "+strComments);
																				strError_des = LoadApplicationProperties.ERROR_TEXT_6;
																				return Response.status(417).entity(strError_des).build();
																			}

																		} else {
																			strComments = "Transaction already exist with the Transaction Reference Number : "+ strTranRefNumber;
																			AppLogger.info(CErrorCodes.REQUEST_ALREADY_PROCESSED+" : "+strComments);
																			strError_des = LoadApplicationProperties.ERROR_TEXT_1;
																			return Response.status(417).entity(strError_des).build();
																		}
																	} else {
																		strComments = "Statement Due date is less then current due date";
																		AppLogger.info(CErrorCodes.REQUEST_VALIDATION_FAILURE+" : "+strComments);
																		strError_des = LoadApplicationProperties.ERROR_TEXT_2;
																		return Response.status(417).entity(strError_des).build();
																	}

																} else {
																	strComments = "Invalid Statement Due Date empty or null";
																	AppLogger.info(CErrorCodes.REQUEST_VALIDATION_FAILURE+" : "+strComments);
																	strError_des = LoadApplicationProperties.ERROR_TEXT_3;
																	return Response.status(417).entity(strError_des).build();
																}
															} else {
																strComments = "Invalid Mobile_no empty or null";
																AppLogger.info(CErrorCodes.REQUEST_VALIDATION_FAILURE+" : "+strComments);
																strError_des = LoadApplicationProperties.ERROR_TEXT_3;
																return Response.status(417).entity(strError_des).build();
															}
														} else {
															strComments = "Invalid Email_id empty or null";
															AppLogger.info(CErrorCodes.REQUEST_VALIDATION_FAILURE+" : "+strComments);
															strError_des = LoadApplicationProperties.ERROR_TEXT_3;
															return Response.status(417).entity(strError_des).build();
														}
													} else {
														strComments = "Invalid strAccount number empty or null";
														AppLogger.info(CErrorCodes.REQUEST_VALIDATION_FAILURE+" : "+strComments);
														strError_des = LoadApplicationProperties.ERROR_TEXT_3;
														return Response.status(417).entity(strError_des).build();
													}
												} else {
													strComments = "Invalid strCredit Card number empty or null";
													AppLogger.info(CErrorCodes.REQUEST_VALIDATION_FAILURE+" : "+strComments);
													strError_des = LoadApplicationProperties.ERROR_TEXT_3;
													return Response.status(417).entity(strError_des).build();
												}
											} else {
												strComments = "Invalid Transaction reference number empty or null";
												AppLogger.info(CErrorCodes.REQUEST_VALIDATION_FAILURE+" : "+strComments);
												strError_des = LoadApplicationProperties.ERROR_TEXT_3;
												return Response.status(417).entity(strError_des).build();
											}
										} else {
											strComments = "Invalid Amount empty or null";
											AppLogger.info(CErrorCodes.REQUEST_VALIDATION_FAILURE+" : "+strComments);
											strError_des = LoadApplicationProperties.ERROR_TEXT_3;
											return Response.status(417).entity(strError_des).build();
										}
									} else {

										strComments = "Invalid transaction_date format";
										AppLogger.info(CErrorCodes.REQUEST_VALIDATION_FAILURE+" : "+strComments);
										strError_des = LoadApplicationProperties.ERROR_TEXT_3;
										return Response.status(417).entity(strError_des).build();
									}

								} else {
									strComments = "Invalid transaction_date empty or null";
									AppLogger.info(CErrorCodes.REQUEST_VALIDATION_FAILURE+" : "+strComments);
									strError_des = LoadApplicationProperties.ERROR_TEXT_3;
									return Response.status(417).entity(strError_des).build();
								}
							} else {
								strComments = "Invalid MCC_Code  empty or null";
								AppLogger.info(CErrorCodes.REQUEST_VALIDATION_FAILURE+" : "+strComments);
								strError_des = LoadApplicationProperties.ERROR_TEXT_3;
								return Response.status(417).entity(strError_des).build();
							}
						} else {
							strComments = "Invalid number of request params";
							AppLogger.info(CErrorCodes.REQUEST_VALIDATION_FAILURE+" : "+strComments);
							strError_des = LoadApplicationProperties.ERROR_TEXT_3;
							return Response.status(417).entity(strError_des).build();
						}

					} else {
						strComments = "Unable to obtain the DB Connection";
						AppLogger.info(CErrorCodes.DATABASE_CONNECTION_FAILURE+" : "+strComments);
						strError_des = LoadApplicationProperties.ERROR_TEXT_5;
						return Response.status(417).entity(strError_des).build();
					}
				} else {
					
					strComments = "Failed to decode the request data";
					AppLogger.info(CErrorCodes.DECRYPTION_FAILED+" : "+strComments);
					strError_des = LoadApplicationProperties.ERROR_TEXT_8;
					return Response.status(417).entity(strError_des).build();

				}
			} else {
				
				strComments = "request data is empty or null";
				AppLogger.info(CErrorCodes.REQUEST_VALIDATION_FAILURE+" : "+strComments);
				strError_des = LoadApplicationProperties.ERROR_TEXT_9;
				return Response.status(417).entity(strError_des).build();
				
			}
		} catch (Exception ex) {
			AppLogger.error(CErrorCodes.INTERNAL_EXCEPTION+" : Exception occurred while processing the Emi Request : ", ex);
			strError_des = LoadApplicationProperties.ERROR_TEXT_7;
			return Response.status(417).entity(strError_des).build();
		} finally {
			dbOperation.AuditEmiRequestData(dbConn, strReqAftDecrypt, strStatus, strComments, strError_des);
			closeDBConnection(dbConn);
		}

	}

	private boolean validateParams(String strMCC_Code) {

		boolean bValidationStatus = false;

		try {

			if (strMCC_Code != null && !strMCC_Code.isEmpty()) {

				bValidationStatus = true;
			}

		} catch (Exception e) {

		}
		return bValidationStatus;
	}

	private boolean validateDateValues(String strStatementDate, String strTypeDate) {

		boolean bDateStatus = false;
		SimpleDateFormat dateFormat = null;
		Date currentDate = null;
		Date statementDate = null;
		try {

			dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			currentDate = new Date();
			dateFormat.setLenient(false);
			statementDate = new Date();
			statementDate = dateFormat.parse(strStatementDate);
			
			
			/*strTypeDate data comparison is for validation for statement date should be checked with 
			current date but transaction date does not require this validation for this purpose this 
			value is validated with  DUE_DATE and TRANSACTION_DATE*/
			if (strTypeDate.equalsIgnoreCase("DUE_DATE")) {

				if (currentDate.before(statementDate)) {

					bDateStatus = true;

				}
			} else if (strTypeDate.equalsIgnoreCase("TRANSACTION_DATE")) {

				bDateStatus = true;

			}

		} catch (ParseException prsEx) {
			AppLogger.error(CErrorCodes.DATA_PARSING_FAILED+" : ParseException occurred while Parsing the Date : ", prsEx);
		} catch (Exception ex) {
			AppLogger.error(CErrorCodes.DATA_PARSING_FAILED+" : Exception occurred while Parsing the Date : ", ex);
		}

		return bDateStatus;
	}

	private String decodeAND_DecryptSring(String strDecodeString) {

		String strkey = "AESEncryption256AESEncryption256";
		String strDecrypt = "";
		String base64Decode = "";

		C_AES_256 c_aes_256 = new C_AES_256(strkey);
		try {

			base64Decode = Base64.base64_decode(strDecodeString);
			c_aes_256.AESDescrypt(base64Decode);

			for (char s : c_aes_256.Decrypted) {
				strDecrypt += s;

			}

		} catch (Exception ex) {
			
			AppLogger.error(CErrorCodes.DECRYPTION_FAILED+" : Exception occurred while decoding : ", ex);
			
		}
		
		if(strDecrypt.isEmpty()) {
			 
			 AppLogger.info(CErrorCodes.DECRYPTION_FAILED + " : Decrypting the request Data is failed");
			 
		 }

		return strDecrypt;
	}

	/*
	 * public static boolean validateJavaDate(String strDate) {
	 * 
	 * SimpleDateFormat sdfrmt = new SimpleDateFormat("dd/MM/yyyy");
	 * sdfrmt.setLenient(false); Create Date object parse the string into date
	 * 
	 * try { Date javaDate = sdfrmt.parse(strDate);
	 * System.out.println(strDate+" is valid date format"); } Date format is invalid
	 * catch (ParseException e) {
	 * System.out.println(strDate+" is Invalid Date format"); return false; } Return
	 * true if date format is valid return true; }
	 */

	private void closeDBConnection(Connection dbConnClose) {

		try {

			if (dbConnClose != null && !dbConnClose.isClosed()) {

				dbConnClose.close();
				dbConnClose = null;

			}

		} catch (SQLException sqlEx) {

			AppLogger.error(CErrorCodes.INTERNAL_SQLEXCEPTION+" : SQLException occurred while closing DB Connection : ", sqlEx);

		} catch (Exception ex) {

			AppLogger.error(CErrorCodes.INTERNAL_EXCEPTION+" : Exception occurred while closing DB Connection : ", ex);

		}

	}

}
