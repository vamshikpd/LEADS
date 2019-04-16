<!DOCTYPE html>
<html>
<head>
    <title>PLM-Search HTTP Request Information</title>
</head>

<script type="javascript">
    /*function resetPassword() {
        var loginid = '<%=request.getHeader("USERID")%>';
        var hostname = '<%=request.getHeader("Host")%>';
        var requestSecure = '<%=request.isSecure()%>';
        var connType = (requestSecure) ? "https://" : "http://";
        var redirectURL = location.href;
        var resetPasswdURL = connType + hostname + "/identity/oblix/apps/lost_pwd_mgmt/bin/lost_pwd_mgmt.cgi?program=redirectforchangepwd&" +
            "login=" + loginid + "&backURL=" + redirectURL + "&target=top";
        //alert(" URL : " + resetPasswdURL);
        location.href = resetPasswdURL;
    }*/

    function logoff() {
        location.href = "<%=request.getContextPath()%>/logout";
    }

</script>
<body>

    <%--<br><a href="#" onclick="javascript:resetPassword()">Reset Password</a>--%>
    <br><a href="#" onclick="logoff()">Logoff</a>
    <br>
    <H1>Header and Request Information</H1>
    <BR>

    <table border="1">
        <% java.util.Enumeration names = request.getHeaderNames();
            while (names.hasMoreElements()) {
                String name = (String) names.nextElement();
        %>
        <tr>
            <td bgcolor="#CCCCCC"><%= name %>
            </td>
            <td><%= request.getHeader(name) %>
            </td>
        </tr>
        <%
            }
        %>
    </table>
    <br/>

    <h2>HTTP Request Attributes:</h2>
    <TABLE border="1">
        <% names = request.getAttributeNames();
            while (names.hasMoreElements()) {
                String name = (String) names.nextElement();
        %>
        <tr>
            <td bgcolor="#CCCCCC"><%= name %></td>
            <td><%= request.getAttribute(name) %></td>
        </tr>
        <%
            }
        %>
    </TABLE>

    <br/>

    <h2>Cookies:</h2>
    <table border="1">
        <%
            Cookie cookies[] = request.getCookies();
            Cookie myCookie;

            for (int i = 0; i < cookies.length; i++) {
                myCookie = cookies[i];
        %>
        <tr>
            <td bgcolor="#CCCCCC"><%= myCookie.getName() %></td>
        </tr>
        <tr>
            <td>
                VALUE: <%= myCookie.getValue() %> <br/>
                MAXAGE: <%= myCookie.getMaxAge() %> <br/>
                PATH: <%= myCookie.getPath() %> <br/>
                DOMAIN: <%= myCookie.getDomain() %> <br/>
                COMMENT: <%= myCookie.getComment() %> <br/>
                SECURE: <%= myCookie.getSecure() %> <br/>
            </td>
        </tr>
        <%
            }
        %>
    </table>
</body>
</html>