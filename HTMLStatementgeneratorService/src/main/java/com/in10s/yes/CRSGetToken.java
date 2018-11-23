package com.in10s.yes;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.in10s.appprop.CRSLoadmqProperties;
import com.in10s.dao.BoneCPDBPool;
import com.in10s.applog.AppLogger;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author raghuram
 */
public class CRSGetToken extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = null;
        Connection con = null;
        Statement stmt = null;
        String ss = "";
        String requesttoken = "";
        ResultSet rs = null;
        String sedtoken = "";
        CRSGetTime obj = null;
        String jobkey = "";
        try {
        	AppLogger.info("Ajax call made to generate token");
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
            response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
            response.setDateHeader("Expires", 0);
            jobkey = request.getParameter("jobkey");
            out = response.getWriter();
            // logger.info("in gettoken request");
            obj = new CRSGetTime();
            con = BoneCPDBPool.getConnection();
            //String strgetSequence = "SELECT DEEPLINKREQUESTTOKENSEQ.NEXTVAL FROM DUAL";
            String strgetSequence = CRSLoadmqProperties.DEEPLINK_REQUESTTOKEN_SEQ;
            
            strgetSequence = strgetSequence.replace("<<SCHEMA_NAME>>", BoneCPDBPool.strSchemaName);
            
            stmt = con.createStatement();
            rs = stmt.executeQuery(strgetSequence);
            if (rs.next()) {
                requesttoken = Long.toString(rs.getInt(1));
                
            } else {
            	AppLogger.info("Exception occured while generating token");
            }
            sedtoken = StringUtils.leftPad(requesttoken, 28, "0");
            
            Date d = new Date();
            
            String strRequestTime = obj.getformattedTime(d);
            AppLogger.info("Formtted time in get Token method : " + strRequestTime + " and sequence is : " + sedtoken);
            ss = strRequestTime + sedtoken;
            
            AppLogger.info("Generate Token invoked Request time : " + strRequestTime + " ,  jobkey : " + jobkey + " and Generated token is : " + sedtoken);
            //  logger.info("token is : " + ss);

            // String finaltoken =ss + sedtoken;
            out.print(ss);
        } catch (IOException ex1) {
            ex1.printStackTrace();
            
        } catch (Exception ex) {
            ex.printStackTrace();
            sedtoken = StringUtils.leftPad(requesttoken, 28, "0");
            String strRequestTime = obj.getformattedTime(new Date());
            ss = strRequestTime + sedtoken;
            AppLogger.info("Generate Token invoked Request time : " + strRequestTime + " ,  jobkey : " + jobkey + "  Generated token is : " + sedtoken);
            
            out.print(ss);
        } finally {
            if (out != null) {
                out.close();
            }
            try {
                if (rs != null) {
                    rs.close();
                    rs = null;
                }
                if (stmt != null) {
                    stmt.close();
                    stmt = null;
                }
                if (con != null) {
                    con.close();
                    con = null;
                }
            } catch (Exception e) {
                try {
                    if (con != null) {
                        con.close();
                        con = null;
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
            
        }
        
    }

// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}

