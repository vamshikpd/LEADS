<%@ page errorPage="error.jsp" %>
<%@ page import = "java.io.*" %>
<%@ page import = "java.sql.*" %>
<%@ page import = "java.util.*" %>
<%@ page import="java.text.*" %>
<%@ page import="java.net.*" %>

<%@page import = "com.endeca.ui.constants.UI_Props"%>
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
	String cdcnum = request.getParameter("cdcNum"); 
	String psize = "p";
	byte[] imgData = null;
	int colspan = 3;
	int diff = 0;
	int loopcnt = 0;
	
	String rootPath = application.getResource("/").getPath();
	if(rootPath  != null) {
		int index = rootPath.indexOf("WEB-INF");
		if(index >= 0 ) {
			rootPath = rootPath.substring(0,index);
		}
	}
	String defaultPhoto = rootPath + PLMConstants.DEFAULT_PHOTO_IMAGE_PATH;

	String filename = "Photo_Gallery"+cdcnum+".pdf";
	response.reset();
	response.setContentType("application/pdf"); 
    response.setHeader("Content-Disposition", (new StringBuilder("inline; filename=")).append(filename).toString());

	try {
		com.lowagie.text.Document document = null;
		PdfPTable table = null;
		PdfPCell cell = null;
		PdfPCell newCell = null;

		document = new com.lowagie.text.Document(PageSize.A4.rotate(), 5, 5, 5, 5);
		PdfWriter.getInstance(document, response.getOutputStream());
		document.open();
		
		cell = new PdfPCell(new Phrase("default"));
		cell.setBorder(com.lowagie.text.Rectangle.LEFT | com.lowagie.text.Rectangle.RIGHT | com.lowagie.text.Rectangle.TOP | com.lowagie.text.Rectangle.BOTTOM);
		cell.setBorderWidth(2);
		cell.setBorderColor(new Color(130, 130, 130));
		cell.setPadding(5);

		// get mugshots and print
		ArrayList mugIds = PLMDatabaseUtil.getPhotoIDs( cdcnum, 1);
		
		if(mugIds.size() > 0) {
			table = new PdfPTable(colspan);
			// write header
			newCell = new PdfPCell(new Phrase("Mug Shots : CDC # " + cdcnum));
			newCell.setColspan(colspan);
			table.addCell(newCell);
	
			if(mugIds.size() > colspan) {
				diff = colspan - mugIds.size() % colspan;			
			} else {
				diff = colspan - mugIds.size();
			}
			loopcnt = mugIds.size() + diff;
			
			for (int j=0;j<loopcnt;j++){				
				newCell = new PdfPCell(cell);
				newCell.setFixedHeight(265f);
				if(j<mugIds.size()){ 
					//imgData = PLMDatabaseUtil.getPhotoByID(((Integer)mugIds.get(j)).intValue()+"",psize, defaultPhoto);
					imgData = PLMDatabaseUtil.getPhotoByID(mugIds.get(j)+"",psize, defaultPhoto);
					com.lowagie.text.Image img1 = com.lowagie.text.Image.getInstance(imgData);
					newCell.setImage(img1);
				}  else {
					newCell.setPhrase(new Phrase(""));
				}
				table.addCell(newCell);
			}
			document.add(table);	
		}

		// get smt shots and print
		ArrayList smtIds = PLMDatabaseUtil.getPhotoIDs( cdcnum, 2 );
		
		if(smtIds.size() > 0) {
			document.newPage();		
			table = new PdfPTable(colspan);
			// write header
			newCell = new PdfPCell(new Phrase("SMT Photos : CDC # " + cdcnum));
			newCell.setColspan(colspan);
			table.addCell(newCell);
			
			if(smtIds.size() > colspan) {
				diff = colspan - smtIds.size() % colspan;			
			} else {
				diff = colspan - smtIds.size();
			}
			loopcnt = smtIds.size() + diff;
			
			for (int j=0;j<loopcnt;j++){
				newCell = new PdfPCell(cell);
				newCell.setFixedHeight(265f);
				if(j<smtIds.size()){ 
					//imgData = PLMDatabaseUtil.getPhotoByID(((Integer)smtIds.get(j)).intValue()+"",psize, defaultPhoto);
					imgData = PLMDatabaseUtil.getPhotoByID(smtIds.get(j)+"",psize, defaultPhoto);
					com.lowagie.text.Image img1 = com.lowagie.text.Image.getInstance(imgData);
					newCell.setImage(img1);
				}  else {
					newCell.setPhrase(new Phrase(""));
				}			
				table.addCell(newCell);
			}
			document.add(table);		
		}	

		// get other shots and print
		ArrayList otherIds = PLMDatabaseUtil.getPhotoIDs( cdcnum, -1 );
		
		if(otherIds.size() > 0) {
			document.newPage();
			table = new PdfPTable(colspan);
			// write header
			newCell = new PdfPCell(new Phrase("Other Photos : CDC # " + cdcnum));
			newCell.setColspan(colspan);
			table.addCell(newCell);
			if(otherIds.size() > colspan) {
				diff = colspan - otherIds.size() % colspan;			
			} else {
				diff = colspan - otherIds.size();
			}
			loopcnt = otherIds.size() + diff;
			
			for (int j=0;j<loopcnt;j++){
				newCell = new PdfPCell(cell);
				newCell.setFixedHeight(265f);
				if(j<otherIds.size()){ 
					//imgData = PLMDatabaseUtil.getPhotoByID(((Integer)otherIds.get(j)).intValue()+"",psize, defaultPhoto) ;   
					imgData = PLMDatabaseUtil.getPhotoByID(otherIds.get(j)+"",psize, defaultPhoto) ;
					com.lowagie.text.Image img1 = com.lowagie.text.Image.getInstance(imgData);
					newCell.setImage(img1);
				}  else {
					newCell.setPhrase(new Phrase(""));
				}			
				table.addCell(newCell);
			}
			document.add(table);
		}
		document.close();
	} catch (Exception ex) {
		ex.printStackTrace();
	}
	 //out.clear();
	 out = pageContext.pushBody(); 

%>