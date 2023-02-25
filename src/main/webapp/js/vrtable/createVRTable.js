function createVRTable(root, body) {
    const $labels = $('<tr></tr>');
    for(var j = 0; j < body['labels'].length; j++) {
        const label = body['labels'][j];
        const $lbl = $('<th class="row_label" id="col_'+j+'_'+body['sysName']+'"><p>'+label+'</p></th>');
        $labels.append($lbl);
    }

    const $tbody = $(' <table id="table_'+body['sysName']+'" class="data_table"><tbody class="vr_tbody"></tbody></table>');
    $tbody.append($labels)

    for(var i = 0; i < body['data'].length; i++) {
        const $row = $('<tr></tr>');
        for(var j = 0; j < body['labels'].length; j++) {
            let val = body['data'][i][j];
            var $data;
            if(j > 1) {
                $data = $('<td onmouseenter="highlightLabels(this,' + j + ',' + i + ',\'' + body['sysName'] + '\')"' +
                    'onmouseleave="resetLabels(' + j + ',' + i + ',\'' + body['sysName'] + '\')"' +
                    'class="interactive_cell"><p>'+val+'</p></td>');
            } else {
                $data = $('<td class="row_label" id="row_'+i+'_'+body['sysName']+'"><p>'+val+'</p></td>');
            }
            $row.append($data);
        }
        $tbody.append($row);
    }

    const $table = $(
        '<div id="'+body['sysName']+'">\n' +
        '        <h2 id="table_h2_'+body['sysName']+'">'+body['name']+'</h2>\n' +
        '</div>'
    );
    $table.append($tbody);
    const $root = $('#'+root);
    $root.append($table);
}