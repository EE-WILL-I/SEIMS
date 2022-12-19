<%@ page import="ru.seims.database.entitiy.DataTable" %>
<%@ page import="ru.seims.localization.LocalizationManager" %>
<%@ page import="java.util.ArrayList" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    DataTable table = (DataTable) pageContext.getRequest().getAttribute("table");
    Object hideAttr = pageContext.getRequest().getAttribute("hide_table");
    boolean isHidden = false;
    if(hideAttr != null) isHidden = hideAttr.toString().equals("true");
//require VRTableScripts.js%>
<div class="vr_table" id="<%=table.getSysName()%>" <%if(isHidden) {%>style="display: none"<%}%>>
    <input type="hidden" name="table_name" id="table_name_<%=table.getSysName()%>">
        <input type="hidden" id="deleted_row_id_<%=table.getName()%>" name="deleted_row_id">
        <input type="hidden" id="deleted_row_col_<%=table.getName()%>" name="deleted_row_col">
        <h2 id="table_<%=table.getSysName()%>"><%=table.getName()%></h2>
        <table class="data_table">
            <tbody class="vr_tbody">
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
                    String cell_val = table.getRow(i).get(columns.get(j)); %>
                <td <%if(j > 1) { if(cell_val == null)  { cell_val = "x"; %>
                        class="blocked_cell"
                        <%} else {%>
                        class="interactive_cell"
                        onclick="showCellInput(this)"
                        <%}%>
                        onmouseenter="highlightLabels(this, <%=j%>, <%=i%>, '<%=table.getSysName()%>')"
                        onmouseleave="resetLabels(<%=j%>, <%=i%>, '<%=table.getSysName()%>')"
                        <%} else {%>
                        class="row_label" id="row_<%=i%>_<%=table.getSysName()%>"
                        <%}%>>
                    <p><%=j == 1 ? (i+1)+". " : ""%><%=cell_val == null ? "x" : cell_val%></p>
                    <input class="row_input"
                           onchange="updateCellValue(this,'<%=id%>','<%=table.getColumnName(j)%>',this.value, '<%=cell_val%>', '<%=table.getSysName()%>', '<%=table.getUpdateType()%>', '<%=table.getR1Name()%>')"
                           onblur="onInputBlur(this)" value="<%=cell_val%>" style="display: none;"/>
                </td>
                <%}%>
            </tr>
            <%}%>
            </tbody>
        </table>
    <button class="submit_btn" type="button" onclick="saveUpdatedCells(<%=(String)request.getAttribute("org_id")%>, '<%=table.getSysName()%>')">
        <%=LocalizationManager.getString("intTable.submit")%>
    </button>
</div>
