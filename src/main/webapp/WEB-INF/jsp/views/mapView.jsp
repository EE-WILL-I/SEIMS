<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html prefix="og: http://ogp.me/ns#" lang="ru" class="fz-md-875"  xml:lang="ru-ru" lang="ru-ru" dir="ltr">
<head>
    <title>Интерактивная карта</title>
    <meta charset="utf-8">
    <!--<link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.min.css">-->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css" />
    <link href="${pageContext.request.contextPath}/JS/bootgrid/jquery.bootgrid.min.css" rel="stylesheet" />
</head>

<body>
<jsp:include page="../elements/header.jsp"/>
<jsp:include page="../elements/popup.jsp"/>
<jsp:include page="../elements/interactiveMap.jsp"/>
<jsp:include page="../elements/footer.jsp" />
</body>
</html>
