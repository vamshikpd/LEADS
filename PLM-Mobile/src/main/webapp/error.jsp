<%@ page language="java" contentType="text/html;charset=UTF-8" %>
<%@ page isErrorPage="true" %>
<%@ page import="com.endeca.navigation.*" %>
<%@ page import="com.endeca.logging.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.net.*" %>
<%@ page import="java.text.*" %>
<%@ page import="java.io.*" %>

<%

// Set global exception
if (exception != null) {
	request.setAttribute("exception", exception);
}

// Print exceptions in development mode only
if ((exception != null)) { 
	%>
	<div>
	<font color="gray">
	<pre><%= exception %></pre><br>
	<pre>Stack trace:
	<%
	// Print out the stack trace of the exception
	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	exception.printStackTrace(new PrintStream(baos));
	out.print(baos);

	 %>
	</pre>
	</font>
	</div>
	<%
}
%>
