<%@ page errorPage="error.jsp" %>
<%@ page import = "java.io.*" %>
<%@ page import = "java.sql.*" %>
<%@ page import = "java.util.*" %>
<%@ page import="java.text.*" %>
<%@ page import="java.net.*" %>
<%@ page import="com.endeca.navigation.*" %>
<%@ page import="com.endeca.ui.*" %>
<%@ page import="com.endeca.ui.constants.*" %>
<%@page import = "com.util.http.QueryHandler"%>
<%@page import = "java.io.IOException"%>
<%@page import = "javax.servlet.ServletException"%>
<%@page import = "javax.servlet.http.*"%>
<%@page import = "com.endeca.ui.export.*"%>
<%@page import = "javax.xml.parsers.*"%>
<%@page import = "org.w3c.dom.*"%>

<%@page import = "java.io.*"%>
<%@page import = "java.awt.*"%>
<%@page import = "com.lowagie.text.*"%>
<%@page import = "com.lowagie.text.pdf.*"%>

<%@ page import="com.plm.util.database.PLMDatabaseUtil" %>
<%@ page import="com.plm.constants.PLMConstants" %>

<%
	Navigation nav = (Navigation)request.getAttribute("navigation");
	ENEQuery usq = (ENEQuery)request.getAttribute("eneQuery");
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

	String psize = request.getParameter("psize");
	String rootPath = application.getResource("/").getPath();
	if(rootPath  != null) {
		int index = rootPath.indexOf("WEB-INF");
		if(index >= 0 ) {
			rootPath = rootPath.substring(0,index);
		}
	}
	String defaultPhoto = rootPath + PLMConstants.DEFAULT_PHOTO_IMAGE_PATH;
	byte[] imgData = null;

	String filename = "PAL_poster_"+spec+".pdf";
	response.reset();
	response.setContentType("application/pdf"); 
    response.setHeader("Content-Disposition", (new StringBuilder("inline; filename=")).append(filename).toString());

	try {
		com.lowagie.text.Document document = null;
		PdfPTable table = null;
		PdfPCell cell = null;
		PdfPCell newCell = null;
		Phrase newPhrase = null;
		com.lowagie.text.Font font16 = FontFactory.getFont(FontFactory.HELVETICA, 16);
		com.lowagie.text.Font font12 = FontFactory.getFont(FontFactory.HELVETICA, 12);
		com.lowagie.text.Font font10 = FontFactory.getFont(FontFactory.HELVETICA, 10);
		com.lowagie.text.Font font8 = FontFactory.getFont(FontFactory.HELVETICA, 8);		
		com.lowagie.text.Font font6 = FontFactory.getFont(FontFactory.HELVETICA, 6);		
		
		document = new com.lowagie.text.Document(PageSize.A4, 5, 5, 5, 5);

		PdfWriter.getInstance(document, response.getOutputStream());
		document.open();
			
		cell = new PdfPCell(new Phrase(" "));
		cell.setBorder(com.lowagie.text.Rectangle.LEFT | com.lowagie.text.Rectangle.RIGHT | com.lowagie.text.Rectangle.TOP | com.lowagie.text.Rectangle.BOTTOM);
		cell.setBorderWidth(0);
		cell.setBorderColor(new Color(130, 130, 130));
		cell.setPadding(5);
		cell.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_LEFT);

		table = new PdfPTable(2);
		// write header
		newCell = new PdfPCell(cell);
		newPhrase = new Phrase("CALIFORNIA DEPARTMENT OF CORRECTIONS AND REHABILITATION", font6);
		newCell.setPhrase(newPhrase);
		table.addCell(newCell);		

		newCell = new PdfPCell(cell);
		newCell.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_RIGHT);
		newPhrase = new Phrase("DIVISION OF ADULT PAROLE OPERATIONS", font6);
		newCell.setPhrase(newPhrase);
		table.addCell(newCell);		
		// show current date
		DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		java.util.Date date = new java.util.Date();

		newCell = new PdfPCell(cell);
		newPhrase = new Phrase(dateFormat.format(date), font8);
		newCell.setPhrase(newPhrase);
		table.addCell(newCell);		
		newCell = new PdfPCell(cell);
		newPhrase = new Phrase(" ", font8);
		newCell.setPhrase(newPhrase);
		table.addCell(newCell);		

		document.add(table);
		
		// add next 2 rows
		table = new PdfPTable(1);
		newCell = new PdfPCell(cell);
		newCell.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_CENTER);
		newPhrase = new Phrase("PAROLEE AT LARGE", font12);
		newCell.setPhrase(newPhrase);
		table.addCell(newCell);	

		newCell = new PdfPCell(cell);
		newCell.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_CENTER);
		newPhrase = new Phrase("RSP", font10);
		newCell.setPhrase(newPhrase);
		table.addCell(newCell);	

		document.add(table);

		// show primary mug shot
		table = new PdfPTable(2);
		newCell = new PdfPCell(cell);
		String primID = PLMDatabaseUtil.getPrimaryMugshotID(spec);
		imgData = PLMDatabaseUtil.getPhotoByID(primID+"", psize, defaultPhoto);
		com.lowagie.text.Image img1 = com.lowagie.text.Image.getInstance(imgData);
		newCell.setImage(img1);
		table.addCell(newCell);

		newCell = new PdfPCell(cell);
		// TBD : following to be changed to side shot
		String secID = PLMDatabaseUtil.getSecondaryMugshotID(spec);
		imgData = PLMDatabaseUtil.getPhotoByID(secID+"", psize, defaultPhoto);
		img1 = com.lowagie.text.Image.getInstance(imgData);
		newCell.setImage(img1);
		table.addCell(newCell);
		
		// show date of picture
		newCell = new PdfPCell(cell);
		HashMap primHash = PLMDatabaseUtil.getPhotoDetailsByID(primID+"");
		if (primHash.get("insert_date") != null){
			newPhrase = new Phrase("Date Submitted: " + dateFormat.format((java.sql.Date)primHash.get("insert_date")), font8);
		}else{
			newPhrase = new Phrase(" ");
		}

		newCell.setPhrase(newPhrase);
		table.addCell(newCell);	

		newCell = new PdfPCell(cell);
		HashMap secHash = PLMDatabaseUtil.getPhotoDetailsByID(secID+"");
		if (secHash.get("insert_date") != null){
			newPhrase = new Phrase("Date Submitted: " + dateFormat.format((java.sql.Date)secHash.get("insert_date")), font8);
		}else{
			newPhrase = new Phrase(" ");
		}
		newCell.setPhrase(newPhrase);
		table.addCell(newCell);
		document.add(table);

		// show name
		table = new PdfPTable(1);
		newCell = new PdfPCell(cell);
		newCell.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_CENTER);
		String name = tempPropsMap.get("First Name") + " " + tempPropsMap.get("Last Name");
		newPhrase = new Phrase(name, font12);
		newCell.setPhrase(newPhrase);
		table.addCell(newCell);	

		document.add(table);

		// write details
		table = new PdfPTable(8);
		// cell 1
		newCell = new PdfPCell(cell);
		newPhrase = new Phrase("CDC#:", font8);
		newCell.setPhrase(newPhrase);
		table.addCell(newCell);		
		// cell 2
		newCell = new PdfPCell(cell);
		newPhrase = new Phrase(spec, font8);
		newCell.setPhrase(newPhrase);
		table.addCell(newCell);		

		// cell 3
		newCell = new PdfPCell(cell);
		newPhrase = new Phrase("DOB:", font8);
		newCell.setPhrase(newPhrase);
		table.addCell(newCell);		
		// cell 4
		newCell = new PdfPCell(cell);
		String strDiscDate = null;
		if ((String)tempPropsMap.get("Birth Date Display") != null){
			strDiscDate = (String)tempPropsMap.get("Birth Date Display");
		}else{
			strDiscDate = "";
		}
		newPhrase = new Phrase(strDiscDate, font8);
		newCell.setPhrase(newPhrase);
		table.addCell(newCell);		

		// cell 5
		newCell = new PdfPCell(cell);
		newPhrase = new Phrase("SEX:", font8);
		newCell.setPhrase(newPhrase);
		table.addCell(newCell);		
		// cell 6
		newCell = new PdfPCell(cell);
		newPhrase = new Phrase((String)tempPropsMap.get("Sex")!=null?(String)tempPropsMap.get("Sex"):"", font8);
		newCell.setPhrase(newPhrase);
		table.addCell(newCell);		

		// cell 7
		newCell = new PdfPCell(cell);
		newPhrase = new Phrase("RACE:", font8);
		newCell.setPhrase(newPhrase);
		table.addCell(newCell);		
		// cell 8
		newCell = new PdfPCell(cell);
		newPhrase = new Phrase((String)tempPropsMap.get("Race")!=null?(String)tempPropsMap.get("Race"):"", font8);
		newCell.setPhrase(newPhrase);
		table.addCell(newCell);		

		// cell 9
		newCell = new PdfPCell(cell);
		newPhrase = new Phrase("EYES:", font8);
		newCell.setPhrase(newPhrase);
		table.addCell(newCell);		
		// cell 10
		newCell = new PdfPCell(cell);
		newPhrase = new Phrase((String)tempPropsMap.get("Eyecolor")!=null?(String)tempPropsMap.get("Eyecolor"):"", font8);
		newCell.setPhrase(newPhrase);
		table.addCell(newCell);		

		// cell 11
		newCell = new PdfPCell(cell);
		newPhrase = new Phrase("HAIR:", font8);
		newCell.setPhrase(newPhrase);
		table.addCell(newCell);		
		// cell 12
		newCell = new PdfPCell(cell);
		newPhrase = new Phrase((String)tempPropsMap.get("Haircolor")!=null?(String)tempPropsMap.get("Haircolor"):"", font8);
		newCell.setPhrase(newPhrase);
		table.addCell(newCell);		

		// cell 13
		newCell = new PdfPCell(cell);
		newPhrase = new Phrase("HEIGHT:", font8);
		newCell.setPhrase(newPhrase);
		table.addCell(newCell);		
		// cell 14
		newCell = new PdfPCell(cell);
		String height = null;
	    if (tempPropsMap.get("Height Feet")!=null){
		  height = tempPropsMap.get("Height Feet") + "'" + tempPropsMap.get("Height Inches") + "\"";
		}else{
		  height = "";
		}
		newPhrase = new Phrase(height, font8);
		newCell.setPhrase(newPhrase);
		table.addCell(newCell);		

		// cell 15
		newCell = new PdfPCell(cell);
		newPhrase = new Phrase("WEIGHT:", font8);
		newCell.setPhrase(newPhrase);
		table.addCell(newCell);		
		// cell 16
		newCell = new PdfPCell(cell);
		newPhrase = new Phrase((String)tempPropsMap.get("P_Weight")!=null?(String)tempPropsMap.get("P_Weight"):"" + " " + "lb", font8);
		newCell.setPhrase(newPhrase);
		table.addCell(newCell);		

		// cell 17
		newCell = new PdfPCell(cell);
		newPhrase = new Phrase("CII#:", font8);
		newCell.setPhrase(newPhrase);
		table.addCell(newCell);		
		// cell 18
		newCell = new PdfPCell(cell);
		newPhrase = new Phrase((String)tempPropsMap.get("CII Number")!=null?(String)tempPropsMap.get("CII Number"):"", font8);
		newCell.setPhrase(newPhrase);
		table.addCell(newCell);		

		// cell 19
		newCell = new PdfPCell(cell);
		newPhrase = new Phrase("FBI#:", font8);
		newCell.setPhrase(newPhrase);
		table.addCell(newCell);		
		// cell 20
		newCell = new PdfPCell(cell);
		newPhrase = new Phrase((String)tempPropsMap.get("FBI Number")!=null?(String)tempPropsMap.get("FBI Number"):"", font8);
		newCell.setPhrase(newPhrase);
		table.addCell(newCell);		

		// cell 21
		newCell = new PdfPCell(cell);
		newPhrase = new Phrase("DRIVER LICENSE#:", font8);
		newCell.setPhrase(newPhrase);
		table.addCell(newCell);		
		// cell 22
		newCell = new PdfPCell(cell);
		newPhrase = new Phrase((String)tempPropsMap.get("Driver License Number")!=null?(String)tempPropsMap.get("Driver License Number"):"", font8);
		newCell.setPhrase(newPhrase);
		table.addCell(newCell);		

		// cell 23 - empty name
		newCell = new PdfPCell(cell);
		newPhrase = new Phrase(" ", font8);
		newCell.setPhrase(newPhrase);
		table.addCell(newCell);		
		// cell 24 - empty value
		newCell = new PdfPCell(cell);
		newPhrase = new Phrase(" ", font8);
		newCell.setPhrase(newPhrase);
		table.addCell(newCell);		
		
		document.add(table);

		// offense info
		table = new PdfPTable(2);
		newCell = new PdfPCell(cell);
		newPhrase = new Phrase("CONTROLLING OFFENSE:", font8);
		newCell.setPhrase(newPhrase);
		table.addCell(newCell);		

		newCell = new PdfPCell(cell);
		newPhrase = new Phrase("OFFENSE CODE:", font8);
		newCell.setPhrase(newPhrase);
		table.addCell(newCell);		

		// loop through
		Collection offenseInfoColl = tempPropsMap.getValues("Offense Information");
		if (offenseInfoColl != null && offenseInfoColl.size() > 0){
			Iterator offenseInfoCollIter = (offenseInfoColl != null)?offenseInfoColl.iterator():null;

			while (offenseInfoCollIter != null && offenseInfoCollIter.hasNext()){
				String offInfo = (String)offenseInfoCollIter.next();
				HashMap hmapOff = new HashMap();
				String[] offInfo_result = offInfo.split(PLMConstants.SEPARATOR);
				if (offInfo_result.length > 0 && (String)offInfo_result[0] != null && !((String)offInfo_result[0]).equals("")){
					hmapOff.put("Offense Code", (String)offInfo_result[0]);
				}else{
					hmapOff.put("Offense Code", "");
				}
				if (offInfo_result.length > 1 && (String)offInfo_result[1] != null && !((String)offInfo_result[1]).equals("")){
					hmapOff.put("Description", (String)offInfo_result[1]);
				}else{
					hmapOff.put("Description", "");
				}
				if (offInfo_result.length > 4 && (String)offInfo_result[4] != null && !((String)offInfo_result[4]).equals("")){
					hmapOff.put("Controlling Offense", (String)offInfo_result[4]);
				}else{
					hmapOff.put("Controlling Offense", "");
				}
				newCell = new PdfPCell(cell);
				newPhrase = new Phrase((String)hmapOff.get("Controlling Offense"), font8);
				newCell.setPhrase(newPhrase);
				table.addCell(newCell);	

				newCell = new PdfPCell(cell);
				newPhrase = new Phrase((String)hmapOff.get("Offense Code"), font8);
				newCell.setPhrase(newPhrase);
				table.addCell(newCell);	
			}
		}
		document.add(table);

		// display address
		//com.lowagie.text.Font font8u = FontFactory.getFont(FontFactory.HELVETICA, 8);	
		com.lowagie.text.Font font8u = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 8, com.lowagie.text.Font.UNDERLINE);

		table = new PdfPTable(1);
		newCell = new PdfPCell(cell);
		newPhrase = new Phrase("LAST ADDRESS", font8u);
		newCell.setPhrase(newPhrase);
		table.addCell(newCell);	
		
		document.add(table);

		table = new PdfPTable(4);
		newCell = new PdfPCell(cell);
		newPhrase = new Phrase("Start Date"+"\n\n"+"Map", font8u);
		newCell.setPhrase(newPhrase);
		table.addCell(newCell);	

		newCell = new PdfPCell(cell);
		newPhrase = new Phrase("Live With", font8u);
		newCell.setPhrase(newPhrase);
		table.addCell(newCell);	

		newCell = new PdfPCell(cell);
		newPhrase = new Phrase("Street"+"\n\n"+"City, State, Zip", font8u);
		newCell.setPhrase(newPhrase);
		table.addCell(newCell);	

		newCell = new PdfPCell(cell);
		newPhrase = new Phrase("Home Phone"+"\n\n"+"Cell Phone/MSG", font8u);
		newCell.setPhrase(newPhrase);
		table.addCell(newCell);	

		newCell = new PdfPCell(cell);
		if ((String)tempPropsMap.get("Address Effective Date")!=null){
			strDiscDate = (String)tempPropsMap.get("Address Effective Date");
		}else{
			strDiscDate = "";
		}

		newPhrase = new Phrase(strDiscDate, font8);
		Phrase n = new Phrase("\n");
		newPhrase.add(n);
		Phrase secPhrase = new Phrase((String)tempPropsMap.get("Map")!=null?(String)tempPropsMap.get("Map"):"", font8);
		newPhrase.add(secPhrase);
		newCell.setPhrase(newPhrase);
		table.addCell(newCell);		

		newCell = new PdfPCell(cell);
		newPhrase = new Phrase((String)tempPropsMap.get("Care of (live with)")!=null?(String)tempPropsMap.get("Care of (live with)"):"", font8);
		newCell.setPhrase(newPhrase);
		table.addCell(newCell);		

		newCell = new PdfPCell(cell);
		newPhrase = new Phrase((String)tempPropsMap.get("Street")!=null?(String)tempPropsMap.get("Street"):"", font8);
		newPhrase.add(n);
		secPhrase = new Phrase((String)tempPropsMap.get("City")!=null?(String)tempPropsMap.get("City"):"", font8);
		newPhrase.add(secPhrase);
		Phrase sp = new Phrase(" ");
		newPhrase.add(sp);
		secPhrase = new Phrase((String)tempPropsMap.get("State Code")!=null?(String)tempPropsMap.get("State Code"):"", font8);	
		newPhrase.add(secPhrase);
		newPhrase.add(sp);		
		
		secPhrase = new Phrase((String)tempPropsMap.get("Zip")!=null?(String)tempPropsMap.get("Zip"):"", font8);
		
		newPhrase.add(secPhrase);
		newCell.setPhrase(newPhrase);
		table.addCell(newCell);		

		newCell = new PdfPCell(cell);
		newPhrase = new Phrase((String)tempPropsMap.get("Full Phone")!=null?(String)tempPropsMap.get("Full Phone"):"", font8);
		newPhrase.add(n);
		table.addCell(newCell);		

		document.add(table);

		table = new PdfPTable(1);
		newCell = new PdfPCell(cell);
		newPhrase = new Phrase("SCARS, MARKS AND TATTOOS", font8u);
		newCell.setPhrase(newPhrase);
		table.addCell(newCell);	
		
		document.add(table);

		table = new PdfPTable(3);
		newCell = new PdfPCell(cell);
		newPhrase = new Phrase("SMT Code", font8u);
		newCell.setPhrase(newPhrase);
		table.addCell(newCell);	

		newCell = new PdfPCell(cell);
		newPhrase = new Phrase("Picture", font8u);
		newCell.setPhrase(newPhrase);
		table.addCell(newCell);	

		newCell = new PdfPCell(cell);
		newPhrase = new Phrase("Text", font8u);
		newCell.setPhrase(newPhrase);
		table.addCell(newCell);	
		// loop through
		Collection smtInfoColl = tempPropsMap.getValues("SMT Information");
		Iterator smtInfoCollIter = (smtInfoColl != null)?smtInfoColl.iterator():null;
		while (smtInfoCollIter != null && smtInfoCollIter.hasNext()){
			String smtInfo = (String)smtInfoCollIter.next();
			HashMap hmapSmt = new HashMap();
			//String[] smtInfo_result = smtInfo.split("\\<\\>");
			String[] smtInfo_result = smtInfo.split(PLMConstants.SEPARATOR);
				
			if (smtInfo_result.length > 0 && (String)smtInfo_result[0] != null && !((String)smtInfo_result[0]).equals("")){
				hmapSmt.put("Code/Location", (String)smtInfo_result[0]);
			}else{
				hmapSmt.put("Code/Location", "");
			}
			if (smtInfo_result.length > 2 && (String)smtInfo_result[2] != null && !((String)smtInfo_result[2]).equals("")){
				hmapSmt.put("Picture", (String)smtInfo_result[2]);
			}else{
				hmapSmt.put("Picture", "");
			}
			if (smtInfo_result.length > 3 && (String)smtInfo_result[3] != null && !((String)smtInfo_result[3]).equals("")){
				hmapSmt.put("Text", (String)smtInfo_result[3]);
			}else{
				hmapSmt.put("Text", "");
			}

			newCell = new PdfPCell(cell);
			newPhrase = new Phrase((String)hmapSmt.get("Code/Location"), font8);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);	

			newCell = new PdfPCell(cell);
			newPhrase = new Phrase((String)hmapSmt.get("Picture"), font8);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);	

			newCell = new PdfPCell(cell);
			newPhrase = new Phrase((String)hmapSmt.get("Text"), font8);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);	
		}
		document.add(table);

		table = new PdfPTable(1);
		newCell = new PdfPCell(cell);
		newPhrase = new Phrase("RECENT ALIASES", font8u);
		newCell.setPhrase(newPhrase);
		table.addCell(newCell);	
		
		document.add(table);

		table = new PdfPTable(3);
		newCell = new PdfPCell(cell);
		newPhrase = new Phrase("Last Name", font8u);
		newCell.setPhrase(newPhrase);
		table.addCell(newCell);	

		newCell = new PdfPCell(cell);
		newPhrase = new Phrase("First Name", font8u);
		newCell.setPhrase(newPhrase);
		table.addCell(newCell);	

		newCell = new PdfPCell(cell);
		newPhrase = new Phrase("Middle Name", font8u);
		newCell.setPhrase(newPhrase);
		table.addCell(newCell);	
		// loop through
		Collection aliasInfoColl = tempPropsMap.getValues("Alias Info");
		Iterator aliasInfoCollIter = (aliasInfoColl != null)?aliasInfoColl.iterator():null;
		while (aliasInfoCollIter != null && aliasInfoCollIter.hasNext()){
			String aliasInfo = (String)aliasInfoCollIter.next();
			HashMap hmapAlias = new HashMap();

			//String[] aliasInfo_result = aliasInfo.split("\\<\\>");
			String[] aliasInfo_result = aliasInfo.split(PLMConstants.SEPARATOR);
				
			if (aliasInfo_result.length > 0 && (String)aliasInfo_result[0] != null && !((String)aliasInfo_result[0]).equals("")){
				hmapAlias.put("Alias First Name", (String)aliasInfo_result[0]);
			}else{
				hmapAlias.put("Alias First Name", "");
			}
			if (aliasInfo_result.length > 1 && (String)aliasInfo_result[1] != null && !((String)aliasInfo_result[1]).equals("")){
				hmapAlias.put("Alias Middle Name", (String)aliasInfo_result[1]);
			}else{
				hmapAlias.put("Alias Middle Name", "");
			}
			if (aliasInfo_result.length > 2 && (String)aliasInfo_result[2] != null && !((String)aliasInfo_result[2]).equals("")){
				hmapAlias.put("Alias Last Name", (String)aliasInfo_result[2]);
			}else{
				hmapAlias.put("Alias Last Name", "");
			}

			newCell = new PdfPCell(cell);
			newPhrase = new Phrase((String)hmapAlias.get("Alias First Name"), font8);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);	

			newCell = new PdfPCell(cell);
			newPhrase = new Phrase((String)hmapAlias.get("Alias Middle Name"), font8);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);	

			newCell = new PdfPCell(cell);
			newPhrase = new Phrase((String)hmapAlias.get("Alias Last Name"), font8);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);	
		}
		document.add(table);

		table = new PdfPTable(1);
		newCell = new PdfPCell(cell);
		newPhrase = new Phrase("COMMENTS", font8u);
		newCell.setPhrase(newPhrase);
		table.addCell(newCell);	

		newCell = new PdfPCell(cell);
		newPhrase = new Phrase((String)tempPropsMap.get("Comments")!=null?(String)tempPropsMap.get("Comments"):"", font8);
		newCell.setPhrase(newPhrase);
		table.addCell(newCell);	

		document.add(table);

		table = new PdfPTable(1);
		newCell = new PdfPCell(cell);
		newPhrase = new Phrase("DO NOT ARREST, DETAIN, SEARCH, OR TAKE ANY ACTION BASED ON THIS DOCUMENT WITHOUT FIRST CONFIRMING THE PAROLEE'S CURRENT STATUS.", font12);
		newCell.setPhrase(newPhrase);
		table.addCell(newCell);	

		document.add(table);
		
		document.close();
	} catch (Exception ex) {
		ex.printStackTrace();
	}
	 out.clear();
	 out = pageContext.pushBody(); 

%>