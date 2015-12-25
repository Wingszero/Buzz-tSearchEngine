<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel = "stylesheet" type ="text/css" href = "Rating.css">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
<link rel="stylesheet" href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
<script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>

<!-- Custom styles for this template -->
<link href="../css/searchObject.css" rel="stylesheet">

<title>Search Result Object</title>
</head>
<body>
<%@ page import="com.myapp.view.*" %>

<% SearchObjectView sov = (SearchObjectView) request.getSession().getAttribute("sov");

      String title = sov.getTitle();
      String url = sov.getUrl();
      String abst = sov.getAbstract();
      String directURL = url; 
%>
      
    <td>
        <a href="<%=url%>"><%=title%></a>
    </td> 
    
    <td>
        <%=url%>
    </td> 

    <td>
        <H2><%=abst%></H2>
    </td> 

</body>
</html>
