<%@ page import="ru.seims.localization.LocalizationManager" %>
<%@ page import="ru.seims.utils.properties.PropertyReader" %>
<%@ page import="ru.seims.utils.properties.PropertyType" %>
<%@ page import="ru.seims.application.servlet.jsp.OrganizationServlet" %>
<%@ page import="ru.seims.application.servlet.jsp.DatabaseServlet" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%String orgId = (String) request.getAttribute("org_id");%>
<html>
<head>
    <title>Загрузка данных</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css" />
</head>
<script>const uploadPath = '<%=DatabaseServlet.postUploadExcel.replace("{id}", orgId)%>';</script>
<script src="${pageContext.request.contextPath}/js/customScripts.js" type="text/javascript"></script>
<body>
<jsp:include page="../elements/header.jsp"/>
<jsp:include page="../elements/popup.jsp"/>
<div class="content_holder" style="display: flex; justify-content: center;">
    <div style="width: 100%;">
        <p class="breadcrumbs" id='navmw' style=''><a href='/' style='background:url(/img/ti_home_dark.svg) left top 10px no-repeat; padding-left:15px;'>Главная</a> / <a href="/">Мониторинг</a></p>
        <ul style="display: flex; flex-direction: column; align-items: center;">
            <div style="display: flex;">
                <p style="font-size: 20px">Тип докумета:</p>
                <select id="doc_type" name="doc_type" onchange="setType(this.value)">
                    <option value="2">OO-1</option>
                    <option value="3">OO-2</option>
                </select>
            </div>
            <form id="form" action="${pageContext.request.contextPath}<%=DatabaseServlet.postUploadExcel.replace("{id}", orgId)%>?type=2" method = "post" enctype = "multipart/form-data">
                <input id="file" class="apps_btn" type = "file" name = "file" size = "50" />
                <br/>
                <input id="uploadBtn" class="apps_btn" type="button" onclick="uploadExcel('file', 'form',
                    <%=PropertyReader.getPropertyValue(PropertyType.SERVER, "app.maxFileSize")%>)"
                       value = "<%=LocalizationManager.getString("excelLoader.upload")%>" />
            </form>
        </ul>
        <% if(pageContext.getRequest().getAttribute("errorMessage") != null) {%>
            <ul style="color: red"><%=pageContext.getRequest().getAttribute("errorMessage")%></ul>
        <%}%>
    </div>
</div>
<script>
    function setType(type) {
        document.getElementById("form").action = "${pageContext.request.contextPath}<%=DatabaseServlet.postUploadExcel.replace("{id}", orgId)%>?type="+type;
    }
</script>
<jsp:include page="../elements/footer.jsp" />
</body>
</html>