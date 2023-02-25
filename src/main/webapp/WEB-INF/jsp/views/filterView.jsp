<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html class="fz-md-875"  xml:lang="ru-ru" lang="ru-ru" dir="ltr">
<head>
    <title>Фильтр параметров</title>
    <meta charset="utf-8">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css" />
</head>
<body id="header-body">
<jsp:include page="../elements/header.jsp"/>
<jsp:include page="../elements/popup.jsp"/>
<table class="table_body" style="width: 100%; background: #d5e1df">
    <tr>
        <td></td>
        <td id="td-content" style="width:80%; background: #fff;">
            <table style="width:100%; min-height: 75%">
                <tr style="vertical-align:top;">
                    <td style="width:900px;padding:0px 30px;">
                        <div class="content_holder">
                            <p id='navmw' style='padding-bottom:14px;font: normal 700 12px Calibri; color:#999;'><a href='/' style='background:url(${pageContext.request.contextPath}/img/ti_home_dark.svg)
                                    left top 10px no-repeat;padding-left:15px;'>Главная</a> / <a href="/monitoring">Мониторинг</a> /  <span style='color:#333'>Фильтр параметров</span>
                            </p>
                            <table style='width:100%;margin-bottom:15px;'>
                                <tr>
                                    <td>
                                        <jsp:include page="../elements/filterPanel.jsp"/>
                                    </td>
                                </tr>
                            </table>
                        </div>
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