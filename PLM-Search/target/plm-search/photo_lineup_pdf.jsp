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
	String filename = "Photo_Lineup.pdf";
	response.reset();
	response.setContentType("application/pdf");
 
    response.setHeader("Content-Disposition", (new StringBuilder("inline; filename=")).append(filename).toString());

	try {
		/* the following code that has been commented works without image :-(
		String ht = request.getParameter("ht");
		if (ht == null)
		{
			ht =  "<html><head><title>My First Document</title><style type=\"text/css\"> b { color: red; } </style></head><body><p><b>Greetings Earthlings!</b>We've come for your Java.</p></body></html>";
		}
		ht = URLDecoder.decode(ht);
		ht = ht.replaceAll("><br>", "/><br/>");
		ht = ht.replaceAll("<br>", "<br/>");
		System.out.println(">>> "+ht);

		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = builder.parse(new StringBufferInputStream(ht));
		ITextRenderer renderer = new ITextRenderer();
		renderer.setDocument(doc, null);
		renderer.layout();
		OutputStream os = response.getOutputStream();
		renderer.createPDF(os);
		os.close();
		*/

		/// Temp  code above this line is working but not showing images :-(

		//com.lowagie.text.Rectangle pageSize = new com.lowagie.text.Rectangle(0,0,2382,3369);
		//com.lowagie.text.Document document = new com.lowagie.text.Document(pageSize);

		String layout = request.getParameter("layout"); 
		String pdpc = request.getParameter("pdpc"); 
		String susp = request.getParameter("susp"); 
		String[] cdcs = pdpc.split(",");
		String psize = "p";
		byte[] imgData = null;
		ArrayList images = new ArrayList();
		int suspIndex = -1;
		
		String rootPath = application.getResource("/").getPath();
		if(rootPath  != null) {
			int index = rootPath.indexOf("WEB-INF");
			if(index >= 0 ) {
				rootPath = rootPath.substring(0,index);
			}
		}
		String defaultPhoto = rootPath + PLMConstants.DEFAULT_PHOTO_IMAGE_PATH;

		for (int i=0; i<cdcs.length; i++){
			String cdcName = cdcs[i];
			String[] cdc = cdcName.split(":");

			try {  
			   String showid = PLMDatabaseUtil.getPrimaryMugshotID(cdc[0]);
			   // get the image from the database
			   imgData = PLMDatabaseUtil.getPhotoByID(showid+"",psize, defaultPhoto) ;   
			   com.lowagie.text.Image img1 = com.lowagie.text.Image.getInstance(imgData);
			   //img1.scaleAbsolute(170, 215);
			   images.add(img1);	
			   if (susp.equalsIgnoreCase(cdc[0])){
				   suspIndex = i;
			   }
			}
			catch (Exception e)
			{
			  e.printStackTrace();
			  throw e;
			}
		}
		int imagesArrSize = images.size();
		int rows = 0;
		int imgLoc = 0;
		com.lowagie.text.Document document = null;
		PdfPTable table = null;
		PdfPCell cell = null;
		PdfPCell newCell = null;
		if ("h".equalsIgnoreCase(layout)){
			document = new com.lowagie.text.Document(PageSize.A4.rotate(), 5, 5, 5, 5);
			// how many rows with 3 per row?

			// if remainder is 0, find rows by doing division
			// else use division +1
			if (imagesArrSize%3 == 0){
				rows = imagesArrSize/3;
			}else{
				rows = (imagesArrSize/3) + 1;
			}
		}else{
			document = new com.lowagie.text.Document(PageSize.A4, 50, 50, 5, 5);
			// how many rows with 2 per row?
			if (imagesArrSize%2 == 0){
				rows = imagesArrSize/2;
			}else{
				rows = (imagesArrSize/2) + 1;
			}
		}

		try {
			PdfWriter.getInstance(document, response.getOutputStream());
			document.open();
			
			cell = new PdfPCell(new Phrase("default"));
			cell.setBorder(com.lowagie.text.Rectangle.LEFT | com.lowagie.text.Rectangle.RIGHT | com.lowagie.text.Rectangle.TOP | com.lowagie.text.Rectangle.BOTTOM);
			cell.setBorderWidth(2);
			cell.setBorderColor(new Color(130, 130, 130));
			cell.setPadding(5);

			if ("h".equalsIgnoreCase(layout)){
				table = new PdfPTable(3);
				imgLoc = 0;
				for (int j=0;j<rows;j++){
					newCell = new PdfPCell(cell);
					newCell.setFixedHeight(265f);
					if (imagesArrSize > imgLoc){
						newCell.setImage((com.lowagie.text.Image)images.get(imgLoc));
					}
					table.addCell(newCell);
					//imgLoc = imgLoc + 1;
					newCell = new PdfPCell(cell);
					newCell.setFixedHeight(215f);
					if (imagesArrSize > (imgLoc+1)){
						newCell.setImage((com.lowagie.text.Image)images.get(imgLoc+1));
					}
					table.addCell(newCell);
					//imgLoc = imgLoc + 1;
					newCell = new PdfPCell(cell);
					newCell.setFixedHeight(215f);
					if (imagesArrSize > (imgLoc+2)){
						newCell.setImage((com.lowagie.text.Image)images.get(imgLoc+2));
					}
					table.addCell(newCell);
					
					newCell = new PdfPCell(cell);
					newCell.setPhrase(new Phrase("#"+(imgLoc+1)));
					table.addCell(newCell);

					newCell = new PdfPCell(cell);
					newCell.setPhrase(new Phrase("#"+(imgLoc+2)));
					table.addCell(newCell);

					newCell = new PdfPCell(cell);
					newCell.setPhrase(new Phrase("#"+(imgLoc+3)));
					table.addCell(newCell);
					imgLoc = imgLoc + 3;
				}
				document.add(table);
			}else{
				table = new PdfPTable(2);

				imgLoc = 0;
				for (int j=0;j<rows;j++){
					newCell = new PdfPCell(cell);
					if (imagesArrSize > imgLoc){
						newCell.setImage((com.lowagie.text.Image)images.get(imgLoc));
					}
					table.addCell(newCell);
					newCell = new PdfPCell(cell);
					if (imagesArrSize > (imgLoc+1)){
						newCell.setImage((com.lowagie.text.Image)images.get(imgLoc+1));
					}
					table.addCell(newCell);
					
					newCell = new PdfPCell(cell);
					newCell.setPhrase(new Phrase("#"+(imgLoc+1)));
					table.addCell(newCell);

					newCell = new PdfPCell(cell);
					newCell.setPhrase(new Phrase("#"+(imgLoc+2)));
					table.addCell(newCell);

					imgLoc = imgLoc + 2;
				}

				document.add(table);
			}
			document.newPage();

			if ("h".equalsIgnoreCase(layout)){			
				table = new PdfPTable(3);


				imgLoc = 0;
				for (int j=0;j<rows;j++){
					newCell = new PdfPCell(cell);
					newCell.setFixedHeight(265f);
					if (suspIndex == imgLoc){
						newCell.setBorderWidth(5);
						newCell.setBorderColor(new Color(204, 0, 0));
					}
					if (imagesArrSize > imgLoc){
						newCell.setImage((com.lowagie.text.Image)images.get(imgLoc));
					}
					table.addCell(newCell);

					newCell = new PdfPCell(cell);
					newCell.setFixedHeight(265f);
					if (suspIndex == (imgLoc+1)){
						newCell.setBorderWidth(5);
						newCell.setBorderColor(new Color(204, 0, 0));
					}
					if (imagesArrSize > (imgLoc+1)){
						newCell.setImage((com.lowagie.text.Image)images.get(imgLoc+1));
					}
					table.addCell(newCell);
					newCell = new PdfPCell(cell);
					newCell.setFixedHeight(265f);
					if (suspIndex == (imgLoc+2)){
						newCell.setBorderWidth(5);
						newCell.setBorderColor(new Color(204, 0, 0));
					}
					if (imagesArrSize > (imgLoc+2)){
						newCell.setImage((com.lowagie.text.Image)images.get(imgLoc+2));
					}
					table.addCell(newCell);
					newCell = new PdfPCell(cell);
					newCell.setPhrase(new Phrase(cdcs[imgLoc]));
					table.addCell(newCell);

					newCell = new PdfPCell(cell);
					if (imagesArrSize > (imgLoc+1)){
						newCell.setPhrase(new Phrase(cdcs[imgLoc+1]));
					}
					table.addCell(newCell);

					newCell = new PdfPCell(cell);
					if (imagesArrSize > (imgLoc+2)){
						newCell.setPhrase(new Phrase(cdcs[imgLoc+2]));
					}
					table.addCell(newCell);
					imgLoc = imgLoc + 3;
				}
				document.add(table);
			}else{
				table = new PdfPTable(2);

				imgLoc = 0;
				for (int j=0;j<rows;j++){
					newCell = new PdfPCell(cell);
					if (suspIndex == imgLoc){
						newCell.setBorderWidth(5);
						newCell.setBorderColor(new Color(204, 0, 0));
					}
					if (imagesArrSize > imgLoc){
						newCell.setImage((com.lowagie.text.Image)images.get(imgLoc));
					}
					table.addCell(newCell);
					newCell = new PdfPCell(cell);
					if (suspIndex == (imgLoc+1)){
						newCell.setBorderWidth(5);
						newCell.setBorderColor(new Color(204, 0, 0));
					}
					if (imagesArrSize > (imgLoc+1)){
						newCell.setImage((com.lowagie.text.Image)images.get(imgLoc+1));
					}
					table.addCell(newCell);
					
					newCell = new PdfPCell(cell);
					if (imagesArrSize > imgLoc){
						newCell.setPhrase(new Phrase(cdcs[imgLoc]));
					}
					table.addCell(newCell);

					newCell = new PdfPCell(cell);
					if (imagesArrSize > (imgLoc+1)){
						newCell.setPhrase(new Phrase(cdcs[imgLoc+1]));
					}
					table.addCell(newCell);

					imgLoc = imgLoc + 2;
				}

				document.add(table);
			}
		}catch(DocumentException de) {
			System.err.println(de.getMessage());
		}catch(IOException ioe) {
			System.err.println(ioe.getMessage());
		}
		document.close();
	} catch (Exception ex) {
		ex.printStackTrace();
	}
	  //out.clear();
	  out = pageContext.pushBody(); 

%>