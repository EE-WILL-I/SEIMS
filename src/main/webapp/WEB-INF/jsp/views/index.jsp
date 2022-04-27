<%@ page import="java.util.ResourceBundle" %>
<%@ page import="ru.seims.localization.LocalizationManager" %>
<%@ page import="ru.seims.database.entitiy.User" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>State Educational Institution Monitoring Service</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css" />
</head>
<body>
<jsp:include page="../elements/header.jsp" />
<jsp:include page="../elements/popup.jsp"/>
<div class="content_holder">
    <h1><%=LocalizationManager.getString("index.hello")%> <%=((User)pageContext.getSession().getAttribute("user")).getFullName()%></h1>
    <%java.util.Date date = new java.util.Date();%>
    <h2>
        <%=LocalizationManager.getString("index.time")%> <%=date.toString()%>
    </h2>
    <jsp:include page="../elements/interactiveMap.jsp"/>
    <jsp:include page="../elements/simpleChart.jsp"/>
</div>
<jsp:include page="../elements/footer.jsp" />
</body>
</html>