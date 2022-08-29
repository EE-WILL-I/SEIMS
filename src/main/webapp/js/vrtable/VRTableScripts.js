sessionStorage.setItem('data', JSON.stringify({}));
const rowLabelColorInactive = '#367554';
const rowLabelColorActive = '#50ab7c';

function isInt(value) {
    return !(value.toString().includes("-") || value.toString().includes("+") || value.toString().includes(".") || value.toString().includes(",")) && !isNaN(value) && (function(x) { return (x | 0) === x; })(parseFloat(value));
}

function updateCellValue(input, id, col, val, initVal, table, updateType) {
    if(!isInt(val)) {
        alert('Значение ячейки должно являться целым числом.');
        input.value = initVal;
        return;
    }
    const compId = id+col;
    const dataArr = JSON.parse(sessionStorage.getItem("data"));
    dataArr[compId] = JSON.parse('{"vr1_name": "'+id.toString()+'", "vr2_name": "'+col.toString()+'", "val": "'+val.toString()+'", "table": "'+table+'", "updateType": "'+updateType+'"}');
    sessionStorage.setItem('data', JSON.stringify(dataArr));
    input.style.display = "none";
    input.parentElement.children[0].innerHTML = val;
    input.parentElement.children[0].style.display = "block";
}

function showCellInput(cell) {
    cell.children[0].style.display = "none";
    var inp = cell.children[1];
    inp.style.display = "block";
    inp.focus();
    inp.selectionStart = inp.selectionEnd = inp.value.length;
    if(inp.value === '0') {
        inp.value = '';
    }
}

function onInputBlur(input) {
    input.style.display = "none";
    var inp = input.parentElement;
    input.parentElement.children[0].style.display = "block";
    if(input.value === '') {
        inp.class = "empty_cell";
    } else {
        inp.class = "interactive_cell";
    }
}

function saveUpdatedCells(orgId) {
    const data = sessionStorage.getItem("data").toString();
    if (data === "{}") {
        alert("Никакие данные не были изменены");
    } else {
        const form = document.getElementById("form");
        const save = window.confirm("Применить следующие изменения?\n" + data);
        if (save) {
            document.getElementById("updated_values").value = data;
            sessionStorage.setItem('data', JSON.stringify({}));
            form.action = "/data/update/org/" + orgId;
        } else sessionStorage.setItem('data', JSON.stringify({}));
        document.getElementById("form").submit();
    }
}

function animateColor(id, color) {
    var anim = document.getElementById(id).animate({background: color}, 200);
    anim.onfinish = event => { document.getElementById(id).style.background = color; };
}

function highlightLabels(obj, col, row, table) {
    animateColor("col_" + col + "_" + table, rowLabelColorActive);
    animateColor("row_" + row + "_" + table, rowLabelColorActive);
    showTitle(obj, col, table);
}

function resetLabels(col, row, table) {
    animateColor("col_" + col + "_" + table, rowLabelColorInactive);
    animateColor("row_" + row + "_" + table, rowLabelColorInactive);
    hideTitle();
}

function focusBtn(id) {
    animateColor(id, rowLabelColorActive);
}

var showTitle = function (obj, col, table) {
    var $this = $(obj);
    var offset = $this.offset();
    var width = $this.width();
    var height = $this.height();

    var centerX = offset.left + width / 2;
    var centerY = offset.top + height / 2;
    var $title = $('<div id="mapTitle" class="down" style="position: absolute; pointer-events:none;"><span>' + document.getElementById("col_"+col+"_"+table).innerText + '<\/span><\/div>');
    $this.append($title);
    $title.css({
        top: centerY - ($title.innerHeight() + 10),
        left: centerX - ($title.innerWidth() / 2)
    });
};

var hideTitle = function () {
    $('#mapTitle').remove();
};