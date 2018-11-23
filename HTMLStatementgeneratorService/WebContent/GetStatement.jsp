<%@page import="eu.bitwalker.useragentutils.Version"%>
<%@page import="eu.bitwalker.useragentutils.UserAgent"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.HashSet"%>
<%@page import="com.in10s.service.GenerateHtmlStatement"%>
<%@page import="com.in10s.applog.AppLogger"%>
<%@page import="java.util.Date"%>
<%@page import="com.in10s.yes.CRSGetTime"%>
<%@page import="com.in10s.appprop.CRSLoadmqProperties"%>
<%@page import="com.in10s.yes.CRSTest"%>
<%@page import="java.util.Random"%>
<%@page import="net.sf.json.JSONObject"%>
<%@page import ="java.lang.Math"%>
<%@page import ="com.in10s.dao.CAuthenticate"%>
<%@page import="com.in10s.yes.CRSSequenceGenerator" %>
<%@page import="com.in10s.applog.AppLogger" %>
<%@page import="com.in10s.appprop.CRSHttpErrorMsgs" %>
<%@page import="com.in10s.dao.DataBaseProcess" %>
<%@page import="com.in10s.service.GenerateHtmlStatement" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Statement Authentication</title>

<script type="text/javascript" src="js/app.js"></script>
</head>

<style>
.center {
	margin: auto;
	width: 60%;
	height: 90%;
	border: 3px solid #003366;
	padding: 40px;
	text-align: center;
}

.center1 {
	margin: auto;
	width: 60%;
	height: 90%;
	color: #003366;
	padding: 40px;
	text-align: center;
}

#pwd { // #003366;
	width: 176px;
	height: 29px;
	background: #fff;
	outline: none;
}

input[type=submit] {
	font-size: 16px;
	background-color: #003366;
	border: none;
	color: white;
	padding: 10px 17px;
	text-decoration: none;
	cursor: pointer;
}

input[type=button] {
	font-size: 16px;
	background-color: #003366;
	border: none;
	color: white;
	padding: 10px 32px;
	text-decoration: none;
	/*        margin-left:310%;*/
	cursor: pointer;
}

input[type=submit]:disabled { //
	background: #dddddd;
	opacity: 0.65;
	cursor: not-allowed;
}

input[type=button]:disabled { //
	background: #dddddd;
	opacity: 0.65;
	cursor: not-allowed;
}
</style>

<script type="text/javascript">
	var request;
	window.history.forward();
	function noBack() {
		document.getElementById('pwd').focus();
		window.history.forward();

	}

	function on() {

		var value1 = document.getElementById('pwd').value;
		console.log("value1 : " + value1);
		document.getElementById('submit').disabled = true;
		document.getElementById('reset').disabled = true;
		if (value1 === null || value1.trim() === "") {

			document.getElementById("123456").innerHTML = "Please Enter the valid password";
			document.getElementById('pwd').value = "";
			document.getElementById('pwd').focus();
			document.getElementById('reset').disabled = false;
			document.getElementById('submit').disabled = false;
			return false;
		} else {
			sendInfo();
			return true;

		}
	}
	
	function sendInfo()
    {

        var jobkey = document.getElementById('ke').value;

        var url = "./CRSGetToken?jobkey=" + jobkey;

        if (window.XMLHttpRequest) {
            request = new XMLHttpRequest();
        }
        else if (window.ActiveXObject) {
            request = new ActiveXObject("Microsoft.XMLHTTP");
        }

        try
        {
            request.onreadystatechange = getInfo;
            request.open("GET", url, false);
            request.send();
        }
        catch (e)
        {
            alert("Unable to connect to server");
        }
    }
	
	function getInfo() {
        if (request.readyState === 4) {
            if (request.status === 200) {
               
                var val = request.responseText;

                var value11 = document.getElementById('pwd').value;
                var lll = val + value11;
                var ll = encrypt(lll);
						
                document.getElementById('pwd').value = ll;

            } else {
                document.getElementById('pwd').value = "";
            }

        }
    }

	function cancelpass() {

		document.getElementById('pwd').value = "";
		document.getElementById('pwd').focus();
		document.getElementById('submit').disabled = false;
	}
</script>

<body  onload="noBack();" onpageshow="if (event.persisted) noBack();" onunload="">

    <%
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
        response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
        response.setDateHeader("Expires", 0);
        String userAgent = request.getHeader("user-agent");
        UserAgent ua = UserAgent.parseUserAgentString(userAgent);
        Version browserVersion = ua.getBrowserVersion();
        String browserName = ua.getBrowser().toString();
        float majVersion = Float.parseFloat(browserVersion.getMajorVersion());
        
        JSONObject httpErrorsmsgs1 = CRSHttpErrorMsgs.getErrorcodes();

//        System.out.println("browserName:::::" + browserName + "         majVersion:::" + majVersion);
        String key = request.getParameter("jobkey");
        String strJobKey = "";
        
        strJobKey = GenerateHtmlStatement.decodeAND_DecryptSring(key, "");
        AppLogger.info("job key after decryption : "+strJobKey);
       
        
        String actucal_key = "";
        
        if(!strJobKey.isEmpty()){
        
        		actucal_key = strJobKey.substring(1,strJobKey.length());
        	
        }  else {
        
        	String scriptdisablecontent = httpErrorsmsgs1.optString("NOJOBKEY", "");
        	AppLogger.info("Jobkey is empty");
        }
                      
        
        //String scriptdisablecontent = ErrorMessages.SCRIPTDISABLECONTENT;
        //JSONObject httpErrorsmsgs1 = CRSHttpErrorMsgs.getErrorcodes();
        String scriptdisablecontent = httpErrorsmsgs1.optString("SCRIPTDISABLECONTENT", "");

        // %>
    <!--       <noscript>
            <div style="position: fixed; top: 0px; left: 0px; z-index: 3000;color: #D8000C; 
                 height: 100%; width: 100%; background-color: #FFFFFF;text-align: center">
                <p style="margin-left: 10px"><%=scriptdisablecontent%></p>
                        This feature requires JavaScript to be enabled. Please enable JavaScript in your browser or use a different browser with JavaScript support.
            </div>
            </noscript>-->


    <%
        String RegulerString = CRSLoadmqProperties.jobkeyregex;
        boolean browserAccess = true;
        Set<String> browserNamesfromDB = CRSLoadmqProperties.html5_compatable_browsers_map.keySet();
        
        for (String s : browserNamesfromDB) {
        
            if (browserName.contains(s.toUpperCase()) && majVersion < CRSLoadmqProperties.html5_compatable_browsers_map.get(s)) {
                browserAccess = false;
                break;
            }
        }

        String ipAddr = request.getRemoteAddr();
        JSONObject auditInfo = new JSONObject();
        if (browserAccess == true) {
            auditInfo.put("STATUS", "1");
            auditInfo.put("COMMENTS", "Login Page loaded Successfully");
        } else {
            auditInfo.put("STATUS", "-1");
            auditInfo.put("COMMENTS", "Browser version not supported");
        }
	
        auditInfo.put("browserName", browserName);
        auditInfo.put("browserVersion", majVersion);
        auditInfo.put("ipAddr", ipAddr);
        
        auditInfo.put("JOBKEY", strJobKey);
        //auditInfo.put("JOBKEY", key); 
        auditInfo.put("FORMAT", "JSP");
        String sequenceId = DataBaseProcess.auditJspStatus(auditInfo); //place this
        //String sequenceId = "1"; //remove this code
        request.setAttribute("data", CRSLoadmqProperties.html5_compatable_browsers_map);
        if (!browserAccess) {
            //String invalidBrowserVersion = ErrorMessages.INVALIDBROWSERVERSION;
            JSONObject httpErrorsmsgs = CRSHttpErrorMsgs.getErrorcodes();
            String invalidBrowserVersion = httpErrorsmsgs.optString("INVALIDBROWSERVERSION", "");
            
    %>


<center> <big><%=invalidBrowserVersion%></big>
    <br><br><big> Supported Browser versions are as follows:</big><br>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
    <table border="1">
        <tr>
            <th>Browser Name</th>
            <th>Version</th>
        </tr>
        <c:forEach items="${data}" var="entry">
            <tr>
                <td> ${entry.key}</td>
                <td>${entry.value} or above</td>

            </tr>
        </c:forEach>
    </table>
</center>
<%} else if (CRSTest.validateKey(actucal_key, RegulerString)) {
    // logger.getInstance();
    AppLogger.info(sequenceId + "===============================================================================================");
    AppLogger.info(sequenceId + ":Login Page requested at " + GenerateHtmlStatement.getDatetimeinMillis() + "  for JobKey : " + key);
%>
	
	<div class="center" id="456">

		<form action="./rest/statement/htmlformat" method="POST"
			onsubmit="return on()" autocomplete="off">
			<table style="margin: auto;">
				<tbody>
					<tr>
						<td><font color="#003366"><b id="123"
								style="width: 129px">Password:</b></font></td>
						<td><input type="hidden" name="ke" id="ke" value='<%=key%>' />
						<input type="hidden" name ="seqence" id="seqence" value='<%=sequenceId%>'/>
							<input type="password" name="pwd" id="pwd" autocomplete="off" />
						</td>

					</tr>
					<tr>
						<td colspan="2">&nbsp;</td>
					</tr>
					<tr>
						<td>&nbsp;</td>
						<td><input type="submit" value="Get Statement" id="submit" />&nbsp;<input
							type="button" value="Reset" id="reset" onclick="cancelpass()" /></td>
					</tr>
				</tbody>
			</table>
		</form>
		<br />
		<div id="123456" style="color: #D8000C; background-color: #FFBABA;"></div>


	</div>
<%
} else {
    
    //String invalidRequest = ErrorMessages.INVALIDREQUEST;
    JSONObject httpErrorsmsgs = CRSHttpErrorMsgs.getErrorcodes();
    String invalidRequest = httpErrorsmsgs.optString("INVALIDREQUEST", "");
%>

<div class="center1">
    <table>
        <tbody>

        <big><%=invalidRequest%></big>

        </tbody>
    </table>

</div>
<%
    }
%>
	<noscript>
		<meta http-equiv="refresh" content="0; url=./ScriptDisabeError.jsp" />
	</noscript>
</body>
</html>



