<%@page import="thesis.test.TestUnit"%>
<%@page import="thesis.logic.FJNUGradeCrawler"%>
<%@page import="thesis.httpMethod.HttpManager"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="pragma" content="no-cache"/>
<meta http-equiv="cache-control" content="no-cache"/>
<meta http-equiv="expires" content="0"/>
<title>Insert title here</title>
</head>
<body>
	<%
		//String viewState = FJNUGradeCrawler.getViewState();
		String img_src ="/SimploServer/check_img.gif?t=" + Math.random(); 
		//HttpManager.getCheckcodeToSave("http://jwgl.fjnu.edu.cn/CheckCode.aspx");
		//TestUnit.regExpTest();
	%>
	<form action="QueryGradeServlet" method="get">
		
		<p>用户名: <input type="text" name="userName" /></p>
  		<p>密码: <input type="text" name="password" /></p>
  		<p>验证码：<input type="text" name="checkCode" /></p>
  		<img alt="验证码" src=<%=img_src %>><img>
 		<input type="hidden" name="viewState" value="" />
  		<input type="submit" value="查询" />
	</form>

</body>
</html>