var isLoading = false;
var blocked = false;
let getTablesAPI = '';
let getRowsAPI = '';
let getColsAPI = '';
let getRegionsAPI = '';
let getOrgsAPI = '';
let getFilterAPI = '';
let tdata = '';
let rdata = '';
let cdata = '';
let ddata = '';
let odata = '';
let fdata = '';
let r1_name = '';
let r2_name = '';
var $activeTab = null;
let vrtable = '';
var rows = [];
var cols = []
var regs = [];
var orgs = [];
var requestContext = {};
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
            $('#out_table_data').remove();
            $('#doc_type').attr("disabled", true);
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
        $('#doc_type').removeAttr('disabled');
    });
    isLoading = false;
}

function setTable(r1, r2, tab, key) {
    if(blocked) return;
    blocked = true;
    if($activeTab !== null) $activeTab.css("background-color", inactiveBtnCol);
    $activeTab = $('#'+tab);
    $activeTab.css("background-color", activeBtnCol);
    r1_name = r1;
    r2_name = r2;
    rows = [];
    cols = [];
    regs = [];
    orgs = [];
    vrtable = tdata[key];
    requestContext['tab'] = vrtable;
    requestContext['rows'] = [];
    requestContext['cols'] = [];
    requestContext['regs'] = [];
    requestContext['orgs'] = [];
    requestContext['obj'] = 'reg';
    //console.log('Table\t', requestContext['tab']);
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
        fetch(getRowsAPI + '?tab=' + r1_name).then(function (response) {
            return response.json();
        }).then(function (data) {
            rdata = data;
            if (vrtable['update_type'] === '1')
                showPanel(rdata, '', 'filter_row_data', 'filter_row_wrapper', 'row_data', initCols, 'setRowToContext');
            else
                showPanel(rdata, '', 'filter_row_data', 'filter_row_wrapper', 'row_data', () => {
                    var $colData = $('#filter_col_data');
                    var $placeholder = $('<div id="col_data"><p>Нет столбцов</p></div>');
                    $colData.append($placeholder);
                    initRegions('');
                }, 'setRowToContext');
        }).catch(function () {
            alert("Server Error. Cant fetch data of rows from database.");
            hideLoadingWrapper($('#filter_row_wrapper'), () => {
                isLoading = false;
            });
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
        if(filter != null && filter !== '' && !data[key]['name'].toLocaleLowerCase('ru-RU')
            .includes(filter.toLocaleLowerCase('ru-RU')))
            continue;
        let out = data[key]['name'] + '<br/>';
        let id = listId+'_'+key;
        const $tab = $('<button id="'+id+'" class="filter_tab_button" onclick="'+selectFunc+'(\''+data[key]['id']+'\',\''+id+'\')"><p id="content_'+id+'" class="filter_tab_wrapper">' + out + '</p></button>');
        if(rows.includes(id) || cols.includes(id) || regs.includes(id) || orgs.includes(id)) {
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

function setContextAttribute(val, context) {
    if (requestContext[context].includes(val)) {
        const index = requestContext[context].indexOf(val);
        if (index > -1) {
            requestContext[context].splice(index, 1);
        }
    } else {
        requestContext[context].push(val);
    }
    //console.log(context, JSON.stringify(requestContext[context]));
}

function setRowToContext(val, btn) {
    if (btn == null) return;
    activateButton(btn, rows);
    setContextAttribute(val, 'rows');
}

function setColToContext(val, btn) {
    if(btn == null) return;
    activateButton(btn, cols);
    let text = $('#content_'+btn).text();
    val = {
        "id" : val,
        "text" : text
    };
    setContextAttribute(val, 'cols');
}

function setRegToContext(val, btn) {
    if(btn == null) return;
    activateButton(btn, regs);
    setContextAttribute(val, 'regs');
}

function setOrgToContext(val, btn) {
    if(btn == null) return;
    activateButton(btn, orgs);
    setContextAttribute(val, 'orgs');
}

function setFilter(tab) {
    $('#filter_type').attr("disabled", true);
    if(tab === "1") {
        orgs = [];
        requestContext['orgs'] = [];
        requestContext['obj'] = 'reg';
        $('#filter_reg').css('display', 'block');
        $('#filter_org').css('display', 'none');
        showRegions('');
    } else if(tab === "2") {
        regs = [];
        requestContext['regs'] = [];
        requestContext['obj'] = 'org';
        $('#filter_reg').css('display', 'none');
        $('#filter_org').css('display', 'block');
        showOrgs('');
    }
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
            ddata = data;
            showPanel(ddata, '', 'filter_reg_data', 'filter_reg_wrapper', 'reg_data', initOrgs, 'setRegToContext');
        }).catch(function () {
            alert("Server Error. Cant fetch data of regions from database.");
            hideLoadingWrapper($('#filter_reg_wrapper'), () => { isLoading = false; });
        });
    }
}

function showRegions(filter) {
    if (!isLoading) {
        showPanel(ddata, filter, 'filter_reg_data', 'filter_reg_wrapper', 'reg_data', () => {
            showOrgs(); $('#filter_type').removeAttr('disabled');
            }, 'setRegToContext');
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
    showPanel(odata, filter, 'filter_org_data', 'filter_org_wrapper', 'org_data', () => {
        blocked = false; $('#filter_type').removeAttr('disabled');
        }, 'setOrgToContext');
}

function showOutput() {
    $('#out_table_data').remove();
    const $div = $('<div id="out_table_data"></div>');
    $('#out_table').append($div);
    createVRTable('out_table_data', fdata);
}

function doFilter() {
    //console.log(JSON.stringify(requestContext));
    if (!isLoading) {
        isLoading = true;
        fetch(getFilterAPI, {
            method: 'POST',
            body: JSON.stringify(requestContext)
        }).then(function (response) {
            return response.json();
        }).then(function (data) {
            fdata = data;
            showOutput();
            isLoading = false;
        }).catch(function () {
            alert("Server Error. Cant fetch data from database.");
           // hideLoadingWrapper($('#filter_out_wrapper'), () => { isLoading = false; });
            isLoading = false;
        });
    }
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