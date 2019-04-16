<%@ page import = "java.io.*" %>
<%@ page import = "java.sql.*" %>
<%@ page import = "java.util.*" %>
<%@ page import="java.text.*" %>
<%@ page import="com.plm.util.database.PLMDatabaseUtil" %>
<%@ page import="com.plm.constants.PLMConstants" %>
<%
 
  String iNumPhoto ;
  //String defaultPhoto = application.getRealPath("/") + PLMConstants.default_image_path;  
  
  String cdcNumber = request.getParameter("cdcNum");
  String showid = request.getParameter("showid");
  String pSize =  request.getParameter("psize");
  String rootPath = application.getResource("/").getPath();
  String defaultPhoto = null;
	
	if(rootPath  != null) {
		int index = rootPath.indexOf("WEB-INF");
		if(index >= 0 ) {
			rootPath = rootPath.substring(0,index);
		}
	}
	 
	if("t".equals(pSize)) {
		defaultPhoto=rootPath + PLMConstants.DEFAULT_THUMBNAIL_IMAGE_PATH;
	} else {
		defaultPhoto=rootPath + PLMConstants.DEFAULT_PHOTO_IMAGE_PATH;
	}
 
  if (cdcNumber != null && !("".equalsIgnoreCase(cdcNumber)))
  {
	  // get the image from the database
      byte[] imgData = PLMDatabaseUtil.getPhoto(cdcNumber,pSize, defaultPhoto) ;   
       // display the image
       //response.reset();  // this is required for weblogic!
		response.setContentType("image/jpeg");
		  //out.clear();
 		OutputStream o = response.getOutputStream();
       o.write(imgData);
       o.flush(); 
       o.close();
 }else{
	  // get the image from the database
     byte[] imgData = PLMDatabaseUtil.getPhotoByID(showid,pSize, defaultPhoto) ;   
      // display the image
      response.reset();
		response.setContentType("image/jpeg");
		OutputStream o = response.getOutputStream();
      o.write(imgData);
      o.flush(); 
      o.close();	 
 }
  //out.clear();
  //out = pageContext.pushBody(); 
%>
