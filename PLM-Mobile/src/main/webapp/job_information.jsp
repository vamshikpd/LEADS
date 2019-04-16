<%@ page import="com.endeca.navigation.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.text.*" %>
<%@ page import="java.net.*" %>
<%@ page import="com.util.format.*" %>
<%@ page import="com.endeca.ui.*" %>
<%@ page import="com.endeca.ui.constants.*" %>
<%@ page import="com.plm.constants.PLMConstants" %>
<%@ page import="com.plm.util.database.PLMDatabaseUtil" %>
<html>
	<head>
		<meta name="viewport" content="width=device-width; initial-scale=1.0; maximum-scale=1.0;" />
		<title>Job Information</title>
		<link href="media/style/main.css" rel="stylesheet" type="text/css" />
	</head>
	<body>
<%
	String spec ="";
	PropertyContainer rec = (PropertyContainer)request.getAttribute("record");
	PropertyMap tempPropsMap = null;
	if(rec instanceof ERec) {
		spec = ((ERec)rec).getSpec();
		tempPropsMap = ((ERec)rec).getProperties();
	}else {
		spec = ((AggrERec)rec).getSpec();
		tempPropsMap = ((AggrERec)rec).getProperties();
	}
%>
		<div align="center">
			<div id="Maindiv">
				<div id="Container">
					<div id="Header">
						<div class="logo"><span class="logo1">PAROLE</span><span class="logo2">LEADS2.0</span></div>
						<div class="top">
							<div class="back"><a href="plm_mobile_controller.jsp?page=paroleedetails&R=<%=spec%>">Back</a></div>
							<%@ include file="logout_include_mobile.jsp" %>
							<div class="home"><a href="plm_mobile_controller.jsp?page=login">Home</a></div>
						</div>
					</div>
					<div id="Middle">
						<div id="MainContainer">
							<div class="searchresulttable detailspage">
								<div class="row1">
									<%@ include file="essence_info.jsp" %>
								</div>
<%
	String sortedJobInfos = (String)tempPropsMap.get("Sorted Employer Information");
	String[] jobInfos = null;
	if(sortedJobInfos != null) {
		jobInfos = sortedJobInfos.split("@@");
	}
	String strDiscDate = null;
	
	int cnt = 0;
	String srow = "";
	while (jobInfos != null && cnt<jobInfos.length){
		if (cnt%2!=0){
			srow = "row2";
		}else{
			srow = "row1";
		}
%>
								<div class="<%=srow%>">
<%
		String jobInfo = jobInfos[cnt];
		String[] jobInfo_result = null;
		String currJobInfo = "&nbsp;";
		String employer = "";
		String title = "";
		String empAware = "";
		String phone = "";
		
		if(jobInfo != null){
			jobInfo_result = jobInfo.split(PLMConstants.SEPARATOR);
		}	
		HashMap hmapJob = new HashMap();
		
		//employer
		if (jobInfo_result.length > 0 && (String)jobInfo_result[0] != null && !((String)jobInfo_result[0]).trim().equals("")){
			employer = jobInfo_result[0];
		}
	
		//street
		if (jobInfo_result.length > 1 && (String)jobInfo_result[1] != null && !((String)jobInfo_result[1]).trim().equals("")){
			currJobInfo = currJobInfo+jobInfo_result[1];
		}
		//city
		if (jobInfo_result.length > 2 && (String)jobInfo_result[2] != null && !((String)jobInfo_result[2]).trim().equals("")){
			currJobInfo = currJobInfo+jobInfo_result[2]+",&nbsp;";
		}
		//county
		if (jobInfo_result.length > 4 && (String)jobInfo_result[4] != null && !((String)jobInfo_result[4]).trim().equals("")){
			currJobInfo = currJobInfo+jobInfo_result[4];
		}	
		//state							
		if (jobInfo_result.length > 3 && (String)jobInfo_result[3] != null && !((String)jobInfo_result[3]).trim().equals("")){
			currJobInfo = currJobInfo+jobInfo_result[3]+",&nbsp;";		
		}
		//zip
		if (jobInfo_result.length > 5 && (String)jobInfo_result[5] != null && !((String)jobInfo_result[5]).trim().equals("")){
			currJobInfo = currJobInfo+jobInfo_result[5];
		}
		//zip4
		if (jobInfo_result.length > 6 && (String)jobInfo_result[6] != null && !((String)jobInfo_result[6]).trim().equals("")){
			currJobInfo = currJobInfo+"-"+jobInfo_result[6];
		}
		//phone
		if (jobInfo_result.length > 7 && (String)jobInfo_result[7] != null && !((String)jobInfo_result[7]).trim().equals("")){
			phone = jobInfo_result[7];
		} else {
			phone = "&nbsp;";
		}
			
		//job title
		if (jobInfo_result.length > 8 && (String)jobInfo_result[8] != null && !((String)jobInfo_result[8]).trim().equals("")){
			title = jobInfo_result[8];
		} else {
			title="&nbsp;";
		}
		
		//emp aware
		if (jobInfo_result.length > 9 && (String)jobInfo_result[9] != null && !((String)jobInfo_result[9]).trim().equals("")){
			empAware = jobInfo_result[9];
		} else {
			empAware="&nbsp;";
		}
	
		//start date
		if (jobInfo_result.length > 10 && (String)jobInfo_result[10] != null && !((String)jobInfo_result[10]).trim().equals("")){
			strDiscDate = jobInfo_result[10];					
		}else{
			strDiscDate ="&nbsp;";
		}
%>
									<div class="name1">Job Start Date</div><div class="value1"><%=strDiscDate%></div>	
									<div class="name1">Employer</div><div class="value1"><%=employer%></div>
									<div class="name1">Employer Address</div><div class="value1"><%=currJobInfo%></div>
									<div class="name1">Title</div><div class="value1"><%=title%></div>
									<div class="name1">Employer Aware</div><div class="value1"><%=empAware%></div>
								</div>
<%
		cnt = cnt +1;
	}

	if(cnt == 0 ){
%>
								<div class="row2">
									<div class="name1">Job</div>
									<div class="value1">NONE</div>
								</div>
<% 	
	}
%>
							</div>
						</div>
					</div>
					<div id="Footer"></div>
				</div>
			</div>
		</div>
	</body>
</html>
