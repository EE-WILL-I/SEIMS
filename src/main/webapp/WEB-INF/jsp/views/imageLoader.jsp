<%@ page import="ru.seims.localization.LocalizationManager" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <title>Data load</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css" />
</head>
<body>
<jsp:include page="../elements/header.jsp"/>
<jsp:include page="../elements/popup.jsp"/>
<div class="content_holder" style="display: flex; justify-content: center;">
  <div style="width: 100%;">
    <ul>
      <form action = "${pageContext.request.contextPath}upload/image?orgId=1" method = "post" enctype = "multipart/form-data">
        <input id="excel_1" type = "file" name = "file" size = "50" />
        <br/>
        <input id="excel_2" type = "submit" value = "<%=LocalizationManager.getString("excelLoader.upload")%>" />
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