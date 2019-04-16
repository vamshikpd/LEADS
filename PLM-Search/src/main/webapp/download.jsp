<%@ page import="java.io.*" %>
<%@ page import="com.endeca.ui.constants.*" %>
<%@ page import="com.plm.constants.PLMConstants" %>
<%
	String fileName = request.getParameter("fileName");
	response.setContentType("application/zip");
	response.setHeader("Content-disposition", "attachment;filename="+fileName); // To pop dialog box

	String downloadFileOutputpath =UI_Props.getInstance().getValue(PLMConstants.DEFAULT_DATA_PHOTO_DOWNLOAD_PATH_LABEL);
	File f = new File (downloadFileOutputpath+"/"+fileName);
	//OPen an input stream to the file and post the file contents thru the
	//servlet output stream to the client m/c
	InputStream in = new FileInputStream(f);

	/*byte[] buffer = new byte[4096];
	int length;
	try {
		while ((length = in.read(buffer)) > 0) {
			out.write(buffer, 0, length);
		}
	} catch (IOException ioe) {
		ioe.printStackTrace(System.out);
	}*/

	int bit = 256;
	int i = 0;
	out.clearBuffer();
	try {
		while ((bit) >= 0) {
			bit = in.read();
			out.write(bit);
		}
	} catch (IOException ioe) {
		ioe.printStackTrace(System.out);
	}


	in.close();
	out.flush();
%>