sessionStorage.setItem('data', JSON.stringify([]));
const rowLabelColorInactive = '#367554';
const rowLabelColorActive = '#59C28C';

function updateCellValue(input, id, col, val, table) {
    //var inId = "in"+id+":"+col;
    var dataArr = JSON.parse(sessionStorage.getItem("data"));
    dataArr.push({"vr1_name": id.toString(), "vr2_name": col.toString(), "val": val.toString(), "table": table});
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

function saveUpdatedCells(orgId) {
    var data = sessionStorage.getItem("data").toString();
    if (data === "[]") {
        alert("Никакие данные не были изменены");
    } else {
        const form = document.getElementById("form");
        var save = window.confirm("Применить следующие изменения?\n" + data);
        if (save) {
            document.getElementById("updated_values").value = data;
            //form.action = "/data/update/" + table;
            form.action = "/data/update/org/" + orgId;
        } else sessionStorage.setItem('data', '');
        document.getElementById("form").submit();
    }
}

function animateColor(id, color) {
    var anim = document.getElementById(id).animate({background: color}, 100);
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