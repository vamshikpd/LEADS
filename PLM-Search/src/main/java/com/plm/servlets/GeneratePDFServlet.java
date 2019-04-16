package com.plm.servlets;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.endeca.navigation.ENEQueryException;
import com.endeca.navigation.ENEQueryResults;
import com.endeca.navigation.ERec;
import com.endeca.navigation.PropertyContainer;
import com.endeca.navigation.PropertyMap;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.plm.constants.PLMConstants;
import com.plm.util.PLMSearchUtil;
import com.plm.util.database.PLMDatabaseUtil;


/**
 * Servlet implementation class GeneratePDFServlet
 */
public class GeneratePDFServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private Document document = null;
	private String rootPath = null;
	private String defaultThumbnailImage = null;
	private String defaultPhotoImage = null;
	private String pdfFileName = null;
	private ServletContext servletContext;
	private static final Logger logger = Logger.getLogger(GeneratePDFServlet.class);
	
	/**
     * @see HttpServlet#HttpServlet()
     */
    public GeneratePDFServlet() {
        super();
    }

	public void init(ServletConfig servletConfig) throws ServletException {
         super.init(servletConfig);
         servletContext = servletConfig.getServletContext();
	}
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ByteArrayOutputStream baosPDF = null;
	    String pdfdisptype = (String)request.getAttribute("pdfdisptype");
		
		if(pdfdisptype==null){
			pdfdisptype = request.getParameter("pdfdisptype");
			if(pdfdisptype==null)
				pdfdisptype = PLMConstants.PDF_DISPLAY_TYPE_PHOTO_NIST;
		}
		
        try{
            rootPath = servletContext.getResource("/").getPath();
        }catch(MalformedURLException e){
        	e.printStackTrace();
        }

		if(rootPath  != null) {
			int index = rootPath.indexOf("WEB-INF");
			if(index >= 0 ) {
				rootPath = rootPath.substring(0,index);
			}
		}	  

		defaultThumbnailImage=rootPath + PLMConstants.DEFAULT_THUMBNAIL_IMAGE_PATH;		  
		defaultPhotoImage=rootPath + PLMConstants.DEFAULT_PHOTO_IMAGE_PATH;
		
		long currentTime = Calendar.getInstance().getTimeInMillis();
		pdfFileName = Long.toString(currentTime).concat(".pdf");

		if(pdfdisptype.equals(PLMConstants.PDF_DISPLAY_TYPE_PHOTO_NIST) ){
			baosPDF = generatePhotoNist(request,response);
		}else if(pdfdisptype.equals(PLMConstants.PDF_DISPLAY_TYPE_GALLERY)){
			baosPDF = generateGallery(request,response);
		}else if(pdfdisptype.equals(PLMConstants.PDF_DISPLAY_TYPE_PAL_POSTER)){
			baosPDF = generatePALPoster(request,response);
		}else if(pdfdisptype.equals(PLMConstants.PDF_DISPLAY_TYPE_PHOTO_LINEUP)){
			baosPDF = generatePhotoLineup(request,response);
		}
		
		ServletOutputStream sos;
		sos = response.getOutputStream();
		if(baosPDF == null) {
			response.setContentType("text/html;charset=UTF-8");
			String str = "Sorry for the inconvenience. Please try again later";
			baosPDF = new ByteArrayOutputStream();
			byte buf[] = str.getBytes();
			baosPDF.write(buf); 
		}else{
			response.setContentType("application/pdf");
			response.setContentLength(baosPDF.size());
		    response.setHeader("Content-Disposition", (new StringBuilder("inline; filename=")).append(pdfFileName).toString());
		}
		baosPDF.writeTo(sos);
		sos.flush();
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request,response);
	}

	private ByteArrayOutputStream generatePhotoLineup(HttpServletRequest request, HttpServletResponse response) {
		String layout = request.getParameter("layout"); 
		String pdpc = request.getParameter("pdpc"); 
		String susp = request.getParameter("susp"); 
		String[] cdcs = pdpc.split(",");
		ByteArrayOutputStream baosPDF = new ByteArrayOutputStream();
		
		byte[] imgData = null;
		ArrayList<Image> images = new ArrayList<Image>();
		int suspIndex = -1;
		int width =220;
		int height = 250;

			for (int i=0; i<cdcs.length; i++){
				String cdcName = cdcs[i];
				String[] cdc = cdcName.split(":");

				try {  
				   String showid = PLMDatabaseUtil.getPrimaryMugshotID(cdc[0]);
				   // get the image from the database
				   imgData = PLMDatabaseUtil.getPhotoByID(showid+"","p", defaultPhotoImage) ;   
				   
				   	//code added for re-size the image 
					InputStream in = new ByteArrayInputStream(imgData);
					BufferedImage bImage = ImageIO.read(in);
					final BufferedImage newImage = PLMDatabaseUtil.resizeImage(
							bImage, width, height);
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					ImageIO.write(newImage, "jpeg", baos);
					baos.flush();
					imgData = baos.toByteArray();
				   
				   Image img1 = Image.getInstance(imgData);
				  
				   images.add(img1);	
				   if (susp.equalsIgnoreCase(cdc[0])){
					   suspIndex = i;
				   }
				} catch (Exception e)	{
				  e.printStackTrace();
				}
			}
			int imagesArrSize = images.size();
			int rows = 0;
			int imgLoc = 0;
			Document document = null;
			PdfPTable table = null;
			PdfPCell cell = null;
			PdfPCell newCell = null;
			if ("h".equalsIgnoreCase(layout)){
				document = new Document(PageSize.A4.rotate(), 5, 5, 5, 5);
				// how many rows with 3 per row?

				// if remainder is 0, find rows by doing division
				// else use division +1
				if (imagesArrSize%3 == 0){
					rows = imagesArrSize/3;
				}else{
					rows = (imagesArrSize/3) + 1;
				}
			}else{
				document = new Document(PageSize.A4, 50, 50, 5, 5);
				// how many rows with 2 per row?
				if (imagesArrSize%2 == 0){
					rows = imagesArrSize/2;
				}else{
					rows = (imagesArrSize/2) + 1;
				}
			}

	    		try {
					PdfWriter.getInstance(document, baosPDF);
				} catch (DocumentException e) {
					e.printStackTrace();
				}
				document.open();
				
				cell = new PdfPCell(new Phrase(" "));
				cell.setBorder(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
				cell.setBorderWidth(2);
				cell.setBorderColor(new Color(130, 130, 130));
				cell.setPadding(5);

				if ("h".equalsIgnoreCase(layout)){
					table = new PdfPTable(3);
					imgLoc = 0;
					for (int j=0;j<rows;j++){
						newCell = new PdfPCell(cell);
						//newCell.setFixedHeight(265f);
						newCell.setFixedHeight(250f);
						if (imagesArrSize > imgLoc){
							newCell.setImage(images.get(imgLoc));
						}
						table.addCell(newCell);
						//imgLoc = imgLoc + 1;
						newCell = new PdfPCell(cell);
						newCell.setFixedHeight(215f);
						if (imagesArrSize > (imgLoc+1)){
							newCell.setImage(images.get(imgLoc+1));
						}
						table.addCell(newCell);
						//imgLoc = imgLoc + 1;
						newCell = new PdfPCell(cell);
						newCell.setFixedHeight(215f);
						if (imagesArrSize > (imgLoc+2)){
							newCell.setImage(images.get(imgLoc+2));
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
					try {
						document.add(table);
					} catch (DocumentException e) {
						e.printStackTrace();
					}
				}else{
					table = new PdfPTable(2);

					imgLoc = 0;
					for (int j=0;j<rows;j++){
						newCell = new PdfPCell(cell);
						if (imagesArrSize > imgLoc){
							newCell.setImage(images.get(imgLoc));
						}
						table.addCell(newCell);
						newCell = new PdfPCell(cell);
						if (imagesArrSize > (imgLoc+1)){
							newCell.setImage(images.get(imgLoc+1));
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

					try {
						document.add(table);
					} catch (DocumentException e) {
						e.printStackTrace();
					}
				}
				//try {
					document.newPage();
				//} catch (DocumentException e) {
				//	e.printStackTrace();
				//}

				if ("h".equalsIgnoreCase(layout)){
					table = new PdfPTable(3);
					
					imgLoc = 0;
					for (int j=0;j<rows;j++){
						newCell = new PdfPCell(cell);
						newCell.setFixedHeight(250f);
						if (suspIndex == imgLoc){
							newCell.setBorderWidth(5);
							newCell.setBorderColor(new Color(204, 0, 0));
						}
						if (imagesArrSize > imgLoc){
							newCell.setImage(images.get(imgLoc));
						}
						table.addCell(newCell);

						newCell = new PdfPCell(cell);
						newCell.setFixedHeight(250f);
						if (suspIndex == (imgLoc+1)){
							newCell.setBorderWidth(5);
							newCell.setBorderColor(new Color(204, 0, 0));
						}
						if (imagesArrSize > (imgLoc+1)){
							newCell.setImage(images.get(imgLoc+1));
						}
						table.addCell(newCell);
						newCell = new PdfPCell(cell);
						newCell.setFixedHeight(250f);
						if (suspIndex == (imgLoc+2)){
							newCell.setBorderWidth(5);
							newCell.setBorderColor(new Color(204, 0, 0));
						}
						if (imagesArrSize > (imgLoc+2)){
							newCell.setImage(images.get(imgLoc+2));
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
					try {
						document.add(table);
					} catch (DocumentException e) {
						e.printStackTrace();
					}
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
							newCell.setImage(images.get(imgLoc));
						}
						table.addCell(newCell);
						newCell = new PdfPCell(cell);
						if (suspIndex == (imgLoc+1)){
							newCell.setBorderWidth(5);
							newCell.setBorderColor(new Color(204, 0, 0));
						}
						if (imagesArrSize > (imgLoc+1)){
							newCell.setImage(images.get(imgLoc+1));
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

					try {
						document.add(table);
					} catch (DocumentException e) {
						e.printStackTrace();
					}
				}
			document.close();
			return baosPDF;
		
	}
	
	private ByteArrayOutputStream generateGallery(HttpServletRequest request, HttpServletResponse response){
		ByteArrayOutputStream baosPDF = new ByteArrayOutputStream();
		String cdcnum = request.getParameter("cdcNum"); 
		
    	String psize = request.getParameter("psize");
		byte[] imgData = null;
		int colspan = 3;
		int diff = 0;
		int loopcnt = 0;
		
		try {
			Document document = null;
			PdfPTable table = null;
			PdfPCell cell = null;
			PdfPCell newCell = null;

			document = new Document(PageSize.A4.rotate(), 5, 5, 5, 5);
    		PdfWriter.getInstance(document, baosPDF);
			document.open();
			
			cell = new PdfPCell(new Phrase(" "));
			cell.setBorder(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
			cell.setBorderWidth(2);
			cell.setBorderColor(new Color(130, 130, 130));
			cell.setPadding(5);

			// get mugshots and print
			ArrayList<String> mugIds = PLMDatabaseUtil.getPhotoIDs( cdcnum, 1);
			
			
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
						imgData = PLMDatabaseUtil.getPhotoByID(mugIds.get(j)+"",psize, defaultPhotoImage);
						Image img1 = Image.getInstance(imgData);
						img1.scaleToFit(240, 300);
						newCell.setImage(img1);
					}  else {
						newCell.setPhrase(new Phrase(""));
					}
					table.addCell(newCell);
				}
				document.add(table);	
			}

			// get smt shots and print
			ArrayList<String> smtIds = PLMDatabaseUtil.getPhotoIDs( cdcnum, 2 );
			
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
						imgData = PLMDatabaseUtil.getPhotoByID(smtIds.get(j)+"",psize, defaultPhotoImage);
						Image img1 = Image.getInstance(imgData);
						img1.scaleToFit(240, 300);
						newCell.setImage(img1);
					}  else {
						newCell.setPhrase(new Phrase(""));
					}			
					table.addCell(newCell);
				}
				document.add(table);		
			}	

			// get other shots and print
			ArrayList<String> otherIds = PLMDatabaseUtil.getPhotoIDs( cdcnum, -1 );
			
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
						imgData = PLMDatabaseUtil.getPhotoByID(otherIds.get(j)+"",psize, defaultPhotoImage) ;   
						Image img1 = Image.getInstance(imgData);
						img1.scaleToFit(240, 300);
						newCell.setImage(img1);
					}  else {
						newCell.setPhrase(new Phrase(""));
					}			
					table.addCell(newCell);
				}
				document.add(table);
			}
			if(document.isOpen() && table==null){
				table = new PdfPTable(colspan);
				newCell = new PdfPCell(cell);
				newCell.setPhrase(new Phrase("No Data"));
				table.addCell(newCell);
			}
			
			document.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return baosPDF;
	}

	private ByteArrayOutputStream generatePALPoster(HttpServletRequest request, HttpServletResponse response) {
		ByteArrayOutputStream baosPDF = new ByteArrayOutputStream();
		//ENEQueryResults qr = (ENEQueryResults)request.getAttribute("eneQueryResults");
		String sUrl = "";
		sUrl = request.getQueryString();
		ENEQueryResults qr = null;
		try {
			qr = PLMSearchUtil.getQueryResults(sUrl);
		} catch (ENEQueryException e) {
			return null;
		}
		
		PropertyContainer rec = qr.getERec();
		
		String spec = "";
		//String imageLocation ="";
		PropertyMap tempPropsMap = null;
		spec = ((ERec)rec).getSpec();
		tempPropsMap = ((ERec)rec).getProperties();	//sk

		String psize = request.getParameter("psize");
		byte[] imgData = null;

		try {
			Document document = null;
			PdfPTable table = null;
			PdfPCell cell = null;
			PdfPCell newCell = null;
			Phrase newPhrase = null;
			//Font font16 = FontFactory.getFont(FontFactory.HELVETICA, 16);
			Font font12 = FontFactory.getFont(FontFactory.HELVETICA, 12);
			Font font10 = FontFactory.getFont(FontFactory.HELVETICA, 10);
			Font font8 = FontFactory.getFont(FontFactory.HELVETICA, 8);		
			Font font6 = FontFactory.getFont(FontFactory.HELVETICA, 6);		
			
			document = new Document(PageSize.A4, 5, 5, 5, 5);
			PdfWriter.getInstance(document, baosPDF);
			document.open();
				
			cell = new PdfPCell(new Phrase(" "));
			cell.setBorder(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
			cell.setBorderWidth(0);
			cell.setBorderColor(new Color(130, 130, 130));
			cell.setPadding(5);
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			table = new PdfPTable(2);
			// write header
			newCell = new PdfPCell(cell);
			newPhrase = new Phrase("CALIFORNIA DEPARTMENT OF CORRECTIONS AND REHABILITATION", font6);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);		

			newCell = new PdfPCell(cell);
			newCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			newPhrase = new Phrase("DIVISION OF ADULT PAROLE OPERATIONS", font6);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);		
			// show current date
			DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
			Date date = new Date();

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
			newCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			newPhrase = new Phrase("PAROLEE AT LARGE", font12);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);	

			newCell = new PdfPCell(cell);
			newCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			newPhrase = new Phrase("RSP", font10);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);	

			document.add(table);

			// show primary mug shot
			table = new PdfPTable(2);
			Image img1 = null;
			
			String primID = PLMDatabaseUtil.getPrimaryMugshotID(spec);
			if(primID.length()>0){
				newCell = new PdfPCell(cell);
				imgData = PLMDatabaseUtil.getPhotoByID(primID+"", psize, defaultPhotoImage);
				img1 = Image.getInstance(imgData);
				newCell.setImage(img1);
				table.addCell(newCell);
			}
			// TBD : following to be changed to side shot
			String secID = PLMDatabaseUtil.getSecondaryMugshotID(spec);
			if(secID.length()>0){
				newCell = new PdfPCell(cell);
				imgData = PLMDatabaseUtil.getPhotoByID(secID+"", psize, defaultPhotoImage);
				img1 = Image.getInstance(imgData);
				newCell.setImage(img1);
				table.addCell(newCell);
			}
			
			// show date of picture
			HashMap<String, String> primHash = PLMDatabaseUtil.getPhotoDetailsByID(primID+"");
			if(primHash!=null){
				newCell = new PdfPCell(cell);
				if (primHash.get("insert_date") != null){
					newPhrase = new Phrase("Date Submitted: " + primHash.get("insert_date"), font8);
				}else{
					newPhrase = new Phrase(" ");
				}
				newCell.setPhrase(newPhrase);
				table.addCell(newCell);	
			}

			HashMap<String, String> secHash = PLMDatabaseUtil.getPhotoDetailsByID(secID+"");
			if(secHash!=null){
				newCell = new PdfPCell(cell);
				if (secHash.get("insert_date") != null){
					newPhrase = new Phrase("Date Submitted: " + secHash.get("insert_date"), font8);
				}else{
					newPhrase = new Phrase(" ");
				}
				newCell.setPhrase(newPhrase);
				table.addCell(newCell);
			}
			document.add(table);

			// show name
			table = new PdfPTable(1);
			newCell = new PdfPCell(cell);
			newCell.setHorizontalAlignment(Element.ALIGN_CENTER);
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
				Iterator<String> offenseInfoCollIter = (offenseInfoColl != null)?offenseInfoColl.iterator():null;

				while (offenseInfoCollIter != null && offenseInfoCollIter.hasNext()){
					String offInfo = offenseInfoCollIter.next();
					if(offInfo!=null){
						HashMap<String,String> hmapOff = new HashMap<String,String>();
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
			}
			document.add(table);

			// display address
			//Font font8u = FontFactory.getFont(FontFactory.HELVETICA, 8);	
			Font font8u = new Font(Font.HELVETICA, 8, Font.UNDERLINE);
			String strZip = "";

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
			
			if((String)tempPropsMap.get("Zip")!= null) {
				strZip = (String)tempPropsMap.get("Zip");
			}
			if((String)tempPropsMap.get("Zip4")!= null) {
				strZip = strZip + (String)tempPropsMap.get("Zip4");
			}
			secPhrase = new Phrase(strZip!=""?strZip:"", font8);
			newPhrase.add(secPhrase);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);		

			newCell = new PdfPCell(cell);
			newPhrase = new Phrase((String)tempPropsMap.get("Full_Phone")!=null?(String)tempPropsMap.get("Full_Phone"):"", font8);
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
			Iterator<String> smtInfoCollIter = (smtInfoColl != null)?smtInfoColl.iterator():null;
			while (smtInfoCollIter != null && smtInfoCollIter.hasNext()){
				String smtInfo = smtInfoCollIter.next();
				if(smtInfo!=null){
					HashMap<String, String> hmapSmt = new HashMap<String, String>();
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
			Collection<String> aliasInfoColl = tempPropsMap.getValues("Alias Info");
			Iterator<String> aliasInfoCollIter = (aliasInfoColl != null)?aliasInfoColl.iterator():null;
			while (aliasInfoCollIter != null && aliasInfoCollIter.hasNext()){
				String aliasInfo = aliasInfoCollIter.next();
				if(aliasInfo!=null){
					HashMap<String, String> hmapAlias = new HashMap<String, String>();

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
					newPhrase = new Phrase((String)hmapAlias.get("Alias Last Name"), font8);
					newCell.setPhrase(newPhrase);
					table.addCell(newCell);	
					
					newCell = new PdfPCell(cell);
					newPhrase = new Phrase((String)hmapAlias.get("Alias First Name"), font8);
					newCell.setPhrase(newPhrase);
					table.addCell(newCell);	

					newCell = new PdfPCell(cell);
					newPhrase = new Phrase((String)hmapAlias.get("Alias Middle Name"), font8);
					newCell.setPhrase(newPhrase);
					table.addCell(newCell);						
				}
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
		return baosPDF;
	}

	private ByteArrayOutputStream generatePhotoNist(HttpServletRequest request, HttpServletResponse response){
		ByteArrayOutputStream baosPDF = new ByteArrayOutputStream();
		
		String showid = request.getParameter("showid"); 
    	String psize = request.getParameter("psize");
    	String firstName =request.getParameter("firstName");
    	String lastName =request.getParameter("lastName");	
    	String cdcnum =request.getParameter("cdcNum");	
    	float width = 0;
    	float height = 0;
		
		byte[] imgData = null;

		try {
            
    		PdfPTable table = null;
    		PdfPCell cell = null;
    		PdfPCell newCell = null;
    		Font font12 = FontFactory.getFont(FontFactory.HELVETICA, 12);
    		document = new Document(PageSize.A4, 25, 25, 25, 25);
    		PdfWriter.getInstance(document, baosPDF);
            document.open();

			cell = new PdfPCell(new Phrase(" "));
			cell.setBorder(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
			cell.setBorderWidth(2);
			cell.setBorderColor(new Color(130, 130, 130));
			cell.setPadding(5);

			// get mugshots and print
			table = new PdfPTable(1);
			// write header
			newCell = new PdfPCell(cell);

    		if("t".equals(psize)) {
    			imgData = PLMDatabaseUtil.getPhotoByID(showid, psize, defaultThumbnailImage);
    		} else {
    			imgData = PLMDatabaseUtil.getPhotoByID(showid, psize, defaultPhotoImage);
    			if("p".equals(psize)){
    				width=240;
    				height=300;
    			}else if("n".equals(psize)){
    				width=480;
    				height=600;
    			}
    		}

			Image img1 = Image.getInstance(imgData);
			img1.scaleToFit(width, height);
			newCell.setImage(img1);

			table.addCell(newCell);

			HashMap<String, String> info = PLMDatabaseUtil.getPhotoDetailsByID(showid);

			Phrase newPhrase = new Phrase("CDC# : " + cdcnum, font12);

			newCell = new PdfPCell(newPhrase);
			table.addCell(newCell);
			newPhrase = new Phrase("Name : " + firstName + " " + lastName,font12);
			
			newCell = new PdfPCell(newPhrase);
			table.addCell(newCell);
			newPhrase = new Phrase("Photographer : " + (info.get("inserted_by")!=null?info.get("inserted_by"):""), font12);
			
			newCell = new PdfPCell(newPhrase);
			table.addCell(newCell);
			newPhrase = new Phrase("Date : " + (info.get("insert_date")!=null?info.get("insert_date"):""), font12);
			
			newCell = new PdfPCell(newPhrase);
			table.addCell(newCell);
			newPhrase = new Phrase("Type : " + (info.get("type_text")!=null?info.get("type_text"):""), font12);
			
			newCell = new PdfPCell(newPhrase);
			table.addCell(newCell);
			newPhrase = new Phrase("Description : " + (info.get("descr")!=null?info.get("descr"):""), font12);
			
			newCell = new PdfPCell(newPhrase);
			table.addCell(newCell);			

			document.add(table);

        } catch(DocumentException de) {
            de.printStackTrace();
        } catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
        document.close();
        return baosPDF;
	}
	
}

