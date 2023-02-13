var isLoading = false;
let getTablesAPI = '';
let getRowsAPI = '';
let getColsAPI = '';
let getRegionsAPI = '';
let getOrgsAPI = '';
let tdata = '';
let rdata = '';
let cdata = '';
let ddata = '';
let odata = '';
let r1_name = '';
let r2_name = '';
var $activeTab = null;
let vrtable = '';
var rows = [];
var cols = []
var regs = [];
var orgs = [];
const inactiveBtnCol = '#335a3f';
const activeBtnCol = '#437652';

function initTabs(type) {
    if (!isLoading) {
        isLoading = true;
        showLoadingWrapper($('#filter_data_wrapper'), () => {
            $('#table_data').remove();
            $('#row_data').remove();
            $('#col_data').remove();
            $('#org_data').remove();
            $('#reg_data').remove();
        });
        fetch(getTablesAPI+'?doc='+type).then(function (response) {
            return response.json();
        }).then(function (data) {
            tdata = data;
            showTables('');
        }).catch(function () {
            alert("Server Error. Cant fetch data of tables from database.");
            hideLoadingWrapper($('#filter_data_wrapper'), () => { isLoading = false; });
        });
    }
}

function showTables(filter) {
    const $filterTableData = $('#filter_table_data');
    const $tableList = $('<div id="table_data"></div>');
    const $wrapper = $('#filter_data_wrapper');
    if (!isLoading) {
        isLoading = true;
        showLoadingWrapper($wrapper, () => {
            $('#table_data').remove();
        });
    }
    for (const key in tdata) {
        if(filter != null && filter !== '' && !tdata[key]['display_name'].toLocaleLowerCase('ru-RU').includes(filter.toLocaleLowerCase('ru-RU')))
            continue;
        let out = tdata[key]['display_name'] + '<br/>';
        const $tab = $('<button id="tab_'+key+'" class="filter_tab_button" onclick="setTable(\''+tdata[key]['r1_name']+'\', \''+tdata[key]['r2_name']+'\', \'tab_'+key+'\',\''+key+'\')"><p class="filter_tab_wrapper">' + out + '</p></button>');
        $tableList.append($tab);
    }
    hideLoadingWrapper($wrapper, () => {
        $filterTableData.append($tableList);
    });
    isLoading = false;
}

function setTable(r1, r2, tab, key) {
    if($activeTab !== null) $activeTab.css("background-color", inactiveBtnCol);
    $activeTab = $('#'+tab);
    $activeTab.css("background-color", activeBtnCol);
    r1_name = r1;
    r2_name = r2;
    rows = [];
    cols = [];
    vrtable = tdata[key];
    $('#row_data').remove();
    $('#col_data').remove();
    $('#org_data').remove();
    $('#reg_data').remove();
    initRows();
}

function initRows() {
    if (!isLoading) {
        isLoading = true;
        showLoadingWrapper($('#filter_row_wrapper'), () => {
            $('#row_data').remove();
        });
        fetch(getRowsAPI+'?tab='+r1_name).then(function (response) {
            return response.json();
        }).then(function (data) {
            rdata = data;
            showPanel(rdata, '', 'filter_row_data', 'filter_row_wrapper', 'row_data', initCols, 'setRowToContext');
        }).catch(function () {
            alert("Server Error. Cant fetch data of rows from database.");
            hideLoadingWrapper($('#filter_row_wrapper'), () => { isLoading = false; });
        });
    }
}

function showRows(filter) {
    if (!isLoading) {
        if (vrtable['update_type'] === '1')
            showPanel(rdata, filter, 'filter_row_data', 'filter_row_wrapper', 'row_data', showCols, 'setRowToContext');
        else
            showPanel(rdata, filter, 'filter_row_data', 'filter_row_wrapper', 'row_data', () => {
                var $colData = $('#filter_col_data');
                var $placeholder = $('<div id="col_data"><p>Нет столбцов</p></div>');
                $colData.append($placeholder);
                showRegions('');
            }, 'setRowToContext');
    }
}

function initCols() {
    if (!isLoading) {
        isLoading = true;
        showLoadingWrapper($('#filter_col_wrapper'), () => {
            $('#col_data').remove();
        });
        fetch(getColsAPI+'?tab='+r2_name).then(function (response) {
            return response.json();
        }).then(function (data) {
            cdata = data;
            showPanel(cdata, '', 'filter_col_data', 'filter_col_wrapper', 'col_data', initRegions, 'setColToContext');
        }).catch(function () {
            alert("Server Error. Cant fetch data of columns from database.");
            hideLoadingWrapper($('#filter_col_wrapper'), () => { isLoading = false; });
        });
    }
}

function showCols(filter) {
    if (!isLoading) {
        showPanel(cdata, filter, 'filter_col_data', 'filter_col_wrapper', 'col_data', showRegions, 'setColToContext');
    }
}

function showPanel(data, filter, panel, wrapper, listId, callback, selectFunc) {
    const $filterData = $('#'+panel);
    const $dataList = $('<div id="'+listId+'"></div>');
    const $wrapper = $('#'+wrapper);
    if (!isLoading) {
        isLoading = true;
        showLoadingWrapper($wrapper, () => {
            $('#'+listId).remove();
        });
    }
    for (const key in data) {
        if(filter != null && filter !== '' && !data[key]['name'].toLocaleLowerCase('ru-RU').includes(filter.toLocaleLowerCase('ru-RU')))
            continue;
        let out = data[key]['name'] + '<br/>';
        let id = listId+'_'+key;
        const $tab = $('<button id="'+id+'" class="filter_tab_button" onclick="'+selectFunc+'(\''+data[key]['id']+'\',\''+id+'\')"><p class="filter_tab_wrapper">' + out + '</p></button>');
        if(rows.includes(id) || cols.includes(id)) {
            $tab.css("background-color", activeBtnCol);
        }
        $dataList.append($tab);
    }
    hideLoadingWrapper($wrapper, function () {
        $filterData.append($dataList);
        if(callback != null && callback instanceof Function) callback();
    });
    isLoading = false;
}

function activateButton(btn, arr) {
    const $activeCol = $('#'+btn);
    if(arr.includes(btn)) {
        const index = arr.indexOf(btn);
        if (index > -1) {
            arr.splice(index, 1);
            $activeCol.css("background-color", inactiveBtnCol);
        }
    } else {
        arr.push(btn);
        $activeCol.css("background-color", activeBtnCol);
    }
}

function setRowToContext(val, btn) {
    if(btn == null) return;
    activateButton(btn, rows);
    //console.log(val);
}

function setColToContext(val, btn) {
    if(btn == null) return;
    activateButton(btn, cols);
    //console.log(val);
}

function setRegToContext(val, btn) {
    if(btn == null) return;
    activateButton(btn, regs);
    //console.log(val);
}

function setOrgToContext(val, btn) {
    if(btn == null) return;
    activateButton(btn, orgs);
    //console.log(val);
}

function initRegions() {
    if (!isLoading) {
        isLoading = true;
        showLoadingWrapper($('#filter_reg_wrapper'), () => {
            $('#reg_data').remove();
        });
        fetch(getRegionsAPI).then(function (response) {
            return response.json();
        }).then(function (data) {
            rdata = data;
            showPanel(rdata, '', 'filter_reg_data', 'filter_reg_wrapper', 'reg_data', initOrgs, 'setRegToContext');
        }).catch(function () {
            alert("Server Error. Cant fetch data of regions from database.");
            hideLoadingWrapper($('#filter_reg_wrapper'), () => { isLoading = false; });
        });
    }
}

function showRegions(filter) {
    if (!isLoading) {
        showPanel(rdata, filter, 'filter_reg_data', 'filter_reg_wrapper', 'reg_data', showOrgs, 'setRegToContext');
    }
}

function initOrgs() {
    if (!isLoading) {
        isLoading = true;
        showLoadingWrapper($('#filter_org_wrapper'), () => {
            $('#org_data').remove();
        });
        fetch(getOrgsAPI).then(function (response) {
            return response.json();
        }).then(function (data) {
            odata = data;
            showOrgs('');
        }).catch(function () {
            alert("Server Error. Cant fetch data of organizations from database.");
            hideLoadingWrapper($('#filter_org_wrapper'), () => { isLoading = false; });
        });
    }
}

function showOrgs(filter) {
    showPanel(odata, filter, 'filter_org_data', 'filter_org_wrapper', 'org_data', null, 'setOrgToContext');
}

(function($){

    $(window).on("load", function () {
        if (!('fetch' in window)) {
            console.log('Fetch API not found, please upgrade your browser.');
            return;
        }

        $(".mapPoint").mouseenter(function(){
            $(this).children("span").show();
        }).mouseleave(function(){
            $(this).children("span").hide();
        });

        //setPaths(paths);
    });
})(jQuery);