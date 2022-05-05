<%@ page import="ru.seims.localization.LocalizationManager" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!--<nav>
    <div id="nav_content">
        <a href="${pageContext.request.contextPath}/"><%=LocalizationManager.getString("header.home")%></a>
        | <a href="${pageContext.request.contextPath}/data"><%=LocalizationManager.getString("header.data_view")%></a>
        | <a href="${pageContext.request.contextPath}/data/upload"><%=LocalizationManager.getString("header.load_file")%></a>
        | <a href="${pageContext.request.contextPath}/mail"><%=LocalizationManager.getString("header.mail_srvc")%></a>
        | <a href="${pageContext.request.contextPath}/login"><%=LocalizationManager.getString("header.login")%></a>
        | <a href="#"><%=LocalizationManager.getString("header.about")%></a>
        <div style="float: right; padding-right: 10px;">
            <%=LocalizationManager.getString("header.search")%> <input name="search">
        </div>
    </div>
</nav>-->
<table id="header-table" class="c_0">
    <tbody>
    <tr>
        <td></td>
        <td class="header-td">
            <div id="header-div-back" class="c_0">
                <span class="small-text">ВГПУ – Воронежский Государственный Педагогический Университет</span>
                <span class="small-text" style="bottom: 20px;">ГЛАВНЫЙ ИНФОРМАЦИОННО-<br>МОНИТОРИНГОВЫЙ СЕРВИС</span>
            </div>

            <div id="header-div">
                <div id="head-nav">
                    <a href="/" class="header-nav" style="background:url(/img/ti_home.svg) left top 10px no-repeat;">Домой</a> |
                    <a href="/data/upload" class="header-nav" style="background:url(/img/ti_map.png) left top 10px no-repeat; background-size: 15px; padding-left: 20px;">Загрузка</a> |
                    <a href="/data" class="header-nav" style="background:url(/img/ti_map.png) left top 10px no-repeat; background-size: 15px; padding-left: 20px;">База данных</a> |
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
