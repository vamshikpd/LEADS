
<%@page import="java.io.InputStream"%><%@ page language="java" contentType="text/html;charset=UTF-8" %>


<%@ page import="com.report.*" %>
<%@ page import="com.endeca.ui.constants.*" %>
<%@ page import="com.util.xml.XMLHandler" %>
<%@ page import="java.io.File" %>
<%@ page import="org.springframework.core.io.ClassPathResource" %>
<%@ page import="org.springframework.core.io.FileSystemResource" %>
<%@ page import="org.apache.log4j.Logger" %>
<%
final Logger logger = Logger.getLogger(this.getClass());
%>

<%
ReportConfig report;
String fileName = getClass().getClassLoader().getResource("/").getPath();
if(fileName != null ) {
	int index = fileName.indexOf("WEB-INF");
	fileName = fileName.substring(0,index+"WEB-INF".length())+File.separator+UI_Props.getInstance().getValue("PERSIST_PATH");
}
logger.info(getClass().getClassLoader().getResource("/")+ UI_Props.getInstance().getValue("PERSIST_PATH"));
logger.info("FileName:" + fileName);
ClassPathResource xmlResource = new ClassPathResource(fileName);
//ClassPathResource xmlResource = new ClassPathResource(UI_Props.getInstance().getValue("PERSIST_PATH"));
// SNS Change 10/02/09 - don't load user specific, it's same for all
//String user = (String)session.getAttribute("user");
//String userpath = UI_Props.getInstance().getValue(UI_Props.PERSIST_PATH)+"/"+user+"/report.xml";
try {
/*	InputStream fis = xmlResource.getInputStream();
	byte fileBytes[] = new byte[1024];
	StringBuffer sbFileContent = new StringBuffer();
	while(true) {
		int iReadStat = fis.read(fileBytes);
		sbFileContent.append(new String(fileBytes));
		if(iReadStat <= 0 ) {
			break;
		}
	}
	logger.info("Report XML:"+sbFileContent.toString());
	out.println("Report XML:"+sbFileContent.toString());
	report = new ReportConfig(sbFileContent.toString());*/
	report = new ReportConfig(xmlResource.getFile());
}catch(Exception ex) {
	ex.printStackTrace();
	report = new ReportConfig();
}
// Write loaded report to current_report.xml for export functions
//String path = UI_Props.getInstance().getValue(UI_Props.PERSIST_PATH)+"/"+user+"/current_report.xml";
// SNS Change - No user
//String path = UI_Props.getInstance().getValue(UI_Props.PERSIST_PATH)+"/current_report.xml";
//XMLHandler.writeToFile(new File(path), report.getDocument());

session.setAttribute("Report", report);
%>