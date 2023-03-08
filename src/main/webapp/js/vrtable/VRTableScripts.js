sessionStorage.setItem('data', JSON.stringify({}));
const rowLabelColorInactive = '#367554';
const rowLabelColorActive = '#50ab7c';
var updateOrgURL = "/edit/org/update"; //old value
var infoData = {};

function isInt(value) {
    return !(value.toString().includes("-")  || (value.toString().startsWith("0") && value.toString().length > 1) || value.toString().includes("+") || value.toString().includes(".") || value.toString().includes(",")) && !isNaN(value) && (function(x) { return (x | 0) === x; })(parseFloat(value));
}

function updateCellValue(input, id, col, val, initVal, table, updateType, r1) {
    if(!isInt(val)) {
        alert('Значение ячейки должно являться неотрицательным целым числом');
        input.value = initVal;
        return;
    }
    if(val > 32767) {
        alert('Значение ячейки слишком большое');
        input.value = initVal;
        return;
    }
    const compId = id+col;
    const dataArr = JSON.parse(sessionStorage.getItem("data"));
    dataArr[compId] = JSON.parse('{"vr1_name": "'+id.toString()+'", "vr2_name": "'+col.toString()+'", "val": "'+val.toString()+'", "table": "'+table+'", "updateType": "'+updateType+'", "r1": "'+r1+'"}');
    sessionStorage.setItem('data', JSON.stringify(dataArr));
    input.style.display = "none";
    input.parentElement.children[0].innerHTML = val;
    input.parentElement.children[0].style.display = "block";
}

function updateFieldValue(input, val, field) {
    infoData[field] =  val;
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
        const save = window.confirm("Применить изменения?");
        if (save) {
            document.getElementById("updated_values").value = data;
            sessionStorage.setItem('data', JSON.stringify({}));
            form.action = updateOrgURL;
            form.submit();
        } else sessionStorage.setItem('data', JSON.stringify({}));
    }
}

function saveUpdatedFields(orgId) {
    if (infoData === "{}") {
        alert("Никакие данные не были изменены");
    } else {
        const form = document.getElementById("info_form");
        const save = window.confirm("Применить изменения?");
        if (save) {
            console.log(JSON.stringify(infoData));
            document.getElementById("info_data").value = JSON.stringify(infoData);
            form.submit();
        } else infoData = {};
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