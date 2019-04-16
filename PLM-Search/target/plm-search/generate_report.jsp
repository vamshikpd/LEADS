<%@ page errorPage="error.jsp" %>
<%@ page import="java.io.*" %>
<%@ page import="com.plm.util.*" %>
<%
	String userId = (String)session.getAttribute("userId");
	//If the userId doesnot contain agency code then get from fullname.
	if(userId.indexOf("_") == -1 ){
		userId = request.getHeader("FULLNM");
	}
	File file = GenerateInactiveUsersReport.generateReport(userId);
	response.setContentType("application/csv");
	response.setHeader("Content-disposition", "attachment;filename=" + file.getName()); // To pop dialog box
	InputStream in = new FileInputStream(file);
	try{
		byte[] buf = new byte[new Long(file.length()).intValue()];
		int len;
		while ((len = in.read(buf)) > 0){
			response.getOutputStream().write(buf, 0, len);
		}
		response.getOutputStream().flush();
		in.close();
	} catch(IOException ioe){
		ioe.printStackTrace();
	}
%>