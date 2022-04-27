<%@ page import="ru.seims.database.entitiy.StoredImage" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String imageId = (String) request.getAttribute("image_id");
    StoredImage image = new StoredImage(imageId, "Не найдено");
%>
<html>
<head>
    <title>images!</title>
</head>
<body>
<div align="center">
    <h2><%=image.getName()%></h2>
    <h3><%=image.getExtension()%></h3>
    <img src="data:image/jpg;base64,<%=image.getBase64Data()%>"/>
</div>
</body>
</html>
