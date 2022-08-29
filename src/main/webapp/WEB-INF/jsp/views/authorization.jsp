<%@ page import="ru.seims.application.security.service.AuthorizationService" %>
<%@ page import="ru.seims.localization.LocalizationManager" %>
<%@ page import="ru.seims.utils.properties.PropertyReader" %>
<%@ page import="ru.seims.utils.properties.PropertyType" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--style init--%>
<% String style_form = PropertyReader.getPropertyValue(PropertyType.STYLE, "authorization.form"); %>
<% String errorStr = (String) pageContext.getRequest().getAttribute("error"); if(errorStr == null) errorStr = "";%>
<html>
<head>
    <title>Login page</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css" />
</head>
<body>
<div id="login_div">
    <jsp:include page="../elements/popup.jsp"/>
    <%if(pageContext.getSession().getAttribute("user") == null || !errorStr.equals("")) {%>
        <form id="login_form" action="${pageContext.request.contextPath}/login" method="post" style="<%=style_form%>">
            <div style="background: #E0E0E0; width: 300px;">
                <h2 id="header_h2">Authorization</h2>
                <ul>
                    <p><%=LocalizationManager.getString("auth.login")%></p>
                    <input id="username" name="username" autofocus="autofocus" />
                </ul>
                <ul>
                    <p><%=LocalizationManager.getString("auth.pass")%></p>
                    <input id="password" name="password" type="password"/>
                </ul>
                <ul>
                    <input type="checkbox" name="remember-me"/> <%=LocalizationManager.getString("auth.remember")%>
                </ul>
                <ul>
                    <input type="submit" value="<%=LocalizationManager.getString("auth.log_in")%>" />
                </ul>
                <%switch (errorStr) { case "failed" :%>
                <ul style="color: red;>">Authentication failed</ul>
                <% break; case "noaccess" :%>
                <ul style="color: red;>">Access denied</ul>
                <% break; case "blocked" :%>
                <ul style="color: red;>">Too many login attempts. Try later.</ul>
                <%}%>
            </div>
        </form>
    <%} else {%>
        <div style="display: flex; justify-content: center; width:100%; ">
            <div style="background: #E0E0E0; justify-content: center; width: 300px;">
                <h2 id="header_h2"><%=LocalizationManager.getString("auth.failed")%></h2>
                <ul>
                    <a name="logout" href="${pageContext.request.contextPath}/logout"><%=LocalizationManager.getString("auth.logout")%></a>
                </ul>
            </div>
        </div>
    <%}%>
</div>
</body>
</html>
