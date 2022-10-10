<%@ page import="ru.seims.database.entitiy.DataTable" %>
<%@ page import="ru.seims.localization.LocalizationManager" %>
<%@ page import="ru.seims.application.servlet.jsp.DatabaseServlet" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%DataTable table = (DataTable) pageContext.getRequest().getAttribute("table");%>
<html>
<head>
    <title>Data view</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css" />
    <script src="${pageContext.request.contextPath}/js/jquery-1.11.0.min.js" type="text/javascript"></script>
</head>
<body>
<jsp:include page="../elements/header.jsp"/>
<jsp:include page="../elements/popup.jsp"/>
<div class="content_holder">
    <jsp:include page="../elements/dataViewTab.jsp"/>
    <form id="form" action="${pageContext.request.contextPath}<%=DatabaseServlet.getTableAPI%>/" method="GET">
        <div class="select_table">
            <h3 id="select_h3"><%=LocalizationManager.getString("dataView.select_a_table")%></h3>
            <jsp:include page="../elements/tableSelect.jsp"/>
            <button id="table_load" type="button" onclick="loadTable()"><%=LocalizationManager.getString("dataView.load_table")%></button>
        </div>
        <div>
            <jsp:include page="../elements/interactiveTable.jsp"/>
        </div>
    </form>
    <div id="dialog" title="Add new data" style="display:none;">
        <form id="insert_form" action="<%=DatabaseServlet.insertJsonAPI%><%=table.getName()%>" method="post">
            <input type="hidden" name="new_data" id="new_data">
            <fieldset id="field_set">
                <%for(String column : table.getColumnLabels()) {%>
                <label for="in_<%=column%>"><%=column%></label>
                <br/>
                <input type="text" onchange="setDialogValue('<%=column%>',this.value)" name="<%=column%>" id="in_<%=column%>" class="text">
                <%}%>
                <button type="button" onclick="submitNewData()">Submit</button>
            </fieldset>
        </form>
    </div>
    <button id="table_add" onclick='addData()'><%=LocalizationManager.getString("dataView.add_row")%></button>
</div>
<jsp:include page="../elements/footer.jsp" />
</body>
<script type="text/javascript">
    function loadTable() {
        const form = document.getElementById("form");
        form.setAttribute("method", "GET");
        form.action = form.action.concat(document.getElementById("selected_table").value);
        form.submit();
    }

    function addData() {
        var data = [];
        <%for(String column : table.getColumnLabels()) {%>
        data.push({"column":"<%=column%>", "newValue":"null"})
        <%}%>
        sessionStorage.setItem('new_row_data', JSON.stringify(data));
        $("#dialog").dialog();
    }

    function setDialogValue(column, value) {
        var data = JSON.parse(sessionStorage.getItem("new_row_data"));
        for(var i = 0; i < data.length; i++) {
            if(data[i].column === column) {
                data[i].newValue = value;
                sessionStorage.setItem('new_row_data', JSON.stringify(data));
                return;
            }
        }
        data.push({"column":column.toString(), "newValue":value.toString()});
        sessionStorage.setItem('new_row_data', JSON.stringify(data));
    }

    function submitNewData() {
        document.getElementById("new_data").value = sessionStorage.getItem("new_row_data").toString();
        document.getElementById("insert_form").submit();
    }
</script>
</html>
