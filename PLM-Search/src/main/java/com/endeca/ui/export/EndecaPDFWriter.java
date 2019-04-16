/* EndecaPDFWriter.java
 * 08-25-2011 Added code to not display data in certain fields for PRCS units. L. Baird
 * 10-05-2011 Added code to mask fields for DAI units. L. Baird
 * 04-09-2012 Modfied Alias information to match names correctly. L. Baird
*/
package com.endeca.ui.export;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import com.endeca.navigation.ENEConnection;
import com.endeca.navigation.ENEException;
import com.endeca.navigation.ENEQueryException;
import com.endeca.navigation.ENEQueryResults;
import com.endeca.navigation.ERec;
import com.endeca.navigation.HttpENEConnection;
import com.endeca.navigation.Navigation;
import com.endeca.navigation.PropertyContainer;
import com.endeca.navigation.PropertyMap;
import com.endeca.navigation.UrlGen;
import com.endeca.ui.AdvancedENEQuery;
import com.endeca.ui.BreadcrumbHandler;
import com.endeca.ui.UnifiedProperty;
import com.endeca.ui.UnifiedPropertyMap;
import com.endeca.ui.UnifiedReportMap;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.plm.constants.PLMConstants;
import com.plm.util.PLMSearchUtil;
import com.plm.util.PLMUtil;
import com.plm.util.database.PLMDatabaseUtil;
import com.report.ReportColumn;
import com.report.ReportConfig;
import com.report.ReportContentElement;
import com.report.ReportTable;
import com.report.ReportTemplate;
import com.util.MapsList;
import com.util.http.QueryHandler;

// Referenced classes of package com.endeca.ui.export:
//            ExportException

public class EndecaPDFWriter {
	private static final Logger logger = Logger.getLogger(EndecaPDFWriter.class);

    private List<String> keepKeys;
    private ReportConfig report;
    private AdvancedENEQuery query;
    private String title;
    private String headerImage;
    private QueryHandler queryH;
	private String rootPath;
	private String defaultThumbnailImage; 
	private ENEConnection nec;
    String eneHost = null;
    int enePort = 0;                               
	
    public EndecaPDFWriter(String queryString) throws ExportException {
        keepKeys = new ArrayList<String>();
        configure(queryString);
    }

    public void configure(String queryString) throws ExportException {
        try {
            QueryHandler qh = new QueryHandler(queryString, "UTF-8");
            rootPath = qh.getParam("rootPath");           
            
			eneHost	= PLMSearchUtil.getEndecaHost();
			enePort	= PLMSearchUtil.getEndecaPort();
            
            if(eneHost == null || enePort == 0) {
                throw new ExportException("ENE_Host and ENE_Port must be specified as context parameters in web.xml");
            }
            
            nec = new HttpENEConnection(eneHost, enePort);
            qh.addParam("Se", "0");
            
            if(qh.getParam("Page").equals(PLMConstants.PRINT_PAROLEE_PAGE)) {
            	qh.removeParam("N");
            	qh.removeParam("Ne");
            	qh.removeParam("Ntt");
            	qh.removeParam("Ntk");
            	qh.removeParam("Nty");
            	qh.removeParam("Ntx");
            	qh.removeParam("hterms");
            }
            query = new AdvancedENEQuery(qh.toString(), nec);            
            query.setBulkThreshold(0);
            
            if(qh.getParam("Ek") != null) {
                for(StringTokenizer keytokens = new StringTokenizer(qh.getParam("Ek"), "|"); keytokens.hasMoreTokens(); keepKeys.add(keytokens.nextToken())) { }
            }
            
            report = null;
            if(qh.getParam("Er") != null) {
                try {
                    report = new ReportConfig(new File(qh.getParam("Er")));
                } catch(Exception e) {
                    throw new ExportException(e);
                }
            }
            
            title = "";
            if(qh.getParam("Et") != null) {
                title = qh.getParam("Et");
            }            	
            
            if(qh.getParam("rootPath") != null) {
                headerImage = rootPath + "/media/images/global/paroleesearch_text.gif";
                defaultThumbnailImage = rootPath + PLMConstants.DEFAULT_THUMBNAIL_IMAGE_PATH;
            }
            queryH = qh;         
            
        } catch(ENEException ene) {
            throw new ExportException(ene.getMessage());
        }
    }
    
    @SuppressWarnings({ "null", "unchecked" })
	public void writeResultsPDF(OutputStream out) throws ExportException {
         try {        	
        	DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
        	java.util.Date date = new java.util.Date();
        	Navigation nav = null;
        	@SuppressWarnings("unused")
			Map resultMap = null;
        	int cnt = 1;

        	try{
        		nav = query.process().getResult(0).getNavigation();
        	}catch(ENEQueryException e){
            	logger.error(PLMUtil.getStackTrace(e));
        		do{
        			eneHost	= PLMSearchUtil.getEndecaHost();
        			enePort	= PLMSearchUtil.getEndecaPort();
        			nec = new HttpENEConnection(eneHost,enePort);
        			query.setConnection(nec);
        			cnt++;
        		}while(!query.dgraphIsAlive() && cnt<=PLMSearchUtil.getEndecaPortSize());
        		
        		nav = query.process().getResult(0).getNavigation();
        	}
        	
            Map propNames = new HashMap(200);
            Map dimNames = new HashMap(200);
            int rows = 0;
            
            if(queryH.getParam("Nu") != null) {
                rows = (int)nav.getTotalNumAggrERecs();
            } else {
                rows = (int)nav.getTotalNumERecs();
            }          
            
            List<PropertyContainer> reclist = new ArrayList<PropertyContainer>(rows);
            
            Iterator bulkIterator = nav.getBulkERecIter();
           
            if(bulkIterator == null) {
                bulkIterator = nav.getBulkAggrERecIter();
            }
            
            Iterator recIter = null;
            
            if(keepKeys.isEmpty() && report == null) {
                while(bulkIterator.hasNext()) {
                    PropertyContainer record = (PropertyContainer)bulkIterator.next();
                    UnifiedPropertyMap properties = new UnifiedPropertyMap(record);
                    reclist.add(record);
                    for(Iterator uPropIt = properties.entrySet().iterator(); uPropIt.hasNext();) {
                        UnifiedProperty uProp = (UnifiedProperty)uPropIt.next();
                        String name = (String)uProp.getKey();
                        if(uProp.isDimension()) {
                            dimNames.put(name, null);
                        } else {
                            propNames.put(name, null);
                        }
                    }
                }
                for(Iterator keepIter = dimNames.keySet().iterator(); keepIter.hasNext(); keepKeys.add((String)keepIter.next())) { }
                for(Iterator keepIter = propNames.keySet().iterator(); keepIter.hasNext(); keepKeys.add((String)keepIter.next())) { }
                recIter = reclist.iterator();
            } else {
                recIter = bulkIterator;
            }

            String NrsQryString = queryH.getParam("Nrs");
            MapsList crumbs = BreadcrumbHandler.getBreadcrumbs(nav, queryH.toString());
            String keywords = "";
            List<String> lstQuery = null;
           
            if(crumbs.size() > 0 || NrsQryString != null) {            	
            	if (NrsQryString != null) {
              		String sRecord = NrsQryString.substring((NrsQryString.indexOf("record[")+7),NrsQryString.lastIndexOf("]"));
    				lstQuery = PLMSearchUtil.getQueryParameters(sRecord);
                }
            	
            	StringBuffer sb = new StringBuffer();
            	if(crumbs.size() > 0 ) {	            	
	                Map posMap;
	                for(Iterator iter = crumbs.iterator(); iter.hasNext(); sb.append(new StringBuilder("  ")).append(posMap.get("Label")).append(": ").append(posMap.get("Filter")).toString(), FontFactory.getFont("Helvetica", 9F)) {
	                    posMap = (Map)iter.next();                 
	                }
            	}
            	if (lstQuery != null) {
            		String rep = "";
	                for(String s: lstQuery){
	                	rep = s.replace("_"," ");
	                	sb.append("  " + rep);
	                }
                }
                if(keywords != null) {
            		keywords = keywords + "  " + sb.toString();
                }                
            } else {
                keywords="All";
            }
            
            Document document = new Document(PageSize.LETTER.rotate(), 3, 3, 10, 5);	
            @SuppressWarnings("unused")
			PdfWriter writer = null;
            try {
            	writer = PdfWriter.getInstance(document, out);
            } catch (DocumentException e) {
            	logger.error(PLMUtil.getStackTrace(e));
            }
            
            document.addTitle(title);
            document.addSubject(title);
            document.addAuthor(queryH.getParam("userid"));
            document.addCreationDate();
            document.addKeywords(keywords);
           
	        HeaderFooter header = new HeaderFooter(new Phrase(title, FontFactory.getFont("Helvetica", 12F)),false);
	        header.setAlignment(Element.ALIGN_CENTER);	        
	        document.setHeader(header);        
	        
           StringBuffer sbFooter = new StringBuffer();
           sbFooter.append("Note : This report contains Confidential & Proprietary information and can not be disseminated without the express permission of the CDCR.\n");
           sbFooter.append("Report Generated by : " + queryH.getParam("userid") + " ");
           sbFooter.append("On : " + dateFormat.format(date) + " ");
           sbFooter.append("    Page : ");
            
           HeaderFooter footer = new HeaderFooter(new Phrase(sbFooter.toString(), FontFactory.getFont("Helvetica", 9F)),true);           
	       footer.setAlignment(Element.ALIGN_LEFT);
	       document.setFooter(footer);            
	       document.open();
	        
	       Image himg = null;
          
	       if(headerImage != null) {
                try {
                    himg = Image.getInstance(headerImage);
                    himg.setAlignment(Element.ALIGN_LEFT);
                } catch(MalformedURLException e) {
                	logger.error(PLMUtil.getStackTrace(e));
                	document.add(new Paragraph(""));
                } catch(DocumentException e) {
                	logger.error(PLMUtil.getStackTrace(e));
                    document.add(new Paragraph(""));
                } catch(IOException e) {
                	logger.error(PLMUtil.getStackTrace(e));
                    document.add(new Paragraph(""));
                }
            }
            
           PdfPTable datatable = new PdfPTable(3);
           PdfPCell cell = new PdfPCell(new Phrase(""));
           int headerwidths[] = new int[3];
           headerwidths[0] = 20;
           headerwidths[1] = 15;
           headerwidths[2] = 92;
           
           datatable.getDefaultCell().setBorder(Rectangle.NO_BORDER);
           datatable.getDefaultCell().setPadding(3F);   
           datatable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
           datatable.getDefaultCell().setVerticalAlignment(Element.ALIGN_LEFT);
           
           datatable.setWidths(headerwidths);
           datatable.addCell(himg);
           cell.setBackgroundColor(new Color(234,234,235));
           datatable.addCell(new Phrase("Search Criteria:", FontFactory.getFont("Helvetica", 10F, 0, new Color(225, 114, 54))));
           cell.setBackgroundColor(new Color(234,234,235));
           StringBuffer b = new StringBuffer();
           b.append(keywords);           
           cell.setPhrase(new Phrase(b.toString(),FontFactory.getFont("Helvetica", 9F)));
           datatable.addCell(cell);
           datatable.addCell(" ");
           document.add(datatable);
            
            if(report == null) {
                datatable = new PdfPTable(keepKeys.size() + 1);
                headerwidths = new int[keepKeys.size() + 1];                
                datatable.getDefaultCell().setBackgroundColor(new Color(234,234,235)); //EAEAEB
                datatable.setHeaderRows(1);                
                datatable.addCell("");                
                headerwidths[0] = 3;
                
                int colcount = 1;
                for(Iterator i = keepKeys.iterator(); i.hasNext();) {
                    String prop = (String)i.next();                    
                   
                    headerwidths[colcount] = 92 / keepKeys.size()+1;
                    
                    if (prop.equals("CDC Number")){ 
						prop ="CDC#";
						headerwidths[colcount] = 5;
					}
                    
                    if(prop.equals("Street") || prop.equals("Last Name") || prop.equals("First Name") ){
						headerwidths[colcount] = 10;
					}                    
                                        
                    if(queryH.getParam("searchResults") != null && queryH.getParam("searchResults").contains("pc290")) {
                    	if(prop.equals("Street")) 
                    		headerwidths[colcount] = 13;
                    }
                    
                    if(prop.equals("name")){
						headerwidths[colcount] = 10;
					}
                    
                    if(prop.equals("Birth Date Display")) {
                    	prop = "Birth Date";
                    	headerwidths[colcount] = 5;
                    } 
                    
                    if(prop.equals("P_Height")) {
                    	prop = "Height";
                    	headerwidths[colcount] = 4;
                    } 
                    
                    if(prop.equals("P_Weight")) {
                    	prop = "Weight";
                    	headerwidths[colcount] = 4;
                    } 
                    
                    if(prop.equals("HRSO") || prop.equals("GPS") || prop.equals("Zip")){ 
                    	headerwidths[colcount] = 3;
                    }
                    
                    if(prop.equals("Action Date Display")){
                    	if(queryH.getParam("searchResults").equals("pc290Registrant")) {
                    		prop ="Release Date";
                        }                        
                        if(queryH.getParam("searchResults").equals("pc290Discharge")) {
                        	prop ="Discharge Date";
                        }
                        headerwidths[colcount] = 5;
					}
                    
					if(prop.equals("Sex")){
						prop ="Gender";
						headerwidths[colcount] = 5;
					}
					
					if(prop.equals("Race")){
						prop ="Ethnicity";
						headerwidths[colcount] = 6;
					}
				
					if(prop.equals("Address Changed Date Display")){
						prop ="Address Chg Date";
						headerwidths[colcount] = 6;
					}
					
					if(prop.equals("Haircolor") || prop.equals("Eyecolor") ){
						headerwidths[colcount] = 92 / keepKeys.size()+1;
					}
					
                    datatable.addCell(new Phrase(prop, FontFactory.getFont("Helvetica", 8F, 1)));
                    colcount++;
                }

                datatable.setWidths(headerwidths);
                datatable.setWidthPercentage(100F);
                datatable.setHorizontalAlignment(0);
                datatable.setHeaderRows(1);
                datatable.getDefaultCell().setBorder(Rectangle.NO_BORDER);                
                datatable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
                
                //Added for PRCS - LBB
                String sUnitName = null;

                for(short rowcount = 1; recIter.hasNext(); rowcount++) {
                	if(rowcount%2 == 0) {
                    	datatable.getDefaultCell().setBackgroundColor(new Color(247,247,244)); 
                    } else {
                    	datatable.getDefaultCell().setBackgroundColor(new Color(255, 255, 255)); //FFFFFF
                    }              	
                    
                    PropertyContainer record = (PropertyContainer)recIter.next();
                    UnifiedPropertyMap properties = new UnifiedPropertyMap(record);
                	
                    for(Iterator i = keepKeys.iterator(); i.hasNext();) {
                        String prop = (String)i.next();
                        String propValue = (String)properties.get(prop);
                        
                        if(prop.equals("CDC Number")) {
                        	if(queryH.getParam("tab") != null && queryH.getParam("tab").equals("1")) {
                        		byte[] imgData = null;
                    			Image img1 = null;	
                    			String primID = null;
                    			try{
                    				primID = PLMDatabaseUtil.getPrimaryMugshotID(propValue);
                    				imgData = PLMDatabaseUtil.getPhotoByID(primID+"", "t", defaultThumbnailImage);                    				
                    				img1 = Image.getInstance(imgData);
                    				img1.scaleToFit(48, 75);
                    				datatable.addCell(img1);
                    			} catch(Exception e) {
                                	logger.error(PLMUtil.getStackTrace(e));
                    			}
                            } else {
                            	datatable.addCell(new Phrase(String.valueOf(rowcount), FontFactory.getFont("Helvetica", 8F)));
                            }                        	
                        }
                       //Added for PRCS - LBB
                        //sUnitName carries forward to the Status statement. If it begins with PRCS or DAI-, it 
                        //sets the Status to blank.
                         if(prop.equals("Unit Name")) {
                           	sUnitName = propValue.toString();
                         }
                       	
                         if(prop.equals("Status")&&sUnitName != null) {
                             // 2018-03-08 emil fix issue with unit name; some unit names are shorter than 4 characters
                             // and that causes NullPointerException
                             String sUnitNameSubstr = "";
                             try {
                                 sUnitNameSubstr = sUnitName.substring(0, 4);
                             } catch (StringIndexOutOfBoundsException e) {}

                        	 if(sUnitNameSubstr.equals("PRCS") || sUnitNameSubstr.equals("DAI-")) {
                        		 propValue = "";
                        		 sUnitName = "";
                        	}
                         }

                        if(prop.equals("P_Height") && propValue != null) {
                    		int ht = Integer.parseInt(propValue);
                    		int htfeet =  ht/12;
                    		int htinch =  ht - (htfeet*12);
                    		propValue = new String(htfeet + "'" + htinch + "\"");
                    	}
                        
                        if(propValue == null) {
                            datatable.addCell("");
                        } else {
                            datatable.addCell(new Phrase(propValue, FontFactory.getFont("Helvetica", 8F, 0, new Color(225, 114, 54))));
                        }
                    }
                }
                document.add(datatable);
            } else {
                short rowcount = 1;
                String lastTemplate = "";
                datatable = null;
                while(recIter.hasNext()) {
                    PropertyContainer record = (PropertyContainer)recIter.next();
                    UnifiedPropertyMap uPropsMap = new UnifiedPropertyMap(record);
                    UnifiedReportMap reportPropsMap = new UnifiedReportMap(uPropsMap);
                    ReportTemplate template = report.getRecord().getMatchingTemplate(reportPropsMap);
                    ReportTable table = (ReportTable)template.getRenderElement(0);
                    if(!lastTemplate.equals(template.getName())) {
                        lastTemplate = template.getName();
                        if(rowcount != 1) {
                            document.add(datatable);
                        }
                        int numCols = table.getRowColumns(0).size() + 1;
                        datatable = new PdfPTable(numCols);
                        datatable.getDefaultCell().setPadding(3F);
                        headerwidths = new int[numCols];
                        datatable.getDefaultCell().setBorder(Rectangle.NO_BORDER);
                        datatable.getDefaultCell().setHorizontalAlignment(1);
                        datatable.addCell("");
                        headerwidths[0] = 8;
                        int colcount = 1;
                        for(Iterator iter = table.getRowColumns(0).iterator(); iter.hasNext();) {
                            ReportColumn column = (ReportColumn)iter.next();
                            headerwidths[colcount] = (column.getWidth() * 92) / 100;
                            String prop = column.getTitle();
                            datatable.addCell(new Phrase(prop, FontFactory.getFont("Helvetica", 8F, 1)));
                            colcount++;
                        }

                        datatable.setWidths(headerwidths);
                        datatable.setWidthPercentage(100F);
                        datatable.setHorizontalAlignment(0);
                        datatable.setHeaderRows(1);
                    }
                    datatable.addCell(new Phrase(String.valueOf(rowcount), FontFactory.getFont("Helvetica", 8F)));
                    String content;
                    for(Iterator iter = table.getRowColumns(0).iterator(); iter.hasNext(); datatable.addCell(new Phrase(content, FontFactory.getFont("Helvetica", 8F)))) {
                        ReportColumn column = (ReportColumn)iter.next();
                        content = "";
                        for(Iterator contentIter = column.getContents().iterator(); contentIter.hasNext();) {
                            ReportContentElement contentElm = (ReportContentElement)contentIter.next();
                            String plainContent = contentElm.getEvalContent(reportPropsMap);
                            if(plainContent != null) {
                                content = (new StringBuilder(String.valueOf(content))).append(plainContent).toString();
                            }
                        }
                    }
                    rowcount++;
                }
                document.add(datatable);
            }
            document.close();
        } catch(ENEQueryException eqe) {
            throw new ExportException(eqe.getMessage());
        } catch(DocumentException de) {
            throw new ExportException(de.getMessage());
        }
    }
     
    @SuppressWarnings("null")
	public String constructQryString(String queryString) {
    	String newQueryString = "";
    	@SuppressWarnings("unused")
		String qs=null;    	
    	UrlGen ug = new UrlGen(queryString, "UTF-8");
		qs = ug.toString();			
		qs = queryString.replaceAll("%20"," "); // for replacing %20 by space
		qs = queryString.replaceAll("%22",""); //for removing double qoutes from the request  Query
		String revocationReleaseDate = "";
		String revocationReleaseDate2 = "";
		if(queryString.indexOf("Revocation_Release_Date_Search") >= 0){
			int startIndex = queryString.indexOf("Revocation_Release_Date_Search")+ "Revocation_Release_Date_Search".length()+6;
			int endIndex = queryString.indexOf("Revocation_Release_Date_Search")+ "Revocation_Release_Date_Search".length()+14;
			revocationReleaseDate = queryString.substring(startIndex,endIndex);					
		}
		if(queryString.lastIndexOf("Revocation_Release_Date_Search") >= 0){
			int startIndex = queryString.lastIndexOf("Revocation_Release_Date_Search")+ "Revocation_Release_Date_Search".length()+6;
			int endIndex = queryString.lastIndexOf("Revocation_Release_Date_Search")+ "Revocation_Release_Date_Search".length()+14;
			revocationReleaseDate2 = queryString.substring(startIndex,endIndex);					
		}
		
		String revocationFieldName ="";
		if(revocationReleaseDate.length() >= 8){
			revocationFieldName = " Between "+revocationReleaseDate.substring(4,6)+"/"+revocationReleaseDate.substring(6,8)+ "/"+revocationReleaseDate.substring(0,4);
		}
		
		if(revocationReleaseDate2.length() >= 8){
			revocationFieldName += " and "+revocationReleaseDate2.substring(4,6)+"/"+revocationReleaseDate2.substring(6,8)+ "/"+revocationReleaseDate2.substring(0,4);
		}
		
		if(revocationFieldName != null && revocationFieldName.length() > 0){
			newQueryString = "Revocation Release Date or Parole Date:" + revocationFieldName;
		}
		
		String vehicleYearFieldName ="";
		String vehicleYear ="";
		String vehicleYear2 ="";
		if(queryString.indexOf("Veh_Year") >= 0){ 
			int startIndex = queryString.indexOf("Veh_Year")+ "Veh_Year".length()+2;
			int endIndex = queryString.indexOf("Veh_Year")+ "Veh_Year".length()+6;
			vehicleYear = queryString.substring(startIndex,endIndex);
		}
		if(queryString.lastIndexOf("Veh_Year") >= 0){ 
			int startIndex = queryString.lastIndexOf("Veh_Year")+ "Veh_Year".length()+2;
			int endIndex = queryString.lastIndexOf("Veh_Year")+ "Veh_Year".length()+6;
			vehicleYear2 = queryString.substring(startIndex,endIndex);
		}
		if(!vehicleYear.equals("") && !vehicleYear2.equals("")){
			 vehicleYearFieldName = " Between " + vehicleYear + " and " + vehicleYear2;
		}	
		
		if(vehicleYearFieldName != null && vehicleYearFieldName.length() > 0){
			vehicleYearFieldName = "Vehicle Year:" + vehicleYearFieldName;
		}		
		
		if(newQueryString != null)
			newQueryString = newQueryString + "  " + vehicleYearFieldName;

		if(queryString.indexOf("matches(.,")>=0){
			String[] advanceSearch = queryString.split("matches\\(.,");
			String[] advanceSearchInfo = null;
			String ignoreCriteria = "Has Address Changed|Action Type";
			String ethnicityFieldName ="";	
			String hairColorFieldName="";
			String OffenseCodeFieldName ="";
			String smtDescFieldName ="";
			int ethnicityCount =0;
			int hairColorCount =0;
			int offenseCount =0;
			int smtCount =0;
			if(advanceSearch != null && advanceSearch.length >= 0){
				for(int i=0; i<advanceSearch.length; i++) {
					if(advanceSearch[i] != null && advanceSearch[i].indexOf(",")>0){
						advanceSearchInfo = advanceSearch[i].split(",");
						if(advanceSearchInfo!=null && advanceSearchInfo.length>=2){
							String info0 = advanceSearchInfo[0].trim();
							if(info0.indexOf("\"")>=0){
								info0 =info0.substring(1,info0.length()-1).trim();
							}
							if(ignoreCriteria.indexOf(info0) < 0){
								if(info0.indexOf("Haircolor")>=0){ 
									hairColorCount = hairColorCount+1;
									if(hairColorFieldName.equals("")){
										hairColorFieldName = advanceSearchInfo[1].substring(0,advanceSearchInfo[1].indexOf(')')).trim();
										if(hairColorFieldName.indexOf("\"")>=0){
											hairColorFieldName =hairColorFieldName.substring(1,hairColorFieldName.length()-1);
										}
									} else{
										String hairColorFieldName2 = advanceSearchInfo[1].substring(0,advanceSearchInfo[1].indexOf(')')).trim();
										if(hairColorFieldName2.indexOf("\"")>=0){
											hairColorFieldName2 =hairColorFieldName2.substring(1,hairColorFieldName2.length()-1);
										}
										hairColorFieldName = hairColorFieldName+" or " + hairColorFieldName2;
									}
								} else if(info0.indexOf("Race")>=0) { 	
									ethnicityCount =ethnicityCount+1;
									if(ethnicityFieldName.equals("")){
										ethnicityFieldName = advanceSearchInfo[1].substring(0,advanceSearchInfo[1].indexOf(')')).trim();
										if(ethnicityFieldName.indexOf("\"")>=0){
											ethnicityFieldName =ethnicityFieldName.substring(1,ethnicityFieldName.length()-1);
										}
									}else{
										String ethnicityFieldName2 = advanceSearchInfo[1].substring(0,advanceSearchInfo[1].indexOf(')')).trim();
										if(ethnicityFieldName2.indexOf("\"")>=0){
											ethnicityFieldName2 =ethnicityFieldName2.substring(1,ethnicityFieldName2.length()-1);
										}
										ethnicityFieldName = ethnicityFieldName+" or " + ethnicityFieldName2;
									}
								} else if(info0.indexOf("Offense Code")>=0) { 
									offenseCount = offenseCount+1;
									String condition ="";
									if(queryString.indexOf("and",queryString.indexOf("Offense Code"))>0){
										condition = "and";								
									}else{
										condition = "or";
									}
									if(OffenseCodeFieldName.equals("")){
										OffenseCodeFieldName = advanceSearchInfo[1].substring(0,advanceSearchInfo[1].indexOf(')')).trim();
										if(OffenseCodeFieldName.indexOf("\"")>=0){
											OffenseCodeFieldName =OffenseCodeFieldName.substring(1,OffenseCodeFieldName.length()-1);
										}
									}else{
										String OffenseCodeFieldName2 = advanceSearchInfo[1].substring(0,advanceSearchInfo[1].indexOf(')')).trim();
										if(OffenseCodeFieldName2.indexOf("\"")>=0){
											OffenseCodeFieldName2 =OffenseCodeFieldName2.substring(1,OffenseCodeFieldName2.length()-1);
										}
										OffenseCodeFieldName = OffenseCodeFieldName+" " + condition+" " + OffenseCodeFieldName2;								
									}
								}else if(info0.indexOf("SMT_Detail")>=0) {
									smtCount= smtCount+1;
									String condition ="";
									if(queryString.indexOf("and",queryString.indexOf("SMT_Detail"))>0){
										condition = "and";								
									}else{
										condition = "or";
									}
									if(smtDescFieldName.equals("")){
										smtDescFieldName = advanceSearchInfo[1].substring(0,advanceSearchInfo[1].indexOf(')')).trim();
										if(smtDescFieldName.indexOf("\"")>=0){
											smtDescFieldName =smtDescFieldName.substring(1,smtDescFieldName.length()-1);
										}
									}else{
										String smtDescFieldName2 = advanceSearchInfo[1].substring(0,advanceSearchInfo[1].indexOf(')')).trim();
										if(smtDescFieldName2.indexOf("\"")>=0){
											smtDescFieldName2 =smtDescFieldName2.substring(1,smtDescFieldName2.length()-1);
										}
										smtDescFieldName = smtDescFieldName+" " + condition+" " + smtDescFieldName2;							
									}												
								}else{
									//logger.debug(advanceSearchInfo.toString());
									//logger.debug(advanceSearchInfo[1].lastIndexOf(')'));
									//logger.debug(info0);
									String fieldName = null;
									if(info0.indexOf("Full_Phone")>=0) {
										fieldName = advanceSearchInfo[1].substring(0,advanceSearchInfo[1].lastIndexOf(')'));
									} else if(info0.indexOf("geocode")>=0) {
										fieldName = advanceSearchInfo[1].substring(0,advanceSearchInfo[1].indexOf(')'));
									} else {
										fieldName = advanceSearchInfo[1].substring(0,advanceSearchInfo[1].indexOf(')')).trim();
									}
									if(fieldName.indexOf("\"")>=0){
										fieldName =fieldName.substring(1,fieldName.length()-1);
									}
									newQueryString=newQueryString + "  " + PLMSearchUtil.getAdvanceSearchDisplayName(info0)+":"+fieldName;
								}
							}
						}
					}
				}
				if(hairColorCount > 0){					
					if(newQueryString != null)
						newQueryString = newQueryString + "  " + PLMSearchUtil.getAdvanceSearchDisplayName("Haircolor")+":"+hairColorFieldName;
				}
				
				if(ethnicityCount > 0){	
					if(newQueryString != null)
						newQueryString = newQueryString + "  " + PLMSearchUtil.getAdvanceSearchDisplayName("Race")+":"+ethnicityFieldName;
				}
				if(offenseCount > 0){		
					if(newQueryString != null)
						newQueryString = newQueryString + "  " + PLMSearchUtil.getAdvanceSearchDisplayName("Offense Code")+":"+OffenseCodeFieldName;
				}
				if(smtCount > 0){		
					if(newQueryString != null)
						newQueryString = newQueryString + "  " + PLMSearchUtil.getAdvanceSearchDisplayName("SMT Criteria")+":"+smtDescFieldName;
				}
			}			
		}
    	return newQueryString;
    }
    
    @SuppressWarnings("unchecked")
	public void writeParoleePDF(OutputStream out) {
    	
    	String defaultPhotoImage = null;		
		ENEQueryResults qr = null;
    	int cnt1 = 1;
		
		try {
			qr = query.process().getResult(0);
		} catch (ENEQueryException e) {
    		do{
    			eneHost	= PLMSearchUtil.getEndecaHost();
    			enePort	= PLMSearchUtil.getEndecaPort();
    			nec = new HttpENEConnection(eneHost,enePort);
    			query.setConnection(nec);
    			cnt1++;
    		}while(!query.dgraphIsAlive() && cnt1<=PLMSearchUtil.getEndecaPortSize());
    		
    		try {
				qr = query.process().getResult(0);
			} catch (ENEQueryException e1) {
            	logger.error(PLMUtil.getStackTrace(e1));
			}
			
		}
		
		PropertyContainer rec = qr.getERec();		
		String spec = "";
		PropertyMap tempPropsMap = null;
		spec = ((ERec)rec).getSpec();
		tempPropsMap = ((ERec)rec).getProperties();	
		String psize = "p";
		byte[] imgData = null;
    	String sUnitNm = (String)tempPropsMap.get("Unit Name");
 
		try {
			Document document = null;
			PdfPTable table = null;
			PdfPCell cell = null;
			PdfPCell newCell = null;
			Phrase newPhrase = null;
					
			Font font6 = FontFactory.getFont(FontFactory.HELVETICA, 6);
			Font font8 = FontFactory.getFont(FontFactory.HELVETICA, 8);
			Font font10 = FontFactory.getFont(FontFactory.HELVETICA, 10);
			@SuppressWarnings("unused")
			Font font12 = FontFactory.getFont(FontFactory.HELVETICA, 12);
			Font headingFont = new Font(Font.HELVETICA, 10, Font.UNDERLINE);
			
			document = new Document(PageSize.LETTER, 2, 2, 10, 5);
			PdfWriter.getInstance(document, out);
			document.addTitle(title);
            document.addCreationDate();           
            document.addAuthor(queryH.getParam("userid"));
            document.addSubject(title);
            
            DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
        	java.util.Date date = new java.util.Date();
        	
            HeaderFooter header = new HeaderFooter(new Phrase(title, FontFactory.getFont("Helvetica", 12F)),false);
	        header.setAlignment(Element.ALIGN_CENTER);
	        document.setHeader(header);
	        
            StringBuffer sbFooter = new StringBuffer();
            sbFooter.append("Note : This report contains Confidential & Proprietary information and can not be disseminated without the express permission of the CDCR.\n");
            sbFooter.append("Report Generated by : " + queryH.getParam("userid") + " ") ;
            sbFooter.append("On : " + dateFormat.format(date) + " ");
            sbFooter.append("    Page : ");
            HeaderFooter footer = new HeaderFooter(new Phrase(sbFooter.toString(), FontFactory.getFont("Helvetica", 9F)),true);
	        footer.setAlignment(Element.ALIGN_LEFT);
	        document.setFooter(footer);
	        
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
			newPhrase = new Phrase("California Department Of Corrections And Rehabilitation", font6);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);		

			newCell = new PdfPCell(cell);
			newCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			newPhrase = new Phrase("Division Of Adult Parole Operations - Parole LEADS 2.0", font6);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);
			
			document.add(table);
			
			//Section 1 Starts here
			//show Heading			
			table = new PdfPTable(1);			
			newCell = new PdfPCell(cell);
			newCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			newPhrase = new Phrase("Parolee Details", headingFont);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);	

			document.add(table);
			
			
			// cell 7
			
			table = new PdfPTable(8);
		 	Image img1 = null;			
			String primID = PLMDatabaseUtil.getPrimaryMugshotID(spec);
			
			if(primID.length()>0){
				newCell = new PdfPCell(cell);
				newPhrase = new Phrase(" ");
				newCell.setPhrase(newPhrase);
				table.addCell(newCell);
				newCell = new PdfPCell(cell);
				newCell.setPhrase(newPhrase);
				table.addCell(newCell);
				newCell = new PdfPCell(cell);
				newCell.setPhrase(newPhrase);
				table.addCell(newCell);
				imgData = PLMDatabaseUtil.getPhotoByID(primID+"", psize, defaultPhotoImage);
				img1 = Image.getInstance(imgData);		
				img1.scaleToFit(240, 480);
				newCell = new PdfPCell(cell);
				newCell.setColspan(2);
				newCell.setImage(img1);			
				table.addCell(newCell);
				newCell = new PdfPCell(cell);
				newCell.setPhrase(newPhrase);
				table.addCell(newCell);
				newCell = new PdfPCell(cell);
				newCell.setPhrase(newPhrase);
				table.addCell(newCell);
				newCell = new PdfPCell(cell);
				newCell.setPhrase(newPhrase);
				table.addCell(newCell);
			}
			
			document.add(table);
			
			table = new PdfPTable(8);
			
            //**************************
			//ROW 1
            //**************************

			// cell 1
			newCell = new PdfPCell(cell);
			newPhrase = new Phrase("CDC#", font8);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);
			
			int headerwidths[] = new int[8];
			headerwidths[0]=10;
			headerwidths[1]=12;
			headerwidths[2]=10;
			headerwidths[3]=12;
			headerwidths[4]=10;
			headerwidths[5]=12;
			headerwidths[6]=10;
			headerwidths[7]=10;
						
            table.setWidths(headerwidths);
            
            // cell 2
			newCell = new PdfPCell(cell);
			newPhrase = new Phrase((String)tempPropsMap.get("CDC Number")!=null ?(String)tempPropsMap.get("CDC Number"):"", font8);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);
						
			// cell 3
			newCell = new PdfPCell(cell);
			newPhrase = new Phrase("Last Name", font8);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);		
			
			// cell 4
			newCell = new PdfPCell(cell);
			newPhrase = new Phrase((String)tempPropsMap.get("Last Name")!=null ?(String)tempPropsMap.get("Last Name"):"", font8);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);		

			// cell 5
			newCell = new PdfPCell(cell);
			newPhrase = new Phrase("First Name", font8);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);		
			
			// cell 6
			newCell = new PdfPCell(cell);
			newPhrase = new Phrase((String)tempPropsMap.get("First Name")!=null ?(String)tempPropsMap.get("First Name"):"", font8);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);		

			// cell 7
			newCell = new PdfPCell(cell);
			newPhrase = new Phrase("Middle Name", font8);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);		
			
			// cell 8
			newCell = new PdfPCell(cell);
			newPhrase = new Phrase((String)tempPropsMap.get("Middle Name")!=null ?(String)tempPropsMap.get("Middle Name"):"", font8);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);
			
            //**************************
			//ROW 2
            //**************************

			// cell 1
			newCell = new PdfPCell(cell);
			newPhrase = new Phrase("Parole Status", font8);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);
			
			// cell 2 
			newCell = new PdfPCell(cell);
			//added check for PRCS - LBB
			if (sUnitNm.substring(0,4).equals("PRCS") || sUnitNm.substring(0,4).equals("DAI-")) {
				newPhrase = new Phrase(" ");
			}else {
				newPhrase = new Phrase((String)tempPropsMap.get("Status")!=null ?(String)tempPropsMap.get("Status"):"", font8);
			}
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);
						
			// cell 3
			newCell = new PdfPCell(cell);
			newPhrase = new Phrase("Parole Date", font8);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);			
			
			// cell 4
			newCell = new PdfPCell(cell);
			newPhrase = new Phrase((String)tempPropsMap.get("Parole Date Display")!=null ?(String)tempPropsMap.get("Parole Date Display"):"", font8);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);
			
			// cell 5
			newCell = new PdfPCell(cell);
			newPhrase = new Phrase("Max Discharge Date", font8);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);
			
			// cell 6
			newCell = new PdfPCell(cell);
			//added check for PRCS - LBB
			if (sUnitNm.substring(0,4).equals("PRCS") || sUnitNm.substring(0,4).equals("DAI-")) {
				newPhrase = new Phrase(" ");
			}else {
				newPhrase = new Phrase((String)tempPropsMap.get("Control Discharg Date")!=null ?(String)tempPropsMap.get("Control Discharg Date"):"", font8);
			}
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);				
			
			// cell 7
			newCell = new PdfPCell(cell);
			newPhrase = new Phrase("Revocation Release Date", font8);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);
			
			// cell 8
			newCell = new PdfPCell(cell);
			//added check for PRCS - LBB
			//removed masking for revocation release date - RD
			//if (sUnitNm.substring(0,4).equals("PRCS") || sUnitNm.substring(0,4).equals("DAI-")) {
			//	newPhrase = new Phrase(" ");
			//}else {
				newPhrase = new Phrase((String)tempPropsMap.get("Revocation Release Date")!=null ?(String)tempPropsMap.get("Revocation Release Date"):"", font8);
			//}
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);		

            //**************************
			//ROW 3
            //**************************

			// cell 1
			newCell = new PdfPCell(cell);
			newPhrase = new Phrase("Supervision Level", font8);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);
			
			// cell 2
			newCell = new PdfPCell(cell);
			//added check for PRCS - LBB
			if (sUnitNm.substring(0,4).equals("PRCS") || sUnitNm.substring(0,4).equals("DAI-")) {
				newPhrase = new Phrase(" ");
			}else {
				newPhrase = new Phrase((String)tempPropsMap.get("Classification Description")!=null ?(String)tempPropsMap.get("Classification Description"):"", font8);
			}
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);
			
			// cell 3
			newCell = new PdfPCell(cell);
			newPhrase = new Phrase("Commitment County", font8);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);
			
			// cell 4
			newCell = new PdfPCell(cell);
			newPhrase = new Phrase((String)tempPropsMap.get("County Commit")!=null ?(String)tempPropsMap.get("County Commit"):"", font8);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);				
			
			//Added cell 5 and 6 for COLLR -- LBB
			// cell 5
			newCell = new PdfPCell(cell);
			newPhrase = new Phrase("County of LLR", font8);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);
			
			// cell 6
			newCell = new PdfPCell(cell);
			newPhrase = new Phrase((String)tempPropsMap.get("CountyOfLLR")!=null ?(String)tempPropsMap.get("CountyOfLLR"):"", font8);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);				
			
			// cell 7
			newCell = new PdfPCell(cell);
			newPhrase = new Phrase("Parole Unit", font8);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);
			
			// cell 8
			newCell = new PdfPCell(cell);
			newPhrase = new Phrase((String)tempPropsMap.get("Unit Name")!=null ?(String)tempPropsMap.get("Unit Name"):"", font8);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);			
			
            //**************************
			//ROW 3a
            //**************************

			// cell 1
			newCell = new PdfPCell(cell);
			newPhrase = new Phrase("USINS#", font8);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);
			
			// cell 2
			newCell = new PdfPCell(cell);
			newPhrase = new Phrase((String)tempPropsMap.get("USINS Number")!=null ?(String)tempPropsMap.get("USINS Number"):"", font8);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);		

			// cell 3 (place holder)
			newCell = new PdfPCell(cell);
			newCell.setColspan(6);
			newPhrase = new Phrase("", font8);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);		
			
            //**************************
			//ROW 4
            //**************************

			// cell 1
			newCell = new PdfPCell(cell);
			newPhrase = new Phrase("Agent Name", font8);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);
			
			// cell 2
			newCell = new PdfPCell(cell);
			//added check for PRCS - LBB
			if (sUnitNm.substring(0,4).equals("PRCS") || sUnitNm.substring(0,4).equals("DAI-")) {
				newPhrase = new Phrase(" ");
			}else {
				newPhrase = new Phrase((String)tempPropsMap.get("Agent Name")!=null ?(String)tempPropsMap.get("Agent Name"):"", font8);
			}
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);
						
			// cell 3
			newCell = new PdfPCell(cell);
			newPhrase = new Phrase("Agent Telephone", font8);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);
			
			// cell 4
			newCell = new PdfPCell(cell);
			newPhrase = new Phrase((String)tempPropsMap.get("Agent Phone")!=null ?(String)tempPropsMap.get("Agent Phone"):"", font8);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);				
			
			// cell 5
			newCell = new PdfPCell(cell);
			newPhrase = new Phrase("Agent e-mail", font8);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);
			
			// cell 6
			newCell = new PdfPCell(cell);
			newCell.setColspan(3);
			//added check for PRCS - LBB
			if (sUnitNm.substring(0,4).equals("PRCS") || sUnitNm.substring(0,4).equals("DAI-")) {
				newPhrase = new Phrase(" ");
			}else {
				newPhrase = new Phrase((String)tempPropsMap.get("Agent Email")!=null ?(String)tempPropsMap.get("Agent Email"):"", font8);
			}
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);	
			
            //**************************
			//ROW 5
            //**************************

			// cell 1
			newCell = new PdfPCell(cell);
			newPhrase = new Phrase("Birth Date", font8);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);		
			
			// cell 2			
			newCell = new PdfPCell(cell);
			newPhrase = new Phrase((String)tempPropsMap.get("Birth Date Display")!=null?(String)tempPropsMap.get("Birth Date Display"):"", font8);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);	
			
			// cell 3
			newCell = new PdfPCell(cell);
			newPhrase = new Phrase("Birth State", font8);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);
			
			// cell 4			
			newCell = new PdfPCell(cell);
			newPhrase = new Phrase((String)tempPropsMap.get("Birth State Name")!=null?(String)tempPropsMap.get("Birth State Name"):"", font8);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);	
			
			// cell 5
			newCell = new PdfPCell(cell);
			newPhrase = new Phrase("Gender", font8);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);
			
			// cell 6
			newCell = new PdfPCell(cell);
			newPhrase = new Phrase((String)tempPropsMap.get("Sex")!=null?(String)tempPropsMap.get("Sex"):"", font8);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);
			
			// cell 7
			newCell = new PdfPCell(cell);
			newPhrase = new Phrase("Ethnicity", font8);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);		
			
			// cell 8
			newCell = new PdfPCell(cell);
			newPhrase = new Phrase((String)tempPropsMap.get("Race")!=null?(String)tempPropsMap.get("Race"):"", font8);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);		

            //**************************
			//ROW 6
            //**************************
			
			// cell 1
			newCell = new PdfPCell(cell);
			newPhrase = new Phrase("Height", font8);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);
			
			// cell 2
			String height = null;
		    if (tempPropsMap.get("Height Feet")!=null){
			  height = tempPropsMap.get("Height Feet") + "'" + tempPropsMap.get("Height Inches") + "\"";
			}else{
			  height = "";
			}
		    newCell = new PdfPCell(cell);
			newPhrase = new Phrase(height, font8);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);		

			// cell 3
			newCell = new PdfPCell(cell);
			newPhrase = new Phrase("Weight", font8);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);
			
			// cell 4
			newCell = new PdfPCell(cell);
			newPhrase = new Phrase((String)tempPropsMap.get("P_Weight")!=null?(String)tempPropsMap.get("P_Weight"):"", font8);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);
			
			// cell 5
			newCell = new PdfPCell(cell);
			newPhrase = new Phrase("Hair Color", font8);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);
			
			// cell 6
			newCell = new PdfPCell(cell);
			newPhrase = new Phrase((String)tempPropsMap.get("Haircolor")!=null?(String)tempPropsMap.get("Haircolor"):"", font8);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);	
			
			// cell 7
			newCell = new PdfPCell(cell);
			newPhrase = new Phrase("Eye Color", font8);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);
			
			// cell 8
			newCell = new PdfPCell(cell);
			newPhrase = new Phrase((String)tempPropsMap.get("Eyecolor")!=null?(String)tempPropsMap.get("Eyecolor"):"", font8);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);	
			
            //**************************
			//ROW 7
            //**************************

			// cell 1
			newCell = new PdfPCell(cell);
			newPhrase = new Phrase("FBI#", font8);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);
			
			// cell 2
			newCell = new PdfPCell(cell);
			newPhrase = new Phrase((String)tempPropsMap.get("FBI Number")!=null?(String)tempPropsMap.get("FBI Number"):"", font8);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);		
			
			// cell 3
			newCell = new PdfPCell(cell);
			newPhrase = new Phrase("CII#", font8);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);		
			
			// cell 4
			newCell = new PdfPCell(cell);
			newPhrase = new Phrase((String)tempPropsMap.get("CII Number")!=null?(String)tempPropsMap.get("CII Number"):"", font8);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);		

			// cell 5
			newCell = new PdfPCell(cell);
			newPhrase = new Phrase("SSA#", font8);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);
			
			// cell 6
			newCell = new PdfPCell(cell);
			newPhrase = new Phrase((String)tempPropsMap.get("SSA Number")!=null?(String)tempPropsMap.get("SSA Number"):"", font8);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);					
			
			// cell 7
			newCell = new PdfPCell(cell);
			newPhrase = new Phrase("Drivers License#", font8);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);
			
			// cell 8
			newCell = new PdfPCell(cell);
			newPhrase = new Phrase((String)tempPropsMap.get("Driver License Number")!=null?(String)tempPropsMap.get("Driver License Number"):"", font8);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);		

			//Section 1 Ends here
			
			//Section 2 Starts here
			document.add(table);
			
			//show Heading - Registration & Notice Information
			table = new PdfPTable(1);
           
			newCell = new PdfPCell(cell);
			newCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			newPhrase = new Phrase("Registration & Notice Information", headingFont);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);	

			document.add(table);

			table = new PdfPTable(8);
			
            //**************************
			//ROW 1
            //**************************

			// cell 1
			newCell = new PdfPCell(cell);
			newPhrase = new Phrase("HS 11590", font8);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);
			
			// cell 2
			newCell = new PdfPCell(cell);
			newPhrase = new Phrase((String)tempPropsMap.get("HS REQ")!=null?(String)tempPropsMap.get("HS REQ"):"", font8);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);
			
			// cell 3
			newCell = new PdfPCell(cell);
			newPhrase = new Phrase("PC 290", font8);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);		
			
			// cell 4
			newCell = new PdfPCell(cell);
			newPhrase = new Phrase((String)tempPropsMap.get("PC 290 REQ")!=null?(String)tempPropsMap.get("PC 290 REQ"):"", font8);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);	
			
			// cell 5
			newCell = new PdfPCell(cell);
			newPhrase = new Phrase("PC457.1", font8);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);		
			
			// cell 6
			newCell = new PdfPCell(cell);
			newPhrase = new Phrase((String)tempPropsMap.get("PC 457 REQ")!=null?(String)tempPropsMap.get("PC 457 REQ"):"", font8);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);	
			
			// cell 7
			newCell = new PdfPCell(cell);
			newPhrase = new Phrase("PC3058.6", font8);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);
			
			// cell 8
			newCell = new PdfPCell(cell);
			newPhrase = new Phrase((String)tempPropsMap.get("PC 3058 REQ")!=null?(String)tempPropsMap.get("PC 3058 REQ"):"", font8);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);		
			
            //**************************
			//ROW 2
            //**************************

			// cell 1
			newCell = new PdfPCell(cell);
			newPhrase = new Phrase("HS 11590 Reg. Date", font8);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);
			
			// cell 2
			newCell = new PdfPCell(cell);
			newPhrase = new Phrase((String)tempPropsMap.get("HS DATE")!=null?(String)tempPropsMap.get("HS DATE"):"", font8);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);		

			// cell 3
			newCell = new PdfPCell(cell);
			newPhrase = new Phrase("PC 290 Reg. Date", font8);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);
			
			// cell 4
			newCell = new PdfPCell(cell);
			newPhrase = new Phrase((String)tempPropsMap.get("PC 290 DATE")!=null?(String)tempPropsMap.get("PC 290 DATE"):"", font8);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);	

			// cell 5
			newCell = new PdfPCell(cell);
			newPhrase = new Phrase("PC457.1 Reg. Date", font8);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);	
			
			// cell 6
			newCell = new PdfPCell(cell);
			newPhrase = new Phrase((String)tempPropsMap.get("PC 457 DATE")!=null?(String)tempPropsMap.get("PC 457 DATE"):"", font8);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);	
			
			// cell 7
			newCell = new PdfPCell(cell);
			newPhrase = new Phrase("PC3058.6 Reg. Date", font8);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);
			
			// cell 8
			newCell = new PdfPCell(cell);
			newPhrase = new Phrase((String)tempPropsMap.get("PC 3058 DATE")!=null?(String)tempPropsMap.get("PC 3058 DATE"):"", font8);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);	
			
			//Section 2 Ends here
			
			//Section 3 Starts here

			document.add(table);
			
			//show Heading - Special Parole Conditions
			table = new PdfPTable(1);
			newCell = new PdfPCell(cell);
			newCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			newPhrase = new Phrase("Special Parole Conditions", headingFont);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);	

			document.add(table);

			table = new PdfPTable(8);
			headerwidths = new int[8];
			headerwidths[0]=10;
			headerwidths[1]=12;
			headerwidths[2]=10;
			headerwidths[3]=12;
			headerwidths[4]=12;
			headerwidths[5]=12;
			headerwidths[6]=5;
			headerwidths[7]=5;
			table.setWidths(headerwidths);
			
			// cell 1
			newCell = new PdfPCell(cell);
			newPhrase = new Phrase("Drug testing", font8);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);
			
			// cell 2
			newCell = new PdfPCell(cell);
			//added check ofr PRCS - LBB
			if (sUnitNm.substring(0,4).equals("PRCS") || sUnitNm.substring(0,4).equals("DAI-")) {
				newPhrase = new Phrase(" ");
			}else {
				newPhrase = new Phrase(tempPropsMap.get("ANT REQ")!=null?(String)tempPropsMap.get("ANT REQ"):"", font8);
			}
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);	

			// cell 3
			newCell = new PdfPCell(cell);
			newPhrase = new Phrase("No Alcohol", font8);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);
			
			// cell 4
			newCell = new PdfPCell(cell);
			//added check ofr PRCS - LBB
			if (sUnitNm.substring(0,4).equals("PRCS") || sUnitNm.substring(0,4).equals("DAI-")) {
				newPhrase = new Phrase(" ");
			}else {
				newPhrase = new Phrase((String)tempPropsMap.get("No Alcohol")!=null?(String)tempPropsMap.get("No Alcohol"):"", font8);
			}
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);	
			
			// cell 5
			newCell = new PdfPCell(cell);
			newPhrase = new Phrase("Psychiatric Outpatient Clinic", font8);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);
			
			// cell 6
			newCell = new PdfPCell(cell);
			//added check ofr PRCS - LBB
			if (sUnitNm.substring(0,4).equals("PRCS") || sUnitNm.substring(0,4).equals("DAI-")) {
				newPhrase = new Phrase(" ");
			}else {
				newPhrase = new Phrase((String)tempPropsMap.get("POC REQ")!=null?(String)tempPropsMap.get("POC REQ"):"", font8);
			}
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);
			
			// cell 7
			newCell = new PdfPCell(cell);
			newPhrase = new Phrase(" ", font8);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);
			
			// cell 8
			newCell = new PdfPCell(cell);
			newPhrase = new Phrase(" ", font8);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);
			
			//Section 3 Ends here

			//Section 4 Starts here
			document.add(table);
		
			//show Heading - Parolee Addresses
			table = new PdfPTable(1);
			newCell = new PdfPCell(cell);
			newCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			newPhrase = new Phrase("Parolee Addresses", headingFont);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);	

			document.add(table);			
			
			//current or latest address
			String strDiscDate = (String)tempPropsMap.get("Address Effective Date");
			String currAdd = null;
			boolean bCurrAddress = false;
			
			if(strDiscDate != null && strDiscDate.trim().length() > 0) {
				currAdd = "";
				
				if (tempPropsMap.get("Address Effective Date") != null) 
					currAdd = currAdd + tempPropsMap.get("Address Effective Date") + PLMConstants.SEPARATOR;
				else 
					currAdd = currAdd + PLMConstants.SEPARATOR;
				
				if (tempPropsMap.get("Care of (live with)") != null) 
					currAdd = currAdd + tempPropsMap.get("Care of (live with)") + PLMConstants.SEPARATOR;
				else 
					currAdd = currAdd + PLMConstants.SEPARATOR;
				
				if (tempPropsMap.get("Street") != null) 
					currAdd = currAdd + tempPropsMap.get("Street") + PLMConstants.SEPARATOR;
				else 
					currAdd = currAdd + PLMConstants.SEPARATOR;
				
				if (tempPropsMap.get("City") != null) 
					currAdd = currAdd + tempPropsMap.get("City") + PLMConstants.SEPARATOR;
				else 
					currAdd = currAdd + PLMConstants.SEPARATOR;
				
				if (tempPropsMap.get("State Code") != null) 
					currAdd = currAdd + tempPropsMap.get("State Code") + PLMConstants.SEPARATOR;
				else 
					currAdd = currAdd + PLMConstants.SEPARATOR;
								
				if (tempPropsMap.get("County Code") != null) 
					currAdd = currAdd + tempPropsMap.get("County Code") + PLMConstants.SEPARATOR;
				else 
					currAdd = currAdd + PLMConstants.SEPARATOR;
				
				if (tempPropsMap.get("County Code") != null) 
					currAdd = currAdd + tempPropsMap.get("County Name") +  PLMConstants.SEPARATOR;
				else 
					currAdd = currAdd + PLMConstants.SEPARATOR;				
			
				if (tempPropsMap.get("Zip") != null) 
					currAdd = currAdd + tempPropsMap.get("Zip") + PLMConstants.SEPARATOR;
				else 
					currAdd = currAdd + PLMConstants.SEPARATOR;
				
				if (tempPropsMap.get("Zip4") != null) 
					currAdd = currAdd + tempPropsMap.get("Zip4") + PLMConstants.SEPARATOR;
				else 
					currAdd = currAdd + PLMConstants.SEPARATOR;
				
				if (tempPropsMap.get("Full_Phone") != null)					 
					currAdd = currAdd + tempPropsMap.get("Full_Phone") + PLMConstants.SEPARATOR;
				else 
					currAdd = currAdd + PLMConstants.SEPARATOR;
				
				if (tempPropsMap.get("Message Phone") != null) 
					currAdd = currAdd + tempPropsMap.get("Message Phone") + PLMConstants.SEPARATOR;
				else 
					currAdd = currAdd + PLMConstants.SEPARATOR;
				
				if (tempPropsMap.get("Map") != null) 
					currAdd = currAdd + tempPropsMap.get("Map") + PLMConstants.SEPARATOR;
				else 
					currAdd = currAdd + PLMConstants.SEPARATOR;
				
				if (tempPropsMap.get("Resident Status") != null) { 
					currAdd = currAdd + tempPropsMap.get("Resident Status") + PLMConstants.SEPARATOR;
				    bCurrAddress = true;
				}else {
					currAdd = currAdd + PLMConstants.SEPARATOR;
				}
				
				currAdd = currAdd + "@@";
			} 
			
			String sortedPrevAddresses = (String)tempPropsMap.get("Sorted Prev Address");
			String[] allAddresses = null;			
			String Addresses = null;

			if (currAdd != null) {
				Addresses = new String(currAdd);
				
				if(sortedPrevAddresses != null) {
					Addresses = Addresses + sortedPrevAddresses;
				}
				allAddresses = Addresses.split("@@");				
			}
			
			if(allAddresses != null && allAddresses.length > 0) {
				table = new PdfPTable(4);
				headerwidths = new int[4];
				headerwidths[0]=5;
				headerwidths[1]=18;
				headerwidths[2]=5;
				headerwidths[3]=18;
			
				table.setWidths(headerwidths);
			} else {
				table = new PdfPTable(1);
				newCell = new PdfPCell(cell);
				newPhrase = new Phrase("NONE", font8);
				newCell.setPhrase(newPhrase);
				table.addCell(newCell);
			}
			
			int cnt=0;
			
			while (allAddresses != null && cnt<allAddresses.length){				
				String prevAdd1 = allAddresses[cnt];
				String prevAddress = "";
				String[] result = null;
				
				if(prevAdd1 != null){
					result = prevAdd1.split(PLMConstants.SEPARATOR);
				}

				if(result != null){
					int iResult =0;
					//date
					if(result[0] != null && result[0].length()>0 && !"".equals(result[0])){
						strDiscDate = result[0];	
					}else{
						strDiscDate ="";
					}
					
					//careof
					if(result.length > 1 && result[1] != null && result[1].length()>0 && !"".equals(result[1]))
						prevAddress = prevAddress+result[1]+"\n";
					
					//street
					if(result.length > 2 && result[2] != null && result[2].length()>0 && !"".equals(result[2]))
						prevAddress = prevAddress+result[2]+"\n";
					
					//city						
					if(result.length > 3 && result[3] != null && result[3].length()>0 && !"".equals(result[3])){
						prevAddress = prevAddress+result[3]+", ";
						iResult =1;
					}
					
					//county
					if(result.length > 5 && result[5] != null && result[5].length()>0 && !"".equals(result[5])){
						prevAddress = prevAddress+result[5];
						iResult =1;
					}
					
					if(iResult >0)
						prevAddress = prevAddress+"\n";
					
					iResult = 0;
					
					//state
					if(result.length > 4 && result[4] != null && result[4].length()>0 && !"".equals(result[4])){
						prevAddress = prevAddress+result[4]+", ";
						iResult =1;
					}
					
					//zip
					if(result.length > 7 && result[7] != null && result[7].length()>0 && !"".equals(result[7])){
						prevAddress = prevAddress+result[7];	
						iResult =1;
					}
					
					//zip4
					if(result.length > 8 && result[8] != null && result[8].length()>0 && !"".equals(result[8])){
						prevAddress = prevAddress+"-"+result[8];	
						iResult =1;
					}
					
					if(iResult >0)
						prevAddress = prevAddress+"\n";
						iResult = 0;
					
					//map
					if(result.length > 11 && result[11] != null && result[11].length()>0 && !"".equals(result[11])) {
						prevAddress = prevAddress+"Map Reference: "+result[11]+"\n";
					}					
					
					//phone
					if(result.length > 9 && result[9] != null && result[9].trim().length()>0 && !"".equals(result[9].trim())){
						prevAddress = prevAddress+ "Phone: " + result[9]+"\n";	
					}
					
					//msg phone
					if(result.length > 10 && result[10] != null && result[10].trim().length()>0 && !"".equals(result[10].trim())){
						prevAddress = prevAddress+ "Msg Phone: " + result[10]+"\n";	
					}
					
					//Resident Status
					if(result.length > 12 && result[12] != null && result[12].trim().length()>0 && !"".equals(result[12].trim())){
						if (bCurrAddress) {
							prevAddress = prevAddress+ "Resident Status: " + result[12]+"\n";
							bCurrAddress = false;
						}
					}

					cnt++;
					newCell = new PdfPCell(cell);
					newPhrase = new Phrase(strDiscDate, font8);
					newCell.setPhrase(newPhrase);
					table.addCell(newCell);	
					
					newCell = new PdfPCell(cell);
					newPhrase = new Phrase(prevAddress, font8);
					newCell.setPhrase(newPhrase);
					table.addCell(newCell);	
				}				
			}
			
			if(cnt %2 > 0) {
				newCell = new PdfPCell(cell);
				newPhrase = new Phrase("", font8);
				newCell.setPhrase(newPhrase);
				table.addCell(newCell);	
				
				newCell = new PdfPCell(cell);
				newPhrase = new Phrase("", font8);
				newCell.setPhrase(newPhrase);
				table.addCell(newCell);
			}
			//Section 4 Ends here

			//Section 5 Starts here			
			document.add(table);
			
			//show Heading - Scars, Marks & Tattoo Information
			table = new PdfPTable(1);
			newCell = new PdfPCell(cell);
			newCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			newPhrase = new Phrase("Scars, Marks & Tattoo Information", headingFont);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);	

			document.add(table);			
						
			Collection smtInfoColl = tempPropsMap.getValues("SMT Information");
			Iterator<String> smtInfoCollIter = (smtInfoColl != null)?smtInfoColl.iterator():null;
			if (smtInfoColl != null && smtInfoColl.size() > 0){
				table = new PdfPTable(6);
				headerwidths = new int[6];
				headerwidths[0]=10;
				headerwidths[1]=12;
				headerwidths[2]=7;
				headerwidths[3]=15;
				headerwidths[4]=5;
				headerwidths[5]=15;				
				table.setWidths(headerwidths);
				while (smtInfoCollIter != null && smtInfoCollIter.hasNext()){
					String smtInfo = smtInfoCollIter.next();
					if(smtInfo!=null){
						HashMap<String, String> hmapSmt = new HashMap<String, String>();
						String[] smtInfo_result = smtInfo.split(PLMConstants.SEPARATOR);
							
						if (smtInfo_result.length > 0 && smtInfo_result[0] != null && !smtInfo_result[0].equals("")){
							hmapSmt.put("Code/Location", smtInfo_result[0]);
						}else{
							hmapSmt.put("Code/Location", "");
						}					
						if (smtInfo_result.length > 2 && smtInfo_result[2] != null && !smtInfo_result[2].equals("")){
							hmapSmt.put("Picture", smtInfo_result[2]);
						}else{
							hmapSmt.put("Picture", "");
						}
						if (smtInfo_result.length > 3 && smtInfo_result[3] != null && !smtInfo_result[3].equals("")){
							hmapSmt.put("Text", smtInfo_result[3]);
						}else{
							hmapSmt.put("Text", "");
						}
	
						newCell = new PdfPCell(cell);
						newPhrase = new Phrase("Code/Location", font8);
						newCell.setPhrase(newPhrase);
						table.addCell(newCell);	
						
						newCell = new PdfPCell(cell);
						newPhrase = new Phrase(hmapSmt.get("Code/Location"), font8);
						newCell.setPhrase(newPhrase);
						table.addCell(newCell);						
	
						newCell = new PdfPCell(cell);
						newPhrase = new Phrase("Picture", font8);
						newCell.setPhrase(newPhrase);
						table.addCell(newCell);	
	
						newCell = new PdfPCell(cell);
						newPhrase = new Phrase(hmapSmt.get("Picture"), font8);
						newCell.setPhrase(newPhrase);
						table.addCell(newCell);	
						
						newCell = new PdfPCell(cell);
						newPhrase = new Phrase("Text", font8);
						newCell.setPhrase(newPhrase);
						table.addCell(newCell);	
						
						newCell = new PdfPCell(cell);
						newPhrase = new Phrase(hmapSmt.get("Text"), font8);
						newCell.setPhrase(newPhrase);
						table.addCell(newCell);	
					}
				}
			} else {
				table = new PdfPTable(1);
				newCell = new PdfPCell(cell);
				newPhrase = new Phrase("NONE", font8);
				newCell.setPhrase(newPhrase);
				table.addCell(newCell);		
			}
			//Section 5 Ends here

			//Section 6 Starts here
			document.add(table);
			
			//show Heading - Alias Information
			table = new PdfPTable(1);
			newCell = new PdfPCell(cell);
			newCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			newPhrase = new Phrase("Alias Information", headingFont);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);	

			document.add(table);		
			
			Collection<String> aliasInfoColl = tempPropsMap.getValues("Alias Info");
			Iterator<String> aliasInfoCollIter = (aliasInfoColl != null)?aliasInfoColl.iterator():null;
			if (aliasInfoColl != null && aliasInfoColl.size() > 0){
				table = new PdfPTable(6);
				headerwidths = new int[6];
				headerwidths[0]=10;
				headerwidths[1]=10;
				headerwidths[2]=10;
				headerwidths[3]=10;
				headerwidths[4]=10;
				headerwidths[5]=10;
			
				table.setWidths(headerwidths);
				while (aliasInfoCollIter != null && aliasInfoCollIter.hasNext()){
					String aliasInfo = aliasInfoCollIter.next();
					if(aliasInfo!=null){
						HashMap<String, String> hmapAlias = new HashMap<String, String>();
						String[] aliasInfo_result = aliasInfo.split(PLMConstants.SEPARATOR);
							
						if (aliasInfo_result.length > 0 && aliasInfo_result[0] != null && !aliasInfo_result[0].equals("")){
							hmapAlias.put("Alias Last Name", aliasInfo_result[0]);
						}else{
							hmapAlias.put("Alias Last Name", "");
						}
						if (aliasInfo_result.length > 1 && aliasInfo_result[1] != null && !aliasInfo_result[1].equals("")){
							hmapAlias.put("Alias First Name", aliasInfo_result[1]);
						}else{
							hmapAlias.put("Alias First Name", "");
						}
						if (aliasInfo_result.length > 2 && aliasInfo_result[2] != null && !aliasInfo_result[2].equals("")){
							hmapAlias.put("Alias Middle Name", aliasInfo_result[2]);
						}else{
							hmapAlias.put("Alias Middle Name", "");
						}
						
						newCell = new PdfPCell(cell);
						newPhrase = new Phrase("Alias Last Name", font8);
						newCell.setPhrase(newPhrase);
						table.addCell(newCell);	
	
						newCell = new PdfPCell(cell);
						newPhrase = new Phrase(hmapAlias.get("Alias Last Name"), font8);
						newCell.setPhrase(newPhrase);
						table.addCell(newCell);
						
						newCell = new PdfPCell(cell);
						newPhrase = new Phrase("Alias First Name", font8);
						newCell.setPhrase(newPhrase);
						table.addCell(newCell);	
	
						newCell = new PdfPCell(cell);
						newPhrase = new Phrase(hmapAlias.get("Alias First Name"), font8);
						newCell.setPhrase(newPhrase);
						table.addCell(newCell);	
	
						newCell = new PdfPCell(cell);
						newPhrase = new Phrase("Alias Middle Name", font8);
						newCell.setPhrase(newPhrase);
						table.addCell(newCell);						
						
						newCell = new PdfPCell(cell);
						newPhrase = new Phrase(hmapAlias.get("Alias Middle Name"), font8);
						newCell.setPhrase(newPhrase);
						table.addCell(newCell);							
					}
				} 
			} else {
				table = new PdfPTable(1);
				newCell = new PdfPCell(cell);
				newPhrase = new Phrase("NONE", font8);
				newCell.setPhrase(newPhrase);
				table.addCell(newCell);		
			}
			//Section 6 Ends here

			//Section 7 Starts here
			document.add(table);
			
			//show Heading - Moniker Information
			table = new PdfPTable(1);
			newCell = new PdfPCell(cell);
			newCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			newPhrase = new Phrase("Moniker Information", headingFont);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);	

			document.add(table);	
			
			Collection monikerInfoColl = tempPropsMap.getValues("Moniker Info");
			StringBuffer sbf = new StringBuffer();
			if (monikerInfoColl != null && monikerInfoColl.size() > 0){
				@SuppressWarnings("null")
				Iterator monikerInfoCollIter = (monikerInfoColl != null)?monikerInfoColl.iterator():null;
	
				while (monikerInfoCollIter != null && monikerInfoCollIter.hasNext()){
					String moniInfo = (String)monikerInfoCollIter.next();
					HashMap hmapMoni = new HashMap();
					String[] moniInfo_result = moniInfo.split(PLMConstants.SEPARATOR);
						
					if (moniInfo_result.length > 0 && moniInfo_result[0] != null && !moniInfo_result[0].equals("")){
						sbf.append("/");
						sbf.append(moniInfo_result[0]);
					}else{
						hmapMoni.put("Moniker", "");
					}
				}
			}		
			
			table = new PdfPTable(1);
			newCell = new PdfPCell(cell);
			newPhrase = new Phrase(sbf.length()>0?sbf.toString().substring(1):"NONE", font8);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);	
			//Section 7 Ends here
			
			//Section 8 Starts here
			document.add(table);
			
			//show Heading - Commitment Offense Information
			table = new PdfPTable(1);
			newCell = new PdfPCell(cell);
			newCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			newPhrase = new Phrase("Commitment Offense Information", headingFont);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);	

			document.add(table);			
			
			Collection offenseInfoColl = tempPropsMap.getValues("Offense Information");
			if (offenseInfoColl != null && offenseInfoColl.size() > 0){
				table = new PdfPTable(8);
				headerwidths = new int[8];
				headerwidths[0]=10;
				headerwidths[1]=12;
				headerwidths[2]=10;
				headerwidths[3]=15;
				headerwidths[4]=12;
				headerwidths[5]=5;
				headerwidths[6]=3;
				headerwidths[7]=3;
				table.setWidths(headerwidths);
				
				@SuppressWarnings("null")
				Iterator<String> offenseInfoCollIter = (offenseInfoColl != null)?offenseInfoColl.iterator():null;
				while (offenseInfoCollIter != null && offenseInfoCollIter.hasNext()){
					String offInfo = offenseInfoCollIter.next();
					if(offInfo!=null){
						HashMap<String,String> hmapOff = new HashMap<String,String>();
						String[] offInfo_result = offInfo.split(PLMConstants.SEPARATOR);
						if (offInfo_result.length > 0 && offInfo_result[0] != null && !offInfo_result[0].equals("")){
							hmapOff.put("Offense Code", offInfo_result[0]);
						}else{
							hmapOff.put("Offense Code", "");
						}
						if (offInfo_result.length > 1 && offInfo_result[1] != null && !offInfo_result[1].equals("")){
							hmapOff.put("Description", offInfo_result[1]);
						}else{
							hmapOff.put("Description", "");
						}
						if (offInfo_result.length > 4 && offInfo_result[4] != null && !offInfo_result[4].equals("")){
							hmapOff.put("Controlling Offense", offInfo_result[4]);
						}else{
							hmapOff.put("Controlling Offense", "");
						}
						newCell = new PdfPCell(cell);
						newPhrase = new Phrase("Offense Code", font8);
						newCell.setPhrase(newPhrase);
						table.addCell(newCell);
						
						newCell = new PdfPCell(cell);
						newPhrase = new Phrase(hmapOff.get("Offense Code"), font8);
						newCell.setPhrase(newPhrase);
						table.addCell(newCell);	

						newCell = new PdfPCell(cell);
						newPhrase = new Phrase("Description:", font8);
						newCell.setPhrase(newPhrase);
						table.addCell(newCell);		
						
						newCell = new PdfPCell(cell);
						newPhrase = new Phrase(hmapOff.get("Description"), font8);
						newCell.setPhrase(newPhrase);
						table.addCell(newCell);	

						newCell = new PdfPCell(cell);
						newPhrase = new Phrase("Controlling Offense:", font8);
						newCell.setPhrase(newPhrase);
						table.addCell(newCell);			
						
						newCell = new PdfPCell(cell);
						newPhrase = new Phrase(hmapOff.get("Controlling Offense"), font8);
						newCell.setPhrase(newPhrase);
						table.addCell(newCell);
						
						newCell = new PdfPCell(cell);
						newPhrase = new Phrase("", font8);
						newCell.setPhrase(newPhrase);
						table.addCell(newCell);
						
						newCell = new PdfPCell(cell);
						newPhrase = new Phrase("", font8);
						newCell.setPhrase(newPhrase);
						table.addCell(newCell);
					}
				}
			} else {
				table = new PdfPTable(1);
				newCell = new PdfPCell(cell);
				newPhrase = new Phrase("NONE", font8);
				newCell.setPhrase(newPhrase);
				table.addCell(newCell);		
			}

			//Section 8 Ends here
			
			//Section 9 Starts here			
			document.add(table);
			
			//show Heading - Job Information
			table = new PdfPTable(1);
			newCell = new PdfPCell(cell);
			newCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			newPhrase = new Phrase("Job Information", headingFont);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);	

			document.add(table);
			
						
			String sortedJobInfos = (String)tempPropsMap.get("Sorted Employer Information");
			cnt=0;
			String[] jobInfos = null;
			if(sortedJobInfos != null) {
				table = new PdfPTable(4);
				jobInfos = sortedJobInfos.split("@@");
				while (jobInfos != null && cnt<jobInfos.length){
					String currJobInfo = "";
					String jobInfo = jobInfos[cnt];
					String[] jobInfo_result = null;
					if(jobInfo != null){
						jobInfo_result = jobInfo.split(PLMConstants.SEPARATOR);
					}
		
					if(jobInfo_result != null){
						int iResult=0;
						//employer
						if (jobInfo_result.length > 0 && jobInfo_result[0] != null && !jobInfo_result[0].trim().equals("")){
							currJobInfo = currJobInfo+jobInfo_result[0]+"\n";
						}
						//street
						if (jobInfo_result.length > 1 && jobInfo_result[1] != null && !jobInfo_result[1].trim().equals("")){
							currJobInfo = currJobInfo+jobInfo_result[1]+"\n";
						}
						//city
						if (jobInfo_result.length > 2 && jobInfo_result[2] != null && !jobInfo_result[2].trim().equals("")){
							currJobInfo = currJobInfo+jobInfo_result[2]+", ";
							iResult = 1;
						}
						//county
						if (jobInfo_result.length > 4 && jobInfo_result[4] != null && !jobInfo_result[4].trim().equals("")){
							iResult = 1;
							currJobInfo = currJobInfo+jobInfo_result[4];
						}
						
						if(iResult > 0)
							currJobInfo = currJobInfo+"\n";
							iResult = 0;
						//state							
						if (jobInfo_result.length > 3 && jobInfo_result[3] != null && !jobInfo_result[3].trim().equals("")){
							currJobInfo = currJobInfo+jobInfo_result[3]+", ";
							iResult = 1;
						}
						//zip
						if (jobInfo_result.length > 5 && jobInfo_result[5] != null && !jobInfo_result[5].trim().equals("")){
							currJobInfo = currJobInfo+jobInfo_result[5];
							iResult = 1;
						}
						//zip4
						if (jobInfo_result.length > 6 && jobInfo_result[6] != null && !jobInfo_result[6].trim().equals("")){
							currJobInfo = currJobInfo+"-"+jobInfo_result[6];
							iResult = 1;
						}
						if(iResult >0)
							currJobInfo = currJobInfo+"\n";
						//phone
						if (jobInfo_result.length > 7 && jobInfo_result[7] != null && !jobInfo_result[7].trim().equals("")){
							currJobInfo = currJobInfo + "Phone:" + jobInfo_result[7]+"\n";
						}
						//job title
						if (jobInfo_result.length > 8 && jobInfo_result[8] != null && !jobInfo_result[8].trim().equals("")){
							currJobInfo = currJobInfo+jobInfo_result[8]+"\n";
						}
						//emp aware
						if (jobInfo_result.length > 9 && jobInfo_result[9] != null && !jobInfo_result[9].trim().equals("")){
							currJobInfo = currJobInfo+"Employer Aware:  "+jobInfo_result[9]+"\n";
						}
						//start date
						if (jobInfo_result.length > 10 && jobInfo_result[10] != null && !jobInfo_result[10].trim().equals("")){
							strDiscDate = jobInfo_result[10];					
						}else{
							strDiscDate ="";
						}
					}
					cnt++;
					newCell = new PdfPCell(cell);
					newPhrase = new Phrase(strDiscDate, font8);
					newCell.setPhrase(newPhrase);
					table.addCell(newCell);	
					
					newCell = new PdfPCell(cell);
					newPhrase = new Phrase(currJobInfo, font8);
					newCell.setPhrase(newPhrase);
					table.addCell(newCell);	
				}		
				
				if(cnt % 2 > 0) {
					newCell = new PdfPCell(cell);
					newPhrase = new Phrase("", font8);
					newCell.setPhrase(newPhrase);
					table.addCell(newCell);	
					
					newCell = new PdfPCell(cell);
					newPhrase = new Phrase("", font8);
					newCell.setPhrase(newPhrase);
					table.addCell(newCell);
				}
			} else {
				table = new PdfPTable(1);
				newCell = new PdfPCell(cell);
				newPhrase = new Phrase("NONE", font8);
				newCell.setPhrase(newPhrase);
				table.addCell(newCell);		
			}
			//Section 9 Ends here

			//Section 10 Starts here			
			document.add(table);
			
			//show Heading - Vehicle Information
			table = new PdfPTable(1);
			newCell = new PdfPCell(cell);
			newCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			newPhrase = new Phrase("Vehicle Information", headingFont);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);	

			document.add(table);
			
			
			Collection vehInfoColl = tempPropsMap.getValues("Vehicle Information");
			
			if (vehInfoColl != null && vehInfoColl.size() > 0){
				table = new PdfPTable(8);
				@SuppressWarnings("null")
				Iterator vehInfoCollIter = (vehInfoColl != null)?vehInfoColl.iterator():null;	
				while (vehInfoCollIter != null && vehInfoCollIter.hasNext()){
					HashMap hmapVeh = new HashMap();
					String vehInfo = (String)vehInfoCollIter.next();
					String[] vehInfo_result = vehInfo.split(PLMConstants.SEPARATOR);
					if (vehInfo_result[1] != null && !vehInfo_result[1].equals("")){
						hmapVeh.put("Make", vehInfo_result[1]);
					}else{
						hmapVeh.put("Model", "");
					}
					if (vehInfo_result[3] != null && !vehInfo_result[3].equals("")){
						hmapVeh.put("Model", vehInfo_result[3]);
					}else{
						hmapVeh.put("Model", "");
					}
					if (vehInfo_result[5] != null && !vehInfo_result[5].equals("")){
						hmapVeh.put("Style", vehInfo_result[5]);
					}else{
						hmapVeh.put("Style", "");
					}
					if (vehInfo_result[6] != null && !vehInfo_result[6].equals("")){
						hmapVeh.put("Vehicle Class", vehInfo_result[6]);
					}else{
						hmapVeh.put("Vehicle Class", "");
					}
					if (vehInfo_result[7] != null && !vehInfo_result[7].equals("")){
						hmapVeh.put("Year", vehInfo_result[7]);
					}else{
						hmapVeh.put("Year", "");
					}
					if (vehInfo_result[9] != null && !vehInfo_result[9].equals("")){
						hmapVeh.put("Color 1", vehInfo_result[9]);
					}else{
						hmapVeh.put("Color 1", "");
					}
					if (vehInfo_result[11] != null && !vehInfo_result[11].equals("")){
						hmapVeh.put("Color 2", vehInfo_result[11]);
					}else{
						hmapVeh.put("Color 2", "");
					}
					if (vehInfo_result[12] != null && !vehInfo_result[12].equals("")){
						hmapVeh.put("License Plate", vehInfo_result[12]);
					}else{
						hmapVeh.put("License Plate", "");
					}
					if (vehInfo_result[13] != null && !vehInfo_result[13].equals("")){
						hmapVeh.put("State", vehInfo_result[13]);
					}else{
						hmapVeh.put("State", "");
					}
					if (vehInfo_result[17] != null && !vehInfo_result[17].equals("")){
						hmapVeh.put("Owned", vehInfo_result[17]);
					}else{
						hmapVeh.put("Owned", "");
					}
					
					newCell = new PdfPCell(cell);
					newPhrase = new Phrase("Make", font8);
					newCell.setPhrase(newPhrase);
					table.addCell(newCell);	
					
					newCell = new PdfPCell(cell);
					newPhrase = new Phrase((String)hmapVeh.get("Make")!=null?(String)hmapVeh.get("Make"):"", font8);
					newCell.setPhrase(newPhrase);
					table.addCell(newCell);	
					
					newCell = new PdfPCell(cell);
					newPhrase = new Phrase("Model", font8);
					newCell.setPhrase(newPhrase);
					table.addCell(newCell);
					
					newCell = new PdfPCell(cell);
					newPhrase = new Phrase((String)hmapVeh.get("Model")!=null?(String)hmapVeh.get("Model"):"", font8);
					newCell.setPhrase(newPhrase);
					table.addCell(newCell);	
					
					newCell = new PdfPCell(cell);
					newPhrase = new Phrase("Style", font8);
					newCell.setPhrase(newPhrase);
					table.addCell(newCell);	
					
					newCell = new PdfPCell(cell);
					newPhrase = new Phrase((String)hmapVeh.get("Style")!=null?(String)hmapVeh.get("Style"):"", font8);
					newCell.setPhrase(newPhrase);
					table.addCell(newCell);	
					
					newCell = new PdfPCell(cell);
					newPhrase = new Phrase("Vehicle Class", font8);
					newCell.setPhrase(newPhrase);
					table.addCell(newCell);	
					
					newCell = new PdfPCell(cell);
					newPhrase = new Phrase((String)hmapVeh.get("Vehicle Class")!=null?(String)hmapVeh.get("Vehicle Class"):"", font8);
					newCell.setPhrase(newPhrase);
					table.addCell(newCell);	
					
					newCell = new PdfPCell(cell);
					newPhrase = new Phrase("Year", font8);
					newCell.setPhrase(newPhrase);
					table.addCell(newCell);	
					
					newCell = new PdfPCell(cell);
					newPhrase = new Phrase((String)hmapVeh.get("Year")!=null?(String)hmapVeh.get("Year"):"", font8);
					newCell.setPhrase(newPhrase);
					table.addCell(newCell);	
					
					newCell = new PdfPCell(cell);
					newPhrase = new Phrase("Color 1", font8);
					newCell.setPhrase(newPhrase);
					table.addCell(newCell);
					
					newCell = new PdfPCell(cell);
					newPhrase = new Phrase((String)hmapVeh.get("Color 1")!=null?(String)hmapVeh.get("Color 1"):"", font8);
					newCell.setPhrase(newPhrase);
					table.addCell(newCell);	
					
					newCell = new PdfPCell(cell);
					newPhrase = new Phrase("Color 2", font8);
					newCell.setPhrase(newPhrase);
					table.addCell(newCell);	
					
					newCell = new PdfPCell(cell);
					newPhrase = new Phrase((String)hmapVeh.get("Color 2")!=null?(String)hmapVeh.get("Color 2"):"", font8);
					newCell.setPhrase(newPhrase);
					table.addCell(newCell);	
					
					newCell = new PdfPCell(cell);
					newPhrase = new Phrase("License Plate", font8);
					newCell.setPhrase(newPhrase);
					table.addCell(newCell);	
					
					newCell = new PdfPCell(cell);
					newPhrase = new Phrase((String)hmapVeh.get("License Plate")!=null?(String)hmapVeh.get("License Plate"):"", font8);
					newCell.setPhrase(newPhrase);
					table.addCell(newCell);						
				}				
			} else {
				table = new PdfPTable(1);
				newCell = new PdfPCell(cell);
				newPhrase = new Phrase("NONE", font8);
				newCell.setPhrase(newPhrase);
				table.addCell(newCell);		
			}
			//Section 10 Ends here	

			//Section 11 Starts here			
			document.add(table);
			
			//show Heading - Comments
			table = new PdfPTable(1);
			newCell = new PdfPCell(cell);
			newCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			newPhrase = new Phrase("Comments", headingFont);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);	

			document.add(table);
			
			table = new PdfPTable(1);
			newCell = new PdfPCell(cell);
			newPhrase = new Phrase((String)tempPropsMap.get("Comments")!=null?(String)tempPropsMap.get("Comments"):"NONE", font8);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);	
			//Section 11 Ends here

			//Section 12 Starts here			
			document.add(table);
			
			//show Heading - Other Special Parole Conditions
			table = new PdfPTable(1);
			newCell = new PdfPCell(cell);
			newCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			newPhrase = new Phrase("Other Special Parole Conditions", headingFont);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);	

			document.add(table);
			
			Collection spInfoColl = tempPropsMap.getValues("Special Condition Information");
			HashMap hmapSp = new HashMap();
			if (spInfoColl != null && spInfoColl.size()>0){
				
				table = new PdfPTable(2);
				headerwidths = new int[2];
				headerwidths[0]=7;
				headerwidths[1]=30;
				table.setWidths(headerwidths);
				
				@SuppressWarnings("null")
				Iterator spInfoCollIter = (spInfoColl != null)?spInfoColl.iterator():null;
				
				while (spInfoCollIter != null && spInfoCollIter.hasNext()){				
					String spInfo = (String)spInfoCollIter.next();
					String[] spInfo_result = spInfo.split(PLMConstants.SEPARATOR);
					if (spInfo_result[0] != null && !spInfo_result[0].equals("")){
						hmapSp.put("Special Comment", spInfo_result[0]);
					}else{
						hmapSp.put("Special Comment", "");
					}				
					
					newCell = new PdfPCell(cell);
					newPhrase = new Phrase("Special Comments", font8);
					newCell.setPhrase(newPhrase);
					table.addCell(newCell);
						
					newCell = new PdfPCell(cell);
					//added check ofr PRCS - LBB
					if (sUnitNm.substring(0,4).equals("PRCS") || sUnitNm.substring(0,4).equals("DAI-")) {
						newPhrase = new Phrase(" ");
					}else {
						newPhrase = new Phrase((String)hmapSp.get("Special Comment")!=null?(String)hmapSp.get("Special Comment"):"NONE", font8);
					}
					newCell.setPhrase(newPhrase);
					table.addCell(newCell);			
				}
			} else {
				table = new PdfPTable(1);
				newCell = new PdfPCell(cell);
				//added check ofr PRCS - LBB
				if (sUnitNm.substring(0,4).equals("PRCS") || sUnitNm.substring(0,4).equals("DAI-")) {
					newPhrase = new Phrase(" ");
				}else {
					newPhrase = new Phrase("NONE", font8);
				}
				newCell.setPhrase(newPhrase);
				table.addCell(newCell);	
			}
			
			document.add(table);

			table = new PdfPTable(1);
			newCell = new PdfPCell(cell);
			newPhrase = new Phrase("\nDO NOT ARREST, DETAIN, SEARCH, OR TAKE ANY ACTION BASED ON THIS DOCUMENT WITHOUT FIRST CONFIRMING THE PAROLEE'S CURRENT STATUS.", font10);
			newCell.setPhrase(newPhrase);
			table.addCell(newCell);	

			document.add(table);
			document.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}   	
    }
}