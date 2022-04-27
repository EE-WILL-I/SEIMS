<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>State Educational Institution Monitoring Service</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css" />
</head>
<body id="header-body">
<table id="header-table" class="c_0">
    <tbody>
    <tr style="height:155px">
        <td></td>
        <td class="header-td">
            <div id="header-div-back" class="c_0">
                <span class="small-text">ВГПУ – Воронежский Государственный Педагогический Университет</span>
                <span class="small-text" style="bottom: 20px;">ГЛАВНЫЙ ИНФОРМАЦИОННО-<br>МОНИТОРИНГОВЫЙ СЕРВИС</span>
            </div>

            <div id="header-div">
                <div id="head-nav">
                    <a href="/" class="header-nav" style="background:url(/img/ti_home.svg) left top 10px no-repeat;">Домой</a> |
                    <a href="/map" class="header-nav" style="background:url(/img/ti_map.png) left top 10px no-repeat; background-size: 15px; padding-left: 20px;">Карта</a> |
                    <button class="header-nav custom-button" type="button" onclick="setAuthPanel()"  style="background:url(/img/ti_login.svg) left top 5px no-repeat;">Войти</button>
                    <jsp:include page="../elements/authorizationPanel.jsp"/>
                </div>
                <div style="font:normal normal 400 20px/27px Tahoma; color:#EFF0F4; text-shadow: -1px -0px 1px rgba(0,0,0,0.6);">
                    Система мониторинга бюджетных образовательных учреждений <br> по Воронежской области
                </div>
            </div>
        </td>
        <td></td>
    </tr>
    </tbody>
</table>
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
