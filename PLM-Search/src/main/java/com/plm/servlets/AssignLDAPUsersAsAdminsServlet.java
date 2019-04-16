package com.plm.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import org.apache.log4j.Logger;

import com.plm.oam.apps.LDAPMoveUsers;
import com.plm.util.PLMUtil;


/**
 * Servlet implementation class AssignLDAPUsersAsAdminsServlet
 */
public class AssignLDAPUsersAsAdminsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	private static final Logger logger = Logger.getLogger(AssignLDAPUsersAsAdminsServlet.class);
	
	private static final String GROUP_LIST = "LEADSPasswordAdmins";
	
	private static final String OLD_OU = "LEADSUsers";
	
	private static final String NEW_OU = "LEADSAccountAdmins";
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AssignLDAPUsersAsAdminsServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request,response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		if(isMultipart) {
			// Create a factory for disk-based file items
			FileItemFactory factory = new DiskFileItemFactory();
			// Create a new file upload handler
			ServletFileUpload upload = new ServletFileUpload(factory);
			// Parse the request
			try {
				List<FileItem> items = upload.parseRequest(request);
				List<String> failedUsers = new ArrayList<String>();
				// Process the uploaded items
				Iterator iter = items.iterator();
				while (iter.hasNext()) {
				    FileItem item = (FileItem) iter.next();
				    if (item.isFormField()) {
				    	//Process only the userlist fieldName
				    	String userList = item.getString();
				    	if(item.getFieldName().equals("userlist") 
				    			&& !userList.equals("")) {
							logger.info("Processing userlist.");
					        int iUsersProcessed = processString(userList,failedUsers);
					        logger.info("Processed Items : " + iUsersProcessed);
					        request.setAttribute("recordsProcessed", Integer.toString(iUsersProcessed));
					        request.setAttribute("failedUsers",failedUsers);
					        break;
				    	}
				    }
				    else if(item.getFieldName().equals("userlistfile")){
						logger.info("Processing userfile.");
				        int iUsersProcessed = processUploadedFile(item,failedUsers);
				        logger.info("Processed Items : " + iUsersProcessed);
				        request.setAttribute("recordsProcessed", Integer.toString(iUsersProcessed));
				        request.setAttribute("failedUsers",failedUsers);
				        break;
				    }
				}
			} catch (FileUploadException e) {
            	logger.error(PLMUtil.getStackTrace(e));
				throw new ServletException("File upload failed.");
			}
		}
		RequestDispatcher dispatcher = request.getRequestDispatcher("/respAssignAdminUsers");
		dispatcher.forward(request, response);
	}

	private int processUploadedFile(FileItem item, List<String> failedUsers) throws ServletException {
		String sCSV = null;
		InputStream uploadedStream = null;
		try {
			uploadedStream = item.getInputStream();
			StringBuffer sb = new StringBuffer();
			BufferedReader br = new BufferedReader(new InputStreamReader(uploadedStream));
			String eachLine = null;
			while ((eachLine = br.readLine())!= null) {
				sb.append(eachLine);
				sb.append("\n");
			}
			sCSV = sb.toString();

		} catch (IOException e) {
        	logger.error(PLMUtil.getStackTrace(e));
			throw new ServletException("File read error.");
		}
		return processString(sCSV,failedUsers);
	}
	
	private int processString(String userList,List<String> failedList) throws ServletException {
		return (LDAPMoveUsers.moveUsers(userList, GROUP_LIST, OLD_OU, NEW_OU, failedList)).size();
	}
}
