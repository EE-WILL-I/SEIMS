<%@ page import="ru.seims.localization.LocalizationManager" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Data preview</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css" />
</head>
<body onload="init()">
<jsp:include page="../elements/header.jsp"/>
<jsp:include page="../elements/popup.jsp"/>
<div class="content_holder" style="width: 90%; align-content: stretch">
    <form id="form" action="${pageContext.request.contextPath}/data/insert" method="post">
        <div>
            <jsp:include page="../elements/interactiveTable.jsp"/>
        </div>
        <jsp:include page="../elements/tableSelect.jsp"/>
    </form>
</div>
<jsp:include page="../elements/footer.jsp" />
</body>
<script>
    function submitCells() {
        var save = window.confirm("<%=LocalizationManager.getString("previewExcel.submit")%>");
        if (!save) {
            document.getElementById("selected_table").value = "none";
        }

        const form = document.getElementById("form");
        form.action = form.action.concat("/", document.getElementById("selected_table").value);
        form.submit();
    }

    function showCellInput(cell) {}

    function init() {
        document.getElementById("submitBtn").onclick = function() { submitCells(); }
        <%session.setAttribute("table", pageContext.getRequest().getAttribute("table"));%>
    }
</script>
</html>
