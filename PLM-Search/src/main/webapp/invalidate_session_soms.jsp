<% session.invalidate(); %>
<!DOCTYPE html>
<html>
<head>
    <title>Parole LEADS 2.0</title>
    <meta http-equiv="X-UA-Compatible" content="IE=11">
    <script type="text/javascript">
        function closeCurrentWindow() {
            console.log("SOMS LEADS logoff...");
            window.open('','_self','').close();
        }

    </script>
</head>
    <body onload="closeCurrentWindow()" style="text-align: center">
        <div style="text-align:left; margin:0 auto;margin-top:30px">Logging off...</div>
    </body>
</html>
