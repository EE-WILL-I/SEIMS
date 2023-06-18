<%@ page import="ru.seims.utils.logging.Logger" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="ru.seims.database.entitiy.DataTable" %>
<%@ page import="ru.seims.application.servlet.jsp.OrganizationServlet" %>
<%@ page import="ru.seims.application.servlet.jsp.DatabaseServlet" %>
<%@ page import="org.json.simple.JSONObject" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    String orgId = (String) request.getAttribute("org_id");
    String vrType = (String) request.getAttribute("vr_type");
    String pageNum = String.valueOf(request.getAttribute("page"));
    Integer pageCount = (Integer) request.getAttribute("max_page");
    String name = (String) ((JSONObject) request.getAttribute("org_data")).get("name");
    ArrayList<DataTable> tables;
    try {
        tables = (ArrayList<DataTable>) pageContext.getRequest().getAttribute("tables");
    } catch (Exception e) {
        Logger.log(this, e.getMessage(), 2);
        tables = new ArrayList<>(0);
    }
    if(name == null) name = "*ошибка базы данных*";
    String orgURL = OrganizationServlet.editOrg.replace("{id}", orgId);
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
<table class="table_body" style="width: 100%; background: #d5e1df">
    <tr>
        <td></td>
        <td id="td-content" style="width:80%; background: #fff;">
            <table style="width:100%; min-height: 75%">
                <tr style="vertical-align:top;">
                    <td style="width:900px;padding:0px 30px;">
                        <div class="content_holder">
                            <div style="display: flex;">
                                <p class="breadcrumbs" id='navmw' style=''><a href='/' style='background:url(/img/ti_home_dark.svg) left top 10px no-repeat; padding-left:15px;'>Главная</a> / <a href="/">Мониторинг</a> / <span style='color:#333;'>Организация</span></p>
                                <p class="breadcrumbs" style="text-align: right;">
                                    <a href="${pageContext.request.contextPath}<%=OrganizationServlet.getOrg.replace("{id}", orgId)%>?doc=<%=vrType%>&page=<%=pageNum%>">Просмотр</a> /
                                    <span style='color:#333;'>Редактировать</span> /
                                    <a href="${pageContext.request.contextPath}<%=OrganizationServlet.apps.replace("{id}", orgId)%>">Приложения и файлы</a>
                                </p>
                            </div>
                            <hr/>
                            <div style="display: flex;width: 100%;">
                                <p class="vr_type_p" id="control_tab_lbl">Управление:</p>
                                <form id="form_reset" class="control_tab_form" method="post">
                                    <button class="control_tab_btn" type="button" onclick="reset_func()">Сбросить данные</button>
                                </form>
                                <form id="form_rand" class="control_tab_form" method="post">
                                    <button class="control_tab_btn" type="button" onclick="rand_func()">Заполнить случайными</button>
                                </form>
                            </div>
                            <hr/>
                            <jsp:include page="../elements/orgPageUpperPart.jsp"/>
                            <div style="display: flex; width: 100%; background: #25533b;">
                                <p class="vr_type_p">Страница:</p>
                                <%for(int i = 1; i <= pageCount; i++) {%>
                                <a id="a_page_<%=i%>_header" href="${pageContext.request.contextPath}<%=orgURL%>?doc=<%=vrType%>&page=<%=i%>" class="vr_type_btn"><%=i%></a>
                                <%}%>
                            </div>
                            <hr/>
                            <%if(tables != null) {%>
                            <form id="form" method="post">
                                <input type="hidden" name="updated_values" id="updated_values">
                                <%for(int i = 0; i < tables.size(); i++) {
                                    int add = 2;
                                    if(i + 2 < tables.size() && tables.get(i + 1).isChild() && tables.get(i + 2).isChild()) {%>
                                        <div>
                                            <div style="text-align: center;">
                                                <button class="tab_button" type="button" onclick="showTab('panel_<%=tables.get(i).getSysName()%>', '<%=tables.get(i).getSysName()%>')">по классам очного обучения</button>
                                                <button class="tab_button" type="button" onclick="showTab('panel_<%=tables.get(i).getSysName()%>', '<%=tables.get(i+1).getSysName()%>')">по классам очно-заочного обучения</button>
                                                <button class="tab_button" type="button" onclick="showTab('panel_<%=tables.get(i).getSysName()%>', '<%=tables.get(i+2).getSysName()%>')">по классам заочного обучения</button>
                                                <%if(i + 3 < tables.size() && tables.get(i + 3).isChild()) { add = 3; %>
                                                <button class="tab_button" type="button" onclick="showTab('panel_<%=tables.get(i).getSysName()%>', '<%=tables.get(i+3).getSysName()%>')">по аттестации экстернов</button>
                                                <%}%>
                                            </div>
                                            <div id="panel_<%=tables.get(i).getSysName()%>">
                                                <%for(int j = i; j <= i + add; j++) {
                                                    pageContext.getRequest().setAttribute("table", tables.get(j));
                                                    pageContext.getRequest().setAttribute("hide_table", j==i ? "false" : "true");%>
                                                <jsp:include page="../elements/interactiveVRTable.jsp"/>
                                                <%} i+=add; %>
                                            </div>
                                        </div>
                                    <hr/>
                                    <%} else {
                                        pageContext.getRequest().setAttribute("table", tables.get(i));
                                        pageContext.getRequest().setAttribute("hide_table", "false");%>
                                    <jsp:include page="../elements/interactiveVRTable.jsp"/>
                                    <hr/>
                                    <%}%>
                                <%}%>
                            </form>
                            <%}%>
                            <div style="display: flex; width: 100%; background: #25533b;">
                                <p class="vr_type_p">Страница:</p>
                                <%for(int i = 1; i <= pageCount; i++) {%>
                                <a id="a_page_<%=i%>_footer" href="${pageContext.request.contextPath}<%=orgURL%>?doc=<%=vrType%>&page=<%=i%>" class="vr_type_btn"><%=i%></a>
                                <%}%>
                            </div>
                            <hr/>
                            <jsp:include page="../elements/orgPageLowerPart.jsp"/>
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
    function showTab(panel, table) {
        $('#'+panel).children().hide();
        $('#'+table).show();
    }

    function reset_func() {
        if(confirm('Вы уверены, что хотите сбросить все данные?')) {
            const form = document.getElementById('form_reset');
            form.action = '${pageContext.request.contextPath}<%=OrganizationServlet.resetOrg.replace("{id}", orgId)%>';
            form.submit();
        }
    }

    function rand_func() {
        if(confirm('Вы уверены?')) {
            const form = document.getElementById('form_rand');
            form.action = '${pageContext.request.contextPath}<%=OrganizationServlet.randomizeOrg.replace("{id}", orgId)%>';
            form.submit();
        }
    }
    var updateOrgURL = "<%=OrganizationServlet.updateOrg.replace("{id}", orgId)%>";
    const vrtype = <%=vrType%>;
    const currpage = <%=pageNum%>;
    focusBtn("a_type_" + vrtype);
    focusBtn("a_page_" + currpage + "_header");
    focusBtn("a_page_" + currpage + "_footer");
    /*
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
