<%@ page import="ru.seims.database.entitiy.DataTable" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<% DataTable table = (DataTable) pageContext.getRequest().getAttribute("table"); %>
<div>
    <input type="hidden" name="updated_values" id="updated_values">
    <input type="hidden" name="table_name" id="table_name">
    <input type="hidden" id="deleted_row_id" name="deleted_row_id">
    <input type="hidden" id="deleted_row_col" name="deleted_row_col">
    <table>
        <h2 id="table"><%=table.getName()%></h2>
        <tr>
            <%for(String name : table.getColumnLabels()) {%>
            <th><%=name%></th>
            <%}%>
        </tr>
        <%
            for(int i = 0; i < table.getDataRows().size(); i++) {
        %>
        <tr><%
            String id = table.getRow(i).get(table.getColumn(0));
            for(String column : table.getColumnLabels())  {
                String cell_val = table.getRow(i).get(column); %>
            <td class="interactive_cell">
                <p><%=cell_val%></p>
            </td>
            <%}%>
        </tr>
        <%}%>
    </table>
</div>