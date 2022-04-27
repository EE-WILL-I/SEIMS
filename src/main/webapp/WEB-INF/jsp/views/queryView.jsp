<%@ page import="ru.seims.database.entitiy.DataTable" %>
<%@ page import="java.util.Locale" %>
<%@ page import="ru.seims.localization.LocalizationManager" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    DataTable table = (DataTable) pageContext.getRequest().getAttribute("table");
    StringBuilder recipients = new StringBuilder("");
    if(table != null) {
        String mailColName = "email";
        for(String col : table.getColumnLabels())
            if(col.toLowerCase(Locale.ROOT).contains("mail"))
                mailColName = col;
        for(int i = 0; i < table.getDataRows().size(); i++) {
            recipients.append(table.getRow(i).get(mailColName));
            if(i < table.getDataRows().size() - 1)
                recipients.append(",");
        }
    }
%>
<html>
<head>
    <title>Query view</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css" />
</head>
<jsp:include page="../elements/header.jsp"/>
<jsp:include page="../elements/popup.jsp"/>
<body>
    <div class="content_holder">
        <jsp:include page="../elements/dataViewTab.jsp"/>
        <div class="q_div">
            <div id="q_div1" style="height: auto;">
                <nav class="q_tab">
                    <a href="${pageContext.request.contextPath}/data/query/get_stipend_candidats.sql?args=*">Candidats</a>
                    <a href="${pageContext.request.contextPath}/data/query/get_students_with_stipend.sql?args=*">Stipend</a>
                </nav>
            </div>
            <%if(table != null) {%>
            <div id="q_div2" style="width: 100%">
                <jsp:include page="../elements/staticTable.jsp"/>
            </div>
        </div>
        <form id="mail_form" action="${pageContext.request.contextPath}/mail/recipients/<%=recipients.toString()%>">
            <button><%=LocalizationManager.getString("queryView.send_mail")%></button>
        </form>
        <%} else {%>
        <p style="color: red">No data.</p>
        <%}%>
    </div>
</body>
<jsp:include page="../elements/footer.jsp" />
</html>
