<%@ page errorPage="error.jsp" %>
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
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
	<head>
		<title>Parolee Photo Gallery</title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
		<script type="text/javascript" src="media/js/jquery-1.3.2.js"></script>
		<script type="text/javascript" src="media/js/thickbox.js"></script>
		<script type="text/javascript" src="media/js/ajax_js.js"></script>
		<link href="media/style/thickbox.css" rel="stylesheet" type="text/css" />
		<link rel="stylesheet" href="media/style/main.css" type="text/css"/>
		<script type="text/javascript">
			function getSelectedPhoto(requestURLVal){
				var resp = execute_get(requestURLVal , false);
				if(resp == true) {
					serverResponse=xmlHttp.responseText;
					var temp = new Array();
					temp = serverResponse.split('~');
					document.getElementById('parolleprintpreviewdivcontentid').innerHTML = temp[0];
					document.getElementById('print_preview_photo_anchor_id').href = temp[1];
					document.getElementById('print_preview_nist_anchor_id').href = temp[2];
					document.getElementById('parolleprintpreviewcontent').innerHTML = temp[3];
				}
			}
		</script>
	</head>
<%
final Logger logger = Logger.getLogger(this.getClass());
	Navigation nav = (Navigation)request.getAttribute("navigation");
	ENEQuery usq = (ENEQuery)request.getAttribute("eneQuery");
	ENEQueryResults qr = (ENEQueryResults)request.getAttribute("eneQueryResults");
	PropertyContainer rec = qr.getERec();
	String tempQueryString = URLDecoder.decode(request.getQueryString(),"UTF-8");
	String queryString = PLMSearchUtil.encodeGeoCodeCriteria(tempQueryString);

	if(rec == null)
		rec = qr.getAggrERec();
	UnifiedPropertyMap uPropsMap = new UnifiedPropertyMap(rec,
			Boolean.valueOf(UI_Props.getInstance().getValue(UI_Props.ROLLUP_RECS)).booleanValue());
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
<%
	HashMap<String,String> info = new HashMap<String,String>();
	ArrayList<String> mugIds = new ArrayList<String>();
	ArrayList<String> smtIds = new ArrayList<String>();
	ArrayList<String> otherIds = new ArrayList<String>();
	String showidstr = request.getParameter("showid");
	int iPhotoCnt = PLMDatabaseUtil.getPhotoCount(spec);
	//logger.info("photo count--------------"+iPhotoCnt);
	if(iPhotoCnt > 0) {
		info = PLMDatabaseUtil.getPhotoDetailsByID(showidstr);
		mugIds = PLMDatabaseUtil.getPhotoIDs(spec, 1);
		smtIds = PLMDatabaseUtil.getPhotoIDs(spec, 2);
		otherIds = PLMDatabaseUtil.getPhotoIDs(spec, -1);
	}
	String sImgUrl = "";
	if (showidstr != null){				// when user clicks on image in photo gallery, we have ID in session
		sImgUrl = "image.jsp?showid=" + showidstr + "&psize=p";		
	}else{								// user clicking on photo tab...find image by cdcNum
		sImgUrl = "image.jsp?cdcNum=" + spec + "&psize=p";
	}
	//logger.info("photo_gallery_rd.jsp-----------------------"+sImgUrl);
%>
	<body>
		<div align="center" id="paroleedetailspage">
			<div id="Maindiv">
				<div id="Container" >
<!-- Header Part End -->
<!-- Middle Part Start -->
					<div id="Middle"  class="content_box" >
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
								<div id="middletoprightbottom">
									<div id="parolleprintpreviewdiv">
										<div id="parolleprintpreviewdivtop">
											<div id="parolleprintpreviewdivtopmiddle">
												<div id="parolleprintpreviewtextdiv"></div>
											</div>
										</div>
										<div id="parolleprintpreviewdivcontent">
											<div class="parolleprintpreviewphotobuttom">
												<div class="parolleprintpreviewphoto">
													<div id="parolleprintpreviewdivcontentid" class="photo">
														 <img alt="" src="<%=sImgUrl%>" />
													</div>
												</div>
<%
	UrlGen urlg = new UrlGen(queryString, "UTF-8");
	urlg.removeParam("Nrs");
	//urlg.removeParam("searchQuery");
	urlg.removeParam("ptab");
	urlg.removeParam("keepThis");
	urlg.addParam("ptab", "0");
	urlg.removeParam("psize");
	urlg.addParam("psize", "p");
	urlg.removeParam("PAL");
	urlg.addParam("PAL", "Y");
	urlg.removeParam("modal");
	urlg.removeParam("pdfdisptype");
	urlg.addParam("pdfdisptype", PLMConstants.PDF_DISPLAY_TYPE_PAL_POSTER);
	String url = "GeneratePDFServlet"+"?"+urlg;
	url = url + "&TB_iframe=true" + "&width=720" + "&height=535";
	UrlGen urlGalleryPrev = new UrlGen("", "UTF-8");
	urlGalleryPrev.addParam("cdcNum", spec);
	urlGalleryPrev.addParam("pdfdisptype", "gallery");
	urlGalleryPrev.addParam("psize", "p");
	String urlGalleryPreview 	= "GeneratePDFServlet?" + urlGalleryPrev 		+ "&TB_iframe=true" + "&width=720" + "&height=535";
	UrlGen urlPhotoNistPrev = new UrlGen("", "UTF-8");
	urlPhotoNistPrev.addParam("cdcNum",spec);
	urlPhotoNistPrev.addParam("showid",showidstr);
	urlPhotoNistPrev.addParam("firstName","" + sFstNm);
	urlPhotoNistPrev.addParam("lastName","" + sLstNm);
	urlPhotoNistPrev.addParam("pdfdisptype","photonist");
	urlPhotoNistPrev.addParam("psize","p");
	String urlPhotoPreview 		= "GeneratePDFServlet?" + urlPhotoNistPrev 		+ "&TB_iframe=true" + "&width=450" + "&height=545";
	urlPhotoNistPrev.removeParam("psize");
	urlPhotoNistPrev.addParam("psize","n");
	String urlNistPreview 		= "GeneratePDFServlet?" + urlPhotoNistPrev 		+ "&TB_iframe=true" + "&width=450" + "&height=545";
	if(iPhotoCnt > 0) {
%>
												<div class="parolleprintpreviewbutton">
													<div style="text-align: center; padding-top: 10px; font-weight: bold; font-size: 12px; color:#000000; padding-bottom: 10px;">PRINT</div>
													<div class="print_preview_photo_button">
														<a id="print_preview_photo_anchor_id" class="thickbox" title="Print Photo" href="<%=urlPhotoPreview%>">
															<img src="media/images/parolee_details/print_preview_photo_button.gif" alt="" border="0" />
														</a>
													</div>
													<div class="print_preview_gallery_button">
														<a class="thickbox" title="Print Photo Gallery" href="<%=urlGalleryPreview%>">
															<img src="media/images/parolee_details/print_preview_gallery_button.gif" alt="" border="0" />
														</a>
													</div>
													<div class="print_preview_nist_button">
														<a id="print_preview_nist_anchor_id" class="thickbox" title="Print NIST" href="<%=urlNistPreview%>">
															<img src="media/images/parolee_details/print_preview_nist_button.gif" alt="" border="0" />
														</a>
													</div>
<%
		if(sStatus.equals(PLMConstants.PAROLE_STATUS_SUSPENDED)) {
%>
													<div class="print_preview_pal_button">
														<a class="thickbox" title="Print PAL Poster" href="<%=url%>">
															<img src="media/images/parolee_details/print_preview_pal_button.gif" alt="" border="0" />
														</a>
													</div>
<%
		}
%>
												</div>
<%
	}
%>
											</div>
											<div id="parolleprintpreviewcontent" class="parolleprintpreviewcontent">
												<div><span class="essenceinfoname">CDC#:</span><%=spec%></div>
												<div><span class="essenceinfoname">Name:</span><%=sFstNm%>&nbsp;<%=sLstNm%></div>
												<div><span class="essenceinfoname">Photographer:</span><%=info.get("inserted_by")!=null?info.get("inserted_by"):"&nbsp;"%></div>
												<div><span class="essenceinfoname">Date:</span><%=info.get("insert_date")!=null?info.get("insert_date"):"&nbsp;"%></div>
												<div><span class="essenceinfoname">Type:</span><%=info.get("type_text")!=null?info.get("type_text"):"&nbsp;"%><%=info.get("subtype")!=null?":"+info.get("subtype"):"&nbsp;"%></div>
												<div><span class="essenceinfoname">Description:</span><%=info.get("descr")!=null?info.get("descr"):"&nbsp;"%></div>
											</div>
										</div>
									</div>
									<div id="mugsmtdiv">
										<div id="mugshotselecteddiv">
											<div id="mugshotselecteddivtop">
												<div id="mugshotselecteddivtopmiddle">
													<img src="media/images/parolee_details/mug_shot_selected_header.gif" alt="" />
												</div>
											</div>
											<div id="mugshotselecteddivcontent">
												<div class="mugshotselectedrow1">
													<div>
<%

	int mugIdsSize = mugIds.size();
	for (int i=0; i<mugIdsSize; i++){
		String mugid = mugIds.get(i);		
		urlg = new UrlGen(queryString, "UTF-8");
		urlg.removeParam("Nrs");
		urlg.removeParam("cdcnum");
		urlg.addParam("cdcnum",spec);
		urlg.removeParam("firstname");
		urlg.addParam("firstname",sFstNm);
		urlg.removeParam("lastname");
		urlg.addParam("lastname",sLstNm);
		urlg.removeParam("showid");
		urlg.addParam("showid",mugid+"");
		url = "GeneratePhotoGalleryServlet"+"?"+urlg;
%>
														<div>
															<div class="mugshotselectedphoto">
																<div class="photo">
																	<a href="#" onclick="javascript:getSelectedPhoto('<%=url%>');return false;">
																		<img src="image.jsp?showid=<%=mugid%>&psize=t" alt="" width="48" height="60"/>
																	</a>
																</div>
<%
		if (showidstr.equalsIgnoreCase(mugid+"")){
%>
																<div class="frame">
																	<a href="#" onclick="javascript:getSelectedPhoto('<%=url%>');return false;">
																		<img src="media/images/parolee_details/mugsmt_frame_on.gif" alt="" border="0" />
																	</a>
																</div>
<%
		}else{
%>
																<div class="frame">
																	<a href="#" onclick="javascript:getSelectedPhoto('<%=url%>');return false;">
																		<img src="media/images/parolee_details/mugsmt_frame.gif" alt="" border="0" />
																	</a>
																</div>
<%
		}
%>
															</div>
														</div>
<%
	}
%>
													</div>
												</div>
											</div>
											<div id="smtphotosdiv">
												<div id="smtphotosdivtop">
													<div id="smtphotosdivtopmiddle">
														<img src="media/images/parolee_details/smt_photos_header.gif" alt="" />
													</div>
												</div>
												<div id="smtphotosdivcontent">
													<div class="smtphotosrow1">
														<div>
<%
	int smtIdsSize = smtIds.size();
	for (int i=0; i<smtIdsSize; i++){
		String smtid = smtIds.get(i);		
		urlg = new UrlGen(queryString, "UTF-8");
		urlg.removeParam("Nrs");
		urlg.removeParam("cdcnum");
		urlg.addParam("cdcnum",spec);
		urlg.removeParam("firstname");
		urlg.addParam("firstname",sFstNm);
		urlg.removeParam("lastname");
		urlg.addParam("lastname",sLstNm);
		urlg.removeParam("showid");
		urlg.addParam("showid",smtid+"");
		url = "GeneratePhotoGalleryServlet"+"?"+urlg;
%>
															<div>
																<div class="smtphotosphoto">
																	<div class="photo">
																		<a href="#" onclick="javascript:getSelectedPhoto('<%=url%>');return false;">
																			<img src="image.jsp?showid=<%=smtid%>&psize=t" alt="" width="48" height="60"/>
																		</a>
																	</div>
																	<div class="frame">
																		<a href="#" onclick="javascript:getSelectedPhoto('<%=url%>');return false;">
																			<img src="media/images/parolee_details/mugsmt_frame.gif" alt="" border="0" />
																		</a>
																	</div>
																</div>
															</div>
<%
	}
%>
														</div>
													</div>
												</div>
												<div id="smtphotosdiv">
													<div id="smtphotosdivtop">
														<div id="smtphotosdivtopmiddle">
															<img src="media/images/parolee_details/other_photos_header.gif" alt="" />
														</div>
													</div>
													<div id="smtphotosdivcontent">
														<div class="smtphotosrow1">
															<div>
<%
	int otherIdsSize = otherIds.size();
	for (int i=0; i<otherIdsSize; i++){
		String othid = otherIds.get(i);		
		urlg = new UrlGen(queryString, "UTF-8");
		urlg.removeParam("Nrs");
		urlg.removeParam("cdcnum");
		urlg.addParam("cdcnum",spec);
		urlg.removeParam("firstname");
		urlg.addParam("firstname",sFstNm);
		urlg.removeParam("lastname");
		urlg.addParam("lastname",sLstNm);
		urlg.removeParam("showid");
		urlg.addParam("showid",othid+"");
		url = "GeneratePhotoGalleryServlet"+"?"+urlg;
%>
																<div>
																	<div class="smtphotosphoto">
																		<div class="photo">
																			<a href="#" onclick="javascript:getSelectedPhoto('<%=url%>');return false;">
																				<img src="image.jsp?showid=<%=othid%>&psize=t" alt="" width="48" height="60"/>
																			</a>
																		</div>
																		<div class="frame">
																			<a href="#" onclick="javascript:getSelectedPhoto('<%=url%>');return false;">
																				<img src="media/images/parolee_details/mugsmt_frame.gif" alt="" border="0" />
																			</a>
																		</div>
																	</div>
																</div>
<%
	}
%>
															</div>
														</div>
													</div>
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
	</body>
</html>