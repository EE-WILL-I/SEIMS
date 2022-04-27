<%@ page import="ru.seims.database.entitiy.Organization" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html>
<head>
    <title>Organization View</title>
    <link rel="stylesheet" href="<%=pageContext.getRequest().getServletContext().getContextPath()%>/css/styles.css" />
</head>
<script src="${pageContext.request.contextPath}/JS/jquery-1.11.0.min.js" type="text/javascript"></script>
<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
<body>
    <div class="chart-div">
        <canvas id="myChart" class="chart"></canvas>
    </div>
    <br/>
    <div class="chart-div">
        <canvas id="myChart1" class="chart"></canvas>
    </div>
    <a id="data">
        no data? :^(
    </a>
</body>
</html>
<script src="${pageContext.request.contextPath}/JS/charts/doughnutChart.js"  type="text/javascript"></script>
<script type="text/javascript">
    const orgData = <%=(String) request.getAttribute("org_data")%>;
    document.getElementById("data").innerText = JSON.stringify(orgData, null, "\t");
    setChart('Всего работников', 'myChart', orgData);
    setChart('Работники со средним профессиональным педогогическим образованием по программам подготовки специалистов среднего звена', 'myChart1', orgData);
</script>
