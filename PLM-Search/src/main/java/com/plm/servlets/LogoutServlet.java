package com.plm.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.endeca.ui.constants.UI_Props;
import org.apache.log4j.Logger;

public class LogoutServlet extends HttpServlet {

	private static final long serialVersionUID = 7483019740473168673L;
	
	private static final Logger logger = Logger
			.getLogger(LogoutServlet.class);

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {		
		doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {		

		String url = UI_Props.getInstance().getValue("f5logout");
		logger.debug("Logging out... logout url=" + url);

        // invalidate the session
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

//		weblogic.servlet.security.ServletAuthentication.invalidateAll(request);
//		String url = UI_Props.getInstance().getValue("idcshost")+UI_Props.getInstance().getValue("idcslogout");

		response.sendRedirect(url);	
	}
	
}
