<%@ page import="ru.seims.database.entitiy.Organization" %>
<%@ page import="ru.seims.database.entitiy.StoredImage" %>
<%@ page import="org.json.simple.JSONObject" %>
<%@ page import="org.json.simple.parser.JSONParser" %>
<%@ page import="org.json.simple.JSONArray" %>
<%@ page import="ru.seims.utils.logging.Logger" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="ru.seims.database.entitiy.DataTable" %>
<%@ page import="com.fasterxml.jackson.databind.ObjectMapper" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    String region = (String) request.getAttribute("region");
    ArrayList<DataTable> tables = (ArrayList<DataTable>) pageContext.getRequest().getAttribute("tables");
%>
<html>
<head>
    <title><%=region%></title>
    <link rel="stylesheet" href="<%=pageContext.getRequest().getServletContext().getContextPath()%>/css/styles.css" />
</head>
<script src="${pageContext.request.contextPath}/JS/jquery-1.11.0.min.js" type="text/javascript"></script>
<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
<body id="header-body">
<jsp:include page="../elements/header.jsp"/>
<jsp:include page="../elements/popup.jsp"/>
<table style="width: 100%; background: #d5e1df">
    <tr>
        <td></td>
        <td id="td-content" style="width:1200px !important; background: #fff;">
            <table style="width:100%; min-height: 75%">
                <tr style="vertical-align:top;">
                    <td style="width:900px;padding:0px 30px;">
                        <div class="content_holder">
                            <p id='navmw' style='padding-bottom:14px;font: normal 700 12px Calibri; color:#999;'><a href='/' style='background:url(/img/ti_home_dark.svg) left top 10px no-repeat; padding-left:15px;'>Главная</a> / <a href="${pageContext.request.contextPath}/monitoring" style="background:url(/img/ti_map.png) left top 10px no-repeat;
                            padding-left: 15px;">Мониторинг</a> / <span style='color:#333;'><%=region%> район</span>
                            </p>
                            <div id="org_header">
                                <div style="padding-left: 20px;">
                                    <p id = "org_name"><%=region%> район</p>
                            </div>
                            <hr/>
                            <script src="${pageContext.request.contextPath}/JS/jquery-1.11.0.min.js" type="text/javascript"></script>
                            <script src="${pageContext.request.contextPath}/JS/vrtable/VRTableScripts.js"></script>
                            <p style="font-size: 20px; text-align: center; padding: 0px">Статистика</p>
                        </div>
                            <%if(tables != null) {%>
                            <form id="form" method="post">
                                <input type="hidden" name="updated_values" id="updated_values">
                                <% for(DataTable table : tables) {
                                pageContext.getRequest().setAttribute("table", table);%>
                            <jsp:include page="../elements/staticVRTable.jsp"/>
                            <hr/>
                            <%}%>
                            </form>
                            <%}%>
                            <hr/>
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
<script src="${pageContext.request.contextPath}/JS/charts/doughnutChart.js"  type="text/javascript"></script>
