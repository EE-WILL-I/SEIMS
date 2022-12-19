<%@ page import="ru.seims.database.entitiy.DataTable" %>
<%@ page import="ru.seims.localization.LocalizationManager" %>
<%@ page import="ru.seims.application.servlet.jsp.DatabaseServlet" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<% DataTable table = (DataTable) pageContext.getRequest().getAttribute("table");%>
<script>
    function init() {
        sessionStorage.setItem('data', JSON.stringify({}));
    }
    function updateCellValue(input, id, col, val) {
        const dataArr = JSON.parse(sessionStorage.getItem("data"));
        const compId = id+col;
        dataArr[compId] = JSON.parse('{"id": "'+id.toString()+'", "col": "'+col.toString()+'", "val": "'+val.toString()+'"}');
        sessionStorage.setItem('data', JSON.stringify(dataArr));
        input.style.display = "none";
        input.parentElement.children[0].innerHTML = val;
        input.parentElement.children[0].style.display = "block";
    }

    function showCellInput(cell) {
        cell.children[0].style.display = "none";
        cell.children[1].style.display = "block";
        cell.children[1].focus();
    }

    function onInputBlur(input) {
        input.style.display = "none";
        input.parentElement.children[0].style.display = "block";
    }

    function saveUpdatedCells(table) {
        const data = sessionStorage.getItem("data").toString();
        if (data === "{}") {
            alert("<%=LocalizationManager.getString("intTable.no_data_updated")%>");
        } else {
            const form = document.getElementById("form");
            const save = window.confirm("<%=LocalizationManager.getString("intTable.save")%>");
            if (save) {
                document.getElementById("updated_values").value = data;
                sessionStorage.setItem('data', JSON.stringify({}));
                form.setAttribute("method", "POST");
                form.action = "<%=DatabaseServlet.updateTableAPI%>/" + table;
                document.getElementById("form").submit();
            } else sessionStorage.setItem('data', JSON.stringify({}));
        }
    }

    function deleteRow(id, col, table) {
        var del = window.confirm("<%=LocalizationManager.getString("intTable.delete")%>" + id + "?");
        if (del) {
            const form = document.getElementById("form");
            form.setAttribute("method", "POST");
            form.action = '<%=DatabaseServlet.deleteFromTableAPI%>/' + table + "?column=" + col + "&value=" + id;
            form.submit();
        }
    }
</script>
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
            <td class="interactive_cell" onclick="showCellInput(this)">
                <p><%=cell_val%></p>
                <input class="row_input" onchange="updateCellValue(this,'<%=id%>','<%=column%>',this.value)"
                       onblur="onInputBlur(this)" value="<%=cell_val%>" style="display: none;"/>
            </td>
            <%}%>
            <td style="width: 10px;">
                <button class="delete_btn" value="delete" type="button"
                        onclick="deleteRow('<%=id%>','<%=table.getColumn(0)%>','<%=table.getName()%>')">x</button>
            </td>
        </tr>
        <%}%>
    </table>
    <button id="submitBtn" type="button" onclick="saveUpdatedCells('<%=table.getName()%>')">
        <%=LocalizationManager.getString("intTable.submit")%>
    </button>
</div>
