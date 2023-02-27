const capital = 'Воронеж';
var xBias = -150;
var yBias = 0;
var isLoading = false;
var currDistr = '';
var ddata = '';
let getDistrictURL = '/api/map/districtData/'; //old default value
let orgLink = "/view/org"

var showTitle = function (obj, map) {
    var box = obj.getBBox();
    var cls = obj.data('name') == '' ? 'class="empty"' : '';
    var $title = $('<div id="mapTitle" ' + cls + ' style="position: absolute; pointer-events:none;">' + obj.data('hoverName') + '<\/div>');
    $('#'+map).append($title);
    $title.css({
        top: box.y + box.height / 2 + (obj.data('offy') || 0) + yBias,
        left: (box.x + box.width / 2) - $title.innerWidth() + (obj.data('offx') || 0) + xBias
    });
};

var hideTitle = function () {
    $('#mapTitle').remove();
};
function clearPaths() {
    $('#map').children().remove();
}

function setMap(a_map, p_map) {
    $('#'+ p_map).css("display", "none");
    $('#' + a_map).css("display", "block");
    if(a_map === 'city_map') {
        xBias = -200;
        yBias = -100;
    } else {
        xBias = -150;
        yBias = 0;
    }
}

function setPaths(map, paths) {
    var w = 600;
    var h = 600;

    const r = Raphael(map, w, h),

        attributes = {
            fill: '#437A65',
            stroke: '#ccc',
            'stroke-width': 2,
            'stroke-linejoin': 'round'
        },

        arr = new Array();

    for (var region in paths) {
        var obj = r.path(paths[region].path);
        var regionColor = paths[region].p_color;
        obj.attr(attributes);
        obj.attr('fill', regionColor);
        arr[obj.id] = region;
        obj.data('name', paths[region].name);
        obj.data('hoverName', paths[region].hoverName);
        obj.data('offx', paths[region].offx);
        obj.data('offy', paths[region].offy);
        obj.hover(function () {
            if (paths[arr[this.id]].name != '') {
                this.animate({
                    fill: paths[arr[this.id]].a_color
                }, 300)
            }
            showTitle(this, map);
        }, function () {
            this.animate({
                fill: paths[arr[this.id]].p_color
            }, 300);
            hideTitle();
        })
            .click(function () {
                if (paths[arr[this.id]].name == '') return false;
                let distr = paths[arr[this.id]].name;
                if (distr === currDistr) return false;
                if (!isLoading) {
                    isLoading = true;
                    if (paths[arr[this.id]].hoverName === capital) {
                        setMap('city_map', 'map');
                        isLoading = false;
                        return false;
                    }
                    showLoadingWrapper($('#map_data_wrapper'), () => {
                        $('#org_filter').remove();
                        $('#district_data').remove();
                    });
                    fetch(getDistrictURL + distr).then(function (response) {
                        return response.json();
                    }).then(function (data) {
                        ddata = data;
                        currDistr = distr;
                        showOrganizations('');
                    }).catch(function () {
                        alert("Server Error. Cant fetch data of " + distr + " district from database.");
                        hideLoadingWrapper($('#map_data_wrapper'), () => { isLoading = false; });
                    });
                    //isLoading = false;
                }
            });
    }
    if(map === 'city_map') {
        $('#city_map').children('svg').children('path').each(function () {
            $(this).css( {'transform' : 'translate(-200px, -100px)' });
        })
    }
}

function showOrganizations(filter) {
    const $mapData = $('#map_data');
    const $filterBar = $('<div id="org_filter"><p style="padding: 0px; margin: 5px;">Поиск:</p><input id="org_filter_input" value="'+filter+'" onchange="showOrganizations(this.value)"></div>');
    const $districtData = $('<div id="district_data"></div>');
    const $wrapper = $('#map_data_wrapper');
    if (!isLoading) {
        isLoading = true;
        showLoadingWrapper($wrapper, () => {
            $('#org_filter').remove();
            $('#district_data').remove();
        });
    }
    ddata.sort(OrderByComparator('name'));
    for (const key in ddata) {
        if(filter !== '' && !ddata[key]['name'].toLocaleLowerCase('ru-RU').includes(filter.toLocaleLowerCase('ru-RU')))
            continue;
        let out = '- ' + ddata[key].name + '<br/>';
        const $org = $('<a class="org_link" href="'+orgLink +'/'+ ddata[key].id + '"><p class="org_link_wrapper">' + out + '</p></a>');
        $districtData.append($org);
    }
    hideLoadingWrapper($wrapper, () => {
        $mapData.append($filterBar);
        $mapData.append($districtData);
    });
    isLoading = false;
}

function OrderByComparator(prop) {
    return function (a, b) {
        return ('' + a[prop].attr).localeCompare(b[prop].attr);
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