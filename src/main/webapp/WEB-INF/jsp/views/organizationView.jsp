<%@ page import="ru.seims.database.entitiy.Organization" %>
<%@ page import="ru.seims.database.entitiy.StoredImage" %>
<%@ page import="org.json.simple.JSONObject" %>
<%@ page import="org.json.simple.parser.JSONParser" %>
<%@ page import="org.json.simple.JSONArray" %>
<%@ page import="ru.seims.utils.logging.Logger" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="ru.seims.database.entitiy.DataTable" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    String orgId = (String) request.getAttribute("org_id");
    String vrType = (String) request.getAttribute("vr_type");
    Integer pageNum = (Integer) request.getAttribute("page");
    Integer pageCount = (Integer) request.getAttribute("max_page");
    String dataString = (String) request.getAttribute("org_data");
    if(dataString == null || dataString.isEmpty()) dataString = "{}";
    String imgId;
    JSONArray fullData;
    JSONObject webData;
    JSONArray appsData;
    try {
        fullData = (JSONArray) new JSONParser().parse(dataString);
        webData = (JSONObject) fullData.get(0);
        appsData = (JSONArray) fullData.get(1);
        imgId = String.valueOf(webData.get("id_img"));
    } catch (Exception e) {
        Logger.log(this, e.getMessage(), 2);
        imgId = "0";
        webData = new JSONObject();
        appsData = new JSONArray();
    }
    StoredImage img;
    try {
        img = new StoredImage(imgId);
    } catch (Exception e) {
        Logger.log(this, e.getMessage(), 3);
        img = new StoredImage();
    }
    String name = (String) webData.get("name");
    String description = (String) webData.get("description");
    if(name == null) name = "*ошибка базы данных*";
    if(description == null || description.isEmpty()) description = "Описание осутствует.";
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
    ArrayList<DataTable> tables = (ArrayList<DataTable>) pageContext.getRequest().getAttribute("tables");
%>
<html>
<head>
    <title><%=name%></title>
    <link rel="stylesheet" href="<%=pageContext.getRequest().getServletContext().getContextPath()%>/css/styles.css" />
</head>
<script src="${pageContext.request.contextPath}/js/jquery-1.11.0.min.js" type="text/javascript"></script>
<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
<body id="header-body">
<jsp:include page="../elements/header.jsp"/>
<jsp:include page="../elements/popup.jsp"/>
<table style="width: 100%; background: #d5e1df">
    <tr>
        <td></td>
        <td id="td-content" style="width:80%; background: #fff;">
            <table style="width:100%; min-height: 75%">
                <tr style="vertical-align:top;">
                    <td style="width:900px;padding:0px 30px;">
                        <div class="content_holder">
                            <div style="display: flex;">
                                <p class="breadcrumbs" id='navmw' style=''><a href='/' style='background:url(/img/ti_home_dark.svg) left top 10px no-repeat; padding-left:15px;'>Главная</a> / <a href="/" style="background:url(/img/ti_map.png) left top 10px no-repeat;">Мониторинг</a> / <span style='color:#333;'>Организация</span></p>
                                <p class="breadcrumbs" style="text-align: right;"><span style='color:#333;'>Просмотр</span> / <a href="${pageContext.request.contextPath}/org/<%=orgId%>">Редактировать</a> / <a href="${pageContext.request.contextPath}/org/<%=orgId%>/apps">Приложения и файлы</a></p>
                            </div>
                            <div id="org_header">
                                <div id="org_img">
                                    <img style="width: 450px;" src="data:image/jpg;base64,<%=img.getBase64Data()%>"/>
                                </div>
                                <div style="padding-left: 20px;">
                                    <p id = "org_name"><%=name%></p>
                                    <br/>
                                    <hr/>
                                    <p id = "org_desc"><%=description%></p>
                                </div>
                            </div>
                            <br/>
                            <hr/>
                            <p><strong>Район: </strong><%=webData.get("district")%></p>
                            <hr/>
                            <p><strong>Web-сайт: </strong><%=webData.get("web_site")%></p>
                            <p><strong>Контактные данные: </strong><%=webData.get("contact_data")%></p>
                            <hr/>
                            <script src="${pageContext.request.contextPath}/js/jquery-1.11.0.min.js" type="text/javascript"></script>
                            <script src="${pageContext.request.contextPath}/js/vrtable/VRTableScripts.js"></script>
                            <div style="display: flex; width: 100%; background: #367554;">
                                <p class="vr_type_p">Отображаемый документ:</p>
                                <a id="a_type_0" href="${pageContext.request.contextPath}/org/<%=orgId%>?doc=0" class="vr_type_btn">Все</a>
                                <a id="a_type_1" href="${pageContext.request.contextPath}/org/<%=orgId%>?doc=1" class="vr_type_btn">85-K</a>
                                <a id="a_type_2" href="${pageContext.request.contextPath}/org/<%=orgId%>?doc=2" class="vr_type_btn">oo1</a>
                                <a id="a_type_3" href="${pageContext.request.contextPath}/org/<%=orgId%>?doc=3" class="vr_type_btn">oo2</a>
                            </div>
                            <hr/>
                            <div style="display: flex; width: 100%; background: #367554;">
                                <p class="vr_type_p">Страница:</p>
                                <%for(int i = 1; i <= pageCount; i++) {%>
                                <a id="a_page_<%=i%>_header" href="${pageContext.request.contextPath}/org/<%=orgId%>?doc=<%=vrType%>&page=<%=i%>" class="vr_type_btn"><%=i%></a>
                                <%}%>
                            </div>
                            <hr/>
                            <%if(tables != null) {%>
                            <form id="form" method="post">
                                <input type="hidden" name="updated_values" id="updated_values">
                                <% for(DataTable table : tables) {
                                pageContext.getRequest().setAttribute("table", table);%>
                            <jsp:include page="../elements/interactiveVRTable.jsp"/>
                            <hr/>
                            <%}%>
                            </form>
                            <%}%>
                            <div style="display: flex; width: 100%; background: #367554;">
                                <p class="vr_type_p">Страница:</p>
                                <%for(int i = 1; i <= pageCount; i++) {%>
                                <a id="a_page_<%=i%>_footer" href="${pageContext.request.contextPath}/org/<%=orgId%>?doc=<%=vrType%>&page=<%=i%>" class="vr_type_btn"><%=i%></a>
                                <%}%>
                            </div>
                            <hr/>
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
                            <hr/>
                            <p style="font-size: 20px; text-align: center; padding: 0px">Статистика</p>
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
<script src="${pageContext.request.contextPath}/js/charts/doughnutChart.js"  type="text/javascript"></script>
<script type="text/javascript">
    const vrtype = <%=vrType%>;
    const currpage = <%=pageNum%>;
    focusBtn("a_type_" + vrtype);
    focusBtn("a_page_" + currpage + "_header");
    focusBtn("a_page_" + currpage + "_footer");
    /*const orgData = <%=webData.toJSONString()%>;
    if(JSON.stringify(orgData).length > 0) {
        document.getElementById("org_web_data").innerText = JSON.stringify(orgData, null, "\t") + '\n' + JSON.stringify(statData, null, "\t");
        document.getElementById("org_distr").innerText = JSON.stringify(orgData).get('district');
    }
    if(JSON.stringify(statData).length > 0) {
        setChart('Всего работников', 'myChart', statData);
        setChart('Работники со средним профессиональным педогогическим образованием по программам подготовки специалистов среднего звена',
            'myChart1', statData);
    }*/
</script>
