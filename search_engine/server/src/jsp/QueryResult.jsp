<%@ page language="java" contentType="text/html; charset=US-ASCII"
    pageEncoding="US-ASCII"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">

<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
<link rel="stylesheet" href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
<script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>

<!-- Custom styles for this template -->
<link href="../css/searchList.css" rel="stylesheet">
    
<%String plain_query = (String)request.getAttribute("plain_query"); %>
<%String image_query = (String)request.getAttribute("image_query");%>
<%if(plain_query!= null){%>
    <title><%=plain_query%></title>
<%}else{%>
    <title><%=image_query%></title>
<%}%>

<link rel="shortcut icon" href="/images/buzzitlogo.jpg">
</head>
<body>

<!-- Fixed navbar -->
    <nav class="navbar navbar-default navbar-fixed-top">
      <div class="container-fluid">
        <div class="navbar-header">
			<a href="/home" data-toggle="tooltip" title="back to home page">
        		<img src="../images/buzzit_logo_transparent.gif" class="img-rounded navbar-brand" alt="logo_transparent">
        	</a>
        </div>
        <div class="navbar-nav">
        	<form action= "/search_result" enctype="multipart/form-data" method="post" class="form-horizontal" role="form">
        	<% if(plain_query!=null){ %>
          	    <input type="text" name="SEARCH_KEY" value="<%=plain_query%>">
          	<% }else{ %>
          	    <input type="text" name="SEARCH_KEY" value="">
          	<% } %>
          	<button type="button" class="btn btn-default" data-toggle="collapse" data-target="#imageSearch"><span class="glyphicon glyphicon-camera"></span></button>

          	<div id="imageSearch" class="collapse">
          	    <input type="file" class="btn btn-default" name="image">
            </div>
          	<button type="submit" class="btn btn-default"><span class="glyphicon glyphicon-search"></span></button>

  			
          	</form>
        </div>    
          
      </div>
    </nav>
<!--
<form action= "/search_result" method="post">
<input type="text" name="SEARCH_KEY" value="" />
<input type="submit" /> 
</form>
-->

<%! int i=1; %>
<%@ page import="com.buzzit.ranker.*" %>
<%@ page import="com.buzzit.indexer.wiki.*" %>
<%@ page import="java.util.*" %>
<%
QueryResultItems qri = (QueryResultItems)request.getAttribute("QueryResultItems");
KnowledgeEntity knowledgeEntity = qri.getKnowledgeEntity();
String wikiTitle = null;
String wikiContent = null;
String wikiUrl = null;
if(knowledgeEntity!=null){
wikiTitle = knowledgeEntity.getTitle();
wikiContent = knowledgeEntity.getContent();
wikiUrl = knowledgeEntity.getUrl();
}
%>
<div class="row">
    <div class="col-lg-6">
        <% if(image_query!=null){ %>
        <p class="lead"> Is <mark><%=image_query%></mark> what you need?</p>
        <ul>
        <%
        List<QueryPageEntity> qpel = qri.getWebPageEntities();
        for(int i=0;i<qpel.size();++i){%>
        <% QueryPageEntity qpe = qpel.get(i);
	        if(qpe != null){
		    //request.getSession().setAttribute("sov", sov);
	 	    String title = qpe.getTitle();
	 	    String url = qpe.getUrl();
	 	    String abst = qpe.getAbs();
	 	    String directURL = url;
        %>
            <ul class="list-group">
  		        <ul>
  			    <h2><a href="<%=url%>"><%=title%></a></h2>
  			    <small><%= url %>></small>
  		        </ul>
  		        <ul><blockquote><%=abst%></blockquote></ul>
	        </ul>
            <%
	        }
        }
        }
        %>
        </ul>
    </div>
    <%if(wikiTitle!=null && wikiContent != null && wikiUrl != null){%>
    <div class="col-lg-6">
        <ul class="list-group">
            <ul>
        	    <h2><a href="<%=wikiUrl%>"><%=wikiTitle%></a></h2>
        	    <small><%=wikiUrl%>></small>
            </ul>
            <ul><blockquote><%=wikiContent%></blockquote></ul>
        </ul>
    <div>
    <%}%>
</div>
</body>
</html>
