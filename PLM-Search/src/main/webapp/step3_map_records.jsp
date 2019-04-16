<%
	//Search results
	// Check for search terms to do hit hightlighting on
	String[] searchTerms = null;
	if(request.getParameter("Ntt") != null) {
		String terms = "";
		Iterator recSrchIter = nav.getESearchReports().entrySet().iterator();
		while(recSrchIter.hasNext()) {
			ESearchReport searchReport = (ESearchReport)((Map.Entry)recSrchIter.next()).getValue();
			terms += " "+searchReport.getTerms();
			// Get the List of auto-correct values
			Iterator autoCorrectIter = searchReport.getAutoSuggestions().iterator();
			while(autoCorrectIter.hasNext()) {
				ESearchAutoSuggestion autoSug = (ESearchAutoSuggestion)autoCorrectIter.next();
				// Display autocorrect message
				terms += " "+autoSug.getTerms();
			}
		}
		terms = terms.replaceAll("(?i)\\s(and|or|not|near\\:)\\s", "\\s");
		searchTerms = terms.split("[^\\w\\d]+");
	}
	String sUrl = PLMSearchUtil.getFormattedPLMURL(request,"/plm_controller.jsp");
	PLMPlotPoints points = new PLMPlotPoints();
	// Build list of geocodes to display (first valid GEOCODE_MAX_DISPLAY)
	Iterator itr;
	Iterator itrNew;
	if(!"y".equalsIgnoreCase(request.getParameter("singleParoleeMap"))){
		if(usq.getNavRollupKey() == null) {
			itrNew= nav.getBulkERecIter();
			if(itrNew==null){
				itrNew=nav.getERecs().iterator();
			}
		}else{
			itrNew=nav.getBulkAggrERecIter();
			if(itrNew==null){
				itrNew=nav.getAggrERecs().iterator();
			}
		}
	}else{
		itrNew=recs.iterator();
	}
	int irec=0;
	int inrec=0;
	int number_of_rec=0;
	Map <Integer,String> geocodeMap= new HashMap <Integer,String>() ;
	Map <Integer,Object> recMap= new HashMap<Integer,Object>();
	List <Object> finalRecs=new ArrayList <Object>();
	//Set the geocode and the record object in two HashMaps
	while(itrNew!=null && itrNew.hasNext() && inrec< Integer.parseInt(UI_Props.getInstance().getValue(UI_Props.GEOCODE_MAX_DISPLAY))){
		//	Object rec = itr.next();
		Object rec=itrNew.next();
		// Get property map for record
		UnifiedPropertyMap propsMap = new UnifiedPropertyMap((PropertyContainer)rec,
		Boolean.valueOf(UI_Props.getInstance().getValue(UI_Props.ROLLUP_RECS)).booleanValue());
		String geocode = (String)propsMap.get(UI_Props.getInstance().getValue(UI_Props.GEOCODE));
		if (geocode != null) {
			geocodeMap.put(inrec, geocode);
			recMap.put(inrec,rec);
		}
		inrec++;
	}
	//Sorting hashmap by geocode
	List mapKeys = new ArrayList(geocodeMap.keySet());
	List mapValues = new ArrayList(geocodeMap.values());
	Collections.sort(mapValues);
	Collections.sort(mapKeys);
	LinkedHashMap sortedMap =new LinkedHashMap();
	Iterator valueIt = mapValues.iterator();
	while (valueIt.hasNext()) {
		Object val = valueIt.next();
		Iterator keyIt = mapKeys.iterator();
		while (keyIt.hasNext()) {
			Object key = keyIt.next();
			String comp1 = geocodeMap.get(key).toString();
			String comp2 = val.toString();
			if (comp1.equals(comp2)){
				geocodeMap.remove(key);
				mapKeys.remove(key);
				sortedMap.put((Integer)key, (String)val);
				break;
			}
		}
	}
	//Iterator over the KeySet
	Iterator mapIter= sortedMap.keySet().iterator();
	int counter=0;
	while(mapIter.hasNext()){
		//getting records for each key
		Object sortedRecs=recMap.get(mapIter.next());
		//putting the record into finalsortedlist
		finalRecs.add(sortedRecs);
		counter++;
	}
	itr=finalRecs.iterator();
	List <String> temp_array_list=new ArrayList();
	boolean samepointFlag;
	while(itr!=null && itr.hasNext() && irec < Integer.parseInt(UI_Props.getInstance().getValue(UI_Props.GEOCODE_MAX_DISPLAY))) {
		samepointFlag=false;
		Object rec = itr.next();
		String spec = "";
		if(rec instanceof ERec) {
			spec = ((ERec)rec).getSpec();
		} else {
			spec = ((AggrERec)rec).getSpec();
		}
		// Get property map for record
		UnifiedPropertyMap propsMap = new UnifiedPropertyMap((PropertyContainer)rec,
				Boolean.valueOf(UI_Props.getInstance().getValue(UI_Props.ROLLUP_RECS)).booleanValue());
		int pointsSize=points.size();
		String geocode = (String)propsMap.get(UI_Props.getInstance().getValue(UI_Props.GEOCODE));
		if (geocode != null) {
			if(temp_array_list.isEmpty()){
				temp_array_list.add(geocode);
			}else{
				//HardCoding to test the functionality
				//geocode="37.328263,-121.814903";
				//Compare the previous with this if equal and if not replace the geocode with current one
				String prevGeocode=temp_array_list.get(0);
				if(prevGeocode.equals(geocode)){
					PLMPlotPoint temp_point = new PLMPlotPoint();
					//Setting flag for encountering similar point
					samepointFlag=true;
					//get the previous point
					temp_point=	(PLMPlotPoint)points.get((pointsSize-1));
					// Constructing the Url and last name parameters
					UrlGen url_same = new UrlGen("", "UTF-8");
					url_same.addParam("R",spec);
					url_same.addParam("displayKey",(String)request.getParameter("displayKey"));
					url_same.addParam("sid",(String)request.getAttribute("sid"));
					String backN_same = request.getParameter("N");
					if(request.getParameter("fromTools")!=null && "y".equals(request.getParameter("fromTools"))){
						url_same.addParam("fromToolsListPage","y");
					}
					if(removeSearchQueryFromTools){
						url_same.addParam("removeSearchQueryFromTools", "y");
					}
					if(request.getParameter("Ntt") != null) {
						String terms_same = "";
						for(int t=0; t<searchTerms.length; t++) {
							terms_same += searchTerms[t]+" ";
						}
						url_same.addParam("hterms", terms_same.trim());
					}
					URL refURL_same =  null;
					if(sUrl!= null){
						refURL_same = new URL(sUrl+"?"+url_same);
					}
					String lastName_same = (String)propsMap.get(UI_Props.getInstance().getValue("LAST_NAME"));
					//logger.info("Index :: " + lastName.indexOf("'"));
					lastName_same = (lastName_same.indexOf("'") > 0) ? lastName_same.replace("'","\\\'") : lastName_same;
					//Get the description and append the current description
					//Get previous description
					String prev_description=temp_point.getDescription();
					//Append new desciption to old description
					String new_description=prev_description+
							"<br/></br><a href=\"javascript:formHref(\\'" + refURL_same + "\\');\">" + "<b>CDC #: " +spec+ "</a>" +
							"<br/>First Name:" + (String)propsMap.get(UI_Props.getInstance().getValue("FIRST_NAME")) +
							"<br/>Last Name: "+ lastName_same +
							"<br/>Status: "+ (String)propsMap.get(UI_Props.getInstance().getValue("STATUS")) +
							"<br/>Ethnticity: "+ (String)propsMap.get(UI_Props.getInstance().getValue("ETHNICITY")) +
							"<br/>Gender: "+ (String)propsMap.get(UI_Props.getInstance().getValue("GENDER")) +"</b>";
					//Set the new description in the previous point
					temp_point.setDescription(new_description);
					if(!temp_point.containsMultipleParolees()){
						temp_point.setMultipleParoleesFlag(true);
					}
					irec++;
				}
			}
			if(samepointFlag==false){
				// Build URL and alt-name tag for avatar on map
				PLMPlotPoint point = new PLMPlotPoint();
				temp_array_list.set(0,geocode);
				point.setLatitude(Double.parseDouble(geocode.substring(0, geocode.indexOf(","))));
				point.setLongitude(Double.parseDouble(geocode.substring(geocode.indexOf(",")+1)));
				UrlGen url = new UrlGen("", "UTF-8");
				url.addParam("R",spec);
				url.addParam("displayKey",(String)request.getParameter("displayKey"));
				url.addParam("sid",(String)request.getAttribute("sid"));
				// save the current N as backN for back to result page link from photo lineup page
				// Photo line up page filters find similar results using N value.  That N value need
				// to be replaced by the N that was existing on results page as user may have refined
				// the results and may have visited the parolee details page.
				String backN = request.getParameter("N");
				url.addParam("backN", backN);
				if(request.getParameter("fromTools")!=null && "y".equals(request.getParameter("fromTools"))){
					url.addParam("fromToolsListPage","y");
				}
				if(removeSearchQueryFromTools){
					url.addParam("removeSearchQueryFromTools", "y");
				}
				if(request.getParameter("Ntt") != null) {
					String terms = "";
					for(int t=0; t<searchTerms.length; t++) {
						terms += searchTerms[t]+" ";
					}
					url.addParam("hterms", terms.trim());
				}
				URL refURL =  null;
				if(sUrl!= null){
					refURL = new URL(sUrl+"?"+url);
				}
				String lastName = (String)propsMap.get(UI_Props.getInstance().getValue("LAST_NAME"));
				//logger.info("Index :: " + lastName.indexOf("'"));
				lastName = (lastName.indexOf("'") > 0) ? lastName.replace("'","\\\'") : lastName;
				//logger.info("Modified LASTNAME : " + lastName);
				point.setDescription(	"<a href=\"javascript:formHref(\\'" + refURL + "\\');\">" + "<b>CDC #: " +spec+ "</a>" +
					"<br/>First Name:" + (String)propsMap.get(UI_Props.getInstance().getValue("FIRST_NAME")) +
					"<br/>Last Name: "+ lastName +
					"<br/>Status: "+ (String)propsMap.get(UI_Props.getInstance().getValue("STATUS")) +
					"<br/>Ethnticity: "+ (String)propsMap.get(UI_Props.getInstance().getValue("ETHNICITY")) +
					"<br/>Gender: "+ (String)propsMap.get(UI_Props.getInstance().getValue("GENDER")) +"</b>");
				points.add(point);
				irec++;
			}//else
		}//geocode not null
	}//while
	double smallestLat;
	double smallestLng;
	double greatestLat;
	double greatestLng;
%>
	<script language="JavaScript">
		var map;
		var latlng;
		var centerLat;
		var centerLng;
		var d;
		var centerLatLng;
		var plotSchoolsChk;
		var midLat;
		var midLng;
		var zoomEndExecuted=0;
		var zoomLevel = 5;
		var lastEvent;
		 
		function reloadMap() {
			zoomEndExecuted=1;
			zoomLevel = map.getZoom();
			
			if(map.getZoom()>=14){
				if(plotSchoolsChk.disabled==true){
					plotSchoolsChk.disabled=false;
				}
<%
				if (request.getParameter("Nf") != null && queryString.indexOf("GCLT")>=0) {
%>
					loadLatsLongs(map,plotSchoolsChk.checked,centerLat,centerLng,'radialSearch');
<%
				} else {
%>
					loadLatsLongs(map,plotSchoolsChk.checked);
<%
				}
%>
			}else{
				if(map.getZoom()<14) {
					plotSchoolsChk.checked=false;
					plotSchoolsChk.disabled=true;
					if(schoolsHidden==false){
						showSchools(false);
					}
				}
			}
		}
		
		function plotRecords(map){
<%
			PLMPlotPoint tempPoint;
			if(points.size()!=0){
				tempPoint = (PLMPlotPoint)points.get(0);
				smallestLat = tempPoint.getLatitude();
				smallestLng = tempPoint.getLongitude();
				greatestLat = smallestLat;
				greatestLng = smallestLng;
			}else{
				smallestLat=0.0;
				smallestLng =0.0;
				greatestLat =0.0;
				greatestLng =0.0;
			}
			for (int j = 0; j < points.size(); j++) {
				tempPoint = (PLMPlotPoint)points.get(j);
				double lat = tempPoint.getLatitude();
				double lng = tempPoint.getLongitude();
				String url = "";
				if(lat<=smallestLat){
					smallestLat =lat;
				}
				if(lng<=smallestLng){
					smallestLng = lng;
				}
				if(lat>=greatestLat) {
					greatestLat =lat;
				}
				if(lng >= greatestLng){
					greatestLng =lng;
				}
%>
				// processes each record
				var point = new google.maps.LatLng(<%=lat%>,<%=lng%>);
				var singleImage = 'media/images/maps/single.png';
				var multipleImage = 'media/images/maps/multiple.png';
<%
				String sParoleeImage = "icon:singleImage";
				if(tempPoint.containsMultipleParolees()){
					sParoleeImage = "icon:multipleImage";
				}
%>
				var marker = new google.maps.Marker(
					{
						position: point,
						<%=sParoleeImage%>,
						map: map
					});
				marker=showMarkerInfo(marker,'<%=url%>','<%=tempPoint.getDescription()%>');
				// plots the processed record on the map
				marker.setMap(map);
<%
			}
%>
		}
		var jsIrec = '<%= irec %>';
		document.getElementById("plot_schools").disabled=true;
		// plots the records within the circle
<%
		if (request.getParameter("Nf") != null && queryString.indexOf("GCLT")>=0) {
			//replacing all %20 with '+'. This is because when we click on parolee detail page and come back the browser is replacing '+' with %20
			String qryStrForCirclePoints = PLMSearchUtil.decodeGeoCodeCriteria(queryString);
			int startPos = qryStrForCirclePoints.indexOf("GCLT");
			String nextChar = null;
			int endPos = 0;
			String CLat = null;
			String CLng = null;
			int endPosNext = 0;
			String dist = null;
			endPos = qryStrForCirclePoints.indexOf(",",startPos);
			CLat = qryStrForCirclePoints.substring(startPos+4,endPos);
			endPosNext = qryStrForCirclePoints.indexOf("+",endPos);
			CLng = qryStrForCirclePoints.substring(endPos + 1, endPosNext);
			dist = qryStrForCirclePoints.substring(endPosNext + 1, qryStrForCirclePoints.length());
			if(dist != null && !dist.equals("") && dist.indexOf("&") > -1) {
				dist = dist.substring(0,dist.indexOf("&"));
			}
			if(dist == null || dist.equals("")){
				dist ="0";
			}
			double midLat = smallestLat+(greatestLat-smallestLat)/2;
			double midLng = smallestLng+(greatestLng-smallestLng)/2;
			double d = Double.parseDouble(dist)/6378.8;
%>
		var centerLat = <%=CLat%>.toRad(); // radians
		var centerLng = <%=CLng%>.toRad(); // radians
		d = <%=dist%>/6378.8;
<%
		}
		String sDistance = "";
		double dCenterLat = 37.166111d;
		double dCenterLong = -119.449444d;
		if(request.getParameter("zoomLevel")!=null){
			sDistance=request.getParameter("zoomLevel");
		}else if(request.getParameter("distance")!=null){
			sDistance=request.getParameter("distance");
		}else{
			sDistance="5";
		}
		if(request.getParameter("centerPoint")!=null){
			String sCenter = request.getParameter("centerPoint");
			dCenterLat = Double.parseDouble(sCenter.substring(sCenter.indexOf("(")+1,sCenter.indexOf(",")).trim());
			dCenterLong = Double.parseDouble(sCenter.substring(sCenter.indexOf(",")+1,sCenter.indexOf(")")).trim());
		}else if(irec>0){
			dCenterLat = (smallestLat+(greatestLat-smallestLat)/2) ;
			dCenterLong = (smallestLng+(greatestLng-smallestLng)/2);
		}
%>
		latlng = new google.maps.LatLng(<%=dCenterLat%>, <%=dCenterLong%>);
		zoomLevel = setZoomLevel('<%=sDistance%>');
		var config= {
			zoom:zoomLevel,
			center:latlng,
			mapTypeId:google.maps.MapTypeId.ROADMAP,
			scrollwheel:false,
			disableContinuousZoom:true,
			disableDoubleClickZoom:true
		};
		map = new google.maps.Map(document.getElementById("mapimage"),config);
		plotRecords(map);
		createCirclePoints(centerLat,centerLng,d,map);
		plotSchoolsChk = document.getElementById("plot_schools");

		google.maps.event.addListener(map,'zoom_changed', function(){
			lastEvent = 'zoom_changed';
		});
		google.maps.event.addListener(map,'bounds_changed', function(){
			lastEvent = 'bounds_changed';
			});
		google.maps.event.addListener(map,'idle', function(){
			if((lastEvent == 'zoom_changed') || (lastEvent == 'bounds_changed')){
				reloadMap();
				lastEvent = '';
			}
		});
		
		function formHref(origHref){
			location.href=origHref + '&zoomLevel=' + map.getZoom()+ '&centerPoint=' + map.getCenter();
		}
	</script>