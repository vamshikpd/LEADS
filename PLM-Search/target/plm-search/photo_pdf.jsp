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
	String showid = request.getParameter("showid"); 
	String psize = request.getParameter("psize");
	String firstName =request.getParameter("firstName");
	String lastName =request.getParameter("lastName");	
	String cdcnum =request.getParameter("cdcNum");	
	String defaultPhoto = null;
	String rootPath = application.getResource("/").getPath();
	  if(rootPath  != null) {
		  int index = rootPath.indexOf("WEB-INF");
		  if(index >= 0 ) {
			  rootPath = rootPath.substring(0,index);
		  }
	  }	  
	  
	  if("t".equals(psize)) {
		  defaultPhoto=rootPath + PLMConstants.DEFAULT_THUMBNAIL_IMAGE_PATH;		  
	  } else {
		  defaultPhoto=rootPath + PLMConstants.DEFAULT_PHOTO_IMAGE_PATH;
	  }

	byte[] imgData = null;
	String filename = "Print_Photo_"+cdcnum+".pdf";
	response.reset();
	response.setContentType("application/pdf"); 
    response.setHeader("Content-Disposition", (new StringBuilder("inline; filename=")).append(filename).toString());

	try {
		com.lowagie.text.Document document = null;
		PdfPTable table = null;
		PdfPCell cell = null;
		PdfPCell newCell = null;
		com.lowagie.text.Font font12 = FontFactory.getFont(FontFactory.HELVETICA, 12);
		document = new com.lowagie.text.Document(PageSize.A4, 25, 25, 25, 25);

			PdfWriter.getInstance(document, response.getOutputStream());
			document.open();
			
			cell = new PdfPCell(new Phrase("default"));
			cell.setBorder(com.lowagie.text.Rectangle.LEFT | com.lowagie.text.Rectangle.RIGHT | com.lowagie.text.Rectangle.TOP | com.lowagie.text.Rectangle.BOTTOM);
			cell.setBorderWidth(2);
			cell.setBorderColor(new Color(130, 130, 130));
			cell.setPadding(5);

			// get mugshots and print
			table = new PdfPTable(1);
			// write header
			newCell = new PdfPCell(cell);
			imgData = PLMDatabaseUtil.getPhotoByID(showid, psize, defaultPhoto);
			com.lowagie.text.Image img1 = com.lowagie.text.Image.getInstance(imgData);
			newCell.setImage(img1);			
			table.addCell(newCell);
			
			HashMap info = PLMDatabaseUtil.getPhotoDetailsByID(showid);
			com.lowagie.text.Font fnt = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 18);
			Phrase newPhrase = new Phrase("CDC# : " + cdcnum, font12);
			newCell = new PdfPCell(newPhrase);
			table.addCell(newCell);
			newCell = new PdfPCell(new Phrase("Name : " + firstName + " " + lastName,font12));
			table.addCell(newCell);
			newCell = new PdfPCell(new Phrase("Photographer : " + (info.get("inserted_by")!=null?info.get("inserted_by"):""), font12));
			table.addCell(newCell);
			newCell = new PdfPCell(new Phrase("Date : " + (info.get("insert_date")!=null?info.get("insert_date"):""), font12));
			table.addCell(newCell);
			newCell = new PdfPCell(new Phrase("Type : " + (info.get("type_text")!=null?info.get("type_text"):""), font12));
			table.addCell(newCell);
			newCell = new PdfPCell(new Phrase("Description : " + (info.get("descr")!=null?info.get("descr"):""), font12));
			table.addCell(newCell);			

			document.add(table);
			document.close();
	} catch (Exception ex) {
		ex.printStackTrace();
	}
	 //out.clear();
	 out = pageContext.pushBody(); 

%>
