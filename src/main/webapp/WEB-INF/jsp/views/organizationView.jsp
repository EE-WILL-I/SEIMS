<%@ page import="ru.seims.database.entitiy.Organization" %>
<%@ page import="ru.seims.database.entitiy.StoredImage" %>
<%@ page import="org.json.simple.JSONObject" %>
<%@ page import="org.json.simple.parser.JSONParser" %>
<%@ page import="org.json.simple.JSONArray" %>
<%@ page import="ru.seims.utils.logging.Logger" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    String dataString = (String) request.getAttribute("org_data");
    if(dataString == null || dataString.isEmpty()) dataString = "{}";
    String imgId;
    JSONArray fullData;
    JSONObject webData;
    JSONArray VR7Data;
    try {
        fullData = (JSONArray) new JSONParser().parse(dataString);
        webData = (JSONObject) fullData.get(0);
        VR7Data = (JSONArray) fullData.get(1);
        imgId = String.valueOf(webData.get("id_img"));
    } catch (Exception e) {
        Logger.log(this, e.getMessage(), 2);
        imgId = "0";
        webData = new JSONObject();
        VR7Data = new JSONArray();
    }
    StoredImage img = new StoredImage(imgId, "Изображение осутствует.");
    String description = (String) webData.get("description");
    if(description.isEmpty()) description = "Описание осутствует.";
    boolean hasApps = false;
%>
<html>
<head>
    <title>Organization View</title>
    <link rel="stylesheet" href="<%=pageContext.getRequest().getServletContext().getContextPath()%>/css/styles.css" />
</head>
<script src="${pageContext.request.contextPath}/JS/jquery-1.11.0.min.js" type="text/javascript"></script>
<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
<body id="header-body">
<jsp:include page="../elements/header.jsp"/>
<jsp:include page="../elements/popup.jsp"/>
<table style="width: 100%;">
    <tr>
        <td></td>
        <td id="td-content" style="width:1200px !important; background: #fff;">
            <table style="width:100%; min-height: 75%">
                <tr style="vertical-align:top;">
                    <td style="width:900px;padding:0px 30px;">
                        <div class="content_holder">
                            <div id="org_header">
                                <div id="org_img">
                                    <img class="org_img" src="data:image/jpg;base64,<%=img.getBase64Data()%>"/>
                                </div>
                                <div style="padding-left: 20px;">
                                    <p id = "org_name"><%=webData.get("name")%></p>
                                    <br/>
                                    <hr/>
                                    <p id = "org_desc"><%=description%></p>
                                </div>
                            </div>
                            <br/>
                            <p id="org_web_data">
                                Информация осутствует.
                                no data? :^{
                            </p>
                            <div id="org_app_wrapper">
                                <div id="org_app_content" <%if(!hasApps) {%>style="display: block" <%}%>>
                                    <%if(!hasApps) {%><p style="text-align: center">Приложений нет.</p><%}%>
                                </div>
                            </div>
                            <div style="display: flex;">
                                <div class="chart-div">
                                    <canvas id="myChart" class="chart"></canvas>
                                </div>
                                <div class="chart-div">
                                    <canvas id="myChart1" class="chart"></canvas>
                                </div>
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
<script src="${pageContext.request.contextPath}/JS/charts/doughnutChart.js"  type="text/javascript"></script>
<script type="text/javascript">
    const orgData = <%=webData.toJSONString()%>;
    const statData = <%=VR7Data.toJSONString()%>;
    if(!orgData.isEmpty) {
        document.getElementById("org_web_data").innerText = JSON.stringify(orgData, null, "\t") + '\n' + JSON.stringify(statData, null, "\t");
    }
    if(!statData.isEmpty) {
        setChart('Всего работников', 'myChart', statData);
        setChart('Работники со средним профессиональным педогогическим образованием по программам подготовки специалистов среднего звена',
            'myChart1', statData);
    }
</script>
