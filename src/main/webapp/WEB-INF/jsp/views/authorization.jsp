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
<body style="background: #d5e1df">
<div id="login_div">
    <jsp:include page="../elements/popup.jsp"/>
    <%if(pageContext.getSession().getAttribute("user") == null || !errorStr.equals("")) {%>
        <form id="login_form" class="form" action="${pageContext.request.contextPath}/login" method="post" style="<%=style_form%>">
            <div id="auth_header">
                <a class="header-nav" href="http://www.vspu.ac.ru/">ВГПУ – Воронежский Государственный Педагогический Университет</a>
                <p></p>Система сбора и обработки статистических данных<br>об общеобразовательных организация<br> по Воронежской области
                <hr>
                <div id="auth_div">
                    <h2 class="header_h2">Авторизация</h2>
                    <ul>
                        <p><%=LocalizationManager.getString("auth.login")%></p>
                        <input id="username" class="form_field" name="username" autofocus="autofocus" />
                    </ul>
                    <ul>
                        <p><%=LocalizationManager.getString("auth.pass")%></p>
                        <input id="password" class="form_field" name="password" type="password"/>
                    </ul>
                    <ul style="font-family: Calibri, serif; margin-top: 25px">
                        <input type="checkbox" name="remember-me"/> <%=LocalizationManager.getString("auth.remember")%>
                    </ul>
                    <ul style="display: grid; justify-content: space-around; padding: 0">
                        <input id="login_btn" type="submit" value="<%=LocalizationManager.getString("auth.log_in")%>" />
                    </ul>
                    <%switch (errorStr) { case "failed" :%>
                    <ul id="auth_failed">Неверный логин или пароль</ul>
                    <% break; case "noaccess" :%>
                    <ul id="auth_failed">Доступ запрещен</ul>
                    <% break; case "blocked" :%>
                    <ul id="auth_failed">Слишком много попыток входа. Попробуйте позже.</ul>
                    <%}%>
                </div>
            </div>
        </form>
    <%} else {%>
        <div style="display: flex; justify-content: center; width:100%; ">
            <div style="background: #E0E0E0; justify-content: center; width: 300px;">
                <h2 class="header_h2"><%=LocalizationManager.getString("auth.failed")%></h2>
                <ul>
                    <a name="logout" href="${pageContext.request.contextPath}/logout"><%=LocalizationManager.getString("auth.logout")%></a>
                </ul>
            </div>
        </div>
    <%}%>
</div>
</body>
</html>
