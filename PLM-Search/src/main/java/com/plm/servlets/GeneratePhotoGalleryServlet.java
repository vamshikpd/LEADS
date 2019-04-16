package com.plm.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.endeca.navigation.UrlGen;
import com.plm.util.database.PLMDatabaseUtil;

/**
 * Servlet implementation class GeneratePhotoGalleryServlet
 */
public class GeneratePhotoGalleryServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GeneratePhotoGalleryServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		String cdcnum = request.getParameter("cdcnum");
		String firstname = request.getParameter("firstname");
		String lastname = request.getParameter("lastname");
		
		String fullname = firstname + " " + lastname;
		String showid = request.getParameter("showid");
		StringBuffer sb = new StringBuffer();
		String sPhotographer = "";
		String sDate = "";
		String sType = "";
		String sDesc = "";
		
		UrlGen urlPhotoNistPrev = new UrlGen("", "UTF-8");
		urlPhotoNistPrev.addParam("cdcNum",cdcnum);
		urlPhotoNistPrev.addParam("showid",showid);
		urlPhotoNistPrev.addParam("firstName",firstname);
		urlPhotoNistPrev.addParam("lastName",lastname);
		urlPhotoNistPrev.addParam("pdfdisptype","photonist");
		
		urlPhotoNistPrev.addParam("psize","p");
		String urlPhotoPreview 		= "GeneratePDFServlet?" + urlPhotoNistPrev 		+ "&TB_iframe=true" + "&width=450" + "&height=545";
		urlPhotoNistPrev.removeParam("psize");
		urlPhotoNistPrev.addParam("psize","n");
		String urlNistPreview 		= "GeneratePDFServlet?" + urlPhotoNistPrev 		+ "&TB_iframe=true" + "&width=450" + "&height=545";
		
		HashMap<String,String> info = new HashMap<String,String>();
		try {
			info = PLMDatabaseUtil.getPhotoDetailsByID(showid);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		sPhotographer = info.get("inserted_by")!=null?info.get("inserted_by"):"&nbsp;";
		sDate = info.get("insert_date")!=null?info.get("insert_date"):"&nbsp;";
		sType = info.get("type_text")!=null?info.get("type_text"):"&nbsp;";
		if(info.get("subtype")!=null)
			sType = sType + ":" + info.get("subtype");
		sDesc = info.get("descr")!=null?info.get("descr"):"&nbsp;";

		sb.append("	<img alt=\"\" src=\"image.jsp?showid=" + showid + "&psize=p\" />");
		sb.append("~");
		sb.append(urlPhotoPreview);
		sb.append("~");
		sb.append(urlNistPreview);
		sb.append("~");
		sb.append("<div><span class="+"\"" +"essenceinfoname"+"\"" +">" +"CDC#:</span> "+cdcnum+"</div>");
		sb.append("<div><span class="+"\"" +"essenceinfoname"+"\"" +">" +"Name:</span>"+fullname+"</div>");
		sb.append("<div><span class="+"\"" +"essenceinfoname"+"\"" +">" +"Photographer:</span>"+sPhotographer+"</div>");
		sb.append("<div><span class="+"\"" +"essenceinfoname"+"\"" +">" +"Date:</span>"+sDate+"</div>");
		sb.append("<div><span class="+"\"" +"essenceinfoname"+"\"" +">" +"Type:</span>" + sType + "</div>");
		sb.append("<div><span class="+"\"" +"essenceinfoname"+"\"" +">" +"Description:</span>"+sDesc+"</div>");

		out.print(sb.toString());
		out.close();

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request,response);
	}

}
