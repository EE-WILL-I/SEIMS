<%@ page import="ru.seims.localization.LocalizationManager" %>
<%@ page import="ru.seims.utils.properties.PropertyReader" %>
<%@ page import="ru.seims.utils.properties.PropertyType" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <title>Data load</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css" />
</head>
<script src="${pageContext.request.contextPath}/JS/customScripts.js" type="text/javascript"></script>
<body>
<jsp:include page="../elements/header.jsp"/>
<jsp:include page="../elements/popup.jsp"/>
<div class="content_holder" style="display: flex; justify-content: center;">
  <div style="width: 100%;">
    <ul>
      <form id="form" action = "${pageContext.request.contextPath}upload/image?orgId=1" method = "post" enctype = "multipart/form-data">
        <input id="file" type = "file" name = "file" size = "4" />
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