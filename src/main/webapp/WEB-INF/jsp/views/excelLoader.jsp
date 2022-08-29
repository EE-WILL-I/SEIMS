<%@ page import="ru.seims.localization.LocalizationManager" %>
<%@ page import="ru.seims.utils.properties.PropertyReader" %>
<%@ page import="ru.seims.utils.properties.PropertyType" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%String orgId = (String) request.getAttribute("org_id");%>
<html>
<head>
    <title>Звгрузка данных</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css" />
</head>
<script>const uploadPath = '${pageContext.request.contextPath}/org/<%=orgId%>/upload/excel?type=';</script>
<script src="${pageContext.request.contextPath}/js/customScripts.js" type="text/javascript"></script>
<body>
<jsp:include page="../elements/header.jsp"/>
<jsp:include page="../elements/popup.jsp"/>
<div class="content_holder" style="display: flex; justify-content: center;">
    <div style="width: 100%;">
        <ul>
            <select id="doc_type" name="doc_type">
                <option value="2">OO-1</option>
                <option value="3">OO-2</option>
            </select>
            <form id="form" action="${pageContext.request.contextPath}/org/<%=orgId%>/upload/excel?type=2" method = "post" enctype = "multipart/form-data">
                <input id="file" type = "file" name = "file" size = "50" />
                <br/>
                <input id="uploadBtn" type = "button" onclick="uploadFile('file', 'form',
                    <%=PropertyReader.getPropertyValue(PropertyType.SERVER, "app.maxFileSize")%>)"
                       value = "<%=LocalizationManager.getString("excelLoader.upload")%>" />
            </form>
        </ul>
        <% if(pageContext.getRequest().getAttribute("errorMessage") != null) {%>
            <ul style="color: red"><%=pageContext.getRequest().getAttribute("errorMessage")%></ul>
        <%}%>
    </div>
</div>
<jsp:include page="../elements/footer.jsp" />
</body>
</html>