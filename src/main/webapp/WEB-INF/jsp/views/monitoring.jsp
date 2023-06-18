<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="ru.seims.utils.logging.Logger" %>
<%@ page import="org.json.simple.JSONObject" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="ru.seims.database.entitiy.DataTable" %>
<%
    ArrayList<DataTable> tables;
    DataTable statTable;
        try {
            tables = (ArrayList<DataTable>) pageContext.getRequest().getAttribute("tables");
            statTable = tables.get(0);
        } catch (Exception e) {
            Logger.log(this, e.getMessage(), 2);
            tables = new ArrayList<>(0);
            statTable = null;
        }
%>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Система сбора и обработки статистических данных об общеобразовательных организация по Воронежской области</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css" />
</head>
<body id="header-body">
<jsp:include page="../elements/header.jsp"/>
<jsp:include page="../elements/popup.jsp"/>
<table class="table_body" style="width: 100%; min-height: 75%; background: #d5e1df">
    <tr>
        <td></td>
        <td id="td-content" style="width:80%; background: #fff;">
            <table style="width:100%;height: 100%;">
                <tr style="vertical-align:top;">
                    <td style="width:900px;padding:0px 30px;">
                        <p id='navmw' style='padding-bottom:14px;font: normal 700 12px Calibri; color:#999;'><a href='/' style='background:url(${pageContext.request.contextPath}/img/ti_home_dark.svg)
                         left top 10px no-repeat;padding-left:15px;'>Главная</a> / <span style='color:#333;'>Мониторинг</span> / <a href="/filter">Фильтр параметров</a>
                        </p>
                        <table style='width:100%;margin-bottom:15px;'>
                            <tr>
                                <td>
                                    <jsp:include page="../elements/interactiveMap.jsp"/>
                                </td>
                            </tr>
                        </table>
                        <hr/>
                        <p style="font-size: 20px; text-align: center; padding: 0px">Статистика</p>
                        <div id="stat-div">
                            <div style="width:50%">
                                <p>Всего организаций в области:<strong> <%=statTable.getRow(0).get(statTable.getColumn(1))%></strong></p>
                                <p>Всего учащихся в области:<strong> <%=statTable.getRow(1).get(statTable.getColumn(1))%></strong></p>
                                <p>Всего учебных классов:<strong> <%=statTable.getRow(2).get(statTable.getColumn(1))%></strong></p>
                                <p>Всего учебных классов для учащихся
                                с ограниченными возможностями здоровья:<strong> <%=statTable.getRow(3).get(statTable.getColumn(1))%></strong></p>
                                <p>Число обучающихся с применением электронного обучения
                                и дистанционных образовательных технологий:<strong> <%=statTable.getRow(4).get(statTable.getColumn(1))%></strong></p>
                                <p>Общее число выпускников (9 и 11 классов):<strong> <%=statTable.getRow(5).get(statTable.getColumn(1))%></strong></p>
                            </div>
                            <div class="chart-div">
                                <canvas id="myChart1" class="chart"></canvas>
                            </div>
                            <div class="chart-div" style="width: 99%; height: 700px;">
                                <canvas id="myChart" class="chart"></canvas>
                            </div>
                            <div class="chart-div">
                                <canvas id="myChart2" class="chart"></canvas>
                            </div>
                            <div class="chart-div">
                                <canvas id="myChart3" class="chart"></canvas>
                            </div>
                            <div class="chart-div">
                                <canvas id="myChart4" class="chart"></canvas>
                            </div>
                            <div class="chart-div">
                                <canvas id="myChart5" class="chart"></canvas>
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
<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
<script src="${pageContext.request.contextPath}/js/charts/doughnutChart.js"  type="text/javascript"></script>
<script type="text/javascript">
    const statData = <%=statTable.toJSON()%>;
    const ODDData = <%=tables.get(1).toJSON()%>;
    const SDData = <%=tables.get(2).toJSON()%>;
    const OTRData = <%=tables.get(3).toJSON()%>;
    const CRData = <%=tables.get(4).toJSON()%>;
    const CRFDData = <%=tables.get(5).toJSON()%>;
    const GSRData = <%=tables.get(6).toJSON()%>;
    const tableNum = <%=tables.size()%>;
    if(tableNum > 0) {
        setBarChart('1', 'Распределение организаций по районам', '0', 'myChart', ODDData);
        setPolarChart('1', 'Распределение учащихся по районам', '0', 'myChart1', SDData);
        setRoundChart('1', 'Соотношение типов организаций', '0', 'myChart2', OTRData);
        setRoundChart('1', 'Соотношение типов учебных классов', '0', 'myChart3', CRData);
        setRoundChart('1', 'Соотношение типов учебных классов для лиц с ОВЗ', '0', 'myChart4', CRFDData);
        setRoundChart('1', 'Соотношение выпускников 9 и 11 классов', '0', 'myChart5', GSRData);
    }
</script>
