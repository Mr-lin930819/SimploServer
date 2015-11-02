<%@page import="thesis.httpMethod.NetworkManager"%>
<%@page import="thesis.httpMethod.HttpManager"%>
<%@page import="thesis.logic.PreLogic"%>
<%@page import="thesis.test.TestUnit" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>测试页面</title>
</head>
<body>
<style type="text/css">

form#mainForm {
	border: green;
	background: #4a5868;
}


</style>
<% 
	NetworkManager nm = new NetworkManager();
	//NetworkManager nm = (NetworkManager)request.getAttribute("nm");
	String viewState = PreLogic.getOne(nm);
	String cookie = nm.cookies.get("http://jwgl.fjnu.edu.cn/default2.aspx");
	String imgSrc = "/SimploServer/CheckCodeTest?cookie=" + cookie;
%>
	<form action="/SimploServer/TransUserInfoServlet" method="get" id="mainForm">
			<input type="hidden" name="viewState" value=<%= viewState%>>
			<input type="hidden" name="cookie" value=<%=cookie %>>
			<input type="hidden" name="xnStr" value="2014-2015">
			<input type="hidden" name="xqStr" value="">
		<p>
			USERNAME:<input type="text" name="username">
		</p>
		<p>
			PASSWORD:<input type="text" name="password">
		</p>
		<p>
			CHECKCODE:<input type="text" name="checkCode">
		</p>
		
	<!--  <img src="http://jwgl.fjnu.edu.cn/CheckCode.aspx"> -->
		<img src=<%=imgSrc %>>
		<input type="submit" value="Post测试">
	</form>
	<br>
	<HR style="FILTER: alpha(opacity=100,finishopacity=0,style=3)" width="80%" color=#987cb9 SIZE=3>
	<br>
	<form action="/SimploServer/MainPageGetTestServlet" method="get">
		<p>
			COOKIE:<input type="text" name="cookie">
		</p>
		<input type="submit" value="Get主界面测试">
	</form>
	<br>
	<HR style="FILTER: alpha(opacity=100,finishopacity=0,style=3)" width="80%" color=#987cb9 SIZE=3>
	<br>
	<form action="/SimploServer/TestGetCookieServlet" method="get">
		<input type="text" name="checkCode" >
		<input type="submit" value="Cookie获取测试">
	</form>
	<br>
	<HR style="FILTER: alpha(opacity=100,finishopacity=0,style=3)" width="80%" color=#987cb9 SIZE=3>
	<br>
	
	
	
	
</body>
</html>