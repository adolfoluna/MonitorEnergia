<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Lista de puertos seriales</title>
</head>
<body>
<%

	String lista[]=(String [])request.getAttribute("lista");

	for(String aux : lista )
		out.print("<br/>"+aux);
	
	%>
</body>
</html>