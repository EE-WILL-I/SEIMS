<%@ page import="ru.seims.database.entitiy.StoredImage" %>
<%@ page import="org.json.simple.JSONObject" %>
<%@ page import="org.json.simple.parser.JSONParser" %>
<%@ page import="org.json.simple.JSONArray" %>
<%@ page import="ru.seims.utils.logging.Logger" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="ru.seims.application.servlet.jsp.OrganizationServlet" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="jdk.nashorn.internal.scripts.JO" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    String orgId = (String) request.getAttribute("org_id");
    JSONObject webData;
    try {
        webData = (JSONObject) request.getAttribute("org_data");
    } catch (Exception e) {
        Logger.log(this, e.getMessage(), 2);
        webData = new JSONObject();
    }
    String name = (String) webData.get("name");
    if(name == null) name = "*ошибка базы данных*";
%>
<html>
<head>
    <title>Приложения и документы</title>
    <link rel="stylesheet" href="<%=pageContext.getRequest().getServletContext().getContextPath()%>/css/styles.css" />
</head>
<script src="${pageContext.request.contextPath}/js/jquery-1.11.0.min.js" type="text/javascript"></script>
<body id="header-body">
<jsp:include page="../elements/header.jsp"/>
<jsp:include page="../elements/popup.jsp"/>
<table class="table_body" style="width: 100%; background: #d5e1df">
    <tr>
        <td></td>
        <td id="td-content" style="width:80%; background: #fff;">
            <table style="width:100%; height: 100%;">
                <tr style="vertical-align:top;">
                    <td style="width:900px;padding:0px 30px;">
                        <div class="content_holder">
                            <div style="display: flex;">
                                <p class="breadcrumbs" id='navmw' style=''><a href='/' style='background:url(/img/ti_home_dark.svg) left top 10px no-repeat; padding-left:15px;'>Главная</a> / <a href="/" style="background:url(/img/ti_map.png) left top 10px no-repeat;">Мониторинг</a> / <span style='color:#333;'>Организация</span></p>
                                <p class="breadcrumbs" style="text-align: right;"><a href="${pageContext.request.contextPath}<%=OrganizationServlet.getOrg.replace("{id}", orgId)%>">Просмотр</a> / <a href="${pageContext.request.contextPath}<%=OrganizationServlet.getOrg.replace("{id}", orgId)%>">Редактировать</a> / <span style="color:#333;">Приложения и файлы</span></p>
                            </div>
                            <div id="org_header">
                                <div style="padding-left: 20px;">
                                    <p id = "org_name"><%=name%></p>
                                </div>
                            </div>
                            <br/>
                            <jsp:include page="../elements/applicationsPanel.jsp"/>
                            <div style="display: flex; justify-content: center; text-align: center">
                                <a href="${pageContext.request.contextPath}<%=OrganizationServlet.uploadExcel.replace("{id}", orgId)%>" class="apps_btn" type="button">Загрузить документ в базу данных</a>
                                <a href="${pageContext.request.contextPath}<%=OrganizationServlet.generateExcel.replace("{id}", orgId)%>?type=2" class="apps_btn" type="button">Сформировать документ OO-1</a>
                                <a href="${pageContext.request.contextPath}<%=OrganizationServlet.generateExcel.replace("{id}", orgId)%>?type=3" class="apps_btn" type="button">Сформировать документ OO-2</a>
                                <a href="${pageContext.request.contextPath}<%=OrganizationServlet.uploadImage.replace("{id}", orgId)%>" class="apps_btn" type="button">Установить фотографию организации</a>
                            </div>
                        </div>
                    </td>
                </tr>
            </table>
        </td>
        <td></td>
    </tr>
</table>
<jsp:include page="../elements/footer.jsp" />
</body>
</html>
