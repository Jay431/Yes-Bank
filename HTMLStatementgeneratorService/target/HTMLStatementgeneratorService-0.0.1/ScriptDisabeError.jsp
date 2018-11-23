<%-- 
    Document   : ScriptDisabeError
    Created on : Oct 29, 2016, 4:44:04 PM
    Author     : hareesh.babu
--%>

<%@page import="com.in10s.yes.CRSSequenceGenerator"%>
<%@page import="com.in10s.appprop.CRSHttpErrorMsgs"%>
<%@page import="net.sf.json.JSONObject"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Script Error</title>
    </head>
    <body>
        <%
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
            response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
            response.setDateHeader("Expires", 0);
            String strReqid = request.getParameter("ID");
            JSONObject httpErrorsmsgs = CRSHttpErrorMsgs.getErrorcodes();
            String scriptDiableError = httpErrorsmsgs.optString("SCRIPTDISABLECONTENT", "");
            CRSSequenceGenerator.updateJspStatus(strReqid, scriptDiableError);
        %>

        <div style="position: fixed; top: 0px; left: 0px; z-index: 3000;color: #D8000C; 
             height: 100%; width: 100%; background-color: #FFFFFF;text-align: center">
            <p style="margin-left: 10px"><%=scriptDiableError%></p>

        </div>
    </body>
</html>
