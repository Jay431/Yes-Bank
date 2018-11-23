package com.in10s.service;

import org.in10s.rasserver.RASConnection;
import org.in10s.rasserver.RASSessionManager;

import com.in10s.applog.AppLogger;
import com.in10s.appprop.CRSLoadmqProperties;

import org.apache.log4j.Logger;

public class RunUniserverDaemonJob {

	String RunDaemonJob(String jobPacket, long currRequest, boolean bTracking, String strOrgID, String sedid,
			Logger log) {

		String retVal = "";
		String orgId = "";
		int nTimeOut = 0;
		boolean bReset = false; // if send command failed then we should reset
								// connection (i.e close socket) to aviod dirty
								// data read.

		RASSessionManager SessionManager = null;
		RASConnection rasConnection = null;

		try {
			nTimeOut = Integer.parseInt(CRSLoadmqProperties.timeout); // Integer.parseInt(prop.getProperty("ONDEMAND_DUPBILLPROCESS_TIMEOUT"));

			orgId = strOrgID;

			SessionManager = RASSessionManager.getInstance();
			rasConnection = SessionManager.getConnection(orgId, "PMS", false);

			rasConnection.setLogger(log);
			rasConnection.setCurrReq(currRequest);

				AppLogger.info(sedid + " : RunDaemonJob:SendingJob");

			if (rasConnection.sendCommand(2, -1, jobPacket, false)) {

				String jobID = rasConnection.getData();
				AppLogger.info(sedid + " : Submitted JobId - " + jobID);

				if (!jobID.isEmpty()) {

					if (bTracking) {

						AppLogger.info(" RunDaemonJob:TrackingJob for " + jobID);
						AppLogger.info(sedid + " : RunDaemonJob:TrackingJob for " + jobID);
						String strParams = jobID + "\1" + " 3" + "\1" + " 0";

						Integer nSeconds = 0;

						if (nTimeOut < 10) {
							nTimeOut = 30;
						}

						do {

							if (rasConnection.sendCommand(7, -1, strParams, false)) {

								retVal = rasConnection.getData();

								int nCount = retVal.split("\1").length;

								if (nCount > 3) {

									break;
								}
							} else {

								AppLogger.info("RunDaemonJob:Tracking failed - " + rasConnection.getError());

								bReset = true;
								break;
							}

							nSeconds = nSeconds + 10;

						} while (nSeconds < nTimeOut);

					} else {

						retVal = jobID;
					}
				} else {

						AppLogger.info(sedid + " : RunDaemonJob:Sending failed - JOBID is Empty: " + rasConnection.getError());

				}
			} else {

				AppLogger.info(" RunDaemonJob:Sending failed - " + rasConnection.getError());
				bReset = true;
			}
		} catch (Exception ex) {

			bReset = true;

			AppLogger.error("Exception : ", ex);

		} finally {

			if (SessionManager != null && rasConnection != null) {

				try {

					SessionManager.releaseConnection(rasConnection, orgId, log, currRequest, bReset);

				} catch (Exception e) {

					// logger.info(sedid + " : RunDaemonJob failed at connection
					// release: " + e);
					e.printStackTrace();

				}
			}
		}

		return retVal;
	}

	
	
}
