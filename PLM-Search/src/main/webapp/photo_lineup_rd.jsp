<%@ page import="com.endeca.navigation.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.text.*" %>
<%@ page import="java.net.*" %>
<%@ page import="com.util.format.*" %>
<%@ page import="com.endeca.ui.*" %>
<%@ page import="com.endeca.ui.constants.*" %>
<%@ page import="com.plm.util.database.PLMDatabaseUtil" %>
<%@ page import="org.apache.log4j.Logger" %>
<%@ page import="java.net.URLDecoder" %>
<%@ page import="com.plm.constants.PLMConstants" %>
<%@ page import="com.plm.util.PLMSearchUtil" %>
<%--<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">--%>
<!DOCTYPE html>
<html>
	<head>
		<title>Parolee Photo Lineup</title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>		
		<script type="text/javascript" src="media/js/jquery-1.3.2.js"></script>
		<script type="text/javascript" src="media/js/thickbox.js"></script>
		<script type="text/javascript" src="media/js/prototype.js"></script>
		<script type="text/javascript" src="media/js/endeca_util.js"></script>
		<script type="text/javascript" src="media/js/jquery-ui-1.7.2.custom.min.js"></script>
		<script type="text/javascript" src="media/js/jquery-1.3.2.min.js"></script>
		<script type="text/javascript" src="media/js/ui/ui.core.js"></script>
		<script type="text/javascript" src="media/js/ui/ui.draggable.js"></script>
		<script type="text/javascript" src="media/js/ui/ui.droppable.js"></script>
		<script type="text/javascript" src="media/js/ajax_js.js"></script>
		<script language="JavaScript">
<%
//	final Logger logger = Logger.getLogger(this.getClass());
	UrlGen urlg = new UrlGen(request.getQueryString(), "UTF-8");
	String individualURL ="plm_controller.jsp"+"?"+urlg;	//this url will be on each image to land on individual page with correct record
	Navigation nav = (Navigation)request.getAttribute("navigation");
	ENEQuery usq = (ENEQuery)request.getAttribute("eneQuery");
	String tempQueryString = URLDecoder.decode(request.getQueryString(),"UTF-8");
	String queryString = PLMSearchUtil.encodeGeoCodeCriteria(tempQueryString);

%>
/********* AJAX FINDSIMILAR LOOKAHEAD *************************/
			var photosAddedForLineup = 0;
			var suspectId = '';
			var resp;
			function getSimilarCount() {
				if ( xmlHttp ) {
					xmlHttp.abort();
				}
				var qrystr = prepURL();
				if(qrystr=="") {
					updateSimilarCount();
					return;
				}
				var url = "advancedxmlQuery"+qrystr; //modifed query by SK
				resp = execute_get_callback(url, true, updateSimilarCount);
			}
			function updateSimilarCount() {
				var textBoxElm = document.find_similar_form.similarCount;
				try {
					if((resp == true) && (xmlHttp.readyState == 4)) {
						var recElm = xmlHttp.responseXML.documentElement.getElementsByTagName("Records");
						var number = recElm.item(0).getAttribute("TotalNumERecs");
						if (number == "Error") {
							alert('No records found.');
							xmlHttp = null;
							var name = document.find_similar_form.lastchecked.value;
							document.getElementById(name).checked = false;

						} else {
							textBoxElm.value = recElm.item(0).getAttribute("TotalNumERecs");
							// This implementation for image line up
							var recordList = recElm.item(0).getElementsByTagName("Record");
							var totalRecords = recordList.length;
							var innerHTMLStr = "";
							for(var i =0; i < totalRecords; i++){
								var rec = recordList[i];
								var imagePath = rec.getAttribute("image");
								var spec = rec.getAttribute("spec");
								var url = '<%=individualURL%>'+"&R="+spec;
								innerHTMLStr +="<a href='"+url+"'><img border='1' src=/ftttf_ui"+imagePath+" width='50' height='50'/></a>&nbsp;"
							}
							document.getElementById("photoLine").innerHTML = innerHTMLStr;
							xmlHttp = null;

						}
					}
				} catch(exc) {
					textBoxElm.value = "";
				}
			}
			/********************* DONE AJAX FINDSIMILAR LOOKAHEAD *****************/
			function ConstructURL(url,removeterms,addterms) {
				var params = BuildURLArray(url);
				var newurl = "";
				for (var i=0; i<params.length; i++) {
					val = CheckArray(removeterms,params[i][0]);
					if (val == -1)
						newurl = newurl + "&" + params[i][0] + "=" + params[i][1];
				}
				for (var i=0; i<addterms.length; i++) {
					newurl = newurl + "&" + addterms[i];
				}
				if (newurl.length > 0) {
					newurl = newurl.substr(1);
					newurl = "?"+newurl;
				} else {
					newurl = "?N="+eneroot;
				}
				return newurl;
			}
			function prepURL() {
				var navigation = "";
				var oldURL = document.find_similar_form.oldURL.value;
				var selectedSMT = "";
				var selectedHeight = "";
				var selectedWeight = "";
				if(document.find_similar_form.SMTCode)
					selectedSMT = document.find_similar_form.SMTCode.options[document.find_similar_form.SMTCode.selectedIndex].value;
				if(document.find_similar_form.Height)
					selectedHeight = document.find_similar_form.Height.options[document.find_similar_form.Height.selectedIndex].value;
				if(document.find_similar_form.Weight)
					selectedWeight = document.find_similar_form.Weight.options[document.find_similar_form.Weight.selectedIndex].value;
				var NFQry = "";
				temp = document.find_similar_form.elements.length;
				for (var i=0; i < temp; i++) {
					if (document.find_similar_form.elements[i].checked) {
						var tempval = "";
						if(document.find_similar_form.elements[i].name == "Birth Year") {
							var byr=document.find_similar_form.elements[i].value;
							var minus7 = eval(byr) - 7;
							var plus7 = eval(byr) + 7;
							// NFQry = 'Birth_Yr|BTWN+'+minus7+'+'+plus7;
							NFQry = 'Birth_Yr%7CBTWN+'+minus7+'+'+plus7; // emil 2018-03-02 replace | with %7C to prevent errors at Tomcat
						} else {
							tempval = document.find_similar_form.elements[i].value;
						}
						if(navigation != "") {
							navigation += "+";
						}
						navigation += tempval;
					}
				}
				if(selectedSMT != "") {
					navigation += "+";
					navigation += selectedSMT;
				}
				if(selectedHeight != "") {
					navigation += "+";
					navigation += selectedHeight;
				}
				if(selectedWeight != "") {
					navigation += "+";
					navigation += selectedWeight;
				}
				if (navigation == "") {
					return "";
				}
				//var removeterms = ["R","Ne","No","D","Nf","N","Ntk","Ntt","Nty","A","Au","An"];
				// SNS Change : we are keeping R as we need it on Find Similar page always
				var removeterms = ["Ne","No","D","Nf","N","Ntk","Ntt","Nty","A","Au","An"];
				if(NFQry != "") {
					var addterms = ['N='+navigation+'&Nf='+NFQry];
				} else {
					var addterms = ['N='+navigation];
				}
				var newurl = ConstructURL(oldURL, removeterms, addterms);
				return newurl;
			}
			function FindSimilar() {
				var url = prepURL();
				if(url=="") {
					alert("Please select a category first.");
					return;
				}
				location.search = url;
			}
			function MoveSuspect(suspect){
					//alert(document.photo_lineup_form.spos.value + '---' + suspect);
					var spos = document.getElementById('spos').value;"src/main/webapp/refine_by.jsp"
					var suspectc;
					var sposc;
					var l, c, t;
					var ul = document.getElementById('photoselectedsimilarresultphoto');
					var ids = [];
					var lis = ul.getElementsByTagName('li');
					var li;
					if (spos == 0 || spos > lis.length){
						alert('Invalid position: Please enter position > 0 and < ' + (lis.length+1));
						return;
					}
					for (var i=0, iLen=lis.length; i<iLen; i++){
						li = lis[i];
						if (li.id) {
							ids.push(li.id);
							c = li.innerHTML;
							if (li.id == suspect){
								suspectc = c;
							}
							// if i == spos-1, then save the c to sposc
							if (i == (spos-1)){
								sposc = c;
							}
						}
					}
					//alert(ids);alert('suspectc = ' +suspectc);alert('sposc = ' + sposc);
					for (var i=0, iLen=lis.length; i<iLen; i++){
						li = lis[i];
						if (li.id) {
							ids.push(li.id);
							if (li.id == suspect){
								// replace with sposc
								li.innerHTML = sposc;
								// change id
								jQuery(li).attr("id",ids[spos-1]);
								// change style (border color)
								jQuery(li).attr("style", 'border:2px #889 solid; display: inline; margin:5px');
							}
							// if i == spos-1, then save the c to sposc
							if (i == (spos-1)){
								// replace with suspectc
								li.innerHTML = suspectc;
								jQuery(li).attr("id",suspect);
								jQuery(li).attr("style", 'border:2px solid rgb(204, 0, 0); display: inline; margin:5px');
								// change class so that it is not draggable anymore
								jQuery(li).attr("class",'currentsimilarresultphoto');
							}
						}
					}
					return;
			}
			function MoveRandom(suspect, spos){
					//alert('spos = ' + '---' + spos);
					//var spos = document.photo_lineup_form.spos.value;
					var suspectc;
					var sposc;
					var l, c, t;
					var ul = document.getElementById('photoselectedsimilarresultphoto');
					var ids = [];
					var lis = ul.getElementsByTagName('li');
					var li;
					if (spos == 0 || spos > lis.length){
						alert('Invalid position: Please enter position > 0 and < ' + (lis.length+1));
						return;
					}
					for (var i=0, iLen=lis.length; i<iLen; i++){
						li = lis[i];
						if (li.id) {
							ids.push(li.id);
							c = li.innerHTML;
							if (li.id == suspect){
								suspectc = c;
							}
							// if i == spos-1, then save the c to sposc
							if (i == (spos-1)){
								sposc = c;
							}
						}
					}
					//alert(ids);alert('suspectc = ' +suspectc);alert('sposc = ' + sposc);
					for (var i=0, iLen=lis.length; i<iLen; i++){
						li = lis[i];
						if (li.id) {
							ids.push(li.id);
							if (li.id == suspect){
								// replace with sposc
								li.innerHTML = sposc;
								// change id
								jQuery(li).attr("id",ids[spos-1]);
								// change style (border color)
								jQuery(li).attr("style", 'border:2px #889 solid; display: inline; margin:5px');
							}
							// if i == spos-1, then save the c to sposc
							if (i == (spos-1)){
								// replace with suspectc
								li.innerHTML = suspectc;
								jQuery(li).attr("id",suspect);
								jQuery(li).attr("style", 'border:2px solid rgb(204, 0, 0); display: inline; margin:5px');
								// change class so that it is not draggable anymore
								jQuery(li).attr("class",'currentsimilarresultphoto');
							}
						}
					}
					return;
			}
			function ScrambleLineup(suspect){
					var length = $("#photoselectedsimilarresultphoto li").length;
					for (var i=1; i<=length; i++){
						//alert('i = ' + i + 'length = ' + length + '--- call moveSuspect');
						var ran = Math.floor(Math.random()*length) + 1;
						MoveRandom(suspect, ran);
					}
					//alert(ran);
					return;
			}
			function customDragDrop(){
				// Photo drag-drop code
				$(function() {
					// there's the fs_results and the lineup
					var $fs_results = $('#fs_results'), $lineup = $('#photoselectedinnerdivcontent'), $fs_resultsContainer=$('#currentsimilarresultdivcontent');
					// let the fs_results items be draggable
					$('li',$fs_results).draggable({
						cancel: 'a.ui-icon',// clicking an icon won't initiate dragging
						revert: 'invalid', // when not dropped, the item will revert back to its initial position
						helper: 'clone',
						cursor: 'move'
					});
					$('li',$lineup).draggable({
						cancel: 'a.ui-icon',// clicking an icon won't initiate dragging
						revert: 'invalid', // when not dropped, the item will revert back to its initial position
						helper: 'clone',
						cursor: 'move'
					});
					// let the lineup be droppable, accepting the fs_results items
					$lineup.droppable({
						accept: '#fs_results > li',
						activeClass: 'ui-state-highlight',
						drop: function(ev, ui) {
							if(photosAddedForLineup < 5){
								if(checkIDExists(ui.draggable.attr("id"))==false){
									deleteImage(ui.draggable);
									photosAddedForLineup = photosAddedForLineup +1;
								}else{
									alert("already exists");
								}
							}
							else{
								alert("Only 5 photos can be added to the Photo Line-up");
							}
						}
					});
					// let the fs_results be droppable as well, accepting items from the lineup
					$fs_resultsContainer.droppable({
						accept: '#photoselectedsimilarresultphoto > li',
						activeClass: 'custom-state-active',
						drop: function(ev, ui) {
							photosAddedForLineup = photosAddedForLineup-1;
							recycleImage(ui.draggable);
						}
					});
					// image deletion function
					function deleteImage($item) {
						$item.fadeOut(function() {
							var $list = $('ul',$lineup).length ? $('ul',$lineup) : $('<ul class="photoselectedinnerphoto"/>').appendTo($lineup);
							$item.find('a.ui-icon-lineup').remove();
							$item.appendTo($list).fadeIn(function() {
								//$item.animate({ width: '96px' }).find('img').animate({ height: '100px' });
								//$item.find('img').animate({ height: '150px' }).animate({ width: '120px' });
							});
						});
					}
					// image recycle function
					function recycleImage($item) {
						if($item.attr('id') == suspectId){
							return;
						}
						$item.fadeOut(function() {
							$item.find('a.ui-icon-refresh').remove();
							$item.appendTo($fs_results).fadeIn(function() {
								//$item.find('img').animate({ height: '150px' }).animate({ width: '120px' });
							});
						});
					}
					// image preview function, demonstrating the ui.dialog used as a modal window
					function viewLargerImage($link) {
						var src = $link.attr('href');
						var title = $link.siblings('img').attr('alt');
						var $modal = $('img[src$="'+src+'"]');
						if ($modal.length) {
							$modal.dialog('open')
						} else {
							var img = $('<img alt="'+title+'" width="384" height="288" style="display:none;padding: 8px;" />')
								.attr('src',src).appendTo('body');
							setTimeout(function() {
								img.dialog({
										title: title,
										width: 400,
										modal: true
									});
							}, 1);
						}
					}
					// resolve the icons behavior with event delegation
					$('ul.fs_results > li').click(function(ev) {
						var $item = $(this);
						var $target = $(ev.target);
						if ($target.is('a.ui-icon-lineup')) {
							deleteImage($item);
						} else if ($target.is('a.ui-icon-zoomin')) {
							viewLargerImage($target);
						} else if ($target.is('a.ui-icon-refresh')) {
							recycleImage($item);
						}
						return false;
					});
				});
			}
			function getPhotoLineupResults(requestURLVal){
				var resp = execute_get(requestURLVal , false);
				if(resp == true) {
					serverResponse=xmlHttp.responseText;
					document.getElementById("currentsimilarresultdivcontent").innerHTML = serverResponse;
					customDragDrop();
				}
			}
			function getIDsFromLineup(){
				var ids=new Array();
				var ul = document.getElementById("photoselectedsimilarresultphoto");
				if (!ul.childNodes || ul.childNodes.length==0) { return; }
				// Iterate LIs
				for (var itemi=0;itemi<ul.childNodes.length;itemi++) {
					var item = ul.childNodes[itemi];
					if (item.nodeName == "LI") {
						ids[itemi]=item.id;
					}
				}
				return ids;
			}
			function checkIDExists(id){
				var ids = getIDsFromLineup();
				var exists = false;
				for (var cnt=0;cnt<ids.length;cnt++) {
					var actId = ids[cnt];
					if(actId == id){
						exists = true;
						break;
					}
				}
				return exists;
			}
		</script>
		<link href="media/style/thickbox.css" rel="stylesheet" type="text/css" />
		<link rel="stylesheet" href="media/style/main.css" type="text/css"/>
	</head>
	<body onload="Javascript:customDragDrop();">
		<div align="center" id="paroleedetailspage">
			<div id="Maindiv">
				<div id="Container" >
<!-- Middle Part Start -->
					<div id="Middle"  class="content_box" >
<%
	ENEQueryResults qr = (ENEQueryResults)request.getAttribute("eneQueryResults");
	PropertyContainer rec = qr.getERec();
	if(rec == null)
		rec = qr.getAggrERec();
	String spec = "";
	String imageLocation ="";
	PropertyMap tempPropsMap = null;
	if(rec instanceof ERec) {
		spec = ((ERec)rec).getSpec();
		tempPropsMap = ((ERec)rec).getProperties();	//sk
	}else {
		spec = ((AggrERec)rec).getSpec();
		tempPropsMap = ((AggrERec)rec).getProperties();	//sk
	}
%>
						<%@ include file="all_properties.jsp" %>
						<div  id="middletop">
							<div  id="middletopright">
								<div id="paroleedetailstopbutton">
									<%@ include file="details_tabs.jsp" %>
								</div>
								<div id="parolledetailsdiv">
									<div id="parolledetailsdivtop">
										<div id="parolledetailsdivtopmiddle">
											<div id="parolleheadertextdiv">Quick Information</div>
										</div>
									</div>
									<%@ include file="essence_info.jsp" %>
								</div>
								<div id="essenceinfodiv">
									<div id="essenceinfodivtop">
										<div id="parolledetailsdivtop">
										<div id="parolledetailsdivtopmiddle">
											<div id="parolleheadertextdiv">Search Similar By</div>
										</div>
									</div>
									</div>
								</div>
								<div class="similersearchboxcontent">
									<form name="find_similar_form" action="Javascript:FindSimilar()">
<%
	//int curCol = 1;
	// Create new navigation query request
	//UrlGen urlg = new UrlGen(request.getQueryString(), "UTF-8");
	//urlg.removeParam("R");
	if(request.getParameter("Au")!=null) {
		urlg.removeParam("Nu");
		urlg.addParam("Nu", request.getParameter("Au"));
		urlg.removeParam("Np");
		urlg.addParam("Np", "1");
	}
	urlg.removeParam("Au");
	urlg.removeParam("An");
	urlg.removeParam("A");
	if(request.getParameter("Ar")!=null){
		urlg.removeParam("Nr");
		urlg.addParam("Nr", request.getParameter("Ar"));
	}
	urlg.removeParam("du");
	String url = "plm_controller.jsp"+"?"+urlg;
%>
										<input type="hidden" name="oldURL" id="oldURL" value='<%= url %>' />
<%
	dimIter = dimArray.iterator();
	int colid = 1;
	String NVal = request.getParameter("N");
	String NFVal = request.getParameter("Nf");
	ERec erec = qr.getERec();
	AssocDimLocations smtADL = null;
	DimLocation smtDimLoc = null;
	//if(erec != null) {
	//	smtADL = erec.getDimValues().getAssocDimLocations("SMT Code");
	//}
%>
										<div id="similersearchboxrow">
<%
	while(dimIter.hasNext()) {
		Map.Entry pair = (Map.Entry)dimIter.next();
		DimLocation dimLoc = (DimLocation)pair.getValue();
		DimVal dimVal = dimLoc.getDimValue();
		String dimName = dimVal.getDimensionName();
		if ("Birth Year".equals(dimName) || "Ethnicity".equals(dimName) || "Gender".equals(dimName) || "Hair Color".equals(dimName) || "Eye Color".equals(dimName)) {
			String checked = "";
			if("Birth Year".equals(dimName)) {
				if(NFVal != null) {
					checked = "checked";
				}
%>
											<div class="col<%=colid%>">
												<input name="<%=dimName%>" type="checkbox" <%=checked%> value="<%=dimVal.getName()%>" id="<%=dimName%>" class="checkbox" />
												<label class="essenceinfoname" for="<%=dimName%>"><%=dimName%>:</label><%=dimVal.getName()%> (+/- 7 yrs)
											</div>
<%
				colid++;
			} else {
				if (NVal.indexOf(dimVal.getId()+"") != -1){
					checked = "checked";
				}
%>
											<div class="col<%=colid%>">
												<input name="<%=dimName%>" type="checkbox" <%=checked%> value="<%=dimVal.getId()%>" id="<%=dimName%>" class="checkbox" />
												<label class="essenceinfoname" for="<%=dimName%>"><%=dimName%>:</label><%=dimVal.getName()%>
											</div>
<%
				colid++;
			}
		}
		if(colid == 5) {
			colid=1;
%>
										</div>
										<div id="similersearchboxrow">
<%
		}
	} //end while
	if(colid == 5) {
%>
										</div>
<%
	}
	colid++;
	smtADL = erec.getDimValues().getAssocDimLocations("Height (+/- 2 inches)");
	if(smtADL != null) {
%>
										<div class="height">
											<label class="essenceinfoname">Height:</label><%=sHghtFt%>'<%=sHghtInch%>"
											<select name="Height" id="Height">
												<option value="">Height(+/-2 inches)</option>
<%									
		for(Iterator y = smtADL.iterator(); y.hasNext();) {
			smtDimLoc = (DimLocation)y.next();
			long id = smtDimLoc.getDimValue().getId();
			String dimName = smtDimLoc.getDimValue().getName();
			String strSelected = "";
			if (NVal.indexOf(id+"") != -1){
				strSelected = "selected";
			}
%>
												<option value="<%=id%>" <%=strSelected%>><%=dimName%></option>
<%
		}
%>
											</select>
										</div>
<%
	}
	colid++;
	smtADL = erec.getDimValues().getAssocDimLocations("Weight (+/- 10 lb)");
	if(smtADL != null) {
%>
										<div class="weight">
											<label class="essenceinfoname">Weight:</label><%=sWght%>lb
											<select name="Weight" id="Weight">
												<option value="">Weight(+/-10 lb)</option>
<%
		for(Iterator y = smtADL.iterator(); y.hasNext();) {
			smtDimLoc = (DimLocation)y.next();
			long id = smtDimLoc.getDimValue().getId();
			String dimName = smtDimLoc.getDimValue().getName();
			String strSelected = "";
			if (NVal.indexOf(id+"") != -1){
				strSelected = "selected";
			}else{
				strSelected = "";
			}
%>
												<option value="<%=id%>" <%=strSelected%>><%=dimName%></option>
<%
		}
%>
											</select>
										</div>
<%
	}
	colid++;
	smtADL = erec.getDimValues().getAssocDimLocations("SMT Code");
	if(smtADL != null) {
%>
										<div class="smt">
											<label class="essenceinfoname">SMT:</label>
											<select name="SMTCode" id="SMTCode">
												<option value="">Choose</option>
<%
		for(Iterator y = smtADL.iterator(); y.hasNext();) {
			smtDimLoc = (DimLocation)y.next();
			long id = smtDimLoc.getDimValue().getId();
			String dimName = smtDimLoc.getDimValue().getName();
			String strSelected = "";
			if (NVal.indexOf(id+"") != -1){
				strSelected = "selected";
			}else{
				strSelected = "";
			}
%>
												<option value="<%=id%>" <%=strSelected%>><%=dimName%></option>
<%
		}
%>
											</select>
										</div>
<%
	}
%>
									</div>
									<div class="searchsimilarbutton" align="right">
										<a href="javascript:FindSimilar()"><img src="media/images/global/search_similar_button.gif" alt="" border="0" /></a>
									</div>
								</form>
							</div>
							<div id="middletoprightbottom">
								<div class="photoselectedtopdiv">
									<div class="specifiypositionfield">
											<div>Specify position for suspect <input id="spos" name="spos" type="text" class="numbering"/></div>
									</div>
									<div class="photoselectedinnerbuttondiv">
										<div class="photoselectedinnerbutton">
											<div>
												<a href="javascript:MoveSuspect('<%=sCDCNum%>:<%=sFstNm%> <%=sLstNm%>')"><img src="media/images/parolee_details/move_suspect_button.gif" alt="" border="0" /></a>
											</div>
											<div class="photoselectedinnerbuttonmiddle">
												<a href="javascript:ScrambleLineup('<%=sCDCNum%>:<%=sFstNm%> <%=sLstNm%>')"><img src="media/images/parolee_details/scramble_lineup_button.gif" alt="" border="0" /></a>
											</div>
										</div>
										<div class="photoselectedinnerbutton">
											<div class="photoselectedinnerbuttonmiddle">
												<a class="thickbox" title="Print Lineup Horizontal" href="print_pdf_xfer.jsp?layout=h&amp;susp=<%=sCDCNum%>&amp;keepThis=true&amp;TB_iframe=true&amp;width=720;height=535"><img src="media/images/parolee_details/print_horizontal_lineup_button.gif" alt="" border="0" /></a>
											</div>
											<div class="photoselectedinnerbuttonmiddle">
												<a class="thickbox" title="Print Lineup Vertical" href="print_pdf_xfer.jsp?layout=v&amp;susp=<%=sCDCNum%>&amp;keepThis=true&amp;TB_iframe=true&amp;width=720;height=535"><img src="media/images/parolee_details/print_vertical_lineup_button.gif" alt="" border="0" /></a>
											</div>
										</div>
									</div>
								</div>
<%
	// Pagination code
	// Set constants
	ERecList recs = nav.getERecs();
	int inc 		= Integer.parseInt(UI_Props.getInstance().getValue(PLMConstants.DEFAULT_NUM_RESULTS_ENDECA_LINEUP));
	int maxPages 		= 10;
	int numLocalRecs 	= recs.size();
	long startRec 		= nav.getERecsOffset() + 1;
	long numTotalRecs 	= nav.getTotalNumERecs();
%>
								<div style="float:left; width:54%;">
									<div id="currentsimilarresultdivtop">
										<div id="currentsimilarresultdivtopmiddle">
											<div id="currentsimilarresulttextdiv">
												<img src="media/images/parolee_details/current_similar_search_result_header.gif" alt="" />
											</div>
											<div id="resultpagenumber" class="pagenumberlineup">&nbsp;&nbsp;&nbsp;&nbsp;<%out.print("Total Matches :"+(numTotalRecs-1));%></div>
										</div>
									</div>
<%
	int divHeight = 375;
	if(numLocalRecs>4 & numLocalRecs<7){
		divHeight=555;
	}else if(numLocalRecs>=7 & numLocalRecs<9){
		divHeight=725;
	}else if(numLocalRecs>=9){
		divHeight=600;
	}
%>
									<div id="currentsimilarresultdivcontent" class="currentsimilarresultdivcontent" style="height:<%=divHeight%>px;">
<%
	// Truncate the number of records if necessary
	//if (numLocalRecs > inc) {
	//	numLocalRecs = inc;
	//}
	// Only display paging controls if necessary
	if (numTotalRecs > inc) {
%>
										<div id="resultpagenumber" class="pagenumberlineup">&nbsp;&nbsp;&nbsp;&nbsp;
<%
		// Compute the page set and the number of pages needed
		int activePage 	= (int)((startRec+inc-1)/inc);
		int startPage 	= (int)((((startRec/(inc*maxPages))) * maxPages) + 1);
		int endPage 	= (int)((numTotalRecs/inc) + 1);
		// Exception for even number sets
		if ((numTotalRecs%inc) == 0) {
			endPage--;
		}
		// Truncate number of pages if overridden with local constant
		// and determine if there is a next page set
		int finalPage = endPage;
		if (endPage > (startPage + maxPages - 1)) {
			endPage = startPage + maxPages - 1;
		}
		// Determine if there is a previous page set
		if (startPage > 1) {
			// Create previous page set request
			String off = Long.toString((startPage - maxPages - 1)*inc);
			urlg = new UrlGen(request.getQueryString(), "UTF-8");
			urlg.removeParam("No");
			urlg.addParam("No",off);
			urlg.removeParam("sid");
			urlg.removeParam("in_dym");
			urlg.removeParam("in_dim_search");
			urlg.addParam("sid",(String)request.getAttribute("sid"));
			url = "GeneratePhotoLineupServlet"+"?"+urlg;
%>
											<a href="#" onclick="javascript:getPhotoLineupResults('<%=url%>');return false;" style="color:rgb(0,64,128);"><<</a>&nbsp;
<%
		}
		// Determine if there is a previous page
		if (activePage > 1) {
			// Create previous page request
			String off = Long.toString(startRec - inc - 1);
			urlg = new UrlGen(request.getQueryString(), "UTF-8");
			urlg.removeParam("No");
			urlg.addParam("No",off);
			urlg.removeParam("sid");
			urlg.removeParam("in_dym");
			urlg.removeParam("in_dim_search");
			urlg.addParam("sid",(String)request.getAttribute("sid"));
			url = "GeneratePhotoLineupServlet"+"?"+urlg;
%>
											<a href="#" onclick="javascript:getPhotoLineupResults('<%=url%>');return false;" style="color:rgb(0,64,128);">Prev</a>&nbsp;
<%
		}
		// Create direct page index
		for (int i = startPage; i <= endPage; i++) {
			if (i == activePage) {
%>
											<b style="font-size:18px"><%= i %></b>&nbsp;
<%
			}else {
				// Create direct page request
				String off = Long.toString((i-1)*inc);
				urlg = new UrlGen(request.getQueryString(), "UTF-8");
				urlg.removeParam("No");
				urlg.addParam("No",off);
				urlg.removeParam("sid");
				urlg.removeParam("in_dym");
				urlg.removeParam("in_dim_search");
				urlg.addParam("sid",(String)request.getAttribute("sid"));
				url = "GeneratePhotoLineupServlet"+"?"+urlg;
%>
											<a href="#" onclick="javascript:getPhotoLineupResults('<%=url%>');return false;" style="color:rgb(0,64,128);"><%=i%></a>&nbsp;
<%
			}
		}
		// Determine if there is a next page
		if (finalPage > activePage){
			// Create next page request
			String off = Long.toString(startRec + inc - 1);
			urlg = new UrlGen(request.getQueryString(), "UTF-8");
			urlg.removeParam("No");
			urlg.addParam("No",off);
			urlg.removeParam("sid");
			urlg.removeParam("in_dym");
			urlg.removeParam("in_dim_search");
			urlg.addParam("sid",(String)request.getAttribute("sid"));
			url = "GeneratePhotoLineupServlet"+"?"+urlg;
%>
											<a href="#" onclick="javascript:getPhotoLineupResults('<%=url%>');return false;" style="color:rgb(0,64,128);">Next</a>&nbsp;
<%
		}
		// Determine if there is a next page set
		if (finalPage > (startPage + maxPages - 1)){
			// Create next page set
			String off = Long.toString(endPage*inc);
			urlg = new UrlGen(request.getQueryString(), "UTF-8");
			urlg.removeParam("No");
			urlg.addParam("No",off);
			urlg.removeParam("sid");
			urlg.removeParam("in_dym");
			urlg.removeParam("in_dim_search");
			urlg.addParam("sid",(String)request.getAttribute("sid"));
			url = "GeneratePhotoLineupServlet"+"?"+urlg;
%>
											<a href="#" onclick="javascript:getPhotoLineupResults('<%=url%>');return false;" style="color:rgb(0,64,128);">>></a>
<%
		}
%>
										</div>
<%
	}
%>
										<ul id="fs_results" class="currentsimilarresultphotobuttom">
<%
	// Get record list iterator, first from bulk export if it was specified
	Iterator recIter;
	if(usq.getNavRollupKey() == null) {
		recIter = nav.getBulkERecIter();
		if(recIter == null)
			recIter = nav.getERecs().iterator();
	}else {
		recIter = nav.getBulkAggrERecIter();
		if(recIter == null)
			recIter = nav.getAggrERecs().iterator();
	}
	// Loop over record list
	String lastName = "";
	int i=0;
	PropertyMap propsMap = null;
	String imageLoc = ""; //sk
	while(recIter.hasNext()) {
		i++;
		String fsim_spec = "";
		Object fsim_rec = recIter.next();
		if(fsim_rec instanceof ERec) {
			fsim_spec = ((ERec)fsim_rec).getSpec();
			propsMap = ((ERec)fsim_rec).getProperties();	//sk
		}else {
			fsim_spec = ((AggrERec)fsim_rec).getSpec();
			propsMap = ((ERec)fsim_rec).getProperties(); //sk
		}
		if (fsim_spec.equals(spec)){
			// don't show the suspect in find similar results
			continue;
		}
		showid = PLMDatabaseUtil.getPrimaryMugshotID(fsim_spec);
%>
											<li class="currentsimilarresultphoto" id="<%=fsim_spec%>:<%=propsMap.get("First Name")%> <%=propsMap.get("Last Name")%>" style="display:inline;border: 2px #889 solid; margin: 5px">
												<div class="photo" id="photo">
													<img alt="" src="image.jsp?showid=<%=showid%>&psize=p" width="120" height="150" title="<%=fsim_spec%>:<%=propsMap.get("First Name")%> <%=propsMap.get("Last Name")%>"/><br/>
												</div>
											</li>
<%
	}	// end while
%>
										</ul>
									</div>
								</div>
								<div style="float:left; width:45%; padding-left:5px">
									<div id="photoselectedinnerdiv">
										<div id="photoselectedinnerdivtop">
											<div id="photoselectedinnerdivtopmiddle">
												<div id="photoselectedinnertextdiv"></div>
												<img src="media/images/parolee_details/photo_selected_header.gif" alt="" />
											</div>
										</div>
										<div id="photoselectedinnerdivcontent" name="photoselectedinnerdivcontent" style="height:400px;">
											<!-- show the suspect to start with -->
											<%showid = PLMDatabaseUtil.getPrimaryMugshotID(sCDCNum); %>
											<div id="photoselectedinnerdivcontentdiv" style="margin:20px;">
												<ul id="photoselectedsimilarresultphoto">
													<li id="<%=sCDCNum%>:<%=sFstNm%> <%=sLstNm%>" class="currentsimilarresultphoto" style="display:inline;border: 2px #CC0000 solid; margin: 5px">
														<div class="photo" id="photo">
															<img alt="" src="image.jsp?showid=<%=showid%>&psize=p" height="150" width="120"/><br/>
														</div>
													</li>
												</ul>
											</div>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
<!-- Middle Part End -->
<!-- Footer Part Start -->
<!-- Footer Part End -->
			</div>
		</div>
	</div>
	<script>
	suspectId = '<%=sCDCNum%>:<%=sFstNm%> <%=sLstNm%>';
	</script>
	</body>
</html>