<%@ page import="com.endeca.navigation.*" %>
<%@ page import="java.text.*" %>
<%@ page import="java.util.*" %>
<%@ page import="com.endeca.ui.constants.*" %>
<%@ page import="com.endeca.ui.constants.UI_Props"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="com.plm.util.PLMSearchUtil" %>
<%@ page import="org.apache.log4j.Logger" %>
<%
	final Logger logger = Logger.getLogger(this.getClass());
%>
<html>
	<head>
		<meta name="viewport" content="width=device-width; initial-scale=1.0; maximum-scale=1.0;" />			
		<title>PC290 Registrant Search</title>
		<link href="media/style/main.css" rel="stylesheet" type="text/css" />
		<script language="JavaScript" src="javascript/ajax_js.js"></script>
	</head>
	<body>
		<div align="center">
			<div id="Maindiv">
				<div id="Container">
					<div id="Header">
						<div class="logo"><span class="logo1">PAROLE</span><span class="logo2">LEADS2.0</span></div>
						<div class="top">
							<div class="back"><a href="plm_mobile_controller.jsp?page=login">Back</a></div>
							<%@ include file="logout_include_mobile.jsp" %>
							<div class="home"><a href="plm_mobile_controller.jsp?page=login">Home</a></div>
						</div>
					</div>
					<div id="Middle">
						<div id="MainContainer">
							<form name="registrantNotificationSearch" >
								<div class="caseandreasonform">
									<div class="formbox">
										<div class="formboxtitle">PC290 Registrant Search</div>
										<div class="searchreasondrop">
											<div>County</div>
											<div>
												<select id="County" name="County" onchange="javascript:findCitiesForCounty(this.value);">
													<option name="" value="">Select County Name</option>
<%
	String scounty = null;
	if(request.getAttribute("County")!=null){
		scounty = (String) request.getAttribute("County");
	}else if(request.getParameter("County")!=null){
		scounty = (String) request.getParameter("County");
	}
	//logger.debug("scounty=" + scounty);
	ArrayList allCountys = PLMSearchUtil.getAllCounties();
	//logger.debug("allCountys=" + allCountys.size());
	String selected = "";
	if(allCountys != null && allCountys.size()>=0){
		String name="";
		String value="";
		int idx = 0;
		for(int iter = 0;iter<allCountys.size();iter++){
			idx=allCountys.get(iter).toString().indexOf("|");
			name=allCountys.get(iter).toString().substring(0,idx);
			value=allCountys.get(iter).toString().substring(idx+1);
			//logger.debug("allCountys:value=" + value);
			if(scounty!=null && value.equals(scounty)){
				selected = "selected";
			}else{
				selected = "";
			}
%>
													<option value="<%=value%>" <%=selected%>><%=name%></option>
<%
		}
	}
%>
												</select>&nbsp;<input type="submit" id="submit" name="submit" value="Get Cities" />
<%
	String userName= request.getHeader("USERID");
	String ip_address= request.getHeader("X-FORWARDED-FOR");
%>
											</div>
										</div>
										<div class="searchreasondrop">
											<div>City</div>
											<div>
												<select name="City" id="City">
										 			<option name="City Name" value="">Select City Name</option>
<%
	//logger.debug("scounty2=" + scounty);
	if(scounty!=null){
		ArrayList allCitiesForCounty = PLMSearchUtil.getCitiesForCounty(scounty);
		//logger.debug("allCitiesForCounty=" + allCitiesForCounty);
		if(allCitiesForCounty != null && allCitiesForCounty.size()>=0){
			String name="";
			String value="";
			int idx = 0;
			for(int iter = 0;iter<allCitiesForCounty.size();iter++){
				idx=allCitiesForCounty.get(iter).toString().indexOf("|");
				name=allCitiesForCounty.get(iter).toString().substring(0,idx);
				value=allCitiesForCounty.get(iter).toString().substring(idx+1);
				//logger.debug("allCitiesForCounty:value=" + value);
%>
													<option value="<%=value%>"><%=name%></option>
<%
			}
		}
	}else{
		ArrayList allCities = PLMSearchUtil.getAllCities();
		//logger.debug("allCities=" + allCities);
		if(allCities != null && allCities.size()>=0){
			String name="";
			String value="";
			int idx = 0;
			for(int iter = 0;iter<allCities.size();iter++){
				idx=allCities.get(iter).toString().indexOf("|");
				name=allCities.get(iter).toString().substring(0,idx);
				value=allCities.get(iter).toString().substring(idx+1);
				//logger.debug("allCities:value=" + value);
%>
													<option value="<%=value%>"><%=name%></option>
<%													
			}
		}
	}
%>
											 	</select>
											</div>
											<div class="gobutton"><input type="button" id="go" onclick="registrantNotifySearch('registrantNotificationSearch','<%=userName%>','<%=ip_address%>')" name="submit" value="Go" /></div>
										</div>
									</div>
								</div>
							</form>
						</div>
					</div>
					<div id="Footer"></div>
				</div>
			</div>
		</div>
	</body>
</html>
<script type="text/javascript">
var isAjaxEnabled = supportsAjax(); 
var s1 = document.getElementById("submit");
if(isAjaxEnabled==true){
	s1.style.visibility="hidden";
}else{
	s1.style.visibility="visible";
}

function findCitiesForCounty(selectedCounty){
	if(isAjaxEnabled==true){
		getCities(selectedCounty);
	}else{
		document.getElementById('submit').click();
	}
}
function registrantNotifySearch(formName,userName,ipAddress){
	var oldUrl = parent.location.href;
	var tab = "";
	if(oldUrl.indexOf("tab")>0){
		tab = oldUrl.substring(oldUrl.indexOf("tab")+4,oldUrl.indexOf("tab")+5);
	}else{
		tab="0";
	}
	var condition ='';
	var cityValue = '';
	var countyValue = '';
	var endecaMatchesQuery = '';

	//for city
	var cityIndex = document.forms[formName]['City'].selectedIndex;
	if(cityIndex ==0){
		cityValue ='';
	}else{
		//cityValue=document.forms[formName]['City'].options[cityIndex].text;
		cityValue=document.forms[formName]['City'].options[cityIndex].value;
	}

	//for county
	var countyIndex = document.forms[formName]['County'].selectedIndex;	
	if(countyIndex == 0){
		countyValue ='';
	}else{
		//countyValue=document.forms[formName]['County'].options[countyIndex].text;
		countyValue=document.forms[formName]['County'].options[countyIndex].value;
	}
	
	if(cityValue == '' && countyValue == ''){
		alert("Please Select a County");		
		return;
	}
	
	//var nURL = "0";
	var nURL = "";
	
	if(cityValue == ''){
		nURL = nURL + countyValue;
		//endecaMatchesQuery = endecaMatchesQuery+"endeca:matches(.,"+'\"County Name\"'+","+'\"'+countyValue+'\"'+") and ";		
	}/*else if(countyValue == ''){
		//endecaMatchesQuery = endecaMatchesQuery+"endeca:matches(.,"+'\"City\"'+","+'\"'+cityValue+'\"'+") and ";	
	}*/else{
		nURL = nURL + countyValue + "+" + cityValue;
		//endecaMatchesQuery = endecaMatchesQuery+"endeca:matches(., "+'\"City\"'+","+'\"'+cityValue+'\"'+") and " + endecaMatchesQuery+"endeca:matches(.," +'\"County Name\"'+","+'\"'+countyValue+'\"'+") and ";
	}
	endecaMatchesQuery = endecaMatchesQuery+"( endeca:matches(., "+'\"Action Type\"'+","+'\"'+"R"+'\"'+")"+" or endeca:matches(., "+'\"Has Address Changed\"'+","+'\"'+"Y"+'\"'+"))";

    // emil 2018-03-10 fix issue in IE: double quotes in request string cause 400 at Tomcat
    // replace double quotes in endecaMatchesQuery with %22 special character
    endecaMatchesQuery = endecaMatchesQuery.replace(/"/g, "%22");

	var startURL = "Nrs=collection()/record[";
	var endURL = "]";	   

	var finalQuery = startURL+endecaMatchesQuery+endURL;	
	var url  = 'plm_mobile_controller.jsp';
	url = url+'?N=' + nURL + '&tab='+tab+"&";
	url = url+finalQuery+'&page=searchresults&prevPage=registrantNotification';
	if(isAjaxEnabled==true){
		var ajaxUrl = "pc290Audit?county="+countyValue+"&city="+cityValue+"&userName="+userName+"&ipAddress="+ipAddress+"&qry_type=R";
		execute_get(ajaxUrl, true);
	}else{
		url = url + "&doAudit=true&county="+countyValue+"&city="+cityValue+"&userName="+userName+"&ipAddress="+ipAddress+"&qry_type=R";
	}
	top.location=url;
}
function supportsAjax() {
	var req;
	try { req = new XMLHttpRequest(); } catch(e) {}
	try { req = new ActiveXObject("Msxml2.XMLHTTP"); } catch (e) {}
	try { req = new ActiveXObject("Microsoft.XMLHTTP"); } catch (e) {}
	if(req)
		return true;
	else
		return false;
}
var resp;
function getResponse(){
	try {
		if(resp == true){
			var recElm = xmlHttp.responseXML.documentElement.getElementsByTagName("Records");
            var number = recElm.item(0).getAttribute("TotalNumERecs");
            if (number == "Error") {
                alert('No records found.');
                return;
            } else {
                // This implementation for image line up
				var recordList = recElm.item(0).getElementsByTagName("Record");
				var totalRecords = recordList.length;
				var innerHTMLStr = '';
				selectField = document.getElementById('City');
				selectField.options.length = 0;
				selectField.options[selectField.length] = new Option('City Name', 'city');
				for(var i =0; i <= totalRecords; i++){
					var rec = recordList[i];
					var city = rec.getAttribute("city");
					var name="";
					var value="";
					var idx = 0;
					idx=city.indexOf("|");
						name=city.substring(0,idx);
						value=city.substring(idx+1);
					selectField.options[selectField.length] = new Option(name, value);
				}
				xmlHttp = null;
                return;
            }
		}
	} catch(exc) {}
}
function getCities(value){
	var url = "getCities?county="+value;
	resp = execute_get(url, false);
	getResponse();
}
</script>