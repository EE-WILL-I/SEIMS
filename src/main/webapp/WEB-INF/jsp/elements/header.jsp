<%@ page import="ru.seims.localization.LocalizationManager" %>
<%@ page import="ru.seims.application.servlet.jsp.IndexServlet" %>
<%@ page import="ru.seims.application.servlet.jsp.DatabaseServlet" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    boolean isAuthorized = request.getAttribute("authorized") != null && request.getAttribute("authorized").equals("true");
    String username = "";
    if(isAuthorized)
        username = request.getAttribute("username").toString();
%>
<table id="header-table" class="c_0">
    <tbody>
    <tr>
        <td></td>
        <td class="header-td">
            <div id="header-div">
                <div id="head-nav">
                    <div><a class="header-nav" style="margin: 0; padding: 0" href="http://www.vspu.ac.ru/">ВГПУ – Воронежский Государственный Педагогический Университет</a></div>
                    <div>
                        <a href="<%=IndexServlet.index%>" class="header-nav" style="background:url(/img/ti_home.svg) left top 10px no-repeat;">Домой</a> |
                        <!--<a href="<%=DatabaseServlet.uploadExcel%>" class="header-nav" style="background:url(/img/ti_map.png) left top 10px no-repeat; background-size: 15px; padding-left: 20px;">Загрузка</a> |-->
                        <a href="<%=DatabaseServlet.data%>" class="header-nav" style="background:url(/img/ti_map.png) left top 10px no-repeat; background-size: 15px; padding-left: 20px;">База данных</a> |
                        <%if(isAuthorized) {%>
                        <button class="header-nav custom-button" type="button" onclick="setUserPanel('<%=username%>')" style="background:url(/img/ti_login.svg) left top 5px no-repeat;"><%=username%></button>
                        <jsp:include page="userPanel.jsp"/>
                        <%} else {%>
                        <button class="header-nav custom-button" type="button" onclick="setAuthPanel()"  style="background:url(/img/ti_login.svg) left top 5px no-repeat;">Войти</button>
                        <jsp:include page="../elements/authorizationPanel.jsp"/>
                        <%}%>
                    </div>
                </div>
                <div style="display:flex;justify-content: space-between;align-items: center;">
                    <img style="width: 110px;height: auto;" src="${pageContext.request.contextPath}/img/f_flag.png">
                    <div style="font:normal normal 400 19px/25px Tahoma; color:#EFF0F4;text-shadow: -1px -0px 1px rgba(0,0,0,0.6);">
                    Система сбора и обработки статистических данных<br>об общеобразовательных организация<br> по Воронежской области
                    </div>
                </div>
            </div>
        </td>
        <td></td>
    </tr>
    </tbody>
</table>
