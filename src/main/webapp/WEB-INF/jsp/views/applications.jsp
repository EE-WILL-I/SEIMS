<%@ page import="ru.seims.database.entitiy.StoredImage" %>
<%@ page import="org.json.simple.JSONObject" %>
<%@ page import="org.json.simple.parser.JSONParser" %>
<%@ page import="org.json.simple.JSONArray" %>
<%@ page import="ru.seims.utils.logging.Logger" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="ru.seims.application.servlet.jsp.OrganizationServlet" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    String orgId = (String) request.getAttribute("org_id");
    String dataString = (String) request.getAttribute("org_data");
    if(dataString == null || dataString.isEmpty()) dataString = "{}";
    JSONObject webData;
    JSONArray fullData;
    JSONArray appsData;
    try {
        fullData = (JSONArray) new JSONParser().parse(dataString);
        webData = (JSONObject) fullData.get(0);
        appsData = (JSONArray) fullData.get(1);
    } catch (Exception e) {
        Logger.log(this, e.getMessage(), 2);
        webData = new JSONObject();
        appsData = new JSONArray();
    }
    String name = (String) webData.get("name");
    if(name == null) name = "*ошибка базы данных*";
    boolean hasApps = appsData.size() > 0;
    ArrayList<StoredImage> apps = new ArrayList<>(appsData.size());
    appsData.forEach((value) ->
    {
        try {
            String id = String.valueOf(((JSONObject)value).get("id"));
            apps.add(new StoredImage(id));
        } catch (Exception e) {
            Logger.log(this, e.getMessage(), 3);
            apps.add(StoredImage.loadDefaultImage());
        }
    });
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
                            <p style="font-size: 20px; text-align: center; padding: 0px">Приложения</p>
                            <div id="org_app_wrapper">
                                <div id="org_app_content" <%if(!hasApps) {%>style="display: block" <%}%>>
                                    <%if(!hasApps) {%>
                                    <p style="text-align: center">Приложений нет.</p>
                                    <%} else { for(StoredImage app : apps) {%>
                                    <div class="org_img_wrapper">
                                        <img class="org_img" src="data:image/jpg;base64,<%=app.getBase64Data()%>"/>
                                    </div>
                                    <%}}%>
                                </div>
                            </div>
                            <div style="display: inline-grid;text-align: center;">
                                <a href="${pageContext.request.contextPath}<%=OrganizationServlet.uploadExcel.replace("{id}", orgId)%>" class="apps_btn" type="button">Загрузить документ в базу данных</a>
                                <a href="${pageContext.request.contextPath}<%=OrganizationServlet.generateExcel.replace("{id}", orgId)%>?type=2" class="apps_btn" type="button">Сформировать документ OO-1</a>
                                <a href="${pageContext.request.contextPath}<%=OrganizationServlet.generateExcel.replace("{id}", orgId)%>?type=3" class="apps_btn" type="button">Сформировать документ OO-2</a>
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
