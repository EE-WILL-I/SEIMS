<%@ page import="ru.seims.localization.LocalizationManager" %>
<%@ page import="ru.seims.database.entitiy.DataTable" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="org.json.simple.JSONArray" %>
<%@ page import="org.json.simple.JSONObject" %>
<%@ page import="ru.seims.application.servlet.jsp.DatabaseServlet" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    JSONArray dataTables = (JSONArray) pageContext.getRequest().getAttribute("excel_tables");
    String orgId = (String) pageContext.getRequest().getAttribute("org_id");
    String docType = (String) pageContext.getRequest().getAttribute("doc_type");
%>
<html>
<head>
    <title>Data preview</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css" />
</head>
<script src="${pageContext.request.contextPath}/js/jquery-1.11.0.min.js" type="text/javascript">
    //console.log(sessionStorage.getItem('excelTables'));
</script>
<body onload="init()">

<div class="content_holder" style="width: 90%; align-content: stretch">
    <form id="form" action="${pageContext.request.contextPath}<%=DatabaseServlet.insertExcel.replace("{id}", orgId)%>?type=<%=docType%>" method="post">
        <input type="hidden" name="tables_data" id="tables_data">
        <div style="display: flex; width: 100%; background: #367554;">
            <p class="vr_type_p">Страница:</p>
            <%for(int i = 1; i <= dataTables.size(); i++) {%>
            <button id="a_page_<%=i%>_header" class="vr_type_btn" type="button" onclick="initTable('<%=i-1%>')"><%=i%></button>
            <%}%>
        </div>
        <hr/>
        <div>
            <jsp:include page="../elements/staticTable.jsp"/>
            <button id="submitBtn" class="submit_btn" type="button" onclick="submitNewData()">
                <%=LocalizationManager.getString("intTable.submit")%>
            </button>
        </div>
    </form>
</div>

</body>
<script>
    document.getElementById("tables_data").value = '<%=dataTables.toJSONString()%>';
    function submitNewData() {
        var save = window.confirm("<%=LocalizationManager.getString("previewExcel.submit")%>");
        if (save) {
            const form = document.getElementById("form");
            form.submit();
            return;
        }
        document.getElementById("selected_table").value = "none";
    }

    function init() {
        var tablesData = {};
        <%for(int j = 0; j < dataTables.size(); j++) {
        String tableJson = ((JSONObject)dataTables.get(j)).toJSONString();
        tableJson = tableJson.replace("\\n", " ");%>
        tablesData['tab_<%=j%>'] = JSON.parse('<%=tableJson%>');
        //console.log(tablesData['tab_<%=j%>']);
        <%}%>
        sessionStorage.setItem('excelTables', JSON.stringify(tablesData));
        initTable('0');
    }
</script>
</html>
