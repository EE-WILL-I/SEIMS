<%@ page import="ru.seims.localization.LocalizationManager" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<nav>
    <div id="nav_content">
        <a href="/data"><%=LocalizationManager.getString("dataView.tab.table")%></a>
        | <a href="/data/query"><%=LocalizationManager.getString("dataView.tab.query")%></a>
    </div>
</nav>
</html>
