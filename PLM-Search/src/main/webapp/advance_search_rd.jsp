<%@ page language="java" contentType="text/html;charset=UTF-8" %>
<%@ page import="com.endeca.navigation.*" %>
<%@ page import="java.text.*" %>
<%@ page import="java.util.*" %>
<%@ page import="com.endeca.ui.constants.*" %>
<%@ page import="com.endeca.ui.constants.UI_Props"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="com.plm.util.PLMSearchUtil" %>
<%@ page import="com.plm.google.*" %>
<%@ page import="org.apache.log4j.Logger" %>
<%
final Logger logger = Logger.getLogger(this.getClass());
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title>Quick Search</title>
		<script src="media/js/jquery-1.3.2.js" type="text/javascript"></script>
		<script type="text/javascript">
			jQuery(document).ready(function() {
				setTimeout(function(){
					$("form :input[type='text']:enabled:first").focus();
				},800);
				$("form").keypress(function (e) {
					if ((e.which && e.which == 13) || (e.keyCode && e.keyCode == 13)) {
						$("form :input[name='find']").click();
						return false;
					} else {
						return true;
					}
				});
			});
		</script>
		<script src="media/js/thickbox.js" type="text/javascript"></script>
		<script language="JavaScript" src="media/js/plm_ohw.js"></script>
		<script language="JavaScript" src="media/js/validate.js"></script>
		<script src="<%= GoogleMapUtil.getGoogleMapUrl(request.isSecure()) %>" type="text/javascript"></script>
		<script language="JavaScript" src="media/js/advance_search.js"></script>
		<link type="text/css" href="media/style/main.css" rel="stylesheet"/>
	</head>
	<body id="advsearchbody" onload="initialize()">
		<form name="advancedSearch" id="advancedSearch" action="#" onSubmit="advanceSearch('advancedSearch'); return false;">

			<!-- Top Section -->
			<div id="advSearchTop">
				<div id="helpdiv">
					<a href="JavaScript:plmHelpPopup('<%=request.getContextPath() %>/ohw_help.jsp?topic_id=advance_search');"><img src="media/images/global/help_icon.gif" alt="" /></a>
				</div>

				<!-- Top First Line  -- Name-->
				<div id="advSearchTopfirstrow">
					<div>
						<div>
							Last Name<br />
							<input name="Last Name" id="Last Name" type="text"/>
						</div>
					</div>
					<div id="advSearchTopMiddle">
						<div>
							First Name<br />
							<input name="First Name" id="First Name" type="text"/>
						</div>
					</div>
					<div>
						<div>
							Middle Name<br />
							<input name="Middle Name" id="Middle Name" type="text"/>
						</div>
					</div>
				</div>

				<!-- Top Second Line -- Alias -->
				<div>
					<div>
						<div>
							Alias Last Name<br />
							<input name="Alias Last Name" id="Alias Last Name" type="text"/>
						</div>
					</div>
					<div id="advSearchTopMiddle">
						<div>
							Alias First Name<br />
							<input  name="Alias First Name" id="Alias First Name" type="text"/>
						</div>
					</div>
					<div>
						<div>
							Moniker<br />
							<input name="Moniker Info" id="Moniker Info" type="text"/>
						</div>
					</div>
				</div>

				<!-- Top Third Line -- Residence -->
				<div> <!-- Begin line 3 -->
					<div>
						<div>
							Residence City<br />
							<input name="City" id="City" type="text"/>
						</div>
					</div>
					<div id="advSearchTopMiddle"> <!-- Begin advSearchTopMiddle -->
						<div>
							Residence County<br />
							<select name="County Name" id="County Name">
								<option value="Select A County">Residence County</option>
<%
									ArrayList allCountys = PLMSearchUtil.getAllCountys();
									if(allCountys!=null && allCountys.size() >= 0) {
										String name="";
										String value="";
										int idx = 0;
										for(int iter = 0;iter<allCountys.size();iter++){
											idx=allCountys.get(iter).toString().indexOf("|");
											name=allCountys.get(iter).toString().substring(0,idx);
											value=allCountys.get(iter).toString().substring(idx+1);
%>
								<option value="<%=value%>"><%=name%></option>
<%
										}
									}
%>
							</select>
						</div>
					</div><!-- End advSearchTopMiddle -->
					<div>
						<div>
							County of LLR<br />
							<select name="CountyOfLastLegalResidence" id="CountyOfLastLegalResidence">
								<option value="Select A County">County of LLR</option>
<%
									ArrayList allCOLLR = PLMSearchUtil.getAllCOLLRs();
									
									if(allCOLLR != null && allCOLLR.size() >= 0) {
										String name="";
										String value="";
										int idx = 0;
										for(int iter = 0;iter<allCOLLR.size();iter++){
											idx     = allCOLLR.get(iter).toString().indexOf("|");
											name = allCOLLR.get(iter).toString().substring(0,idx);
											value = allCOLLR.get(iter).toString().substring(idx+1);
%>
								<option value="<%=value%>"><%=name%></option>
<%
										}
									}
%>
							</select>
						</div>
					</div>
				</div><!-- End line 3 -->
				
				<div><!-- Begin line 3b -->
						<div>
						<div>
							Zip Code&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;CDC#<br />
							<input name="Zip" id="Zip" type="text" maxLength="5" style="width:70px;" onKeyUp="validateZipcode(this.value,'advancedSearch','Zip')"/>
							&nbsp;&nbsp;
							<input name="CDC Number" id="CDC Number" type="text" maxLength="6" style="width:70px;" />
						</div>
					</div>
				</div><!-- End line 3b -->

				<!-- Top Fourth Line -- Birth/height/weight -->
				<div>
					<div>
						<div>
							Birth State<br />
							<select name="Birth State Name" id="Birth State Name">
								<option value="Select A Birth State">Birth State</option>
<%
								ArrayList allBirthStates = PLMSearchUtil.getAllBirthStates();
									if(allBirthStates!=null && allBirthStates.size() >= 0) {
										String name="";
										String value="";
										int idx = 0;
										for(int iter = 0;iter<allBirthStates.size();iter++){
											idx=allBirthStates.get(iter).toString().indexOf("|");
											name=allBirthStates.get(iter).toString().substring(0,idx);
											value=allBirthStates.get(iter).toString().substring(idx+1);
%>
								<option value="<%=value%>"><%=name%></option>
<%
										}
									}
%>
							</select>
						</div>
					</div>
					<div id="advSearchTopMiddle" class="heightfield">
						<div>
							Height (+/- 2 inches)<br />
							<select name="Height (+/- 2 inches)" id="Height (+/- 2 inches)">
								<option value="">Height</option>
<%
									ArrayList allHeight = PLMSearchUtil.getAllHeight();
									if(allHeight!=null && allHeight.size() >= 0) {
										String name="";
										String value="";
										int idx = 0;
										for(int iter = 0;iter<allHeight.size();iter++){
											idx=allHeight.get(iter).toString().indexOf("|");
											name=allHeight.get(iter).toString().substring(0,idx);
											value=allHeight.get(iter).toString().substring(idx+1);
%>
								<option value="<%=value%>"><%=name%></option>
<%
										}
									}
%>
							</select>
						</div>
					</div>
					<div>
						<div class="weightfield">
							Weight (+/- 10 lb)<br />
							<select name="Weight (+/- 10 lb)" id="Weight (+/- 10 lb)">
								<option value="">Weight</option>
<%
								ArrayList allWeight = PLMSearchUtil.getAllWeight();
									if(allWeight!=null && allWeight.size() >= 0) {
										String name="";
										String value="";
										int idx = 0;
										for(int iter = 0;iter<allWeight.size();iter++){
											idx=allWeight.get(iter).toString().indexOf("|");
											name=allWeight.get(iter).toString().substring(0,idx);
											value=allWeight.get(iter).toString().substring(idx+1);
%>
								<option value="<%=value%>"><%=name%></option>
<%
										}
									}
%>
							</select>
						</div>
					</div>
				</div>

				<!-- Top Fifth Line -- Parolee Data -->
				<div>
					<div>
						<div>
							SSA#<br />
							<input name="SSA Number" id="SSA Number" type="text" style="width:70px;" />
						</div>
					</div>
					<div id="advSearchTopMiddlesmall">
						<div>
							CII#<br />
							<input name="CII Number" id="CII Number" type="text" style="width:70px;" />
						</div>
					</div>
					<div id="advSearchTopMiddlesmall2">
						<div>
							FBI#<br />
							<input name="FBI Number" id="FBI Number" type="text" style="width:70px;" />
						</div>
					</div>
					<div>
						<div>
							License Plate #<br />
							<input name="Vehicle License Plate" id="Vehicle License Plate" type="text" style="width:70px;" />
						</div>
					</div>
					<div class="birthdate">
						<div>
							Birth Date(mmddyyyy)<br />
							<input name="Birth Date Display" id="Birth Date Display" type="text" maxLength="8" class="w90" onKeyUp="validateDateOnKeyUp(this.value,'advancedSearch','Birth Date Display')"/>
						</div>
					</div>
				</div>

				<!--Top Sixth Line -- Parolee data -->
				<div>
					<div>
						<div>
							Parole Release Date Range<br />
							<div>
								<input name="daterange1" id="daterange1" type="text" maxLength="8" style="width:70px;" onKeyUp="validateDateOnKeyUp(this.value,'advancedSearch','daterange1')"/>
								&nbsp;&nbsp;
								<input name="daterange2" id="daterange2" type="text" maxLength="8" style="width:70px;"  onKeyUp="validateDateOnKeyUp(this.value,'advancedSearch','daterange2')"/>
							</div>
						</div>
					</div>
					<div id="advSearchTopMiddle" class="vehicleyear">
						<div>
							Vehicle Year (yyyy)<br />
							<div>
								<input name="vehyear1" id="vehyear1" type="text" maxLength="4" style="width:40px;" onKeyUp="validateDateOnKeyUp(this.value,'advancedSearch','vehyear1')"/>
								&nbsp;&nbsp; - &nbsp;&nbsp;
								<input name="vehyear2" id="vehyear2" type="text" maxLength="4" style="width:40px;" onKeyUp="validateDateOnKeyUp(this.value,'advancedSearch','vehyear2')"/>
							</div>
						</div>
					</div>
				</div>

				<!-- Top Seventh Line -- Race/Hair -->
				<div>
					<div  class="centertext">
						<div>
							Ethnicity
							<div>
								<select name="Race1" id="Race1" style="width:120px;">
									<option value="Ethnicity">Ethnicity</option>
<%
							ArrayList allEthnicity = PLMSearchUtil.getAllEthnicity();
							if(allEthnicity!=null && allEthnicity.size() >= 0) {
								for(int iter = 0;iter<allEthnicity.size();iter++){
%>
									<option value="<%= allEthnicity.get(iter)%>"><%= allEthnicity.get(iter)%></option>
<%
								}
							}
%>
								</select>
							&nbsp;OR&nbsp;
								<select name="Race2" id="Race2" style="width:120px;">
									<option value="Ethnicity">Ethnicity</option>
<%
							if(allEthnicity!=null && allEthnicity.size() >= 0){
								for(int iter = 0;iter<allEthnicity.size();iter++){
%>
									<option value="<%= allEthnicity.get(iter)%>"><%= allEthnicity.get(iter)%></option>
<%
								}
							}
%>
								</select>
							</div>
						</div>
						<div class="centertexthaircolor">
							Hair Color
							<div>
								<select name="HairColor1" id="HairColor1" style="width:100px;">
									<option value="Hair Color">Hair Color</option>
<%
									ArrayList allHairColor = PLMSearchUtil.getAllHairColor();
									if(allHairColor!=null && allHairColor.size() >= 0) {
										for(int iter = 0;iter<allHairColor.size();iter++){
%>
									<option value="<%= allHairColor.get(iter)%>"><%= allHairColor.get(iter)%></option>
<%
										}
									}
%>
								</select>
								&nbsp;OR&nbsp;
								<select name="HairColor2" id="HairColor2" style="width:100px;">
									<option value="Hair Color">Hair Color</option>
<%
									if(allHairColor!=null && allHairColor.size() >= 0) {
										for(int iter = 0;iter<allHairColor.size();iter++){
%>
									<option value="<%= allHairColor.get(iter)%>"><%= allHairColor.get(iter)%></option>
<%
										}
									}
%>
								</select>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div id="advSearchMiddle1">
				<div id="regFelonyNotify">
					<div><img src="media/images/global/grey_box_curve_top_left.gif" alt="" /></div>
					<div class="middle"><img src="media/images/global/registration_felony_notification_header.gif" alt="" /></div>
					<div class="regFelonyNotifyfloatr"><img src="media/images/global/grey_box_curve_top_right.gif" alt="" /></div>
				</div>
				<div id="regFelonyNotify1">
					<div class="mt23"><img src="media/images/global/lightgrey_box_curve_bottom_left.gif" alt="" /></div>
					<div class="middle">
						<input name="PC 290 REQ" type="checkbox" value="" class="firstinput" id="PC 290 REQ" onClick="setCheckBoxValue(this)" /><label for="PC290">PC290 Sex Off.</label> <input name="PC 457 REQ" type="checkbox" value="" id="PC 457 REQ" onClick="setCheckBoxValue(this)" /><label for="PC457">PC457.1 Arson </label>
						<input name="HS REQ" type="checkbox" value="" id="HS REQ" onClick="setCheckBoxValue(this)"/><label for="ES31590">HS 11590 Drugs</label> <input name="PC 3058 REQ" type="checkbox" value="" id="PC 3058 REQ" onClick="setCheckBoxValue(this)" /><label for="PC3058">PC3058.6 Violent Felon.</label>
					</div>
					<div class="mt23r"><img src="media/images/global/lightgrey_box_curve_bottom_right.gif" alt="" /></div>
				</div>
			</div>

			<!-- Middle Section -->
			<div id="advSearchMiddle2" class="smt">

			<!-- Middle Line 1 -- SMT -->
				<div id="advSearchMiddle2left">
					<div>SMT Type/Location</div>
					<div class="padb10">
						<select name="SMT_DESC1" id="SMT_DESC1">
							<option value="">SMT</option>
<%
								ArrayList allSmtDesc = PLMSearchUtil.getAllSmtDesc();
								if(allSmtDesc!=null && allSmtDesc.size() >= 0) {
									for(int iter = 0;iter<allSmtDesc.size();iter++){
%>
							<option value="<%= allSmtDesc.get(iter)%>"><%= allSmtDesc.get(iter)%></option>
<%
									}
								}
%>
						</select>
					</div>
				</div>
				<div class="andor1">
					<span><input name="smtdescradio" type="radio" id="smtdescand" value="and" class="radio" checked/>AND OR <input name="smtdescradio" type="radio" id="smtdescor" value="or"  class="radio"/></span>
				</div>
				<div id="advSearchMiddle2right">
					<div>SMT Type/Location</div>
					<div class="padb10">
						<select name="SMT_DESC2" id="SMT_DESC2">
							<option value="SMT">SMT</option>
<%
								allSmtDesc = PLMSearchUtil.getAllSmtDesc();
								if(allSmtDesc!=null && allSmtDesc.size() >= 0) {
									for(int iter = 0;iter<allSmtDesc.size();iter++){
%>
							<option value="<%= allSmtDesc.get(iter)%>"><%= allSmtDesc.get(iter)%></option>
<%
									}
								}
%>
						</select>
					</div>
				</div>
			</div>

			<!-- Middle Line 2 -- Tattoo -->
			<div id="advSearchMiddle2" class="tattoo">
				<div id="advSearchMiddle2left">
					<div class="floatl">
						<div>Picture of Tattoo</div>
						<div class="padb10"><input id ="SMT Picture1" name="SMT Picture1" type="text"/></div>
					</div>
					<div class="floatl" style="padding-right:5px; float:right;">
						<div>Text of Tattoo</div>
						<div class="padb10"><input id="SMT Text1" name="SMT Text1" type="text"/></div>
					</div>
				</div>
				<div id="advSearchMiddle2right">
					<div class="floatl">
						<div>Picture of Tattoo</div>
						<div class="padb10"><input id ="SMT Picture2" name="SMT Picture2" type="text"/></div>
					</div>
					<div class="floatl"  style="padding-right:5px; float:right;">
						<div>Text of Tattoo</div>
						<div class="padb10"><input id="SMT Text2" name="SMT Text2" type="text"/></div>
					</div>
				</div>
				<br clear="all">
				<hr color="#E8E6E7" size="1">
			</div>

			<!-- Middle Line 3 -- Commitment -->
			<div id="advSearchMiddle2" class="offence">
				<div id="advSearchMiddle2left">
					<div>Commitment Offense</div>
					<div class="padb10">
						<select name="Offense Code1" id="Offense Code1">
							<option value="Offense Code">Offense Code</option>
<%
								ArrayList allOffenseCodes = PLMSearchUtil.getAllOffenseCodes();
								if(allOffenseCodes!=null && allOffenseCodes.size() >= 0) {
									for(int iter = 0;iter<allOffenseCodes.size();iter++){
%>
							<option value="<%= allOffenseCodes.get(iter)%>"><%= allOffenseCodes.get(iter)%></option>
<%
									}
								}
%>
						</select>
					</div>
				</div>
				<div class="andor1">
					<span><input name="offenseradio" type="radio" id="offenseand" value="and" class="radio" checked/>AND OR <input name="offenseradio" type="radio" id="offenseor" value="or"  class="radio"/></span>
				</div>
				<div id="advSearchMiddle2right">
					<div>Commitment Offense</div>
					<div class="padb10">
						<select name="Offense Code2" id="Offense Code2">
							<option value="Offense Code">Offense Code</option>
<%
								allOffenseCodes = PLMSearchUtil.getAllOffenseCodes();
								if(allOffenseCodes!=null && allOffenseCodes.size() >= 0) {
									for(int iter = 0;iter<allOffenseCodes.size();iter++){
%>
							<option value="<%= allOffenseCodes.get(iter)%>"><%= allOffenseCodes.get(iter)%></option>
<%
									}
								}
%>
						</select>
					</div>
				</div>
			</div>

			<!-- Radial Search -->
			<div id="radialSearchTop">
				<div id="regFelonyNotify">
					<div class="middle"><img src="media/images/global/radial_search_advanced_search_header.gif" alt="" /></div>
				</div>

				<!-- Radial Line 1 -- Instructions -->
				<div id="refinebymiddle">
					<div id="refinebymiddlediv" style="width:578px">
						<div id="radialSearchTopfirstrow">
							<div style="width: 100%; text-align: center;"><strong>Enter a Location Address -OR- Street Intersections below</strong></div>
						</div>
						<br clear="all" />

				<!-- Radial Line 2 -- Location -->
						<div style="width:100%">
							<div style="float: left; width: auto; padding-top: 5px;">
								<div>
									Location Address<br />
									<input type="text" name="location" id="location" value="">
								</div>
							</div>
							<span style="width: auto; float: left; padding-top: 24px; padding-left: 20px; font-weight: bold;">- OR -</span>
							<div style="width: auto; clear: none; float: left; padding-left:20px; padding-top:5px;">
								<div>
									<div>
										Intersection Street 1<br />
										<input type="text" name="intStreet1" id="intStreet1" value="">
									</div>
								</div>
								<div id="radialSearchTopMiddle" style="padding-left:15px;">
									<div>
										Intersection Street 2<br />
										<input type="text" name="intStreet2" id="intStreet2" value="">
									</div>
								</div>
							</div>
						</div>

				<!-- Radial Line 3 -- Zip/radius -->
						<div style="border-top:1px solid #ccc; margin-top:20px; width:100%; padding-top:10px;">
							<div>
								<div>
									Zipcode<br />
									<input type="text" name="zipcode" id="zipcode" value="" onKeyUp="validateZipcode(this.value)" maxLength="5">
								</div>
							</div>
							<div id="radialSearchTopMiddle" style="float:left; padding-left:15px;">
								<div>
									Search Radius<br />
									<select name="miles" id='miles'>
										<option value='0'>Select Miles/Feet</option>
										<option value='15 Miles'>15 Miles</option>
										<option value='5 Miles'>5 Miles</option>
										<option value='1 Mile'>1 Mile</option>
										<option value='1000 Feet'>1000 Feet</option>
										<option value='500 Feet'>500 Feet</option>
									</select>
								</div>
							</div>
						</div>
					</div>

					<!-- Buttons -->
					<div id="advSearchMiddle2" class="radialbuttons">
						<div id="advSearchbottom2main" style="float:none">
							<div class="advsearchbutton"><input type="image" name="find" value="Search" src="media/images/global/search_button.gif"/></div>
							<div class="advsearchbutton"><input type="image" onClick="this.form.reset();return false;" name="clearfields" value="ClearFields" src="media/images/global/clear_fields_button.gif"/></div>
							<div class="advsearchbutton"><input title="Close" type="image" onClick="parent.tb_remove()" name="clearfields" value="ClearFields" src="media/images/global/cancel_button.gif"/></div>
						</div>
					</div>
				</div>
			</div>
		</form>
	</body>
</html>
