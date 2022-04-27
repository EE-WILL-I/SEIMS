<%@ page import="java.sql.ResultSet" %>
<%@ page import="ru.seims.database.entitiy.DataTable" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="ru.seims.database.proccessing.SQLExecutor" %>
<%@ page import="ru.seims.utils.properties.PropertyReader" %>
<%@ page import="ru.seims.utils.properties.PropertyType" %>
<%@ page import="org.json.simple.JSONArray" %>
<%@ page import="org.json.simple.parser.JSONParser" %>
<%@ page import="ru.seims.utils.logging.Logger" %>
<%@ page import="org.json.simple.JSONObject" %>
<%@ page import="java.util.Iterator" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    JSONArray tables = new JSONArray();
    try {
         tables = (JSONArray) new JSONParser().parse((String) pageContext.getRequest().getAttribute("tables"));
    } catch (Exception e) {
        Logger.log(this, e.getMessage(), 2);
    }
    String table_name = ((DataTable)pageContext.getRequest().getAttribute("table")).getName();
    if(table_name == null || table_name.isEmpty())
        table_name = "people";
%>
<div id="table_div">
    <select id="selected_table" name="selected_table">
        <%
            Iterator<JSONObject> iterator = tables.iterator();
            while(iterator.hasNext()) {
        String table = (String) iterator.next().get("table");%>
        <option value="<%=table%>" <%if(table.equals(table_name)) {%>selected<%}%>><%=table%></option>
        <%}%>
    </select>
</div>
