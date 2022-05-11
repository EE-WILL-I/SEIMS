<%@ page import="ru.seims.database.entitiy.DataTable" %>
<%@ page import="ru.seims.localization.LocalizationManager" %>
<%@ page import="java.util.ArrayList" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<% DataTable table = (DataTable) pageContext.getRequest().getAttribute("table");%>
<div>
    <input type="hidden" name="table_name" id="table_name_<%=table.getSysName()%>">
        <input type="hidden" id="deleted_row_id_<%=table.getName()%>" name="deleted_row_id">
        <input type="hidden" id="deleted_row_col_<%=table.getName()%>" name="deleted_row_col">
        <table class="data_table">
            <h2 id="table_<%=table.getSysName()%>"><%=table.getName()%></h2>
            <tr>
                <% ArrayList<String> labels = table.getColumnLabels();
                    for(int j = 1; j < labels.size(); j++) {
                        String label = labels.get(j);
                        if(label.equals("name")) label = "Показатель";
                        if(label.equals("value")) label = "Значение";%>
                <th class="row_label" id="col_<%=j%>_<%=table.getSysName()%>"><p><%=label%></p></th>
                <%}%>
            </tr>
            <%
                for(int i = 0; i < table.getDataRows().size(); i++) {
            %>
            <tr><%
                String id = table.getRow(i).get(table.getColumn(1));
                ArrayList<String> columns = table.getColumnLabels();
                for(int j = 1; j < columns.size(); j++) {
                    String cell_val = table.getRow(i).get(columns.get(j)); if(cell_val == null) cell_val = "x"; %>
                <td <%if(j > 1) {%> class="interactive_cell"
                                    onclick="showCellInput(this)"
                                    onmouseenter="highlightLabels(this, <%=j%>, <%=i%>, '<%=table.getSysName()%>')"
                                    onmouseleave="resetLabels(<%=j%>, <%=i%>, '<%=table.getSysName()%>')"
                                    <%} else{%>
                                    class="row_label" id="row_<%=i%>_<%=table.getSysName()%>"<%}%>>
                    <p><%=cell_val%></p>
                    <input class="row_input"
                           onchange="updateCellValue(this,'<%=id%>','<%=columns.get(j)%>',this.value, '<%=table.getSysName()%>')"
                           onblur="onInputBlur(this)" value="<%=cell_val%>" style="display: none;"/>
                </td>
                <%}%>
            </tr>
            <%}%>
        </table>
    <button class="submit_btn" type="button" onclick="saveUpdatedCells(<%=request.getAttribute("org_id")%>, '<%=table.getSysName()%>')">
        <%=LocalizationManager.getString("intTable.submit")%>
    </button>
</div>
