<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div>
    <input type="hidden" name="updated_values" id="updated_values">
    <input type="hidden" name="table_name" id="table_name">
    <input type="hidden" id="deleted_row_id" name="deleted_row_id">
    <input type="hidden" id="deleted_row_col" name="deleted_row_col">
    <h2 id="ep_name">Undefined</h2>
    <table id="ep_table">
    </table>
</div>
<script>
    function initTable(num) {
        const tab = JSON.parse(sessionStorage.getItem('excelTables'))['tab_'+num];
        sessionStorage.setItem('data', JSON.stringify([]));
        $('#ep_name').text(tab['name']);
        const $labels = $('<tr id="ep_labels"></tr>');
        for(const key in tab['labels']) {
            const $label = $('<th>' + tab['labels'][key] + '</th>');
            $labels.append($label);
        }
        const $table = $('#ep_table');
        $table.empty();
        $table.append($labels);
        for(const row in tab['data']) {
            const $tr = $('<tr></tr>');
            for(const cell in tab['data'][row]) {
                const $td = $('<td class="interactive_cell">');
                const $p = $('<p>'+ tab['data'][row][cell] +'</p>');
                $td.append($p);
                $tr.append($td);
            }
            $table.append($tr);
        }
    }
</script>
