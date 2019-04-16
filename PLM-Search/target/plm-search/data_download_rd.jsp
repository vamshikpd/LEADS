<!--  
File Name : data_download_rd.jsp
History   :
			Date		User			Description
			03/22/2012  Madhu Menon		Starting history recording
			03/22/2012  Madhu Menon		Changes to use List instead of HashMap to display sorted values in the county dropdown
			03/22/2012  Madhu Menon		Changed "upto" to "up to"
			04/12/2012  Madhu Menon		Added instructions text for the user to help them select multiple counties			
			
 -->
<%@ page language="java" contentType="text/html;charset=UTF-8" %>
<%@ page import="java.text.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.io.*" %>
<%@ page import="com.endeca.ui.constants.*" %>
<%@ page import="com.endeca.ui.constants.UI_Props"%>
<%@ page import="com.plm.util.PLMSearchUtil" %>
<%@ page import="com.plm.util.PLMDataDownloadUtil" %>
<%@ page import="com.endeca.navigation.*" %>
<%@ page import="javax.xml.parsers.DocumentBuilderFactory" %>
<%@ page import="org.apache.log4j.Logger" %>
<%@ page import="org.apache.commons.io.comparator.LastModifiedFileComparator" %>
<%
final Logger logger = Logger.getLogger(this.getClass());
File inprogressFile = null;
File dataFile = null;
File photoFile = null;
File errFile = null;
boolean isPhotoDownloadAvailable = PLMDataDownloadUtil.isPhotoDownloadAvailable();
//Collection<File> inProgressFileNames = PLMSearchUtil.getListOfDataFiles(request.getHeader("USERID"),"inprogress");
//Collection<File> prevDataFileNames = PLMSearchUtil.getListOfDataFiles(request.getHeader("USERID"),"zip");
//Collection<File> prevPhotoFileNames = null;
boolean inProgressExists = false;
boolean prevExists = false;
boolean errExists = false;
SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy");//
inprogressFile = PLMSearchUtil.getLatestDataFile((String) session.getAttribute("userId"),"inprogress");
errFile = PLMSearchUtil.getLatestFile((String) session.getAttribute("userId"),"err");
dataFile = PLMSearchUtil.getLatestDataFile((String) session.getAttribute("userId"),"zip");
if(isPhotoDownloadAvailable){
	photoFile = PLMSearchUtil.getLatestPhotoFile((String) session.getAttribute("userId"),"zip");
}

if(inprogressFile != null){
	inProgressExists = true;
}
if(errFile != null ){
	errExists = true;
}
if(dataFile != null || (isPhotoDownloadAvailable && photoFile !=null)){
	prevExists = true;
}
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title>Data Download Page</title>
		<script src="media/js/jquery-1.3.2.js" type="text/javascript"></script>
		<script type="text/javascript" src="media/js/ajax_js.js"></script>
		<link rel="stylesheet" href="media/style/main.css" type="text/css"/>
		<script src="media/js/plm_ohw.js" type="text/javascript"></script>
		<script type="text/javascript">
			function setFocus(){
				document.getElementById("lastupdatedate").focus();
			}
			function clearFields(){
				 document.getElementById("lastupdatedate").value ="";
			}
			function validateDate(bDate){
				if(bDate != '' && bDate.length != 10 ){
					alert("Please enter date in mm/dd/yyyy format");
					document.getElementById("lastupdatedate").value='';
					return false;
				}else{
					if((bDate.charAt(2)!='/') ||(bDate.charAt(5)!='/')){
						alert("Please enter date in mm/dd/yyyy format");
						 document.getElementById("lastupdatedate").value='';
						 return false;
					}
					var date = bDate.substring(0,2)+bDate.substring(3,5)+bDate.substring(6,10);
					if(isNaN(date)){
						alert("Please enter digits only");
						document.getElementById("lastupdatedate").value='';
						return false;
					}else{
						return true;
					}
				}
			}
			
			function validateAndDownloadData(){
				currDate = new Date();
				var exitDownload = 0;
				if(currDate.getMinutes() >= 59 || (currDate.getMinutes() >= 0 && currDate.getMinutes() <=3) || (currDate.getMinutes() >=19 && currDate.getMinutes() <= 23) || (currDate.getMinutes() >= 39 && currDate.getMinutes() <= 43)) {
					alert("Your request was interrupted by LEADS data refresh cycle. Please resubmit your download in 4 minutes.");
					var url1 = 'plm_controller.jsp' + '?N=0';
					parent.location.href = url1;
					exitDownload = 1;
				}
				//if the download is executed during partial update, do not display the messages.
				if (exitDownload != 1) {
					var inProgressExists = <%=inProgressExists%>;
					if(inProgressExists){
						alert("Data download is already in progress.\nPlease wait till currently running process completes to request for next download.");
						return false;
					}
					var sCaseNumber = '<%=session.getAttribute("case_no")%>';
					var sReason = '<%=session.getAttribute("reason_no")%>';
					var lastupdatedate = document.getElementById("lastupdatedate").value;
					var dateValid = validateDate(lastupdatedate);
					if(!dateValid){
						return false;
					}
					
					var selected = new Array(); 
					var mySelect = document.getElementById("County"); 
					for(var j = 0; j < mySelect.options.length; j++) { 
						if(mySelect.options[j].selected) { 
							selected.push(mySelect.options[j].value); 
						} 
					}					
					
					if(selected.length < 1) {
						alert("Minimum 1 county should be selected");
						return false;
					} else if (selected.length > 5) {
						alert("Maximum 5 counties can be selected");
						return false;
					}

					var url = "";
					url = "datadownload?lastupdatedate=" + lastupdatedate + "&casenumber=" + sCaseNumber + "&reason=" + sReason + "&county=" + selected.toString() + "&includeStateWidePAL=Y";
					var serverResponse = "";
					var resp = execute_get(url, false);
					if((resp == true)) {
						serverResponse = xmlHttp.responseText;
						if (serverResponse == "Success") {
							alert("Data download has been submitted and is in progress. Please select the “Data Download” link to retrieve the download data and photos. ");
						} else {
							alert("There are some technical difficulties with our webservice tool. Please try again later.");
						}
						var url1 = 'plm_controller.jsp' + '?N=0';
						parent.location.href = url1;
					}
				}
			}
			
			function downloadFile(fileName) {
				window.location.href = '<%=request.getContextPath()%>'+"/download.jsp?fileName="+fileName;
			}
		</script>
	</head>
	<body id="radialSearchbody" onload="javascript:setFocus();">
		<form name="radialSearch" id="radialSearch" action="#">
			<div id="radialSearchTop">
				<div id="helpdiv">
					<a href="JavaScript:plmHelpPopup('<%=request.getContextPath() %>/ohw_help.jsp?topic_id=data_download');"><img src="media/images/global/help_icon.gif" alt="" /></a>
				</div>
<%
if(inProgressExists || prevExists || errExists) {
	if(inProgressExists){
%>
				<div>
					Following requests are still in progress. Please check again to download the file/s.
					<ul>
<%
		Calendar createdDate = Calendar.getInstance();
		createdDate.setTimeInMillis(inprogressFile.lastModified());
		String sDataPhoto ="";
		if(inprogressFile.getName().indexOf("-Photo-")>-1){
			sDataPhoto = "Photo";
		}else if(inprogressFile.getName().indexOf("-Data-")>-1){
			sDataPhoto = "Data";
		}
%>
						<li><%= sDataPhoto %> - <%=sdf.format(createdDate.getTime())%></li>
					</ul>
				</div>
<%
	}
	if(prevExists){
%>
				<div>
					Download a Previous Database Update Request from the system
					<ul>
<%
		if(dataFile != null && photoFile!=null){
				Calendar createdDataFileDate = Calendar.getInstance();
				createdDataFileDate.setTimeInMillis(dataFile.lastModified());
				Calendar createdPhotoFileDate = Calendar.getInstance();
				createdPhotoFileDate.setTimeInMillis(photoFile.lastModified());
				double dataLengthKB = (dataFile.length()/1024);
				double photoLengthKB = (photoFile.length()/1024);
				DecimalFormat dataFilesize = new DecimalFormat("#0.0 KB");
				DecimalFormat photoFilesize = new DecimalFormat("#0.0 KB");
%>
						<li>
							<a style="color:#E17236" href="#" onClick="downloadFile('<%=dataFile.getName()%>')">Data - <%=sdf.format(createdDataFileDate.getTime())%>&nbsp;&nbsp;(<%=dataFilesize.format(dataLengthKB)%>)</a> : <a style="color:#E17236" href="#" onClick="downloadFile('<%=photoFile.getName()%>')">Photo - <%=sdf.format(createdPhotoFileDate.getTime())%>&nbsp;&nbsp;(<%=photoFilesize.format(photoLengthKB)%>)</a>
						</li>
<%
		}else if(dataFile!=null){
			Calendar createdDataFileDate = Calendar.getInstance();
			createdDataFileDate.setTimeInMillis(dataFile.lastModified());
			double dataLengthKB = (dataFile.length()/1024);
			DecimalFormat dataFilesize = new DecimalFormat("#0.0 KB");
%>
						<li>
							<a style="color:#E17236" href="#" onClick="downloadFile('<%=dataFile.getName()%>')">Data - <%=sdf.format(createdDataFileDate.getTime())%>&nbsp;&nbsp;(<%=dataFilesize.format(dataLengthKB)%>)</a>
						</li>
<%
		}else{
			Calendar createdPhotoFileDate = Calendar.getInstance();
			createdPhotoFileDate.setTimeInMillis(photoFile.lastModified());
			double photoLengthKB = (photoFile.length()/1024);
			DecimalFormat photoFilesize = new DecimalFormat("#0.0 KB");
%>
						<li>
							<a style="color:#E17236" href="#" onClick="downloadFile('<%=photoFile.getName()%>')">Photo - <%=sdf.format(createdPhotoFileDate.getTime())%>&nbsp;&nbsp;(<%=photoFilesize.format(photoLengthKB)%>)</a>
						</li>
<%
		}
%>
					</ul>
				</div>
<%
	}
	if(errExists){
%>
				<div>
					Requests with exceptions [Please create a new request]
					<ul>
<%
				Calendar createdErrFileDate = Calendar.getInstance();
				createdErrFileDate.setTimeInMillis(errFile.lastModified());
				HashMap<String,String> hm = PLMDataDownloadUtil.getErrorMessageForDownload(errFile);
				Iterator<String> iterator = hm.keySet().iterator();
				String errCode = "";
				String errMsg = "";
				String sDataPhoto = "";
				if(errFile.getName().contains("photo") || errFile.getName().contains("Photo")){
					sDataPhoto = "Photo";
				}else{
					sDataPhoto = "Data";
				}
				while( iterator.hasNext() )
				{
					errCode = iterator.next();
					errMsg = hm.get(errCode);
%>
						<li style="color:#FF0000;"><%=sDataPhoto%> [<%=sdf.format(createdErrFileDate.getTime())%> : <%=errMsg%>]</li>
<%
				}
%>
					</ul>
				</div>
<%
	}
}
%>
				<br/>
				<div id="radialSearchTopfirstrow">
					<div><strong>Please consult the Data Download Guide in LMS under the Catalog for questions regarding selection criteria, data format, content, and updates.</strong></div>
					<br clear="all" />
				</div>
				<br/>
				<div id="radialSearchTopfirstrow">
					<div><strong>Enter Last Update Date</strong><span>{mm/dd/yyyy}</span></div>
					<input type="text" value="" id="lastupdatedate" name="lastupdatedate" maxLength="10" style="width:80px;"/>
					<br clear="all" />
				</div>
				<br/>
				<div id="radialSearchTopfirstrow">
					<div><strong>Choose up to 5 Counties</strong></div>
					<select id="County" name="County" multiple="multiple" size="5">				  		
<%
	List allCounties = PLMSearchUtil.readCountiesXML(session.getServletContext());
	if(allCounties != null && allCounties.size()>=0){
		String code="";
		String name="";
		String s = null;
		for(int i=0; i<allCounties.size(); i++) {
			s = allCounties.get(i).toString();
			code=s.substring(0,s.indexOf(":"));
			name=s.substring(s.indexOf(":")+1);
			//logger.debug(code + ":" + name);
%>
			<option value="<%=code%>"><%=name%></option>
<%
		}
	}
%>	
</select>
				<br clear="all" />
				<div><strong>Use CTRL or SHIFT keys to choose multiple counties</strong></div>
				</div>
			</div>
			<div id="advSearchMiddle2" style="width:315px;">
				<div id="advSearchbottom2main">
					<div class="advsearchbutton"><input type="image" name="find" value="Search" onclick="javascript:validateAndDownloadData();" src="media/images/global/download_button.gif"/></div>
					<div class="advsearchbutton"><input type="image" name="clearfields" value="ClearFields" onclick="javascript:clearFields();" src="media/images/global/clear_fields_button.gif"/></div>
					<div class="advsearchbutton"><input type="image" name="close" value="close" onclick="javascript:parent.tb_remove();" src="media/images/global/cancel_button.gif"/></div>
				</div>
			</div>
		</form>
	</body>
</html>