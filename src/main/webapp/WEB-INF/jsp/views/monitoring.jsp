<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>State Educational Institution Monitoring Service</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css" />
</head>
<body id="header-body">
<jsp:include page="../elements/header.jsp"/>
<table style="width: 100%;">
    <tr>
        <td></td>
        <td id="td-content" style="width:1200px !important; background: #fff;">
            <table style="width:100%">
                <tr style="vertical-align:top;">
                    <td style="width:900px;padding:0px 30px;">
                        <p id='navmw' style='padding-bottom:14px;font: normal 700 12px Calibri; color:#999;'><a href='/' style='background:url(img/ti_home_dark.svg) left top 10px no-repeat;padding-left:15px;'>Главная</a> / <span style='color:#333;'>Мониторинг</span></p><!-- Cached copy, generated 22:51 -->
                        <table style='width:100%;margin-bottom:15px;'>
                            <tr>
                                <td>
                                    <jsp:include page="../elements/interactiveMap.jsp"/>
                                </td>
                            </tr>
                        </table>
                    </td>
                </tr>
            </table>
        </td>
        <td></td>
    </tr>
</table>
<jsp:include page="../elements/footer.jsp" />
</body>
</html>
